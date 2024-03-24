package com.raisetech.inventoryapi.service;

import com.raisetech.inventoryapi.entity.InventoryProduct;
import com.raisetech.inventoryapi.entity.Product;
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
        return this.productMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product ID:" + id + " does not exist"));
    }

    @Override
    public void createProduct(Product product) {
        productMapper.createProduct(product);
    }

    @Override
    public void updateProductById(int id, String name) {
        productMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
        productMapper.updateProductById(id, name);
    }

    @Override
    public void deleteProductById(int id) {
        productMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("resource not found with id: " + id));
        Optional<InventoryProduct> inventoryProductOptional = inventoryProductMapper.findInventoryByProductId(id);
        if (inventoryProductOptional.isPresent()) {
            InventoryProduct inventoryProduct = inventoryProductOptional.get();
            int quantity = inventoryProduct.getQuantity();
            if (quantity == 0) {
                productMapper.deleteProductById(id);
            } else {
                throw new IllegalStateException("Cannot delete Product because the quantity is 0");
            }
        } else {
            productMapper.deleteProductById(id);
        }
    }
}
