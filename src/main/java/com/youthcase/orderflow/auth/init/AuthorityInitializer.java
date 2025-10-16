package com.youthcase.orderflow.auth.init;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.domain.enums.AuthorityType;
import com.youthcase.orderflow.auth.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorityInitializer implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;

    @Override
    public void run(String... args) {

        for (AuthorityType type : AuthorityType.values()) {
            authorityRepository.findByAuthority(type.name())
                    .orElseGet(() -> {
                        Authority newAuth = Authority.builder()
                                .authority(type.name())
                                .url(type.getUrlPattern())
                                .description(type.getDescription())
                                .build();
                        Authority saved = authorityRepository.save(newAuth);
                        System.out.printf("✅ Authority 등록됨: %s (%s)%n",
                                saved.getAuthority(), saved.getUrl());
                        return saved;
                    });
        }

        System.out.println("✅ 모든 AuthorityType Enum → DB 동기화 완료");
    }
}
