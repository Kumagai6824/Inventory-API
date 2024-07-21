package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.entity.Product;
import com.raisetech.inventoryapi.exception.InvalidInputException;
import com.raisetech.inventoryapi.exception.InventoryShortageException;
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
        int productId = inventoryProduct.getProductId();
        Optional<Product> productOptional = productMapper.findById(productId);

        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("Product ID:" + productId + " does not exist"));
        if (product.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Product ID:" + productId + " does not exist");
        }

        int shippingQuantity = inventoryProduct.getQuantity();
        int inventoryQuantity = this.inventoryProductMapper.getQuantityByProductId(inventoryProduct.getProductId());

        if (shippingQuantity <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero");
        } else {
            if (shippingQuantity > inventoryQuantity) {
                throw new InventoryShortageException("Inventory shortage, only " + inventoryQuantity + " items left");
            }
        }

        inventoryProduct.setQuantity(shippingQuantity * (-1));
        inventoryProductMapper.createInventoryProduct(inventoryProduct);
    }

    @Override
    public void updateReceivedInventoryProductById(int productId, int id, int quantity) {
        Optional<Product> productOptional = productMapper.findById(productId);
        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("Product ID:" + productId + " does not exist"));
        if (product.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Product ID:" + productId + " does not exist");
        }

        Optional<InventoryProduct> inventoryProductOptional = inventoryProductMapper.findLatestInventoryByProductId(productId);
        InventoryProduct latestInventoryProduct = inventoryProductOptional.orElseThrow(() -> new ResourceNotFoundException("Inventory item does not exist"));

        if (latestInventoryProduct.getId() != id) {
            throw new InvalidInputException("Cannot update id: " + id + ", Only the last update can be altered.");
        } else if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero");
        }

        inventoryProductMapper.updateInventoryProductById(id, quantity);
    }

}
