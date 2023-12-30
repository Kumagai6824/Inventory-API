package com.raisetech.inventoryapi;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateForm {
    private String name;

    public CreateForm() {
    }

    public CreateForm(String name) {
        this.name = name;
    }

    public Product convertToNameEntity() {
        Product product = new Product();
        product.setName(this.name);
        return product;
    }
}
