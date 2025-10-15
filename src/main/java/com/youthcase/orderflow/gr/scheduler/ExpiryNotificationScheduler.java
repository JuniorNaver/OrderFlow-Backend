// src/backend/src/main/java/com/orderflow/scheduler/ExpiryNotificationScheduler.java
package com.youthcase.orderflow.gr.scheduler;

import com.orderflow.notification.entity.Notification;
import com.orderflow.notification.service.NotificationService;
import com.orderflow.receipt.entity.Lot;
import com.orderflow.receipt.repository.LotRepository;
import com.orderflow.stock.entity.Stock;
import com.orderflow.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExpiryNotificationScheduler {
    
    private final StockRepository stockRepository;
    private final LotRepository lotRepository;
    private final NotificationService notificationService;
    
    private static final int EXPIRY_ALERT_DAYS = 7;
    
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void checkExpiringProducts() {
        log.info("유통기한 임박 상품 체크 시작");
        
        LocalDate alertDate = LocalDate.now().plusDays(EXPIRY_ALERT_DAYS);
        List<Lot> expiringLots = lotRepository.findByExpiryDateBetween(
                LocalDate.now(), alertDate);
        
        for (Lot lot : expiringLots) {
            if (lot.getCurrentQuantity() > 0) {
                String message = String.format(
                        "상품 '%s' (LOT: %s)의 유통기한이 %d일 남았습니다. 남은 수량: %d",
                        lot.getProduct().getProductName(),
                        lot.getLotNumber(),
                        lot.getExpiryDate().toEpochDay() - LocalDate.now().toEpochDay(),
                        lot.getCurrentQuantity()
                );
                
                notificationService.createNotification(
                        1L,
                        Notification.NotificationType.EXPIRY,
                        "유통기한 임박 알림",
                        message,
                        lot.getLotId(),
                        "LOT"
                );
                
                lot.setStatus(Lot.LotStatus.NEAR_EXPIRY);
            }
        }
        
        log.info("유통기한 임박 상품 체크 완료: {}건", expiringLots.size());
    }
    
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void markExpiredProducts() {
        log.info("유통기한 만료 상품 처리 시작");
        
        List<Lot> expiredLots = lotRepository.findByExpiryDateBeforeAndStatus(
                LocalDate.now(), Lot.LotStatus.AVAILABLE);
        
        for (Lot lot : expiredLots) {
            lot.setStatus(Lot.LotStatus.EXPIRED);
            
            String message = String.format(
                    "상품 '%s' (LOT: %s)의 유통기한이 만료되었습니다. 수량: %d",
                    lot.getProduct().getProductName(),
                    lot.getLotNumber(),
                    lot.getCurrentQuantity()
            );
            
            notificationService.createNotification(
                    1L,
                    Notification.NotificationType.EXPIRY,
                    "유통기한 만료 알림",
                    message,
                    lot.getLotId(),
                    "LOT"
            );
        }
        
        log.info("유통기한 만료 상품 처리 완료: {}건", expiredLots.size());
    }
}
