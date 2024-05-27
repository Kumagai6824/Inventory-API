package com.raisetech.inventoryapi.entity;

import java.time.OffsetDateTime;
import java.util.Objects;

public class InventoryHistory {
    private int id;
    private int productId;
    private String name;
    private int quantity;
    private OffsetDateTime history;

    public InventoryHistory(int id, int productId, String name, int quantity, OffsetDateTime history) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.history = history;
    }

    public int getId() {
        return id;
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

    public OffsetDateTime getHistory() {
        return history;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryHistory history1)) return false;
        return id == history1.id && productId == history1.productId && quantity == history1.quantity && name.equals(history1.name) && history.equals(history1.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productId, name, quantity, history);
    }
}
