package com.raisetech.inventoryapi;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class ProductController {
    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.findAll();
    }

    @GetMapping("/products/{id}")
    public Product findById(
            @PathVariable(value = "id")
            int id) throws Exception {
        return productService.findById(id);

    }

    @PostMapping("/products")
    public ResponseEntity<Map<String, String>> createProduct
            (@RequestBody @Validated CreateForm form, UriComponentsBuilder uriComponentsBuilder) {
        Product entity = form.convertToProductEntity();
        productService.createProduct(entity);
        int id = entity.getId();
        String name = entity.getName();
        URI url = uriComponentsBuilder.path("/products/" + id).build().toUri();
        return ResponseEntity.created(url).
                body(Map.of("message", "name:" + name + " was successfully created"));
    }

    @PatchMapping("/names/{id}")
    public ResponseEntity<Map<String, String>> patchById(
            @RequestBody @Validated UpdateForm form,
            @PathVariable(value = "id") int id) throws Exception {
        Product entity = form.convertToNameEntity();
        String name = entity.getName();
        productService.patchById(id, name);
        return ResponseEntity.ok(Map.of("message", "id < " + id + " > was successfully updated to " + name));
    }

    @DeleteMapping("/names/{id}")
    public Map<String, String> deleteById(
            @PathVariable(value = "id")
            int id) throws Exception {
        productService.deleteById(id);
        return Map.of("message", "id: " + id + " was successfully deleted");
    }


}
