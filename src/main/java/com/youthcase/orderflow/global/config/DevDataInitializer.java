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
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DevDataInitializer implements CommandLineRunner { // 서버 시작 시 실행

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // 1. RoleType과 Role ID를 명확히 지정하여 Role 엔티티를 생성합니다.
        Role adminRole = roleRepository.findByRoleId("ROLE_ADMIN") // 🚨 메서드 이름도 findByName -> findByRoleId로 변경 필요
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleId("ROLE_ADMIN") // 🚨 필드 이름: roleId 사용
                        .roleType(RoleType.ADMIN) // 🚨 필드 이름: roleType 사용 (RoleType.ADMIN은 Enum이라고 가정)
                        .description("시스템 관리자")
                        .build()));

        // 2. admin01 계정 생성 (중복 확인)
        if (!userRepository.existsByUserId("admin01")) {

            // 🚨 비밀번호 '1234'를 암호화하여 저장
            String encodedPassword = passwordEncoder.encode("1234");

            User adminUser = User.builder()
                    .userId("admin01")
                    .name("관리자 계정")
                    .email("admin01@orderflow.com")
                    .password(encodedPassword)
                    .enabled(true)
                    .workspace("본사")
                    .build();

            // 3. 사용자 권한 설정
            adminUser.addRoles(Set.of(adminRole)); // 사용자에 권한 부여 로직 추가 필요

            userRepository.save(adminUser);
            System.out.println(">>> 초기 관리자 계정(admin01/1234) 생성 완료.");
        }
    }
}