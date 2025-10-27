package com.youthcase.orderflow.mockTest.auth;

import com.youthcase.orderflow.auth.domain.Role;
import com.youthcase.orderflow.auth.domain.User;
import com.youthcase.orderflow.auth.domain.enums.RoleType;
import com.youthcase.orderflow.auth.repository.RoleRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.master.store.domain.Store;
import com.youthcase.orderflow.master.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 👥 AppUserSeeder
 * --------------------------------------------------------
 * - APP_USER 기본 계정 생성용 시더
 * - ADMIN / MANAGER / CLERK 역할별 계정 생성
 * - Store 및 Role이 이미 존재해야 함
 * --------------------------------------------------------
 */
@Slf4j
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
public class AppUserSeeder{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void run(String... args) {
        log.info("👥 [AppUserSeeder] Creating default user accounts...");

        // 1️⃣ 점포 참조 (DevDataInitializer 또는 StoreSeeder에서 생성됨)
        Store store = storeRepository.findById("S001")
                .orElseThrow(() -> new IllegalStateException("⚠️ 기본 점포 S001이 존재하지 않습니다."));

        // 2️⃣ 역할(Role) 참조
        Role adminRole = roleRepository.findByRoleId(RoleType.ADMIN.getRoleId())
                .orElseThrow(() -> new IllegalStateException("⚠️ ADMIN 역할이 존재하지 않습니다."));
        Role managerRole = roleRepository.findByRoleId(RoleType.MANAGER.getRoleId())
                .orElseThrow(() -> new IllegalStateException("⚠️ MANAGER 역할이 존재하지 않습니다."));
        Role clerkRole = roleRepository.findByRoleId(RoleType.CLERK.getRoleId())
                .orElseThrow(() -> new IllegalStateException("⚠️ CLERK 역할이 존재하지 않습니다."));

        // 3️⃣ 관리자 계정
        createIfNotExists("admin01", "관리자 계정", "1234", adminRole, store);

        // 4️⃣ 점장 계정 (manager01~10)
        for (int i = 1; i <= 10; i++) {
            String id = String.format("manager%02d", i);
            createIfNotExists(id, "지점장-" + i, "managerpass", managerRole, store);
        }

        // 5️⃣ 점원 계정 (clerk01~20)
        for (int i = 1; i <= 20; i++) {
            String id = String.format("clerk%02d", i);
            createIfNotExists(id, "점원-" + i, "clerkpass", clerkRole, store);
        }

        log.info("✅ [AppUserSeeder] All default accounts created successfully.");
    }

    /**
     * 개별 사용자 생성 (중복 시 무시)
     */
    private void createIfNotExists(String userId, String name, String rawPassword, Role role, Store store) {
        if (!userRepository.existsByUserId(userId)) {
            User user = User.builder()
                    .userId(userId)
                    .name(name)
                    .email(userId + "@orderflow.com")
                    .password(passwordEncoder.encode(rawPassword))
                    .enabled(true)
                    .store(store)
                    .role(role)
                    .build();
            userRepository.save(user);
            log.info("🧩 User created: {} ({})", userId, role.getRoleId());
        }
    }
}
