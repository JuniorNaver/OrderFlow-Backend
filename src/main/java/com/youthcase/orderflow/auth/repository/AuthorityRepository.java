package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    // JpaRepository<[엔티티 타입: Authority], [PK 타입: Long]>

    /**
     * 권한명(authority)을 사용하여 권한 엔티티를 조회합니다.
     * @param authority 권한을 식별하는 고유 이름
     * @return Authority 엔티티 (Optional)
     */
    Optional<Authority> findByAuthority(String authority);

    List<Authority> findByUrlPatternContaining(String urlPattern);
}