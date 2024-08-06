package com.raisetech.inventoryapi.entity;

import java.util.Objects;

public class Inventory {
    private int productId;
    private String name;
    private int quantity;

    public Inventory(int productId, String name, int quantity) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inventory that)) return false;
        return productId == that.productId && quantity == that.quantity && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name, quantity);
    }
}
