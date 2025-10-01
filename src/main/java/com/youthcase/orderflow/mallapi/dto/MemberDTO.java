package com.youthcase.orderflow.mallapi.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class MemberDTO extends User{  // Spring Security의 User 클래스 상속
    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String password;
    private String workspace;
    private String email;

    private List<String> roleNames = new ArrayList<>();

    public MemberDTO(String userId, String username, String password, String workspace,String email, List<String> roleNames) {
        super(userId,
                password,
                roleNames.stream()
                        .map(str -> new SimpleGrantedAuthority("ROLE_" + str))
                        .collect(Collectors.toList()));
        this.userId = userId;
        this.username = username;
        //this.password = password;
        this.workspace = workspace;
        this.email = email;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("userId", userId);
        dataMap.put("username", username);
        //dataMap.put("password", password);
        dataMap.put("workspace",workspace);
        dataMap.put("email", email);
        dataMap.put("roleNames", roleNames);

        return dataMap;
    }
}