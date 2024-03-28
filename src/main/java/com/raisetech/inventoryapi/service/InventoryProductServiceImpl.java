package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.mapper.InventoryProductMapper;
import org.springframework.stereotype.Service;

@Service
public class InventoryProductServiceImpl implements InventoryProductService {
    private final InventoryProductMapper inventoryProductMapper;

    public InventoryProductServiceImpl(InventoryProductMapper inventoryProductMapper) {
        this.inventoryProductMapper = inventoryProductMapper;
    }

/*    @Override
    public Integer getQuantityByProductId(int productId) {
        return this.inventoryProductMapper.getQuantityByProductId(productId).orElseThrow(() -> new ResourceNotFoundException("no id"));
    }*/

    @Override
    public Integer getQuantityByProductId(int productId) {
        return this.inventoryProductMapper.getQuantityByProductId(productId);
    }

}
