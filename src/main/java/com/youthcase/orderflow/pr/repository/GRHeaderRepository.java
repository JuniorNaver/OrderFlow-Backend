package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.GRHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GRHeaderRepository extends JpaRepository<GRHeader, String> {
}