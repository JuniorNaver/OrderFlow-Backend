package com.youthcase.orderflow.bi.service;

import com.youthcase.orderflow.bi.domain.BI;
import com.youthcase.orderflow.bi.repository.BIRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BIService {

    private final BIRepository biRepository;

    public BIService(BIRepository biRepository) {
        this.biRepository = biRepository;
    }

    public List<BI> findAll() {
        return biRepository.findAll();
    }

    public BI save(BI bi) {
        return biRepository.save(bi);
    }
}
