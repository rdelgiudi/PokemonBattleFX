package com.delgiudice.pokemonbattlefx.item;

import com.delgiudice.pokemonbattlefx.attributes.Enums;

public class Item {
    private String name;
    Enums.ItemType type;
    int value;
    String description = "No description";

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Enums.ItemType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public Item(String name, Enums.ItemType type, int value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }
}
