package com.youthcase.orderflow.master.repository;

import com.youthcase.orderflow.master.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    List<Warehouse> findBySpotId(Long spotId);
}