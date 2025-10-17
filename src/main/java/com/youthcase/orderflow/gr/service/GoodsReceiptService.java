package com.youthcase.orderflow.gr.service;

import com.youthcase.orderflow.gr.domain.GoodsReceiptHeader;
import com.youthcase.orderflow.gr.dto.GoodsReceiptHeaderDTO;
import com.youthcase.orderflow.gr.mapper.GoodsReceiptMapper;
import com.youthcase.orderflow.gr.repository.GoodsReceiptHeaderRepository;
import com.youthcase.orderflow.auth.repository.UserRepository;
import com.youthcase.orderflow.po.repository.POHeaderRepository;
import com.youthcase.orderflow.pr.repository.ProductRepository;
import com.youthcase.orderflow.master.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoodsReceiptService {

    private final GoodsReceiptHeaderRepository headerRepo;
    private final UserRepository userRepo;
    private final WarehouseRepository warehouseRepo;
    private final POHeaderRepository poHeaderRepo;
    private final ProductRepository productRepo;
    private final GoodsReceiptMapper mapper;

    public GoodsReceiptHeaderDTO create(GoodsReceiptHeaderDTO dto) {
        var user = userRepo.findById(dto.getUserId()).orElseThrow();
        var warehouse = warehouseRepo.findById(dto.getWarehouseId()).orElseThrow();
        var poHeader = poHeaderRepo.findById(dto.getPoId()).orElseThrow();
        var products = productRepo.findAll();

        GoodsReceiptHeader entity = mapper.toEntity(dto, user, warehouse, poHeader, products);
        headerRepo.save(entity);
        return dto;
    }

    @Transactional(readOnly = true)
    public GoodsReceiptHeaderDTO findById(Long id) {
        return headerRepo.findById(id)
                .map(header -> mapper.toDTO(header))
                .orElseThrow();
    }
}
