package com.youthcase.orderflow.stk.service;

import com.youthcase.orderflow.stk.domain.STK;
import com.youthcase.orderflow.stk.repository.STKRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class STKService {

    private final STKRepository stkRepository;

    public STKService(STKRepository stkRepository) {
        this.stkRepository = stkRepository;
    }

    public List<STK> findAll() {
        return stkRepository.findAll();
    }

    public STK save(STK stk) {
        return stkRepository.save(stk);
    }
}
