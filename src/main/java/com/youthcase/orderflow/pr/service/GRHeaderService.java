package com.youthcase.orderflow.pr.service;

import com.youthcase.orderflow.pr.domain.GRHeader;
import com.youthcase.orderflow.pr.repository.GRHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GRHeaderService {

    private final GRHeaderRepository grHeaderRepository;

    public List<GRHeader> getAllGRHeaders() {
        return grHeaderRepository.findAll();
    }

    public Optional<GRHeader> getGRHeaderById(String grId) {
        return grHeaderRepository.findById(grId);
    }

    public GRHeader saveGRHeader(GRHeader grHeader) {
        return grHeaderRepository.save(grHeader);
    }

    public void deleteGRHeader(String grId) {
        grHeaderRepository.deleteById(grId);
    }
}