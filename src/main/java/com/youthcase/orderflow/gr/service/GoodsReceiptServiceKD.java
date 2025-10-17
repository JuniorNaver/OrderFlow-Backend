//// src/backend/src/main/java/com/orderflow/receipt/service/GoodsReceiptService.java
//package com.youthcase.orderflow.gr.service;
//
//import com.youthcase.orderflow.gr.dto.GrHeaderDto;
//import com.youthcase.orderflow.gr.repository.GrHeaderRepositoryKD;
//import com.youthcase.orderflow.gr.repository.GrItemRepositoryKD;
//import com.youthcase.orderflow.gr.repository.LotRepositoryKD;
//import com.youthcase.orderflow.po.repository.POHeaderRepository;
//import com.youthcase.orderflow.master.product.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
///**
// * 입고 관리 서비스
// * MM_GR: 입고 관리
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class GoodsReceiptServiceKD {
//
//    private final GrHeaderRepositoryKD grHeaderRepository;
//    private final GrItemRepositoryKD grItemRepository;
//    private final LotRepositoryKD lotRepository;
//    private final POHeaderRepository poHeaderRepository;
//    private final ProductRepository productRepository;
//
//    /**
//     * 입고 목록 조회
//     */
//    @Transactional(readOnly = true)
//    public Page<GrHeaderDto> getGoodsReceipts(Pageable pageable) {
//        return grHeaderRepository.findAll(pageable)
//                .map(GrHeaderDto::fromSummary);
//    }
//
//    /**
//     * 입고 목록 조회 (점포별)
//     */
//    @Transactional(readOnly = true)
//    public Page<GrHeaderDto> getGoodsReceiptsByStore(Long storeId, Pageable pageable) {
//        return grHeaderRepository.findByStoreId(storeId, pageable)
//                .map(GrHeaderDto::fromSummary);
//    }
//
//    /**
//     * 입고 목록 조회 (발주별)
//     */
//    @Transactional(readOnly = true)
//    public Page<GrHeaderDto> getGoodsReceiptsByPo(Long poId, Pageable pageable) {
//        return grHeaderRepository.findByPoHeaderPoId(poId, pageable)
//                .map(GrHeaderDto::fromSummary);
//    }
//
//    /**
//     * 입고 목록 조회 (기간별)
//     */
//    @Transactional(readOnly = true)
//    public Page<GrHeaderDto> getGoodsReceiptsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
//        return grHeaderRepository.findByGrDateBetween(startDate, endDate, pageable)
//                .map(GrHeaderDto::fromSummary);
//    }
//
//    /**
//     * 입고 상세 조회
//     */
//    @Transactional(readOnly = true)
//    public GrHeaderDto getGoodsReceipt(Long grId) {
//        GrHeader grHeader = grHeaderRepository.findById(grId)
//                .orElseThrow(() -> new ResourceNotFoundException("GoodsReceipt", "id", grId));
//        return GrHeaderDto.from(grHeader);
//    }
//
//    /**
//     * 입고번호로 조회
//     */
//    @Transactional(readOnly = true)
//    public GrHeaderDto getGoodsReceiptByNumber(String grNumber) {
//        GrHeader grHeader = grHeaderRepository.findByGrNumber(grNumber)
//                .orElseThrow(() -> new ResourceNotFoundException("GoodsReceipt", "grNumber", grNumber));
//        return GrHeaderDto.from(grHeader);
//    }
//
//    /**
//     * 입고 등록
//     * MM_GR_001: 입고 등록
//     */
//    @Transactional
//    public GrHeaderDto createGoodsReceipt(GrCreateRequest request, Long userId) {
//        // 입고번호 생성
//        String grNumber = generateGrNumber();
//
//        // 발주 정보 조회 (선택)
//        PoHeader poHeader = null;
//        if (request.getPoId() != null) {
//            poHeader = poHeaderRepository.findById(request.getPoId())
//                    .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", request.getPoId()));
//
//            // 발주 상태 확인
//            if (poHeader.getStatus() != PoHeader.PoStatus.APPROVED) {
//                throw new BusinessException("Purchase order must be approved before goods receipt");
//            }
//        }
//
//        // GrHeader 생성
//        GrHeader grHeader = GrHeader.builder()
//                .grNumber(grNumber)
//                .poHeader(poHeader)
//                .storeId(request.getStoreId())
//                .userId(userId)
//                .grDate(request.getGrDate())
//                .grTime(request.getGrTime() != null ? request.getGrTime() : LocalDateTime.now())
//                .status(GrHeader.GrStatus.COMPLETED)
//                .scanType(request.getScanType() != null ?
//                        GrHeader.ScanType.valueOf(request.getScanType()) : GrHeader.ScanType.MANUAL)
//                .remarks(request.getRemarks())
//                .build();
//
//        // GrItem 생성 및 LOT 처리
//        int itemNumber = 1;
//        for (GrItemDto itemDto : request.getItems()) {
//            Product product = productRepository.findById(itemDto.getProductCode())
//                    .orElseThrow(() -> new ResourceNotFoundException("Product", "code", itemDto.getProductCode()));
//
//            // LOT 생성
//            Lot lot = createOrUpdateLot(product, itemDto);
//
//            // GrItem 생성
//            GrItem grItem = GrItem.builder()
//                    .product(product)
//                    .lot(lot)
//                    .itemNumber(itemNumber++)
//                    .quantity(itemDto.getQuantity())
//                    .costPrice(itemDto.getCostPrice())
//                    .expiryDate(itemDto.getExpiryDate())
//                    .barcodeData(itemDto.getBarcodeData())
//                    .remarks(itemDto.getRemarks())
//                    .build();
//
//            grHeader.addItem(grItem);
//        }
//
//        // 총액 계산
//        grHeader.calculateTotals();
//
//        // 저장
//        grHeader = grHeaderRepository.save(grHeader);
//
//        // 발주 상태 업데이트 (모든 품목이 입고되었는지 확인)
//        if (poHeader != null) {
//            updatePoStatus(poHeader);
//        }
//
//        log.info("Goods receipt created: {} by user {}", grNumber, userId);
//
//        return GrHeaderDto.from(grHeader);
//    }
//
//    /**
//     * 입고 취소
//     * MM_GR_003: 입고 취소
//     */
//    @Transactional
//    public void cancelGoodsReceipt(Long grId, Long userId) {
//        GrHeader grHeader = grHeaderRepository.findById(grId)
//                .orElseThrow(() -> new ResourceNotFoundException("GoodsReceipt", "id", grId));
//
//        // 권한 확인
//        if (!grHeader.getUserId().equals(userId)) {
//            throw new BusinessException("You can only cancel your own goods receipts");
//        }
//
//        // 상태 확인
//        if (grHeader.getStatus() == GrHeader.GrStatus.CANCELLED) {
//            throw new BusinessException("Goods receipt is already cancelled");
//        }
//
//        // TODO: 출고 이력 확인 (출고된 재고가 있으면 취소 불가)
//        // 실제로는 Stock 테이블을 확인해야 함
//
//        // LOT 수량 복원
//        for (GrItem item : grHeader.getItems()) {
//            if (item.getLot() != null) {
//                item.getLot().decreaseQuantity(item.getQuantity());
//                lotRepository.save(item.getLot());
//            }
//        }
//
//        // 상태 변경
//        grHeader.setStatus(GrHeader.GrStatus.CANCELLED);
//        grHeaderRepository.save(grHeader);
//
//        // 발주 상태 업데이트
//        if (grHeader.getPoHeader() != null) {
//            updatePoStatus(grHeader.getPoHeader());
//        }
//
//        log.info("Goods receipt cancelled: {}", grHeader.getGrNumber());
//    }
//
//    /**
//     * LOT 생성 또는 업데이트
//     * MM_GR_004: LOT 관리
//     */
//    private Lot createOrUpdateLot(Product product, GrItemDto itemDto) {
//        // LOT 번호 생성 (YYYYMMDD-상품코드-순번)
//        String lotNumber = generateLotNumber(product.getProductCode());
//
//        // 동일 유통기한의 LOT가 있는지 확인
//        Lot lot = lotRepository.findByProductAndExpiryDateAndStatus(
//                product,
//                itemDto.getExpiryDate(),
//                Lot.LotStatus.AVAILABLE
//        ).orElse(null);
//
//        if (lot != null) {
//            // 기존 LOT에 수량 추가
//            lot.increaseQuantity(itemDto.getQuantity());
//            log.info("Updated existing lot: {} (+{})", lot.getLotNumber(), itemDto.getQuantity());
//        } else {
//            // 새 LOT 생성
//            lot = Lot.builder()
//                    .lotNumber(lotNumber)
//                    .product(product)
//                    .manufactureDate(LocalDate.now())
//                    .expiryDate(itemDto.getExpiryDate())
//                    .initialQuantity(itemDto.getQuantity())
//                    .currentQuantity(itemDto.getQuantity())
//                    .status(Lot.LotStatus.AVAILABLE)
//                    .build();
//
//            log.info("Created new lot: {} (qty: {})", lotNumber, itemDto.getQuantity());
//        }
//
//        // 유통기한 상태 업데이트
//        updateLotStatus(lot);
//
//        return lotRepository.save(lot);
//    }
//
//    /**
//     * LOT 상태 업데이트 (유통기한 기준)
//     */
//    private void updateLotStatus(Lot lot) {
//        if (lot.isExpired()) {
//            lot.setStatus(Lot.LotStatus.EXPIRED);
//        } else if (lot.isNearExpiry(7)) {  // 7일 이내
//            lot.setStatus(Lot.LotStatus.NEAR_EXPIRY);
//        } else {
//            lot.setStatus(Lot.LotStatus.AVAILABLE);
//        }
//    }
//
//    /**
//     * 발주 상태 업데이트
//     */
//    private void updatePoStatus(PoHeader poHeader) {
//        // TODO: 모든 발주 품목이 입고되었는지 확인
//        // 간단하게 RECEIVED로 변경
//        poHeader.setStatus(PoHeader.PoStatus.RECEIVED);
//        poHeaderRepository.save(poHeader);
//    }
//
//    /**
//     * 입고번호 생성 (GR-YYYYMMDD-순번)
//     */
//    private String generateGrNumber() {
//        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String prefix = "GR-" + dateStr + "-";
//
//        String lastGrNumber = grHeaderRepository.findTopByGrNumberStartingWithOrderByGrNumberDesc(prefix)
//                .map(GrHeader::getGrNumber)
//                .orElse(prefix + "0000");
//
//        int sequence = Integer.parseInt(lastGrNumber.substring(lastGrNumber.length() - 4)) + 1;
//
//        return prefix + String.format("%04d", sequence);
//    }
//
//    /**
//     * LOT 번호 생성 (YYYYMMDD-상품코드-순번)
//     */
//    private String generateLotNumber(String productCode) {
//        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String prefix = dateStr + "-" + productCode + "-";
//
//        String lastLotNumber = lotRepository.findTopByLotNumberStartingWithOrderByLotNumberDesc(prefix)
//                .map(Lot::getLotNumber)
//                .orElse(prefix + "00");
//
//        int sequence = Integer.parseInt(lastLotNumber.substring(lastLotNumber.length() - 2)) + 1;
//
//        return prefix + String.format("%02d", sequence);
//    }
//}
