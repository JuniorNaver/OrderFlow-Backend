package com.youthcase.orderflow.gr.repository;

import com.youthcase.orderflow.gr.domain.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
}