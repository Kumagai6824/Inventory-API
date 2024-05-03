package com.raisetech.inventoryapi.entity;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Setter
public class Product {
    private int id;
    private String name;
    private OffsetDateTime deletedAt;

    public Product(String name) {
        this.name = name;
    }

    public Product(int id, String name, OffsetDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.deletedAt = deletedAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return id == product.id && name.equals(product.name) && Objects.equals(deletedAt, product.deletedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, deletedAt);
    }
}
