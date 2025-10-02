package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.repository.AuthorityRepository;
import com.youthcase.orderflow.auth.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service // 스프링 서비스 빈으로 등록
@RequiredArgsConstructor // Repository 주입
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Override
    @Transactional // 쓰기 작업
    public Authority createAuthority(String authority, String url) {
        // (선택적) 권한명 중복 체크 로직 추가 가능
        if (authorityRepository.findByAuthority(authority).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 권한명입니다: " + authority);
        }

        Authority newAuthority = Authority.builder()
                .authority(authority)
                .url(url)
                .build();

        return authorityRepository.save(newAuthority);
    }

    @Override
    public Optional<Authority> findById(Long authorityId) {
        return authorityRepository.findById(authorityId);
    }

    @Override
    public List<Authority> findAllAuthorities() {
        return authorityRepository.findAll();
    }

    @Override
    @Transactional // 쓰기 작업
    public Authority updateAuthority(Long authorityId, String newAuthority, String newUrl) {
        Authority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new IllegalArgumentException("권한을 찾을 수 없습니다. ID: " + authorityId));

        // Authority 도메인 객체의 비즈니스 로직(업데이트 메서드) 호출
        authority.updateAuthority(newAuthority, newUrl);

        // @Transactional로 인해 자동 업데이트
        return authority;
    }

    @Override
    @Transactional // 쓰기 작업
    public void deleteAuthority(Long authorityId) {
        // 실제 운영 환경에서는 해당 권한이 ROLE_AUTH_MAPPING에서 사용 중인지 확인하는 로직이 필요합니다.
        if (!authorityRepository.existsById(authorityId)) {
            throw new IllegalArgumentException("삭제할 권한을 찾을 수 없습니다. ID: " + authorityId);
        }
        authorityRepository.deleteById(authorityId);
    }
}