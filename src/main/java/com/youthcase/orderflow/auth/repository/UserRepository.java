package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Spring Security 로드 시 사용자의 모든 역할과 권한을 EAGER 로딩하여 N+1 문제를 방지합니다.
     * User -> UserRole -> Role -> RoleAuthMapping -> Authority 까지 연결하여 한 번에 가져옵니다.
     */
    @EntityGraph(attributePaths = {
            "userRoles",
            "userRoles.role",
            "userRoles.role.roleAuthMappings",
            "userRoles.role.roleAuthMappings.authority"
    })
    Optional<User> findByUserId(String userId);

    /**
     * 사용자 ID와 워크스페이스가 모두 일치하는 User 엔티티를 조회합니다.
     * 로그인 인증 로직(AuthServiceImpl)에서 사용됩니다.
     * 이 메서드에는 EntityGraph를 적용하지 않아 빠른 조회를 유도합니다.
     */
    Optional<User> findByUserIdAndWorkspace(String userId, String workspace); // 🚨 추가된 메서드

    Optional<User> findByEmail(String email);

    List<User> findByNameContaining(String name);

    boolean existsByUserId(String userId);

    List<User> findAllByUserIdContainingOrNameContainingOrEmailContaining(
            String userId,
            String name,
            String email
    );
}
