package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Authority;
import java.util.List;
import java.util.Optional;

public interface AuthorityService {

    /**
     * 새로운 권한을 생성하고 저장합니다.
     * @param authority 권한명 (예: STK_WRITE)
     * @param url 권한이 적용되는 URL 패턴
     * @return 저장된 Authority 엔티티
     */
    Authority createAuthority(String authority, String url);

    /**
     * ID로 권한을 조회합니다.
     * @param authorityId 권한 ID
     * @return Authority 엔티티 (Optional)
     */
    Optional<Authority> findById(Long authorityId);

    /**
     * 모든 권한 목록을 조회합니다. (관리자 기능)
     * @return 모든 Authority 엔티티 리스트
     */
    List<Authority> findAllAuthorities();

    /**
     * 권한 정보를 업데이트합니다.
     * @param authorityId 업데이트할 권한 ID
     * @param newAuthority 새로운 권한명
     * @param newUrl 새로운 URL
     * @return 업데이트된 Authority 엔티티
     */
    Authority updateAuthority(Long authorityId, String newAuthority, String newUrl);

    /**
     * 권한을 삭제합니다.
     * @param authorityId 삭제할 권한 ID
     */
    void deleteAuthority(Long authorityId);
}