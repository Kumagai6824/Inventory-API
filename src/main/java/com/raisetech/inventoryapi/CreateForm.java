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

    public boolean isValid() {
        int len = name.length();
        return name != null && !name.isEmpty() && len < 31;
    }

    public Product convertToProductEntity() {
        Product product = new Product();
        product.setName(this.name);
        return product;
    }
}
