package com.raisetech.inventoryapi;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
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
        productMapper.deleteProductById(id);
    }
}
