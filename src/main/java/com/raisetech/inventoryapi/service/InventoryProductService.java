package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.CurrentInventory;
import com.raisetech.inventoryapi.entity.InventoryProduct;

import java.util.List;

public interface InventoryProductService {
    Integer getQuantityByProductId(int productId);

    void receivingInventoryProduct(InventoryProduct inventoryProduct);

    void shippingInventoryProduct(InventoryProduct inventoryProduct);

    void updateReceivedInventoryProductById(int productId, int id, int quantity);

    void updateShippedInventoryProductById(int productId, int id, int quantity);

    InventoryProduct findInventoryById(int id) throws Exception;

    void deleteInventoryById(int id);

    List<CurrentInventory> getCurrentInventories();
}
