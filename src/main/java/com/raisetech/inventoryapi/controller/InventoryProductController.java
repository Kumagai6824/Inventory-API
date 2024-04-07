package com.raisetech.inventoryapi.controller;

import com.raisetech.inventoryapi.service.InventoryProductService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Validated
public class InventoryProductController {
    private final InventoryProductService inventoryProductService;


    public InventoryProductController(InventoryProductService inventoryProductService) {
        this.inventoryProductService = inventoryProductService;
    }

    @GetMapping("/inventory-products/{product_id}")
    public Map<String, Integer> getQuantityByProductId(
            @PathVariable(value = "product_id")
            int product_id) {
        Integer quantity = inventoryProductService.getQuantityByProductId(product_id);
        return Map.of("quantity", quantity);
    }
}
