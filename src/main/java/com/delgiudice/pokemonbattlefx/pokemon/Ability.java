package com.delgiudice.pokemonbattlefx.pokemon;

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
    KEEN_EYE("Keen Eye",
            "The Pokémon's keen eyes prevent its accuracy from being lowered. It also ignores the target's evasion"),
    TANGLED_FEET("Tangled Feet"),
    GUTS("Guts",
            "It's so gutsy that having a status condition boosts the Pokémon's Attack stat."),
    SHED_SKIN("Shed Skin",
            "The Pokémon may cure its own status conditions by shedding its skin."),
    INTIMIDATE("Intimidate"),
    COMPOUND_EYES("Compound Eyes",
            "The Pokémon's accuracy goes up."),
    SWARM("Swarm",
            "Powers up Bug-type moves when the Pokémon's HP is low.");

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
