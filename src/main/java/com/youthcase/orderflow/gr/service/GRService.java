package com.youthcase.orderflow.gr.service;

import com.youthcase.orderflow.gr.domain.GR;
import com.youthcase.orderflow.gr.repository.GRRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GRService {

    private final GRRepository grRepository;

    public GRService(GRRepository grRepository) {
        this.grRepository = grRepository;
    }

    public List<GR> findAll() {
        return grRepository.findAll();
    }

    public GR save(GR gr) {
        return grRepository.save(gr);
    }
}
