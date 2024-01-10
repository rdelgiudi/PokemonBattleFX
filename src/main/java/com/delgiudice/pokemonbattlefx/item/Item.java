package com.delgiudice.pokemonbattlefx.item;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.pokemon.PokemonSpecie;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;

public class Item {

    public static final int MAX_HP = -1;
    private static final HashMap<String, Item> itemMap = new HashMap<>();
    private String name;
    Enums.ItemType type;
    Enums.Status statusHeal;
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

    public Enums.Status getStatusHeal() {
        return statusHeal;
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

    public Item(String name, Enums.Status status, int value, String sprite) {
        this.name = name;
        this.type = Enums.ItemType.STATUS_HEALING;
        this.value = value;
        this.statusHeal = status;
        this.sprite = sprite;
    }


    public static void setItemMap() {
        Item newItem = new Item("Potion", Enums.ItemType.HP_RESTORE, 20, "/sprites/potion.png");
        newItem.description = "A spray-type medicine for treating wounds. It can be used to restore 20 HP to a Pokémon.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Super Potion", Enums.ItemType.HP_RESTORE, 60, "/sprites/super_potion.png");
        newItem.description = "A spray-type medicine for treating wounds. It can be used to restore 60 HP to a Pokémon.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Hyper Potion", Enums.ItemType.HP_RESTORE, 120, "/sprites/hyper_potion.png");
        newItem.description = "A spray-type medicine for treating wounds. It can be used to restore 120 HP to a Pokémon.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Max Potion", Enums.ItemType.HP_RESTORE, MAX_HP, "/sprites/max_potion.png");
        newItem.description = "A spray-type medicine for treating wounds. " +
                "It can be used to fully restore the max HP of a Pokémon.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Revive", Enums.Status.FAINTED, 0, "/sprites/revive.png");
        newItem.description = "A medicine that can be used to revive a Pokémon that has fainted. " +
                "It also restores half the Pokémon's max HP.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Antidote", Enums.Status.POISONED, 0, "/sprites/antidote.png");
        newItem.description = "A spray-type medicine for treating poisoning. " +
                "It can be used to lift the effects of being poisoned from a Pokémon.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Awakening", Enums.Status.SLEEPING, 0, "/sprites/awakening.png");
        newItem.description = "A spray-type medicine to wake the sleeping. " +
                "It can be used to rouse a Pokémon from the clutches of sleep.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Burn Heal", Enums.Status.BURNED, 0, "/sprites/burn_heal.png");
        newItem.description = "A spray-type medicine for treating burns. " +
                "It can be used to cure a Pokémon suffering from a burn.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Full Heal", Enums.Status.ANY, 0, "/sprites/full_heal.png");
        newItem.description = "A spray-type medicine that is broadly effective." +
                "It can be used to cure any status condition a Pokémon may have.";
        itemMap.put(newItem.name, newItem);
    }
}
