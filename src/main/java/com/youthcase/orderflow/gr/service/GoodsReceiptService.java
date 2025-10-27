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
import com.youthcase.orderflow.master.product.domain.StorageMethod;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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


    /** ✅ 입고 생성 */
    public GoodsReceiptHeaderDTO create(GoodsReceiptHeaderDTO dto) {
        var user = userRepo.findById(dto.getUserId()).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        var poHeader = poHeaderRepo.findById(dto.getPoId()).orElseThrow(() -> new IllegalArgumentException("발주 없음"));

        var gtins = dto.getItems() == null ? List.<String>of()
                : dto.getItems().stream().map(GoodsReceiptItemDTO::getGtin).distinct().toList();

        var productMap = productRepo.findByGtinIn(gtins).stream()
                .collect(Collectors.toMap(Product::getGtin, p -> p));

        // 창고는 아이템별 자동 매핑하므로 header에는 대표만 세팅
        var defaultWarehouse = warehouseRepo.findFirstByStore_StoreId(user.getStore().getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("점포에 창고 없음"));

        GoodsReceiptHeader entity = mapper.toEntity(dto, user, defaultWarehouse, poHeader, productMap);
        entity.setStatus(Optional.ofNullable(entity.getStatus()).orElse(GoodsReceiptStatus.RECEIVED));
        entity.setReceiptDate(Optional.ofNullable(entity.getReceiptDate()).orElse(LocalDate.now()));

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
    /** ✅ 입고 확정 (StorageMethod별 창고 자동매핑 + LOT + STK 반영) */
    @Transactional
    public void confirmReceipt(Long grId) {
        GoodsReceiptHeader header = headerRepo.findWithItemsByGrHeaderId(grId)
                .orElseThrow(() -> new IllegalArgumentException("입고 데이터 없음"));

        if (header.getStatus() == GoodsReceiptStatus.CONFIRMED)
            throw new IllegalStateException("이미 확정된 입고입니다.");
        if (header.getStatus() != GoodsReceiptStatus.RECEIVED)
            throw new IllegalStateException("입고 상태가 RECEIVED가 아닙니다.");

        Store store = header.getUser().getStore();

        for (GoodsReceiptItem item : header.getItems()) {
            Product product = item.getProduct();
            if (item.getQty() == null || item.getQty() <= 0)
                throw new IllegalArgumentException("입고 수량이 0 이하입니다. itemNo=" + item.getItemNo());

            // ✅ StorageMethod에 따른 창고 자동 매핑
            StorageMethod method = product.getStorageMethod();
            Warehouse warehouse = warehouseRepo
                    .findByStore_StoreIdAndStorageMethod(store.getStoreId(), method)
                    .orElseThrow(() -> new IllegalArgumentException("해당 보관방식 창고 없음: " + method));

            item.setWarehouse(warehouse); // 개별 아이템에 창고 세팅

            // ✅ LOT 생성
            Lot lot = new Lot();
            lot.setProduct(product);
            lot.setQty(item.getQty());
            lot.setGoodsReceiptItem(item);
            lot.setExpiryType(product.getExpiryType());

            ExpiryType policy = product.getExpiryType();
            GRExpiryType calcType = item.getExpiryCalcType();

            if (policy != ExpiryType.NONE && calcType != GRExpiryType.NONE) {
                if (calcType == GRExpiryType.FIXED_DAYS && product.getShelfLifeDays() != null)
                    lot.setExpDate(header.getReceiptDate().plusDays(product.getShelfLifeDays()));
                else if (calcType == GRExpiryType.MFG_BASED && product.getShelfLifeDays() != null) {
                    if (item.getMfgDate() == null)
                        throw new IllegalStateException("제조일이 필요합니다: itemNo=" + item.getItemNo());
                    lot.setExpDate(item.getMfgDate().plusDays(product.getShelfLifeDays()));
                } else if (calcType == GRExpiryType.MANUAL) {
                    if (item.getExpDateManual() == null)
                        throw new IllegalStateException("수동 유통기한 누락: itemNo=" + item.getItemNo());
                    lot.setExpDate(item.getExpDateManual());
                }
            }

            boolean exists = lotRepository
                    .findByProduct_GtinAndExpDateAndStatus(product.getGtin(), lot.getExpDate(), LotStatus.ACTIVE)
                    .isPresent();
            if (exists)
                throw new IllegalStateException("이미 동일 유통기한 LOT 존재: " + product.getGtin());

            lotRepository.save(lot);

            // ✅ 재고 반영
            stockService.increaseStock(
                    warehouse.getWarehouseId(),
                    product.getGtin(),
                    item.getQty(),
                    lot.getLotId(),
                    lot.getExpDate()
            );

            // ✅ 입고 이력
            stkHistoryRepository.save(STKHistory.builder()
                    .warehouseId(warehouse.getWarehouseId())
                    .product(product)
                    .lotId(lot.getLotId())
                    .actionType("IN")
                    .changeQty(item.getQty())
                    .note("입고 확정 (자동 창고 매핑)")
                    .build());
        }

        header.setStatus(GoodsReceiptStatus.CONFIRMED);
        headerRepo.save(header);

        poProgressService.updateReceiveProgress(header.getPoHeader().getPoId());
    }



    // 발주 취소
    @Transactional
    public void cancelByPo(Long poId, String reason, User user) {
        // 1️⃣ GR 존재 여부 확인
        Optional<GoodsReceiptHeader> optionalGR = headerRepo.findByPoHeader_PoId(poId);

        if (optionalGR.isPresent()) {
            GoodsReceiptHeader gr = optionalGR.get();

            switch (gr.getStatus()) {
                case CONFIRMED -> throw new IllegalStateException("이미 확정된 입고는 취소할 수 없습니다.");
                case CANCELED -> throw new IllegalStateException("이미 취소된 입고입니다.");
                default -> {
                    gr.setStatus(GoodsReceiptStatus.CANCELED);
                    gr.setNote("취소 사유: " + reason);
                    headerRepo.save(gr);
                }
            }
        } else {
            // 2️⃣ 아직 GR이 생성되지 않은 경우 (PENDING 상태)
            POHeader po = poHeaderRepo.findById(poId)
                    .orElseThrow(() -> new IllegalArgumentException("발주 데이터를 찾을 수 없습니다."));

            // 발주 자체를 취소 처리 (선택사항)
            po.setStatus(POStatus.CANCELED);
            poHeaderRepo.save(po);
        }
    }

    /** ✅ 발주 → 입고 자동 생성 및 확정 */
    @Transactional
    public GoodsReceiptHeaderDTO createAndConfirmFromPO(Long poId) {
        if (headerRepo.findByPoHeader_PoId(poId).isPresent())
            throw new IllegalStateException("이미 입고 처리된 발주입니다.");

        POHeader po = poHeaderRepo.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("발주 없음"));
        Store store = po.getUser().getStore();

        var defaultWarehouse = warehouseRepo.findFirstByStore_StoreId(store.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("점포 창고 없음"));

        GoodsReceiptHeader gr = GoodsReceiptHeader.builder()
                .poHeader(po)
                .user(po.getUser())
                .status(GoodsReceiptStatus.CONFIRMED)
                .receiptDate(LocalDate.now())
                .build();
        GoodsReceiptHeader saved = headerRepo.save(gr);

        for (POItem item : po.getItems()) {
            Product product = item.getProduct();
            StorageMethod method = product.getStorageMethod();

            Warehouse warehouse = warehouseRepo
                    .findByStore_StoreIdAndStorageMethod(store.getStoreId(), method)
                    .orElseThrow(() -> new IllegalArgumentException("해당 보관방식 창고 없음: " + method));

            LocalDate expDate = null;
            if (product.getShelfLifeDays() != null && product.getShelfLifeDays() > 0)
                expDate = LocalDate.now().plusDays(product.getShelfLifeDays());

            Lot lot = Lot.builder()
                    .product(product)
                    .qty(item.getOrderQty())
                    .expDate(expDate)
                    .status(LotStatus.ACTIVE)
                    .build();
            lotRepository.save(lot);

            stockService.increaseStock(
                    warehouse.getWarehouseId(),
                    product.getGtin(),
                    item.getOrderQty(),
                    lot.getLotId(),
                    expDate
            );
        }

        poProgressService.updateReceiveProgress(po.getPoId());
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
                POStatus.CANCELED,     // 삭제된 발주 제외
                POStatus.S,       // 🧩 장바구니(임시저장) 상태 제외
                GoodsReceiptStatus.PENDING // 입고대기 상태로 표시
        );// 🔸 2. 입고 기본 상태
    }

    // ✅ Service
    @Transactional(readOnly = true)
    public List<GRListDTO> searchGoodsReceipts(String query, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        if (query == null || query.isBlank()) {
            return headerRepo.findByReceiptDateBetween(startDate, endDate)
                    .stream()
                    .map(GRListDTO::from)
                    .toList();
        }

        return headerRepo.searchByKeywordAndDate(query, startDate, endDate)
                .stream()
                .map(GRListDTO::from)
                .toList();
    }



}
