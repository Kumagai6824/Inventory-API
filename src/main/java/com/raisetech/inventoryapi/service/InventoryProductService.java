package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.InventoryProduct;

public interface InventoryProductService {
    Integer getQuantityByProductId(int productId);

    void receivingInventoryProduct(InventoryProduct inventoryProduct);
}
