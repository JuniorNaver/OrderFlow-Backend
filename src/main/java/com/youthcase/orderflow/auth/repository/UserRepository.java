package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: 필요 시 커스텀 쿼리 추가
}
