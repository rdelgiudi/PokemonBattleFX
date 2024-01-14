package com.delgiudice.pokemonbattlefx.move;

public enum MoveEnum {
    CONFUSION_DAMAGE("Confusion Damage"),
    STRUGGLE("Struggle"),
    SWAP_POKEMON("Swap Pokemon"),
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
    ICE_PUNCH("Ice Punch"),
    DIVE("Dive"),
    FLASH_CANNON("Flash Cannon"),
    ICE_BEAM("Ice Beam"),
    HYDRO_CANNON("Hydro Cannon"),
    STRING_SHOT("String Shot"),
    BUG_BITE("Bug Bite"),
    ELECTROWEB("Electroweb"),
    IRON_DEFENSE("Iron Defense"),
    TAILWIND("Tailwind"),
    BUG_BUZZ("Bug Buzz"),
    PSYCHIC("Psychic"),
    GIGA_DRAIN("Giga Drain"),
    POISON_STING("Poison Sting"),
    HARDEN("Harden"),
    PIN_MISSILE("Pin Missile"),
    POISON_JAB("Poison Jab"),
    TOXIC_SPIKES("Toxic Spikes"),
    AGILITY("Agility"),
    AERIAL_ACE("Aerial Ace"),
    SAND_ATTACK("Sand Attack"),
    HURRICANE("Hurricane"),
    ROOST("Roost"),
    DOUBLE_TEAM("Double Team"),
    FLY("Fly"),
    BOUNCE("Bounce"),
    SKY_DROP("Sky Drop"),
    HYPER_BEAM("Hyper Beam"),
    FEATHER_DANCE("Feather Dance"),
    SUPER_FANG("Super Fang"),
    FOCUS_ENERGY("Focus Energy"),
    ENDEAVOR("Endeavor"),
    TAKE_DOWN("Take Down"),
    BITE("Bite"),
    CRUNCH("Crunch"),
    LASER_FOCUS("Laser Focus"),
    DRILL_PECK("Drill Peck"),
    WING_ATTACK("Wing Attack"),
    DRILL_RUN("Drill Run"),
    GIGA_IMPACT("Giga Impact"),
    FACADE("Facade"),
    SLUDGE_BOMB("Sludge Bomb"),
    SCREECH("Screech"),
    GASTRO_ACID("Gastro Acid"),
    GUNK_SHOT("Gunk Shot"),
    BELCH("Belch"),
    COIL("Coil"),
    THUNDER_FANG("Thunder Fang"),
    THUNDERBOLT("Thunderbolt"),
    IRON_TAIL("Iron Tail"),
    ELECTRO_BALL("Electro Ball"),
    THUNDER("Thunder"),
    RAIN_DANCE("Rain Dance"),
    VOLT_SWITCH("Volt Switch"),
    SUBSTITUTE("Substitute"),
    BULLDOZE("Bulldoze"),
    RAPID_SPIN("Rapid Spin"),
    SWIFT("Swift"),
    SANDSTORM("Sandstorm"),
    BIND("Bind"),
    CLAMP("Clamp"),
    INFESTATION("Infestation"),
    LEECH_SEED("Leech Seed"),
    MAGMA_STORM("Magma Storm"),
    SAND_TOMB("Sand Tomb"),
    SNAP_TRAP("Snap Trap"),
    SPIKES("Spikes"),
    STEALTH_ROCK("Stealth Rock"),
    STICKY_WEB("Sticky Web"),
    WRAP("Wrap");


    private final String name;

    MoveEnum(String name) {
        this.name = name;
    }

    public static MoveEnum findByName(String name) {
        MoveEnum result = null;
        for (MoveEnum moveEnum : values()) {
            if (moveEnum.name.equalsIgnoreCase(name)) {
                result = moveEnum;
                break;
            }
        }
        return result;
    }

    public String toString() {
        return name;
    }
}
