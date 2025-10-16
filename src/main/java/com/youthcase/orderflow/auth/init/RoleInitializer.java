package com.youthcase.orderflow.auth.init;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 서버 구동 시 RoleType Enum을 기반으로 Role 테이블 자동 동기화
 */
@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {

        for (RoleType type : RoleType.values()) {
            // DB에 존재하는지 확인
            roleRepository.findByRoleType(type)
                    .orElseGet(() -> {
                        Role newRole = Role.builder()
                                .roleId(type.getRoleId())
                                .roleType(type)
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
