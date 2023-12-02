package com.raisetech.inventoryapi;

import java.util.List;

public interface InventoryService {
    List<Inventory> findAll();

    Inventory findById(int id) throws Exception;

    void createName(Inventory inventory);

    void patchById(int id, String name) throws Exception;

    void deleteById(int id) throws Exception;
}
