package com.youthcase.orderflow.auth.repository;

import com.youthcase.orderflow.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // JpaRepository<[엔티티 타입: User], [PK 타입: String]>

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

    List<User> findByNameContaining(String name);

    boolean existsByUserId(String userId);
}