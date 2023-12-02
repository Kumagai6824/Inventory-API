package com.raisetech.inventoryapi;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@RequiredArgsConstructor
@Setter
public class Inventory {
    private int id;
    private String name;

    public Inventory(String name) {
        this.name = name;
    }

    public Inventory(int id, String name) {
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
        if (!(o instanceof Inventory inventory1)) return false;
        return id == inventory1.id && name.equals(inventory1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
