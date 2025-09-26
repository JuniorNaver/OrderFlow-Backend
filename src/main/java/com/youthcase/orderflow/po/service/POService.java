package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.PO;
import com.youthcase.orderflow.po.repository.PORepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class POService {

    private final PORepository poRepository;

    public POService(PORepository poRepository) {
        this.poRepository = poRepository;
    }

    public List<PO> findAll() {
        return poRepository.findAll();
    }

    public PO save(PO po) {
        return poRepository.save(po);
    }
}
