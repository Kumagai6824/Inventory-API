package com.raisetech.inventoryapi.entity;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@RequiredArgsConstructor
@Setter
public class Product {
    private int id;
    private String name;

    public Product(String name) {
        this.name = name;
    }

    public Product(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product1)) return false;
        return id == product1.id && name.equals(product1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
