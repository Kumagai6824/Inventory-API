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

    public Inventory convertToNameEntity() {
        Inventory inventory = new Inventory();
        inventory.setName(this.name);
        return inventory;
    }
}
