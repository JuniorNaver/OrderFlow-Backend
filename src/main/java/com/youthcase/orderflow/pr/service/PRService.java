package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.domain.PR;
import com.youthcase.orderflow.pr.repository.PRRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PRService {

    private final PRRepository prRepository;

    public PRService(PRRepository prRepository) {
        this.prRepository = prRepository;
    }

    public List<PR> findAll() {
        return prRepository.findAll();
    }

    public PR save(PR pr) {
        return prRepository.save(pr);
    }
}
