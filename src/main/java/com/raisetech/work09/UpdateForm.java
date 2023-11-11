package com.raisetech.work09;


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

    public Name convertToNameEntity() {
        Name name = new Name(this.name);
        return name;
    }
}
