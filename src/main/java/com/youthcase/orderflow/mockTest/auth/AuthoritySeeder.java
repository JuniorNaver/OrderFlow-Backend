package com.youthcase.orderflow.mockTest.auth;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.domain.enums.AuthorityType;
import com.youthcase.orderflow.auth.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Profile({"dev", "local"})
public class AuthoritySeeder{

    private final AuthorityRepository authorityRepository;

    @Transactional
    public void run(String... args) {
        for (AuthorityType type : AuthorityType.values()) {
            authorityRepository.findByAuthority(type.name())
                    .orElseGet(() -> {
                        Authority newAuth = Authority.builder()
                                .authority(type.name())
                                // 💡 수정: url 대신 urlPattern 필드로 설정
                                .urlPattern(type.getUrlPattern())
                                .description(type.getDescription())
                                .build();

                        Authority saved = authorityRepository.save(newAuth);

                        // 💡 수정: getUrl 대신 getUrlPattern()으로 호출
                        System.out.printf("✅ Authority 등록됨: %s (%s)%n",
                                saved.getAuthority(), saved.getUrlPattern());
                        return saved;
                    });
        }

        System.out.println("✅ 모든 AuthorityType Enum → DB 동기화 완료");
    }
}
