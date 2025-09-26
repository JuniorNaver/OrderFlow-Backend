package com.youthcase.orderflow.sd.service;

import com.youthcase.orderflow.sd.domain.SD;
import com.youthcase.orderflow.sd.repository.SDRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SDService {

    private final SDRepository sdRepository;

    public SDService(SDRepository sdRepository) {
        this.sdRepository = sdRepository;
    }

    public List<SD> findAll() {
        return sdRepository.findAll();
    }

    public SD save(SD sd) {
        return sdRepository.save(sd);
    }
}
