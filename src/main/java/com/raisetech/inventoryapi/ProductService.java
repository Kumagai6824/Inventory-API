package com.raisetech.inventoryapi;

import java.util.List;

public interface ProductService {
    List<Product> findAll();

    Product findById(int id) throws Exception;

    void createName(Product product);

    void patchById(int id, String name) throws Exception;

    void deleteById(int id) throws Exception;
}
