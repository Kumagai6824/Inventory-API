package com.raisetech.inventoryapi.entity;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Setter
public class InventoryProduct {
    private int id;
    private int productId;
    private int quantity;
    private OffsetDateTime history;

    public InventoryProduct(int id, int productId, int quantity, OffsetDateTime history) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.history = history;
    }

    public int getId() {
        return id;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public OffsetDateTime getHistory() {
        return history;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryProduct that)) return false;
        return id == that.id && productId == that.productId && quantity == that.quantity && history.equals(that.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, quantity, history);
    }
}

