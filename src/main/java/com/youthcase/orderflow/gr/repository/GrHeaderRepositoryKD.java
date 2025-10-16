//// src/backend/src/main/java/com/orderflow/receipt/repository/GrHeaderRepository.java
//package com.youthcase.orderflow.gr.repository;
//
//import com.orderflow.receipt.entity.GrHeader;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
///**
// * 입고 헤더 Repository
// */
//@Repository
//public interface GrHeaderRepositoryKD extends JpaRepository<GrHeader, Long> {
//
//    Optional<GrHeader> findByGrNumber(String grNumber);
//
//    Page<GrHeader> findByStoreId(Long storeId, Pageable pageable);
//
//    Page<GrHeader> findByPoHeaderPoId(Long poId, Pageable pageable);
//
//    Page<GrHeader> findByGrDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
//
//    Optional<GrHeader> findTopByGrNumberStartingWithOrderByGrNumberDesc(String prefix);
//}
