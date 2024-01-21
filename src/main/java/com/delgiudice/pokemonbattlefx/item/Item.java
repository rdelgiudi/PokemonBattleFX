package com.delgiudice.pokemonbattlefx.item;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.pokemon.PokemonSpecie;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;

/**
 * Class representing a usable item.
 */
public class Item {

    public static final int MAX_HP = -1;
    /**
     * <code>HashMap</code> containing all existing items.
     */
    private static final HashMap<String, Item> itemMap = new HashMap<>();
    /**
     * Name of the item.
     */
    private String name;
    /**
     * Type of the item.
     */
    Enums.ItemType type;
    /**
     * Status healed by this item.
     */
    Enums.Status statusHeal = Enums.Status.NONE;
    /**
     * Value of item, usually signals the amount of health it restores.
     */
    int value;
    /**
     * Description of item that appears when item is hovered over.
     */
    String description = "No description";
    /**
     * Sprite path.
     */
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

    /**
     * Gets sprite from the path held in the <code>sprite</code> variable. If file of specified path does not exist,
     * loads the default question mark sprite.
     * @return object containing the requested image
     */
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

    /**
     * Sets a map containing all programmed in items.
     */
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

        newItem = new Item("Full Restore", Enums.ItemType.HP_RESTORE, MAX_HP, "/sprites/full_restore.png");
        newItem.statusHeal = Enums.Status.ANY;
        newItem.description = " A medicine that can be used to fully restore the max HP of a Pokémon " +
                "and cure any status conditions it may have.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Ice Heal", Enums.Status.FROZEN, 0, "/sprites/ice_heal.png");
        newItem.description = "A spray-type medicine for treating freezing. " +
                "It can be used to thaw out a Pokémon that has been frozen solid.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Max Revive", Enums.Status.FAINTED, MAX_HP, "/sprites/max_revive.png");
        newItem.description = "A medicine that can be used to revive a Pokémon that has fainted. " +
                "It also fully restores the Pokémon’s max HP.";
        itemMap.put(newItem.name, newItem);

        newItem = new Item("Paralyze Heal", Enums.Status.PARALYZED, 0, "/sprites/paralyze_heal.png");
        newItem.description = "A spray-type medicine for treating paralysis. " +
                "It can be used to free a Pokémon that has been paralyzed.";
        itemMap.put(newItem.name, newItem);
    }
}
