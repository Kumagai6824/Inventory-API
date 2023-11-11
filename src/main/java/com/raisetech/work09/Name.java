package com.raisetech.work09;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@RequiredArgsConstructor
@Setter
public class Name {
    private int id;
    private String name;

    public Name(String name) {
        this.name = name;
    }

    public Name(int id, String name) {
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
        if (!(o instanceof Name name1)) return false;
        return id == name1.id && name.equals(name1.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
