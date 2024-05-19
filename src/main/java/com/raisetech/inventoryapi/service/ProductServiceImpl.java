package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.InventoryHistory;
import com.raisetech.inventoryapi.entity.Product;
import com.raisetech.inventoryapi.exception.InventoryStillExistsException;
import com.raisetech.inventoryapi.exception.ResourceNotFoundException;
import com.raisetech.inventoryapi.mapper.InventoryProductMapper;
import com.raisetech.inventoryapi.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final InventoryProductMapper inventoryProductMapper;

    public ProductServiceImpl(ProductMapper productMapper, InventoryProductMapper inventoryProductMapper) {
        this.productMapper = productMapper;
        this.inventoryProductMapper = inventoryProductMapper;
    }

    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }

    @Override
    public Product findById(int id) {
        Optional<Product> productOptional = productMapper.findById(id);

        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("Product ID:" + id + " does not exist"));
        if (product.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Product ID:" + id + " does not exist");
        }

        return product;
    }

    @Override
    public void createProduct(Product product) {
        productMapper.createProduct(product);
    }

    @Override
    public void updateProductById(int id, String name) {
        Optional<Product> productOptional = productMapper.findById(id);

        Product product = productOptional.orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
        if (product.getDeletedAt() != null) {
            throw new ResourceNotFoundException("resource not found with id: " + id);
        }
        productMapper.updateProductById(id, name);
    }

    @Override
    public void deleteProductById(int id) {
        productMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
        Integer quantity = inventoryProductMapper.getQuantityByProductId(id);

        if (quantity == 0) {
            productMapper.deleteProductById(id);
        } else {
            throw new InventoryStillExistsException("Cannot delete Product because the quantity is not 0");
        }
    }

    @Override
    public List<InventoryHistory> findHistoriesByProductId(int id) {
        productMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
        return productMapper.findHistoriesByProductId(id);
    }
}
