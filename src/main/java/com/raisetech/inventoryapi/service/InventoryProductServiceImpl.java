package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.entity.Product;
import com.raisetech.inventoryapi.exception.InvalidInputException;
import com.raisetech.inventoryapi.exception.ResourceNotFoundException;
import com.raisetech.inventoryapi.mapper.InventoryProductMapper;
import com.raisetech.inventoryapi.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryProductServiceImpl implements InventoryProductService {
    private final InventoryProductMapper inventoryProductMapper;
    private final ProductMapper productMapper;

    public InventoryProductServiceImpl(InventoryProductMapper inventoryProductMapper, ProductMapper productMapper) {
        this.inventoryProductMapper = inventoryProductMapper;
        this.productMapper = productMapper;
    }

    @Override
    public Integer getQuantityByProductId(int productId) {
        Integer quantity = this.inventoryProductMapper.getQuantityByProductId(productId);
        return quantity;
    }

    @Override
    public void receivingInventoryProduct(InventoryProduct inventoryProduct) {
        if (inventoryProduct.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero");
        }

        int productId = inventoryProduct.getProductId();
        Optional<Product> productOptional = productMapper.findById(productId);

        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("Product ID:" + productId + " does not exist"));
        if (product.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Product ID:" + productId + " does not exist");
        }

        inventoryProductMapper.createInventoryProduct(inventoryProduct);
    }

    @Override
    public void shippingInventoryProduct(InventoryProduct inventoryProduct) {
        int quantity = inventoryProduct.getQuantity();
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero");
        }

        int productId = inventoryProduct.getProductId();
        Optional<Product> productOptional = productMapper.findById(productId);

        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("Product ID:" + productId + " does not exist"));
        if (product.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Product ID:" + productId + " does not exist");
        }

        inventoryProduct.setQuantity(quantity * (-1));
        inventoryProductMapper.createInventoryProduct(inventoryProduct);
    }

}
