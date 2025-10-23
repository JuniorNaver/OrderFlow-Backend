package com.youthcase.orderflow.global.config;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.UserRole;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import com.youthcase.orderflow.auth.repository.UserRoleRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

/**
 * 개발 환경에서 필요한 초기 데이터를 설정하는 클래스입니다.
 * 애플리케이션 시작 시점에 한 번 실행됩니다.
 */
@Component
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // =========================================================
        // 1. 역할(Role) 생성 및 확인
        // =========================================================

        // ROLE_ADMIN
        Role adminRole = roleRepository.findByRoleId(RoleType.ADMIN.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.ADMIN.getRoleId())
                        .description(RoleType.ADMIN.getDescription())
                        .build()));

        // ROLE_MANAGER
        Role managerRole = roleRepository.findByRoleId(RoleType.MANAGER.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.MANAGER.getRoleId())
                        .description(RoleType.MANAGER.getDescription())
                        .build()));

        // ROLE_CLERK
        Role clerkRole = roleRepository.findByRoleId(RoleType.CLERK.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.CLERK.getRoleId())
                        .description(RoleType.CLERK.getDescription())
                        .build()));


        // =========================================================
        // 2. 관리자 계정 (admin01) 생성 (1개)
        // =========================================================
        if (!userRepository.existsByUserId("admin01")) {

            String encodedPassword = passwordEncoder.encode("1234");

            User adminUser = userRepository.save(User.builder()
                    .userId("admin01")
                    .name("관리자 계정")
                    .email("admin01@orderflow.com")
                    .password(encodedPassword)
                    .enabled(true)
                    .workspace("본사")
                    .build());

            UserRole adminUserRole = UserRole.builder()
                    .user(adminUser)
                    .role(adminRole)
                    .build();
            userRoleRepository.save(adminUserRole);

            System.out.println(">>> 초기 관리자 계정(admin01/1234) 생성 완료.");
        }


        // =========================================================
        // 3. 점장 계정 (managerXX) 생성 (10개)
        // =========================================================
        final int MANAGER_COUNT = 10;
        for (int i = 1; i <= MANAGER_COUNT; i++) {
            // 사용자 ID: manager01, manager02, ...
            String userId = String.format("manager%02d", i);
            String name = String.format("지점장-%02d", i);
            String email = String.format("manager%02d@orderflow.com", i);
            String password = "managerpass";
            String workspace = String.format("지점-M%02d", i);

            if (!userRepository.existsByUserId(userId)) {
                String encodedPassword = passwordEncoder.encode(password);

                User managerUser = userRepository.save(User.builder()
                        .userId(userId)
                        .name(name)
                        .email(email)
                        .password(encodedPassword)
                        .enabled(true)
                        .workspace(workspace)
                        .build());

                UserRole managerUserRole = UserRole.builder()
                        .user(managerUser)
                        .role(managerRole)
                        .build();
                userRoleRepository.save(managerUserRole);
            }
        }
        System.out.println(">>> 점장 계정 " + MANAGER_COUNT + "개 생성 완료.");


        // =========================================================
        // 4. 점원 계정 (clerkXX) 생성 (20개)
        // =========================================================
        final int CLERK_COUNT = 20;
        for (int i = 1; i <= CLERK_COUNT; i++) {
            // 사용자 ID: clerk01, clerk02, ...
            String userId = String.format("clerk%02d", i);
            String name = String.format("점원-%02d", i);
            String email = String.format("clerk%02d@orderflow.com", i);
            String password = "clerkpass";
            String workspace = String.format("지점-C%02d", i);

            if (!userRepository.existsByUserId(userId)) {
                String encodedPassword = passwordEncoder.encode(password);

                User clerkUser = userRepository.save(User.builder()
                        .userId(userId)
                        .name(name)
                        .email(email)
                        .password(encodedPassword)
                        .enabled(true)
                        .workspace(workspace)
                        .build());

                UserRole clerkUserRole = UserRole.builder()
                        .user(clerkUser)
                        .role(clerkRole)
                        .build();
                userRoleRepository.save(clerkUserRole);
            }
        }
        System.out.println(">>> 점원 계정 " + CLERK_COUNT + "개 생성 완료.");

        // 총 계정: 1 (admin) + 10 (manager) + 20 (clerk) = 31개
    }
}
