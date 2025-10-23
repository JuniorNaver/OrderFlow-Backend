package com.youthcase.orderflow.auth.init;

import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.domain.StoreType;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 개발 환경 초기 데이터 설정
 * - 기본 점포 / 관리자 계정 / 일반 계정 생성
 * - 단일 Role 구조(User → Role) 반영
 */
@Component
@RequiredArgsConstructor
@Order(4)
public class DevDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // 1️⃣ 점포 생성
        Store store = storeRepository.findById("S001").orElseGet(() -> {
            Store s = Store.builder()
                    .storeId("S001")
                    .storeName("서울 강남점")
                    .brandCode("CU")
                    .regionCode("SEOUL")
                    .managerId("admin01")
                    .storeType(StoreType.DIRECT)
                    .address("서울특별시 강남구 테헤란로 123")
                    .addressDetail("강남역 5번 출구 앞")
                    .postCode("06234")
                    .ownerName("홍길동")
                    .bizHours("08:00~23:00")
                    .contactNumber("02-3456-7890")
                    .active(true)
                    .longitude(new BigDecimal("127.028000"))
                    .latitude(new BigDecimal("37.498000"))
                    .build();
            storeRepository.saveAndFlush(s);
            System.out.println("✅ 점포 생성 완료: " + s.getStoreName());
            return s;
        });

        // 2️⃣ ROLE_ADMIN이 없으면 생성
        Role adminRole = roleRepository.findByRoleId(RoleType.ADMIN.getRoleId())
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleId(RoleType.ADMIN.getRoleId())
                                .description(RoleType.ADMIN.getDescription())
                                .build()
                ));

        // 3️⃣ ROLE_CLERK이 없으면 생성
        Role clerkRole = roleRepository.findByRoleId(RoleType.CLERK.getRoleId())
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleId(RoleType.CLERK.getRoleId())
                                .description(RoleType.CLERK.getDescription())
                                .build()
                ));

        // 4️⃣ 관리자 계정(admin01) 생성
        if (!userRepository.existsByUserId("admin01")) {
            User adminUser = User.builder()
                    .userId("admin01")
                    .name("관리자 계정")
                    .email("admin01@orderflow.com")
                    .password(passwordEncoder.encode("1234"))
                    .enabled(true)
                    .store(store)
                    .role(adminRole) // ✅ 단일 Role 직접 지정
                    .build();

            userRepository.save(adminUser);
            System.out.println("✅ 관리자 계정(admin01/1234) 생성 완료");
        }

        // 5️⃣ 일반 계정(user01) 생성
        if (!userRepository.existsByUserId("user01")) {
            User normalUser = User.builder()
                    .userId("user01")
                    .name("일반 사용자")
                    .email("user01@orderflow.com")
                    .password(passwordEncoder.encode("userpass"))
                    .enabled(true)
                    .store(store)
                    .role(clerkRole) // ✅ 단일 Role 직접 지정
                    .build();

            userRepository.save(normalUser);
            System.out.println("✅ 일반 사용자(user01/userpass) 생성 완료");
        }

        System.out.println("✅ 초기 데이터 세팅 완료 (Store + Users + Roles)");
    }
}
