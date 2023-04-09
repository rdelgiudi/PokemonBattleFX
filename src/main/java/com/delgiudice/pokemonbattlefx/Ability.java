package com.delgiudice.pokemonbattlefx;

public enum Ability {
    NONE("No ability"),
    OVERGROW("Overgrow"),
    BLAZE("Blaze"),
    TORRENT("Torrent"),
    SHIELD_DUST("Shield Dust"),
    KEEN_EYE("Keen Eye"),
    TANGLED_FEET("Tangled Feet"),
    GUTS("Guts"),
    SHED_SKIN("Shed Skin"),
    INTIMIDATE("Intimidate");

    final private String ability;

    Ability(String ability) {
        this.ability = ability;
    }

    @Override
    public String toString() {
        return ability;
    }
}
