package com.youthcase.orderflow.mallapi.member.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(exclude = "memberRoleList")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user")
public class Member {
    @Id
    private String userId;
    private String username;
    private String password;
    private String workspace;
    private String email;

    /*엔티티 클래스의 컬렉션을 매핑할 때 사용*/
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    public void addRole(MemberRole memberRole){
        memberRoleList.add(memberRole);
    }
    public void clearRole() {
        memberRoleList.clear();
    }

    public void changePw(String password) {
        this.password = password;
    }

    public void changeWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public void changeEmail(String email) {
        this.email = email;
    }
}




