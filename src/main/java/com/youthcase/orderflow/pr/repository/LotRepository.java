package com.youthcase.orderflow.pr.repository;

import com.youthcase.orderflow.pr.domain.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
}