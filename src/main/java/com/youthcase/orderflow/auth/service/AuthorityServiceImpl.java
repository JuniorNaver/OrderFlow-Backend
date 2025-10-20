package com.youthcase.orderflow.auth.service;

import com.youthcase.orderflow.auth.domain.Authority;
import com.youthcase.orderflow.auth.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    /**
     * 새로운 권한을 생성하고 저장합니다. (권한명 중복 검사 포함)
     */
    @Override
    @Transactional
    public Authority createAuthority(String authority, String url) {
        // 1. 권한명 중복 확인
        if (authorityRepository.findByAuthority(authority).isPresent()) {
            throw new IllegalArgumentException(String.format("이미 존재하는 권한명입니다: %s", authority));
        }

        // 2. 엔티티 생성 및 저장
        Authority newAuthority = Authority.builder()
                .authority(authority)
                // 🚨 수정: 엔티티 필드명 'urlPattern'에 맞게 builder 메서드 이름을 변경합니다.
                .urlPattern(url)
                .build();

        return authorityRepository.save(newAuthority);
    }

    /**
     * ID로 권한을 조회합니다.
     */
    @Override
    public Optional<Authority> findById(Long authorityId) {
        return authorityRepository.findById(authorityId);
    }

    /**
     * 모든 권한 목록을 조회합니다.
     */
    @Override
    public List<Authority> findAllAuthorities() {
        return authorityRepository.findAll();
    }

    /**
     * 권한 정보를 업데이트합니다. (업데이트 전 존재 여부 확인 및 권한명 중복 확인 포함)
     */
    @Override
    @Transactional
    public Authority updateAuthority(Long authorityId, String newAuthority, String newUrl) {
        // 1. 기존 엔티티 조회 (없으면 예외 발생)
        Authority existingAuthority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("업데이트할 권한 ID를 찾을 수 없습니다: %d", authorityId)));

        // 2. 새로운 권한명이 기존 권한명과 다를 경우, 중복 확인
        if (!existingAuthority.getAuthority().equals(newAuthority)) {
            if (authorityRepository.findByAuthority(newAuthority).isPresent()) {
                throw new IllegalArgumentException(String.format("새로운 권한명(%s)이 이미 존재합니다.", newAuthority));
            }
        }

        // 3. 필드 업데이트
        // 🚨 수정: Authority 엔티티에 update(String authority, String urlPattern) 메서드가 있다고 가정하고 호출합니다.
        existingAuthority.update(newAuthority, newUrl);

        return existingAuthority; // 트랜잭션 종료 시 Dirty Checking으로 자동 반영됨
    }

    /**
     * 권한을 삭제합니다. (삭제 전 존재 여부 확인)
     */
    @Override
    @Transactional
    public void deleteAuthority(Long authorityId) {
        // 1. 존재 여부 확인
        if (!authorityRepository.existsById(authorityId)) {
            throw new IllegalArgumentException(String.format("삭제할 권한 ID를 찾을 수 없습니다: %d", authorityId));
        }

        // 2. 삭제
        authorityRepository.deleteById(authorityId);
    }
}
