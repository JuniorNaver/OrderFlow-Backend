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
                .url(url)
                // description은 DTO에 포함되지 않았으므로 null로 설정되거나, 엔티티 빌더에서 처리됨
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

        // 3. 필드 업데이트 (Authority 엔티티에 업데이트 메서드가 필요하다고 가정)
        // 안전한 업데이트를 위해 엔티티에 메서드를 추가하거나 Setter를 사용해야 합니다.
        // 여기서는 편의상 Builder를 이용한 새로운 객체 반환 로직을 사용하거나, 엔티티에 setter 혹은 change 메서드를 가정합니다.

        // ** JPA 모범 사례에 따른 가정: Authority 엔티티에 업데이트 메서드가 존재한다고 가정합니다. **
        // 예: existingAuthority.update(newAuthority, newUrl);

        // 현재 엔티티에는 setter가 없으므로, 편의를 위해 간단한 업데이트 로직을 사용하거나,
        // DTO를 활용한 업데이트 메서드를 엔티티에 추가해야 합니다.

        // JPA의 Dirty Checking을 활용하여 엔티티 필드를 직접 변경한다고 가정
        // (Authority 엔티티에 @Setter나 update 메서드가 없으므로, 코드가 컴파일되지 않을 수 있음.
        //  이를 해결하기 위해 Authority 엔티티에 아래와 같은 메서드를 추가해야 합니다.)

        // === Authority.java 엔티티에 update 메서드가 있다고 가정하고 진행 ===
        // public void update(String authority, String url) { this.authority = authority; this.url = url; }

        // 4. 업데이트 수행
        Authority updatedAuthority = Authority.builder()
                .id(authorityId)
                .authority(newAuthority)
                .url(newUrl)
                .build();

        return authorityRepository.save(updatedAuthority); // 엔티티를 새로 생성하여 저장하는 방식 (PK 포함)
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