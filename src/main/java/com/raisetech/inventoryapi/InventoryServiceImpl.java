package com.raisetech.inventoryapi;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryMapper inventoryMapper;

    public InventoryServiceImpl(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public List<Inventory> findAll() {
        return inventoryMapper.findAll();
    }

    @Override
    public Inventory findById(int id) {
        return this.inventoryMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
    }

    @Override
    public void createName(Inventory inventory) {
        inventoryMapper.createName(inventory);
    }

    @Override
    public void patchById(int id, String name) {
        inventoryMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
        inventoryMapper.patchById(id, name);
    }

    @Override
    public void deleteById(int id) {
        inventoryMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
        inventoryMapper.deleteById(id);
    }
}
