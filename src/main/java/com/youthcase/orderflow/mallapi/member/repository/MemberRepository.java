package com.youthcase.orderflow.mallapi.member.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.youthcase.orderflow.mallapi.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
    /* @EntityGraph는 특정 엔티티와 그 엔티티와 연관된 엔티티들을 함께 가져올 때 사용된다.
     * 이로 인해 쿼리 실행 시 연관된 엔티티들을 미리 로드할 수 있다. */
    @EntityGraph(attributePaths = { "memberRoleList" })
    @Query("select m from Member m where m.email = :email")
    Member getWithRoles(@Param("email") String email);
}