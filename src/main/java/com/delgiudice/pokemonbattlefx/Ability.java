package com.delgiudice.pokemonbattlefx;

public enum Ability {
    NONE("No ability"),
    OVERGROW("Overgrow",
            "Powers up Grass-type moves when the Pokémon's HP is low."),
    BLAZE("Blaze",
            "Powers up Fire-type moves when the Pokémon's HP is low."),
    TORRENT("Torrent",
            "Powers up Water-type moves when the Pokémon's HP is low."),
    SHIELD_DUST("Shield Dust",
            "Protective dust shields the Pokémon from the additional effects of moves."),
    KEEN_EYE("Keen Eye"),
    TANGLED_FEET("Tangled Feet"),
    GUTS("Guts"),
    SHED_SKIN("Shed Skin",
            "The Pokémon may cure its own status conditions by shedding its skin."),
    INTIMIDATE("Intimidate");

    final private String ability;
    private String description = "No description";

    public String getDescription() {
        return description;
    }

    Ability(String ability, String description) {
        this.ability = ability;
        this.description = description;
    }

    Ability(String ability) {
        this.ability = ability;
    }

    @Override
    public String toString() {
        return ability;
    }
}
