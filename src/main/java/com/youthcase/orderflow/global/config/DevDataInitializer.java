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
import java.util.Set; // Set이 사용되지 않지만 기존 코드에 있었으므로 유지

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

        // 1. ROLE_ADMIN Role이 없으면 생성
        Role adminRole = roleRepository.findByRoleId(RoleType.ADMIN.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.ADMIN.getRoleId())
                        .description(RoleType.ADMIN.getDescription())
                        .build()));

        // 2. admin01 계정 생성 (중복 확인)
        if (!userRepository.existsByUserId("admin01")) {

            String encodedPassword = passwordEncoder.encode("1234");

            // 2-1. User 객체 저장
            User adminUser = userRepository.save(User.builder()
                    .userId("admin01")
                    .name("관리자 계정")
                    .email("admin01@orderflow.com")
                    .password(encodedPassword)
                    .enabled(true)
                    .workspace("본사")
                    .build());

            // 2-2. UserRole 객체 생성 및 저장
            UserRole adminUserRole = UserRole.builder()
                    .user(adminUser)
                    .role(adminRole)
                    .build();
            userRoleRepository.save(adminUserRole);

            System.out.println(">>> 초기 관리자 계정(admin01/1234) 생성 완료.");
        }

// ----------------------------------------------------------------------
// 💡 추가된 코드 시작: manager01 계정 및 ROLE_MANAGER 설정
// ----------------------------------------------------------------------

        // 3. ROLE_MANAGER Role이 없으면 생성
        // RoleType에 MANAGER가 정의되어 있다고 가정합니다.
        Role managerRole = roleRepository.findByRoleId(RoleType.MANAGER.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.MANAGER.getRoleId())
                        .description(RoleType.MANAGER.getDescription())
                        .build()));

        // 4. manager01 계정 생성 (중복 확인)
        if (!userRepository.existsByUserId("manager01")) {
            String encodedPassword = passwordEncoder.encode("manager1234"); // 비밀번호 인코딩

            // 4-1. User 객체 저장
            User managerUser = userRepository.save(User.builder()
                    .userId("manager01")
                    .name("매니저 계정")
                    .email("manager01@orderflow.com")
                    .password(encodedPassword)
                    .enabled(true)
                    .workspace("본사/매니저")
                    .build());

            // 4-2. UserRole 객체 생성 및 저장
            UserRole managerUserRole = UserRole.builder()
                    .user(managerUser)
                    .role(managerRole)
                    .build();
            userRoleRepository.save(managerUserRole);

            System.out.println(">>> 초기 매니저 계정(manager01/manager1234) 생성 완료.");
        }

// ----------------------------------------------------------------------
// 💡 추가된 코드 끝
// ----------------------------------------------------------------------

        // 5. 일반 사용자 user01 계정 생성 - RoleType.CLERK 사용 (기존 3번)
        Role clerkRole = roleRepository.findByRoleId(RoleType.CLERK.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.CLERK.getRoleId())
                        .description(RoleType.CLERK.getDescription())
                        .build()));

        if (!userRepository.existsByUserId("user01")) {
            String encodedPassword = passwordEncoder.encode("userpass");

            // 5-1. User 객체 저장
            User normalUser = userRepository.save(User.builder()
                    .userId("user01")
                    .name("일반 사용자")
                    .email("user01@orderflow.com")
                    .password(encodedPassword)
                    .enabled(true)
                    .workspace("지점A")
                    .build());

            // 5-2. UserRole 객체 생성 및 저장
            UserRole normalUserRole = UserRole.builder()
                    .user(normalUser)
                    .role(clerkRole)
                    .build();
            userRoleRepository.save(normalUserRole);

            System.out.println(">>> 초기 일반 사용자 계정(user01/userpass) 생성 완료.");
        }
    }
}