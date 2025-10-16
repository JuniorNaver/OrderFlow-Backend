package com.youthcase.orderflow.global.config;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
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
 * (Mock Data 역할을 대체하며, DB에 영속적으로 저장됩니다.)
 */
@Component
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner { // 서버 시작 시 실행

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional // 데이터 생성 및 저장 작업을 하나의 트랜잭션으로 묶습니다.
    public void run(String... args) throws Exception {

        // 1. ROLE_ADMIN Role이 없으면 생성
        Role adminRole = roleRepository.findByRoleId(RoleType.ADMIN.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.ADMIN.getRoleId())
                        .roleType(RoleType.ADMIN)
                        .description("시스템 관리자 권한")
                        .build()));

        // 2. admin01 계정 생성 (중복 확인)
        if (!userRepository.existsByUserId("admin01")) {

            // 평문 비밀번호 '1234'를 BCrypt로 암호화하여 저장
            String encodedPassword = passwordEncoder.encode("1234");

            // 🚨 수정: User.builder()를 통해 roles 컬렉션을 직접 설정합니다.
            User adminUser = User.builder()
                    .userId("admin01")
                    .name("관리자 계정")
                    .email("admin01@orderflow.com")
                    .password(encodedPassword)
                    .enabled(true)
                    .workspace("본사")
                    .roles(Set.of(adminRole)) // 빌더를 통해 roles 직접 설정
                    .build();

            // adminUser.addRoles(Set.of(adminRole)); // 🚨 메서드 호출 제거

            userRepository.save(adminUser);
            System.out.println(">>> 초기 관리자 계정(admin01/1234) 생성 완료.");
        }

        // (선택) 일반 사용자 user01 계정 생성 예시 - RoleType.CLERK 사용
        Role clerkRole = roleRepository.findByRoleId(RoleType.CLERK.getRoleId())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId(RoleType.CLERK.getRoleId())
                        // 🚨 수정: RoleType.USER 대신 RoleType.CLERK 사용
                        .roleType(RoleType.CLERK)
                        .description("점원(일반 사용자) 권한")
                        .build()));

        if (!userRepository.existsByUserId("user01")) {
            String encodedPassword = passwordEncoder.encode("userpass");

            // 🚨 수정: User.builder()를 통해 roles 컬렉션을 직접 설정합니다.
            User normalUser = User.builder()
                    .userId("user01")
                    .name("일반 사용자")
                    .email("user01@orderflow.com")
                    .password(encodedPassword)
                    .enabled(true)
                    .workspace("지점A")
                    .roles(Set.of(clerkRole)) // 빌더를 통해 roles 직접 설정
                    .build();

            // normalUser.addRoles(Set.of(userRole)); // 🚨 메서드 호출 제거
            userRepository.save(normalUser);
            System.out.println(">>> 초기 일반 사용자 계정(user01/userpass) 생성 완료.");
        }
    }
}
