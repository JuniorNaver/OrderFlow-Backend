package com.youthcase.orderflow.gr.service;

import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.domain.GoodsReceiptItem;
import com.youthcase.orderflow.gr.dto.GoodsReceiptHeaderDTO;
import com.youthcase.orderflow.gr.dto.GoodsReceiptItemDTO;
import com.youthcase.orderflow.gr.dto.GoodsReceiptRequest;
import com.youthcase.orderflow.gr.dto.GoodsReceiptResponse;
import com.youthcase.orderflow.gr.mapper.GoodsReceiptMapper;
import com.youthcase.orderflow.gr.repository.GoodsReceiptHeaderRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.gr.repository.GoodsReceiptItemRepository;
import com.youthcase.orderflow.gr.status.GoodsReceiptStatus;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import com.youthcase.orderflow.master.warehouse.repository.WarehouseRepository;
import com.youthcase.orderflow.po.service.POService;
import com.youthcase.orderflow.stk.service.STKService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsReceiptService {

    private final GoodsReceiptHeaderRepository headerRepo;
    private final GoodsReceiptItemRepository itemRepo;
    private final UserRepository userRepo;
    private final WarehouseRepository warehouseRepo;
    private final POHeaderRepository poHeaderRepo;
    private final ProductRepository productRepo;
    private final GoodsReceiptMapper mapper;
//    private final GoodsReceiptValidator validator;

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
        GoodsReceiptHeader header = headerRepo.findWithItemsById(id)
                .orElseThrow(() -> new IllegalArgumentException("입고 데이터 없음"));
        return mapper.toDTO(header);
    }

    // ✅ 핵심: 입고 확정 → 재고 반영
    public void confirmReceipt(Long grId) {
        GoodsReceiptHeader header = headerRepo.findWithItemsById(grId)
                .orElseThrow(() -> new IllegalArgumentException("입고 데이터 없음"));

        // 상태 검증
        // if (header.getStatus() != GoodsReceiptStatus.RECEIVED) throw ...
        if (header.getStatus() != GoodsReceiptStatus.RECEIVED) {
            throw new IllegalArgumentException("입고 상태가 RECEIVED가 아닙니다.");
        }

        // 아이템 검증 (수량 0 등)
        for (GoodsReceiptItem item : header.getItems()) {
            if (item.getQty() == null || item.getQty() <= 0) {
                throw new IllegalArgumentException("입고 수량이 0 이하인 품목이 있습니다. itemNo=" + item.getItemNo());
            }
            if (item.getProduct() == null) {
                throw new IllegalStateException("상품 매핑이 누락된 품목이 있습니다. itemNo=" + item.getItemNo());
            }
        }

        // ✅ 재고 반영 (창고 단위)
        String warehouseId = header.getWarehouse().getWarehouseId();
        for (GoodsReceiptItem item : header.getItems()) {
            stockService.increaseStock(
                    warehouseId,
                    item.getProduct().getGtin(),
                    item.getQty(),
                    null,           // lotNo (추후 확장)
                    null            // expDate (추후 확장)
            );
        }

        // 상태 전환
        header.setStatus(GoodsReceiptStatus.CONFIRMED);
        headerRepo.save(header);

        // ✅ 발주 진척도 갱신 (선택: 부분입고/완료 상태 업데이트)
        // 내부 구현 예: 발주 아이템 대비 누계 수령수량 계산 후 PARTIAL/FULL 상태 전환
        poProgressService.updateReceiveProgress(header.getPoHeader().getPoId());
    }

    // (선택) 확정 취소(Reverse)도 같은 패턴으로 만들 수 있음
    public void cancelConfirmedReceipt(Long grId, String reason) {
        GoodsReceiptHeader header = headerRepo.findWithItemsById(grId)
                .orElseThrow(() -> new IllegalArgumentException("입고 데이터 없음"));

        if (header.getStatus() != GoodsReceiptStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "확정 상태가 아니어서 취소할 수 없습니다. (status=" + header.getStatus() + ")"
            );
        }

        String warehouseId = header.getWarehouse().getWarehouseId();
        // 재고 차감
        for (GoodsReceiptItem item : header.getItems()) {
            stockService.decreaseStock(
                    warehouseId,
                    item.getProduct().getGtin(),
                    item.getQty(),
                    null, null
            );
        }

        // 상태 전환
        // header.setStatus(GoodsReceiptStatus.CANCELED);
        header.setStatus(GoodsReceiptStatus.CANCELED);
        header.setNote(
                (header.getNote() == null ? "" : header.getNote() + " | ")
                        + "Canceled: " + reason
        );
        headerRepo.save(header);

        // 발주 진척도 롤백
        poProgressService.updateReceiveProgress(header.getPoHeader().getPoId());
    }

    @Transactional
    public GoodsReceiptHeaderDTO createAndConfirmFromPO(Long poId) {
        POHeader po = poHeaderRepo.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("발주 없음"));

        GoodsReceiptHeader gr = GoodsReceiptHeader.builder()
                .poHeader(po)
                .warehouse(po.getUser().getWorkspace() != null
                        ? warehouseRepo.findById(po.getUser().getWorkspace()).orElseThrow()
                        : null)
                .user(po.getUser())
                .status(GoodsReceiptStatus.CONFIRMED)
                .receiptDate(LocalDate.now())
                .build();

        GoodsReceiptHeader saved = headerRepo.save(gr);
        // ✅ 재고 반영
        for (Object item : po.getItems()) {
            try {
                var clazz = item.getClass();

                // 1️⃣ 상품 정보 꺼내기 (getProduct() or getGtin())
                Object product = null;
                try {
                    product = clazz.getMethod("getProduct").invoke(item);
                } catch (NoSuchMethodException e1) {
                    try {
                        product = clazz.getMethod("getGtin").invoke(item);
                    } catch (NoSuchMethodException e2) {
                        product = null;
                    }
                }

                String gtin = null;
                if (product != null) {
                    try {
                        gtin = (String) product.getClass().getMethod("getGtin").invoke(product);
                    } catch (Exception e) {
                        // product가 String일 수도 있으니까 그냥 캐스팅 시도
                        gtin = product.toString();
                    }
                }

                // 2️⃣ 수량 꺼내기 (getOrderQty() or getQty())
                Long qty = 0L;
                try {
                    Object result = clazz.getMethod("getOrderQty").invoke(item);
                    qty = (result instanceof Number) ? ((Number) result).longValue() : 0L;
                } catch (NoSuchMethodException e1) {
                    try {
                        Object result = clazz.getMethod("getQty").invoke(item);
                        qty = (result instanceof Number) ? ((Number) result).longValue() : 0L;
                    } catch (NoSuchMethodException e2) {
                        qty = 0L;
                    }
                }

                // 3️⃣ 실제 재고 반영
                if (gtin != null && qty > 0) {
                    stockService.increaseStock(
                            gr.getWarehouse().getWarehouseId(),
                            gtin,
                            qty,
                            null, null
                    );
                } else {
                    System.out.println("⚠️ GTIN 또는 수량이 유효하지 않아 스킵됨: " + item);
                }

            } catch (Exception e) {
                System.out.println("⚠️ 재고 반영 중 오류 발생: " + e.getMessage());
            }
        }
        return GoodsReceiptHeaderDTO.from(saved);
    }



   /* @Transactional
    public GoodsReceiptResponse createReceipt(GoodsReceiptRequest request) {
        // ✅ 1. 전체 유효성 검사
        validator.validateAll(request);

        // ✅ 2. 저장 로직 수행
        GoodsReceiptHeader header = new GoodsReceiptHeader();
    }*/
}