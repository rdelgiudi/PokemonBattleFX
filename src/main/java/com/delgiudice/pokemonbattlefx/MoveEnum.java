package com.delgiudice.pokemonbattlefx;

public enum MoveEnum {
    CONFUSION_DAMAGE("Confusion Damage"),
    STRUGGLE("Struggle"),
    TACKLE("Tackle"),
    GROWL("Growl"),
    VINE_WHIP("Vine Whip"),
    SCRATCH("Scratch"),
    EMBER("Ember"),
    TAIL_WHIP("Tail Whip"),
    QUICK_ATTACK("Quick Attack"),
    WATER_GUN("Water Gun"),
    DOUBLE_KICK("Double Kick"),
    SLASH("Slash"),
    DRAGON_BREATH("Dragon Breath"),
    FLARE_BLITZ("Flare Blitz"),
    FIRE_SPIN("Fire Spin"),
    DIG("Dig"),
    RAZOR_LEAF("Razor Leaf"),
    SOLAR_BEAM("Solar Beam"),
    DOUBLE_EDGE("Double-edge"),
    SLEEP_POWDER("Sleep Powder"),
    SEED_BOMB("Seed Bomb"),
    SYNTHESIS("Synthesis"),
    SWEET_SCENT("Sweet Scent"),
    OUTRAGE("Outrage"),
    PETAL_DANCE("Petal Dance"),
    PETAL_BLIZZARD("Petal Blizzard"),
    CONFUSE_RAY("Confuse Ray"),
    WILL_O_WISP("Will-O-Wisp"),
    INFERNO("Inferno"),
    SCARY_FACE("Scary Face"),
    FIRE_FANG("Fire Fang"),
    DRAGON_CLAW("Dragon Claw"),
    AIR_SLASH("Air Slash"),
    HEAT_WAVE("Heat Wave"),
    DRAGON_DANCE("Dragon Dance"),
    EARTHQUAKE("Earthquake"),
    HYDRO_PUMP("Hydro Pump"),
    AQUA_TAIL("Aqua Tail"),
    SKULL_BASH("Skull Bash"),
    SHELL_SMASH("Shell Smash"),
    ROLLOUT("Rollout"),
    WHIRLPOOL("Whirlpool"),
    ICE_PUNCH("Ice Punch");


    private final String name;

    MoveEnum(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
