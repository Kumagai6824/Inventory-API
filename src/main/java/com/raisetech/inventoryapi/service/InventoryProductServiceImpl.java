package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.mapper.InventoryProductMapper;
import org.springframework.stereotype.Service;

@Service
public class InventoryProductServiceImpl implements InventoryProductService {
    private final InventoryProductMapper inventoryProductMapper;

    public InventoryProductServiceImpl(InventoryProductMapper inventoryProductMapper) {
        this.inventoryProductMapper = inventoryProductMapper;
    }
    
    @Override
    public Integer getQuantityByProductId(int productId) {
        Integer quantity = this.inventoryProductMapper.getQuantityByProductId(productId);
        return quantity;
    }

}
