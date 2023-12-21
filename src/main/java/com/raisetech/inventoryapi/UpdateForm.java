package com.raisetech.inventoryapi;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateForm {
    private String name;

    public UpdateForm(String name) {
        this.name = name;
    }

    public Product convertToNameEntity() {
        Product product = new Product(this.name);
        return product;
    }
}
