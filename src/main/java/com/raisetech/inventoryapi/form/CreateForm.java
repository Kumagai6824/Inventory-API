package com.raisetech.inventoryapi.form;

import com.raisetech.inventoryapi.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateForm {
    @NotBlank
    @NotNull
    @Size(max = 30)
    private String name;

    public CreateForm() {
    }

    public CreateForm(String name) {
        this.name = name;
    }

    public Product convertToProductEntity() {
        Product product = new Product();
        product.setName(this.name);
        return product;
    }
}
