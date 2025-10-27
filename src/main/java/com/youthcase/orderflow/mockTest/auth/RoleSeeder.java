package com.youthcase.orderflow.mockTest.auth;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 서버 구동 시 RoleType Enum을 기반으로 Role 테이블 자동 동기화
 */
@Component
@RequiredArgsConstructor
@Profile({"dev", "local"})
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        for (RoleType type : RoleType.values()) {
            // DB에 존재하는지 확인 (RoleType을 인수로 받는 findByRoleType은 RoleType이 제거되었으므로,
            // roleId를 인수로 받는 findByRoleId로 가정하고 수정합니다. - Repository는 추후 확정)
            roleRepository.findByRoleId(type.getRoleId()) // 💡 findByRoleType -> findByRoleId로 변경 가정
                    .orElseGet(() -> {
                        Role newRole = Role.builder()
                                .roleId(type.getRoleId())
                                // 🚨 제거: roleType 필드가 엔티티에서 제거되었으므로 이 라인 제거
                                //.roleType(type)
                                .description(type.getDescription())
                                .build();
                        Role saved = roleRepository.save(newRole);
                        System.out.printf("✅ ROLE 등록됨: %s (%s)%n",
                                saved.getRoleId(), saved.getDescription());
                        return saved;
                    });
        }

        System.out.println("✅ 모든 RoleType Enum → DB 동기화 완료");
    }
}