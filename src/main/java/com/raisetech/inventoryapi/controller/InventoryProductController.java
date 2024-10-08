package com.raisetech.inventoryapi.controller;

import com.raisetech.inventoryapi.entity.Inventory;
import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.form.CreateInventoryProductForm;
import com.raisetech.inventoryapi.form.UpdateInventoryProductForm;
import com.raisetech.inventoryapi.service.InventoryProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class InventoryProductController {
    private final InventoryProductService inventoryProductService;


    public InventoryProductController(InventoryProductService inventoryProductService) {
        this.inventoryProductService = inventoryProductService;
    }

    @PostMapping("/inventory-products/received-items")
    public ResponseEntity<Map<String, String>> receivingInventoryProduct
            (@RequestBody @Validated CreateInventoryProductForm from, UriComponentsBuilder uriComponentsBuilder) {
        InventoryProduct entity = from.convertToInventoryProductEntity();
        inventoryProductService.receivingInventoryProduct(entity);

        int id = entity.getId();
        URI url = uriComponentsBuilder.path("/inventory-products/received-items/" + id).build().toUri();
        return ResponseEntity.created(url).
                body(Map.of("message", "item was successfully received"));
    }

    @PostMapping("/inventory-products/shipped-items")
    public ResponseEntity<Map<String, String>> shippingInventoryProduct
            (@RequestBody @Validated CreateInventoryProductForm form, UriComponentsBuilder uriComponentsBuilder) {
        InventoryProduct entity = form.convertToInventoryProductEntity();
        inventoryProductService.shippingInventoryProduct(entity);

        int id = entity.getId();
        URI url = uriComponentsBuilder.path("/inventory-products/shipped-items/" + id).build().toUri();
        return ResponseEntity.created(url).
                body(Map.of("message", "item was successfully shipped"));
    }

    @PatchMapping("/inventory-products/received-items/{id}")
    public ResponseEntity<Map<String, String>> updateReceivedInventoryProductById
            (@RequestBody @Validated UpdateInventoryProductForm form,
             @PathVariable(value = "id") int id) throws Exception {
        InventoryProduct entity = form.convertToInventoryProductEntity();
        inventoryProductService.updateReceivedInventoryProductById(entity.getProductId(), id, entity.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Quantity was successfully updated"));

    }

    @PatchMapping("/inventory-products/shipped-items/{id}")
    public ResponseEntity<Map<String, String>> updateShippedInventoryProductById
            (@RequestBody @Validated UpdateInventoryProductForm form,
             @PathVariable(value = "id") int id) throws Exception {
        InventoryProduct entity = form.convertToInventoryProductEntity();
        inventoryProductService.updateShippedInventoryProductById(entity.getProductId(), id, entity.getQuantity());
        return ResponseEntity.ok(Map.of("message", "Quantity was successfully updated"));
    }

    @GetMapping("/inventory-products/{id}")
    public InventoryProduct findInventoryById(@PathVariable(value = "id") int id) throws Exception {
        return inventoryProductService.findInventoryById(id);
    }

    @DeleteMapping("/inventory-products/{id}")
    public ResponseEntity<Map<String, String>> deleteInventoryById
            (@PathVariable(value = "id") int id) throws Exception {
        inventoryProductService.deleteInventoryById(id);
        return ResponseEntity.ok(Map.of("message", "Successful operation"));
    }

    @GetMapping("/inventory-products/current-inventories")
    public List<Inventory> getCurrentInventories() {
        return inventoryProductService.getCurrentInventories();
    }
}
