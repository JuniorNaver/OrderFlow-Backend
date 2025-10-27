package com.youthcase.orderflow.common.sequence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class SequenceGeneratorService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Oracle 시퀀스로부터 다음 값을 가져온다.
     * @param sequenceName 시퀀스 이름 (예: WAREHOUSE_SEQ)
     * @return 다음 시퀀스 값
     */
    public Long getNextSequenceValue(String sequenceName) {
        Object result = entityManager
                .createNativeQuery("SELECT " + sequenceName + ".NEXTVAL FROM DUAL")
                .getSingleResult();
        return ((Number) result).longValue();
    }
}
