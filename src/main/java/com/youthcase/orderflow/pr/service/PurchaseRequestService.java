package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.domain.Product;
import com.youthcase.orderflow.pr.domain.PurchaseRequest;
import com.youthcase.orderflow.pr.dto.PurchaseRequestCreateDto;
import com.youthcase.orderflow.pr.dto.PurchaseRequestDto;
import com.youthcase.orderflow.pr.mapper.PurchaseRequestMapper;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import com.youthcase.orderflow.pr.repository.PurchaseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseRequestService {

    private final PurchaseRequestRepository prRepository;
    private final ProductRepository productRepository;

    // 점장 이상만, 그리고 본인 점포만 발주 가능
    @PreAuthorize("(hasAuthority('PR_ORDER') or hasRole('ADMIN')) and @storeGuard.canAccess(#auth, #storeId)")
    public PurchaseRequestDto placeOrder(String storeId, PurchaseRequestCreateDto dto, Authentication auth) {

        Product p = productRepository.findById(dto.gtin())
                .orElseThrow(() -> new NotFoundException("상품 없음: " + dto.gtin()));

        // 비즈 규칙 예시
        if (dto.qty() <= 0) throw new IllegalArgumentException("발주 수량은 1 이상이어야 합니다.");
        if (Boolean.FALSE.equals(p.getOrderable())) {
            throw new IllegalStateException("해당 상품은 발주 불가 상태입니다.");
        }

        PurchaseRequest pr = PurchaseRequest.create(storeId, dto.gtin(), dto.qty(), dto.expectedDate());
        prRepository.save(pr);
        return PurchaseRequestMapper.toDto(pr);
    }

    // 발주 조회는 읽기 권한
    @PreAuthorize("hasAuthority('PR_READ')")
    @Transactional(readOnly = true)
    public Page<PurchaseRequestDto> listOrders(String storeId, Pageable pageable) {
        return prRepository.findByStoreId(storeId, pageable).map(PurchaseRequestMapper::toDto);
    }

    @PreAuthorize("hasAuthority('PR_READ')")
    @Transactional(readOnly = true)
    public PurchaseRequestDto getOrder(Long id) {
        return prRepository.findById(id)
                .map(PurchaseRequestMapper::toDto)
                .orElseThrow(() -> new NotFoundException("발주 없음: " + id));
    }
}