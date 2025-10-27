package com.youthcase.orderflow.common.sequence;

import org.springframework.stereotype.Component;

@Component
public class StringIdGenerator {

    private final SequenceGeneratorService sequenceService;

    public StringIdGenerator(SequenceGeneratorService sequenceService) {
        this.sequenceService = sequenceService;
    }

    /**
     * 접두어와 Oracle 시퀀스를 결합한 ID 생성 (예: W001, S010 등)
     */
    public String generateId(String prefix, String sequenceName) {
        long seq = sequenceService.getNextSequenceValue(sequenceName);
        return String.format("%s%03d", prefix, seq); // 3자리 패딩
    }
}
