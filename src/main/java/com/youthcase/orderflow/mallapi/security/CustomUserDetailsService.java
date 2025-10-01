package com.youthcase.orderflow.mallapi.security;

import com.youthcase.orderflow.mallapi.dto.MemberDTO;
import com.youthcase.orderflow.mallapi.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.youthcase.orderflow.mallapi.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService{
    public final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("-------------------loadUserByUsername-------------------");
        Member member = memberRepository.getWithRoles(username);
        if(member == null){
            throw new UsernameNotFoundException("Not Found");
        }

        MemberDTO memberDTO = new MemberDTO(
                member.getUserId(),
                member.getUsername(),
                member.getPassword(),
                member.getWorkspace(),
                member.getEmail(),
                member.getMemberRoleList().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );
        log.info(memberDTO.toString());

        return null;
    }
}