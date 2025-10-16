//// src/backend/src/main/java/com/orderflow/receipt/repository/LotRepository.java
//package com.youthcase.orderflow.gr.repository;
//
//import com.orderflow.product.entity.Product;
//import com.orderflow.receipt.entity.Lot;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface LotRepositoryKD extends JpaRepository<Lot, Long> {
//
//    Optional<Lot> findByLotNumber(String lotNumber);
//
//    List<Lot> findByProductProductCodeAndStatusOrderByExpiryDateAsc(
//            String productCode,
//            Lot.LotStatus status
//    );
//
//    Optional<Lot> findByProductAndExpiryDateAndStatus(
//            Product product,
//            LocalDate expiryDate,
//            Lot.LotStatus status
//    );
//
//    List<Lot> findByExpiryDateBetweenAndStatusOrderByExpiryDateAsc(
//            LocalDate startDate,
//            LocalDate endDate,
//            Lot.LotStatus status
//    );
//
//    List<Lot> findByExpiryDateBeforeAndStatusNot(
//            LocalDate date,
//            Lot.LotStatus status
//    );
//
//    List<Lot> findByStatusNot(Lot.LotStatus status);
//
//    Optional<Lot> findTopByLotNumberStartingWithOrderByLotNumberDesc(String prefix);
//
//    List<Lot> findByExpiryDateBetween(LocalDate start, LocalDate end);
//
//    List<Lot> findByExpiryDateBeforeAndStatus(LocalDate date, Lot.LotStatus status);
//}
