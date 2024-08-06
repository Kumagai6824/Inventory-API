package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.Inventory;
import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.entity.Product;
import com.raisetech.inventoryapi.exception.InvalidInputException;
import com.raisetech.inventoryapi.exception.InventoryNotLatestException;
import com.raisetech.inventoryapi.exception.InventoryShortageException;
import com.raisetech.inventoryapi.exception.ResourceNotFoundException;
import com.raisetech.inventoryapi.mapper.InventoryProductMapper;
import com.raisetech.inventoryapi.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            throw new InventoryNotLatestException("Cannot update id: " + id + ", Only the last update can be altered.");
        } else if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero");
        }

        inventoryProductMapper.updateInventoryProductById(id, quantity);
    }

    @Override
    public void updateShippedInventoryProductById(int productId, int id, int quantity) {
        Optional<Product> productOptional = productMapper.findById(productId);
        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("Product ID:" + productId + " does not exist"));
        if (product.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Product ID:" + productId + " does not exist");
        }

        Optional<InventoryProduct> inventoryProductOptional = inventoryProductMapper.findLatestInventoryByProductId(productId);
        InventoryProduct latestInventoryProduct = inventoryProductOptional.orElseThrow(() -> new ResourceNotFoundException("Inventory item does not exist"));

        int inventoryQuantity = this.inventoryProductMapper.getQuantityByProductId(productId);
        int lastUpdatedQuantity = latestInventoryProduct.getQuantity();

        if (latestInventoryProduct.getId() != id) {
            throw new InventoryNotLatestException("Cannot update id: " + id + ", Only the last update can be altered.");
        } else if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero");
        } else if (quantity > inventoryQuantity - lastUpdatedQuantity) {
            throw new InventoryShortageException("Inventory shortage, only " + (inventoryQuantity - lastUpdatedQuantity) + " items left");
        }

        inventoryProductMapper.updateInventoryProductById(id, quantity * (-1));
    }

    @Override
    public InventoryProduct findInventoryById(int id) {
        Optional<InventoryProduct> inventoryProductOptional = inventoryProductMapper.findInventoryById(id);

        InventoryProduct inventoryProduct = inventoryProductOptional.orElseThrow(() -> new ResourceNotFoundException("ID:" + id + " does not exist"));

        return inventoryProduct;
    }

    @Override
    public void deleteInventoryById(int id) {
        InventoryProduct inventoryProduct = inventoryProductMapper.findInventoryById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID:" + id + " does not exist"));

        InventoryProduct latestInventoryProduct = inventoryProductMapper.findLatestInventoryByProductId(inventoryProduct.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item does not exist"));

        if (latestInventoryProduct.getId() != id) {
            throw new InventoryNotLatestException("Cannot update id: " + id + ", Only the last update can be altered.");
        }

        inventoryProductMapper.deleteInventoryById(id);
    }

    @Override
    public List<Inventory> getCurrentInventories() {
        List<Product> products = productMapper.findAll();

        List<Inventory> currentInventories = products.stream()
                .map(product -> {
                    int productId = product.getId();
                    String name = product.getName();
                    int quantity = inventoryProductMapper.getQuantityByProductId(productId);
                    return new Inventory(productId, name, quantity);
                })
                .collect(Collectors.toList());

        return currentInventories;
    }
}
