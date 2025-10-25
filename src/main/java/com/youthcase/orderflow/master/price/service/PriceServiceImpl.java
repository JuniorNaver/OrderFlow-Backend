package com.youthcase.orderflow.master.price.service;

import com.youthcase.orderflow.master.price.domain.Price;
import com.youthcase.orderflow.master.price.dto.PriceRequestDTO;
import com.youthcase.orderflow.master.price.dto.PriceResponseDTO;
import com.youthcase.orderflow.master.price.repository.PriceRepository;
import com.youthcase.orderflow.master.product.domain.Product;
import com.youthcase.orderflow.master.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 💼 PriceServiceImpl
 * - PriceService 인터페이스 구현체
 * - 실제 비즈니스 로직 및 예외 처리 담당
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final ProductRepository productRepository;

    // =========================================================
    // 📌 [C] 신규 등록
    // =========================================================
    @Override
    public PriceResponseDTO createPrice(PriceRequestDTO request) {
        Product product = productRepository.findById(request.getGtin())
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        if (priceRepository.existsById(product.getGtin())) {
            throw new IllegalStateException("이미 가격 정보가 존재합니다: " + product.getGtin());
        }

        Price price = request.toEntity(product);
        Price saved = priceRepository.save(price);

        // ✅ 신규 등록 후 DTO 변환
        return PriceResponseDTO.from(saved);
    }

    // =========================================================
    // 📌 [U] 수정
    // =========================================================
    @Override
    public PriceResponseDTO updatePrice(PriceRequestDTO request) {
        Price price = priceRepository.findById(request.getGtin())
                .orElseThrow(() -> new EntityNotFoundException("가격 정보가 존재하지 않습니다: " + request.getGtin()));

        // ✅ 매입가, 매출가 수정 후 저장
        price.setPurchasePrice(request.getPurchasePrice());
        price.setSalePrice(request.getSalePrice());
        Price updated = priceRepository.save(price);

        return PriceResponseDTO.from(updated);
    }

    // =========================================================
    // 📌 [D] 삭제
    // =========================================================
    @Override
    public void deletePrice(String gtin) {
        if (!priceRepository.existsById(gtin)) {
            throw new EntityNotFoundException("삭제할 가격 정보가 존재하지 않습니다: " + gtin);
        }

        // ✅ GTIN 기준 삭제
        priceRepository.deleteById(gtin);
    }

    // =========================================================
    // 📌 [R] 조회 (GTIN 기준)
    // =========================================================
    @Override
    public PriceResponseDTO getPrice(String gtin) {
        // ✅ findById()가 곧 GTIN 기준 매입+매출가 조회
        Price price = priceRepository.findById(gtin)
                .orElseThrow(() -> new EntityNotFoundException("가격 정보가 존재하지 않습니다: " + gtin));

        return PriceResponseDTO.from(price);
    }

    @Override
    public BigDecimal getPurchasePrice(String gtin) {
        // ✅ GTIN 기준 매입가만 단일 조회 (PR/PO 모듈용)
        return priceRepository.findPurchasePriceByGtin(gtin)
                .orElseThrow(() -> new EntityNotFoundException("매입 단가 정보가 없습니다: " + gtin));
    }

    @Override
    public BigDecimal getSalePrice(String gtin) {
        // ✅ GTIN 기준 매출가만 단일 조회 (SD 모듈용)
        return priceRepository.findSalePriceByGtin(gtin)
                .orElseThrow(() -> new EntityNotFoundException("매출 단가 정보가 없습니다: " + gtin));
    }

    // =========================================================
    // 📌 [R] 전체 조회 (관리자용 리스트업)
    // =========================================================
    @Override
    public List<PriceResponseDTO> getAllPrices() {
        // ✅ 모든 가격 정보를 매입+매출가 포함하여 반환
        return priceRepository.findAll().stream()
                .map(PriceResponseDTO::from)
                .collect(Collectors.toList());
    }
}
