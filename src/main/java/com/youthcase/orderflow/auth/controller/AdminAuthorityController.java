package com.youthcase.orderflow.auth.controller;

import com.youthcase.orderflow.auth.dto.AuthorityRequestDTO; // 요청 DTO가 필요합니다.
import com.youthcase.orderflow.auth.dto.AuthorityResponseDTO; // 응답 DTO가 필요합니다.
import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.service.AuthorityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/authorities") // 권한 관리를 위한 관리자 전용 경로
@RequiredArgsConstructor
public class AdminAuthorityController {

    private final AuthorityService authorityService;

    // 참고: DTO는 필요시 별도 파일로 정의해야 합니다. (아래 코드에서는 생략)

    /**
     * [POST] 새로운 권한을 생성합니다.
     * POST /api/admin/authorities
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorityResponseDTO> createAuthority(@Valid @RequestBody AuthorityRequestDTO request) {

        Authority newAuthority = authorityService.createAuthority(
                request.getAuthority(),
                request.getUrlPattern()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AuthorityResponseDTO.from(newAuthority)); // DTO 변환 후 반환
    }

    /**
     * [GET] 모든 권한 목록을 조회합니다.
     * GET /api/admin/authorities
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuthorityResponseDTO>> getAllAuthorities() {

        List<AuthorityResponseDTO> responseList = authorityService.findAllAuthorities().stream()
                .map(AuthorityResponseDTO::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    /**
     * [PUT] 권한 정보를 수정합니다.
     * PUT /api/admin/authorities/{authorityId}
     */
    @PutMapping("/{authorityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorityResponseDTO> updateAuthority(
            @PathVariable Long authorityId,
            @RequestBody AuthorityRequestDTO request) {

        Authority updatedAuthority = authorityService.updateAuthority(
                authorityId,
                request.getAuthority(),
                request.getUrlPattern()
        );

        return ResponseEntity.ok(AuthorityResponseDTO.from(updatedAuthority));
    }

    /**
     * [DELETE] 권한을 삭제합니다.
     * DELETE /api/admin/authorities/{authorityId}
     */
    @DeleteMapping("/{authorityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthority(@PathVariable Long authorityId) {

        authorityService.deleteAuthority(authorityId);
        return ResponseEntity.noContent().build();
    }
}
