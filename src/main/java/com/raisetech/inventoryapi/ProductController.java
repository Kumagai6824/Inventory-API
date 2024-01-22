package com.raisetech.inventoryapi;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
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

    @PostMapping("/names")
    public ResponseEntity<Map<String, String>> postUser(@RequestBody @Validated CreateForm form, UriComponentsBuilder uriComponentsBuilder) {
        Product entity = form.convertToNameEntity();
        productService.createName(entity);
        int id = entity.getId();
        String name = entity.getName();
        URI url = uriComponentsBuilder.path("/names/" + id).build().toUri();
        return ResponseEntity.created(url).body(Map.of("message", "name:" + name + " was successfully registered"));
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

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFound(
            ResourceNotFoundException e, HttpServletRequest request) {

        Map<String, String> body = Map.of(
                "timestamp", ZonedDateTime.now().toString(),
                "status", String.valueOf(HttpStatus.NOT_FOUND.value()),
                "error", HttpStatus.NOT_FOUND.getReasonPhrase(),
                "message", e.getMessage(),
                "path", request.getRequestURI());
        return new ResponseEntity(body, HttpStatus.NOT_FOUND);
    }
}
