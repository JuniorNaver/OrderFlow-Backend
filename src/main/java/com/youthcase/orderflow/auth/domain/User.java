package com.youthcase.orderflow.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id; // USER_ID가 PK이므로 @Id 사용
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // JPA 엔티티임을 명시
@Getter // Lombok: Getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용을 위한 기본 생성자
@Table(name = "USER") // 매핑할 테이블 이름
public class User {

    // 계정ID (USER_ID) - 기본키(PK)
    @Id
    @Column(name = "USER_ID", length = 50, nullable = false)
    private String userId;

    // 이름 (USERNAME)
    @Column(name = "USERNAME", length = 100, nullable = false)
    private String username;

    // 비밀번호 (PASSWORD) - 해시된 비밀번호를 저장
    @Column(name = "PASSWORD", length = 200, nullable = false)
    private String password;

    // 근무지 (WORKSPACE)
    @Column(name = "WORKSPACE", length = 100, nullable = false)
    private String workspace;

    // 이메일 (EMAIL)
    @Column(name = "EMAIL", length = 255, nullable = false)
    private String email;

    // 역할ID (ROLE_ID) - ROLE 테이블의 FK (ROLE_ID)
    // ROLE 엔티티와의 관계 설정은 현재는 단순 FK로, 필요에 따라 @ManyToOne 등으로 변경 가능
    @Column(name = "ROLE_ID", length = 50, nullable = false)
    private String roleId;

    // --- 생성자/빌더 ---

    @Builder
    public User(String userId, String username, String password, String workspace, String email, String roleId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.workspace = workspace;
        this.email = email;
        this.roleId = roleId;
    }

    // --- 비즈니스 로직 ---

    /**
     * 비밀번호를 업데이트하는 메소드 (비밀번호는 반드시 해시되어 전달되어야 함)
     * @param newHashedPassword 새로 해시된 비밀번호
     */
    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }

    /**
     * 사용자 정보를 업데이트하는 메소드 (비밀번호, 역할ID 제외)
     * @param username 새로운 이름
     * @param workspace 새로운 근무지
     * @param email 새로운 이메일
     */
    public void updateDetails(String username, String workspace, String email) {
        this.username = username;
        this.workspace = workspace;
        this.email = email;
    }

    // RoleId를 변경하는 관리자용 메소드 등도 추가 가능
}