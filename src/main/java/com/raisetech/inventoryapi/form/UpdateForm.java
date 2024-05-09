package com.raisetech.inventoryapi.form;


import com.raisetech.inventoryapi.entity.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateForm {
    @NotBlank
    @Size(max = 30)
    private String name;

    public UpdateForm(String name) {
        this.name = name;
    }

    public Product convertToNameEntity() {
        Product product = new Product(this.name);
        return product;
    }
}
