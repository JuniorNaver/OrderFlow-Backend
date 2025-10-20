package com.youthcase.orderflow.global.config;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.UserRole; // 💡 UserRole import 추가
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import com.youthcase.orderflow.auth.repository.UserRoleRepository; // 💡 UserRoleRepository import 추가
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
    private final UserRoleRepository userRoleRepository; // 💡 UserRoleRepository 주입
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1. ROLE_ADMIN Role이 없으면 생성
        Role adminRole = roleRepository.findByRoleId(RoleType.ADMIN.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.ADMIN.getRoleId()) // 💡 roleType 대신 roleId 사용
                        .description(RoleType.ADMIN.getDescription())
                        .build()));

        // 2. admin01 계정 생성 (중복 확인)
        if (!userRepository.existsByUserId("admin01")) {

            String encodedPassword = passwordEncoder.encode("1234");

            // 2-1. User 객체 저장 (UserRole과의 순환 참조를 피하기 위해 먼저 저장)
            User adminUser = userRepository.save(User.builder()
                    .userId("admin01")
                    .name("관리자 계정")
                    .email("admin01@orderflow.com")
                    .password(encodedPassword)
                    .enabled(true)
                    .workspace("본사")
                    // 🚨 User 엔티티에 roles 필드가 없습니다. Builder에서 제거합니다.
                    // .roles(Set.of(adminRole))
                    .build());

            // 2-2. UserRole 객체 생성 및 저장
            UserRole adminUserRole = UserRole.builder()
                    .user(adminUser)
                    .role(adminRole)
                    .build();
            userRoleRepository.save(adminUserRole); // 💡 UserRole 저장

            System.out.println(">>> 초기 관리자 계정(admin01/1234) 생성 완료.");
        }

        // 3. 일반 사용자 user01 계정 생성 - RoleType.CLERK 사용
        Role clerkRole = roleRepository.findByRoleId(RoleType.CLERK.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.CLERK.getRoleId()) // 💡 roleType 대신 roleId 사용
                        .description(RoleType.CLERK.getDescription())
                        .build()));

        if (!userRepository.existsByUserId("user01")) {
            String encodedPassword = passwordEncoder.encode("userpass");

            // 3-1. User 객체 저장
            User normalUser = userRepository.save(User.builder()
                    .userId("user01")
                    .name("일반 사용자")
                    .email("user01@orderflow.com")
                    .password(encodedPassword)
                    .enabled(true)
                    .workspace("지점A")
                    // 🚨 User 엔티티에 roles 필드가 없습니다. Builder에서 제거합니다.
                    // .roles(Set.of(clerkRole))
                    .build());

            // 3-2. UserRole 객체 생성 및 저장
            UserRole normalUserRole = UserRole.builder()
                    .user(normalUser)
                    .role(clerkRole)
                    .build();
            userRoleRepository.save(normalUserRole); // 💡 UserRole 저장

            System.out.println(">>> 초기 일반 사용자 계정(user01/userpass) 생성 완료.");
        }
    }
}