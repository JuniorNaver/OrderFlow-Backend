package com.youthcase.orderflow.mockTest.auth;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.RoleAuthMapping;
import com.youthcase.orderflow.auth.domain.RoleAuthMappingId;
import com.youthcase.orderflow.auth.repository.AuthorityRepository;
import com.youthcase.orderflow.auth.repository.RoleAuthMappingRepository;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 서버 구동 시 ROLE_ADMIN → 모든 Authority 자동 매핑
 */
@Component
@RequiredArgsConstructor
public class RoleAuthoritySeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleAuthMappingRepository roleAuthMappingRepository;

    @Override
    public void run(String... args) {
        Role admin = roleRepository.findByRoleId("ROLE_ADMIN").orElseThrow();
        List<Authority> authorities = authorityRepository.findAll();

        int createdCount = 0;

        for (Authority authority : authorities) {
            // 복합키 구성 (💡 Authority 엔티티의 ID는 authorityId로 가정)
            RoleAuthMappingId id = new RoleAuthMappingId(admin.getRoleId(), authority.getAuthorityId());

            // 이미 매핑되어 있다면 skip
            if (roleAuthMappingRepository.existsById(id)) continue;

            // 새 매핑 생성 및 ID 명시
            RoleAuthMapping mapping = RoleAuthMapping.builder()
                    .role(admin)
                    .authority(authority)
                    .build();

            // 반드시 ID 지정 (EmbeddedId는 자동 생성되지 않음)
            mapping.setId(id);

            roleAuthMappingRepository.save(mapping);
            createdCount++;
        }

        System.out.printf("✅ ROLE_ADMIN → 모든 Authority 자동 매핑 완료 (%d개 추가)%n", createdCount);
    }
}
