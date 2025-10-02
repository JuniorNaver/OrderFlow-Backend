package com.youthcase.orderflow.po.service;

import com.youthcase.orderflow.po.domain.POHeader;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface POHeaderService {
    List<POHeader> findAll();
}

