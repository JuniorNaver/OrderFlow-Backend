package com.youthcase.orderflow.gr.service;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.gr.domain.*;
import com.youthcase.orderflow.gr.dto.*;
import com.youthcase.orderflow.gr.mapper.GoodsReceiptMapper;
import com.youthcase.orderflow.gr.repository.GoodsReceiptHeaderRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.gr.repository.LotRepository;
import com.youthcase.orderflow.gr.repository.StkHistoryRepository;
import com.youthcase.orderflow.gr.status.GRExpiryType;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import com.youthcase.orderflow.gr.status.LotStatus;
import com.youthcase.orderflow.master.product.domain.ExpiryType;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.warehouse.domain.Warehouse;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.domain.POItem;
import com.youthcase.orderflow.po.domain.POStatus;
import com.youthcase.orderflow.po.dto.POItemResponseDTO;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.po.service.POService;
import com.youthcase.orderflow.stk.service.STKService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsReceiptService {

    private final GoodsReceiptHeaderRepository headerRepo;
    private final UserRepository userRepo;
    private final WarehouseRepository warehouseRepo;
    private final POHeaderRepository poHeaderRepo;
    private final ProductRepository productRepo;
    private final LotRepository lotRepository;
    private final GoodsReceiptMapper mapper;
    private final StkHistoryRepository stkHistoryRepository;

    // ✅ 재고 반영을 외부 포트(도메인 서비스)로 분리 — 실무 확장 포인트
    private final STKService stockService; // 아래 인터페이스 참고
    private final POService poProgressService; // 발주 수령 진척도 갱신(선택)

    public GoodsReceiptHeaderDTO create(GoodsReceiptHeaderDTO dto) {
        var user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        var warehouse = warehouseRepo.findById(dto.getWarehouseId()).orElseThrow(() -> new IllegalArgumentException("창고 없음"));
        var poHeader = poHeaderRepo.findById(dto.getPoId()).orElseThrow(() -> new IllegalArgumentException("발주 없음"));

        // 1) GTIN 목록 뽑아 한 번에 조회
        var gtins = dto.getItems() == null ? List.<String>of()
                : dto.getItems().stream().map(GoodsReceiptItemDTO::getGtin).distinct().toList();

        var productMap = productRepo.findByGtinIn(gtins).stream()
                .collect(Collectors.toMap(Product::getGtin, p -> p));

        // 2) 매핑
        GoodsReceiptHeader entity = mapper.toEntity(dto, user, warehouse, poHeader, productMap);

        // 3) 기본 상태값 세팅 (Enum 사용 시)
        if (entity.getStatus() == null) {
            entity.setStatus(GoodsReceiptStatus.RECEIVED);
        }
        if (entity.getReceiptDate() == null) {
            entity.setReceiptDate(LocalDate.now());
        }

        // 4) 저장 후 저장된 값 기준으로 반환
        GoodsReceiptHeader saved = headerRepo.save(entity);
        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<GoodsReceiptHeaderDTO> findAll() {
        return headerRepo.findAll().stream()
                .map(GoodsReceiptHeaderDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoodsReceiptHeaderDTO findById(Long id) {
        GoodsReceiptHeader header = headerRepo.findWithItemsByGrHeaderId(id)
                .orElseThrow(() -> new IllegalArgumentException("입고 데이터 없음"));

        var dto = mapper.toDTO(header);

        List<LotDTO> lots = lotRepository
                .findByGoodsReceiptItem_Header_GrHeaderId(id)
                .stream()
                .map(LotDTO::from)
                .toList();

        dto.setLots(lots);
        return dto;
    }

    // ✅ 핵심: 입고 확정 → 재고 반영
    @Transactional
    public void confirmReceipt(Long grId) {
        GoodsReceiptHeader header = headerRepo.findWithItemsByGrHeaderId(grId)
                .orElseThrow(() -> new IllegalArgumentException("입고 데이터 없음"));

        if (header.getStatus() == GoodsReceiptStatus.CONFIRMED) {
            throw new IllegalStateException("이미 확정된 입고입니다.");
        }

        // ✅ 상태 검증
        if (header.getStatus() != GoodsReceiptStatus.RECEIVED) {
            throw new IllegalArgumentException("입고 상태가 RECEIVED가 아닙니다.");
        }

        // ✅ 아이템 검증
        for (GoodsReceiptItem item : header.getItems()) {
            if (item.getQty() == null || item.getQty() <= 0) {
                throw new IllegalArgumentException("입고 수량이 0 이하인 품목이 있습니다. itemNo=" + item.getItemNo());
            }
            if (item.getProduct() == null) {
                throw new IllegalStateException("상품 매핑이 누락된 품목이 있습니다. itemNo=" + item.getItemNo());
            }
        }

        // ✅ 창고 정보
        String warehouseId = header.getWarehouse().getWarehouseId();

        // ✅ LOT + 재고(STK) 반영
        for (GoodsReceiptItem item : header.getItems()) {
            Product product = item.getProduct();

            // 1️⃣ LOT 생성
            Lot lot = new Lot();
            lot.setProduct(product);
            lot.setQty(item.getQty());
            lot.setGoodsReceiptItem(item);
            lot.setExpiryType(product.getExpiryType());

            // 상품 정책
            ExpiryType policy = product.getExpiryType();
            GRExpiryType calcType = item.getExpiryCalcType();

            // 2️⃣ 유통기한 계산
            if (policy != ExpiryType.NONE && calcType != GRExpiryType.NONE) {

                if (calcType == GRExpiryType.FIXED_DAYS && product.getShelfLifeDays() != null) {
                    lot.setExpDate(header.getReceiptDate().plusDays(product.getShelfLifeDays()));

                } else if (calcType == GRExpiryType.MFG_BASED && product.getShelfLifeDays() != null) {
                    if (item.getMfgDate() == null) {
                        throw new IllegalStateException("제조일(MFG_DATE)이 필요합니다: itemNo=" + item.getItemNo());
                    }
                    lot.setExpDate(item.getMfgDate().plusDays(product.getShelfLifeDays()));

                } else if (calcType == GRExpiryType.MANUAL) {
                    if (item.getExpDateManual() == null) {
                        throw new IllegalStateException("수동 입력 유통기한이 누락되었습니다: itemNo=" + item.getItemNo());
                    }
                    lot.setExpDate(item.getExpDateManual());
                }
            }

            boolean exists = lotRepository.findByProduct_GtinAndExpDateAndStatus(product.getGtin(), lot.getExpDate(), LotStatus.ACTIVE).isPresent();
            if (exists) throw new IllegalStateException("이미 동일 유통기한 LOT가 존재합니다.");

            // 3️⃣ LOT 저장
            lotRepository.save(lot);

            // 4️⃣ 재고 반영 (LOT 기반)
            stockService.increaseStock(
                    warehouseId,
                    product.getGtin(),
                    item.getQty(),
                    lot.getLotId(),   // LOT 연결
                    lot.getExpDate()  // 유통기한 전달
            );

            // ✅ 재고 로그 남기기 (STK는 건드리지 않음)
            stkHistoryRepository.save(
                    STKHistory.builder()
                            .warehouseId(warehouseId)
                            .product(product)
                            .lotId(lot.getLotId())
                            .actionType("IN") // 입고
                            .changeQty(item.getQty())
                            .note("입고 확정으로 자동 생성됨")
                            .build()
            );
        }

        // ✅ GR 상태 전환
        header.setStatus(GoodsReceiptStatus.CONFIRMED);
        headerRepo.save(header);

        // ✅ 발주 진척도 갱신
        poProgressService.updateReceiveProgress(header.getPoHeader().getPoId());
    }


    // (선택) 확정 취소(Reverse)도 같은 패턴으로 만들 수 있음
    @Transactional
    public void cancelConfirmedReceipt(Long id, String reason, User currentUser) {
        Optional<GoodsReceiptHeader> optionalGR = headerRepo.findById(id);
        GoodsReceiptHeader header;

        if (optionalGR.isPresent()) {
            // ✅ GR이 이미 존재 → 상태만 변경
            header = optionalGR.get();
            header.setStatus(GoodsReceiptStatus.CANCELED);
        } else {
            // ✅ GR이 없으면 (PENDING 상태)
            POHeader po = poHeaderRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("발주 데이터를 찾을 수 없습니다."));

            // ✅ 현재 로그인한 유저의 Store 기반으로 Warehouse 찾기
            Store store = currentUser.getStore();  // User 엔티티에 Store가 연관되어 있다고 가정
            Warehouse warehouse = warehouseRepo.findByStore_StoreId(store.getStoreId())
                    .orElseThrow(() -> new RuntimeException("점포에 해당하는 창고를 찾을 수 없습니다."));

            header = GoodsReceiptHeader.builder()
                    .poHeader(po)
                    .warehouse(warehouse)
                    .user(currentUser)
                    .receiptDate(LocalDate.now())
                    .status(GoodsReceiptStatus.CANCELED)
                    .note("PENDING 상태에서 취소됨: " + reason)
                    .build();

            headerRepo.save(header);
        }

        header.setNote(reason);
        headerRepo.save(header);
    }

    @Transactional
    public GoodsReceiptHeaderDTO createAndConfirmFromPO(Long poId) {
        // ✅ 1) 기존 GR 존재 여부 확인
        Optional<GoodsReceiptHeader> existing = headerRepo.findByPoHeader_PoId(poId);
        if (existing.isPresent()) {
            // ❌ 단순 반환 → ✅ 예외 발생으로 변경
            throw new IllegalStateException("이미 입고 처리된 발주입니다.");
        }

        // ✅ 2) 발주 조회
        POHeader po = poHeaderRepo.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("발주 없음"));

        // ✅ 3) Warehouse 조회
        var warehouse = warehouseRepo.findFirstByStore_StoreId(
                po.getUser().getStore().getStoreId()
        ).orElseThrow(() -> new IllegalArgumentException("해당 점포의 창고 없음"));

        // ✅ 4) GR 생성
        GoodsReceiptHeader gr = GoodsReceiptHeader.builder()
                .poHeader(po)
                .warehouse(warehouse)
                .user(po.getUser())
                .status(GoodsReceiptStatus.CONFIRMED)
                .receiptDate(LocalDate.now())
                .build();

        GoodsReceiptHeader saved = headerRepo.save(gr);

        // ✅ 5) 재고 반영
        for (POItem item : po.getItems()) {
            try {
                String gtin = item.getProduct().getGtin();
                Long qty = item.getOrderQty();
                LocalDate expDate = null;

                // ✅ 상품의 ExpiryType을 기반으로 GRExpiryType 매핑
                GRExpiryType expiryType = switch (item.getProduct().getExpiryType()) {
                    case NONE -> GRExpiryType.NONE;
                    case USE_BY, BEST_BEFORE -> GRExpiryType.FIXED_DAYS;
                };

                // ✅ 유통기한 계산 로직
                switch (expiryType) {
                    case NONE -> expDate = null;
                    case FIXED_DAYS -> {
                        Integer shelfLife = item.getProduct().getShelfLifeDays();
                        if (shelfLife != null && shelfLife > 0)
                            expDate = LocalDate.now().plusDays(shelfLife);
                    }
                    case MANUAL -> expDate = item.getExpectedArrival();
                    case MFG_BASED -> {
                        Integer shelfLife = item.getProduct().getShelfLifeDays();
                        LocalDate mfgDate = item.getExpectedArrival();
                        if (mfgDate != null && shelfLife != null && shelfLife > 0)
                            expDate = mfgDate.plusDays(shelfLife);
                    }
                }

                // ✅ Lot 생성 및 저장
                Lot newLot = Lot.builder()
                        .product(item.getProduct())
                        .qty(qty)
                        .expDate(expDate)
                        .status(LotStatus.ACTIVE)
                        .build();
                lotRepository.save(newLot);

                // ✅ 재고 반영
                if (gtin != null && qty != null && qty > 0) {
                    stockService.increaseStock(
                            warehouse.getWarehouseId(),
                            gtin,
                            qty,
                            newLot.getLotId(),
                            expDate
                    );
                }

            } catch (Exception e) {
                System.out.println("⚠️ 재고 반영 중 오류 발생: " + e.getMessage());
            }
        }

        return GoodsReceiptHeaderDTO.from(saved);
    }




    @Transactional(readOnly = true)
    public POForGRDTO searchPOForGR(String barcode) {
        var po = poHeaderRepo.findByBarcodeWithItems(barcode)
                .orElseThrow(() -> new IllegalArgumentException("발주 없음"));

        var itemDTOs = po.getItems().stream()
                .map(item -> POItemResponseDTO.builder()
                        .itemNo(item.getItemNo())
                        .productName(item.getProduct().getProductName())
                        .gtin(item.getProduct().getGtin())
                        .expectedArrival(item.getExpectedArrival())
                        .purchasePrice(
                                item.getPurchasePrice() != null
                                        ? item.getPurchasePrice() // ✅ Price 안의 BigDecimal 꺼냄
                                        : BigDecimal.ZERO
                        )
                        .orderQty(item.getOrderQty())
                        .total(item.getTotal())
                        .status(item.getStatus())
                        .build())
                .toList();

        return POForGRDTO.builder()
                .poId(po.getPoId())
                .name(po.getUser() != null ? po.getUser().getUserId() : "unknown")
                .totalAmount(po.getTotalAmount())
                .status(po.getStatus() != null ? po.getStatus().name() : "UNKNOWN")
                .items(itemDTOs)
                .build();
    }

    @Transactional
    @Scheduled(cron = "0 0 9 * * *")
    public void checkoutExpiredLots() {
        List<Lot> lots = lotRepository.findAll().stream()
                .filter(l -> l.getRemainDays() <= 0 && l.getStatus() != LotStatus.EXPIRED)
                .peek(l -> l.setStatus(LotStatus.EXPIRED))
                .toList();

        lotRepository.saveAll(lots);
    }

    @Transactional(readOnly = true)
    public List<GRListDTO> findAllWithPOStatus() {
        return headerRepo.findAllWithPOStatus(
                POStatus.DELETED,     // 삭제된 발주 제외
                POStatus.S,       // 🧩 장바구니(임시저장) 상태 제외
                GoodsReceiptStatus.PENDING // 입고대기 상태로 표시
        );// 🔸 2. 입고 기본 상태
    }

    @Transactional
    public void delete(Long id) {
        GoodsReceiptHeader header = headerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("입고 내역을 찾을 수 없습니다."));

        // ✅ 상태를 CANCEL로 전환
        switch (header.getStatus()) {
            case PENDING, RECEIVED -> {
                header.setStatus(GoodsReceiptStatus.CANCELED);
                headerRepo.save(header);
            }
            case CANCELED -> throw new IllegalStateException("이미 취소된 내역입니다.");
            default -> throw new IllegalStateException("삭제 불가능한 상태입니다: " + header.getStatus());
        }
    }

}
