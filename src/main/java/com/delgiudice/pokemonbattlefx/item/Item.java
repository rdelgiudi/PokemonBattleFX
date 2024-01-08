package com.delgiudice.pokemonbattlefx.item;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.pokemon.PokemonSpecie;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;

public class Item {

    private static final HashMap<String, Item> itemMap = new HashMap<>();
    private String name;
    Enums.ItemType type;
    int value;
    String description = "No description";
    String sprite = "sprites/default.png";

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

    public static HashMap<String, Item> getItemMap() {
        return itemMap;
    }

    public Image getSprite() {
        Image image;
        URL frontSpriteUrl = getClass().getResource(sprite);

        if (frontSpriteUrl != null)
            image = new Image(sprite);
        else
            image = new Image("sprites/default.png");

        image = PokemonSpecie.resample(image, 5);
        return image;
    }

    public Item(String name, Enums.ItemType type, int value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public Item(String name, Enums.ItemType type, int value, String sprite) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.sprite = sprite;
    }

    public static void setItemMap() {
        Item newItem = new Item("Potion", Enums.ItemType.HP_RESTORE, 20, "/sprites/potion.png");
        newItem.description = "A spray-type medicine for treating wounds. It can be used to restore 20 HP to a Pokémon.";
        itemMap.put(newItem.name, newItem);
    }
}
