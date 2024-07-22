package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.InventoryProduct;

public interface InventoryProductService {
    Integer getQuantityByProductId(int productId);

    void receivingInventoryProduct(InventoryProduct inventoryProduct);

    void shippingInventoryProduct(InventoryProduct inventoryProduct);

    void updateReceivedInventoryProductById(int productId, int id, int quantity);
}
