package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    Product findById(int id) throws Exception;

    void createProduct(Product product);

    void updateProductById(int id, String name) throws Exception;

    void deleteProductById(int id) throws Exception;

    /*    List<InventoryHistory> findHistoriesByProductId(int id) throws Exception;*/
}
