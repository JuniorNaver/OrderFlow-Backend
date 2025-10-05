package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class POHeaderServiceImpl implements POHeaderService {
    private final POHeaderRepository poHeaderRepository;

    @Override
    public List<POHeader> findAll(){
        return poHeaderRepository.findAll();
    }
}



