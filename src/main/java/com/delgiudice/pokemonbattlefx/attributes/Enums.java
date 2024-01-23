package com.delgiudice.pokemonbattlefx.attributes;

import com.delgiudice.pokemonbattlefx.battle.BattleLogic;
import com.delgiudice.pokemonbattlefx.battle.SwapPokemonController;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import com.delgiudice.pokemonbattlefx.move.MoveTemplate;
import com.delgiudice.pokemonbattlefx.trainer.NpcTrainer;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;

/**
 * Contains various Enums used in the program.
 */
public class Enums {
    /**
     * Defines the mode in which the game has been started.
     * @see BattleLogic#gameMode
     */
    public enum GameMode {
        OFFLINE,
        SERVER,
        CLIENT;
    }

    /**
     * Contains all Types present in the game, as well as the color value that should be used when displaying it.
     * @see Type
     */
    public enum Types {
        ANY("ANY", Color.WHITE),
        NORMAL("NORMAL", Color.valueOf("#A8A878")),       //0
        FIRE("FIRE", Color.valueOf("#F08030")),           //1
        WATER("WATER", Color.valueOf("#6890F0")),         //2
        ELECTRIC("ELECTRIC", Color.valueOf("#F8D030")),   //3
        GRASS("GRASS", Color.valueOf("#78C850")),         //4
        ICE("ICE", Color.valueOf("#98D8D8")),             //5
        FIGHTING("FIGHTING", Color.valueOf("#C03028")),   //6
        POISON("POISON", Color.valueOf("#A040A0")),       //7
        GROUND("GROUND", Color.valueOf("#E0C068")),       //8
        FLYING("FLYING", Color.valueOf("#A890F0")),       //9
        PSYCHIC("PSYCHIC", Color.valueOf("#F85888")),     //10
        BUG("BUG", Color.valueOf("#6D7815")),             //11
        ROCK("ROCK", Color.valueOf("#786824")),           //12
        GHOST("GHOST", Color.valueOf("#493963")),         //13
        DRAGON("DRAGON", Color.valueOf("#7038F8")),       //14
        DARK("DARK", Color.valueOf("#49392F")),           //15
        STEEL("STEEL", Color.valueOf("#787887")),         //16
        FAIRY("FAIRY", Color.valueOf("#B8B8D0")),         //17
        MISSING("NONE", Color.WHITE),        //18
        NO_TYPE("TYPELESS", Color.valueOf("#44685E"));      //19

        final private String typeString;
        final private Color typeColor;

        public String toString() {
            return typeString;
        }

        public Color getTypeColor() {
            return typeColor;
        }

        Types(String typeString, Color typeColor) {
            this.typeString = typeString;
            this.typeColor = typeColor;
        }
    }

    /**
     * Contains all Subtypes present in the game.
     */
    public enum Subtypes {
        PHYSICAL("PHYSICAL"),
        SPECIAL("SPECIAL"),
        STATUS("STATUS");

        final private String typeString;

        public String toString()
        {
            return typeString;
        }

        Subtypes(String typeString) {
            this.typeString = typeString;
        }
    }

    /**
     * Contains all types of stats used in the game. This includes special stat modifiers that don't appear in regular
     * numerical stats. These are <code>ACCURACY</code> and <code>EVASIVENESS</code>.
     * @see Pokemon#stats
     * @see Pokemon#statModifiers
     */
    public enum StatType {       //pokemon statistics (both regular and stat changes)
        MAX_HP(0,"Max HP"),
        ATTACK(1,"Attack"),
        DEFENSE(2,"Defense"),
        ACCURACY(-1,"Accuracy"), // in battle only
        SPECIAL_ATTACK(3,"Special Attack"),
        SPECIAL_DEFENSE(4,"Special Defense"),
        SPEED(5,"Speed"),
        EVASIVENESS(-2,"Evasiveness"); //in battle only

        final private String typeString;
        final int baseStatId;

        public String toString()
        {
            return typeString;
        }

        public int getBaseStatId() {
            return baseStatId;
        }

        public static StatType getFromBaseStatId(int val) {
            for (StatType statType : StatType.values())
                if (statType.baseStatId == val)
                    return statType;

             throw new IllegalArgumentException("Argument out of range for StatType");
        }

        StatType(int id ,String typeString) {
            this.baseStatId = id;
            this.typeString = typeString;
        }
    }

    /**
     * Contains all status effects that can be applied to a Pokémon.
     * @see Pokemon#status
     */
    public enum Status {       //status list
        NONE("NONE"),
        PARALYZED("paralyzed"),
        POISONED("poisoned"),
        BADLY_POISONED("badly poisoned"),
        SLEEPING("asleep"),
        BURNED("burned"),
        FROZEN("frozen"),
        FAINTED("fainted"),
        ANY("any");

        final private String statusString;

        public String toString() {
            return statusString;
        }

        Status(String statusString) {
            this.statusString = statusString;
        }
    }

    /**
     * Substatus effects that can be applied to Pokémon. These can be stacked and usually are discarded when the Pokémon
     * is switched out.
     * @see Pokemon#subStatuses
     */
    public enum SubStatus {
        NONE("no abnormal status"),
        CONFUSED("became confused"),
        FLINCHED("flinched"),
        ATTRACTED("fell in love"), //TODO: implementation of Pokemon genders
        RECHARGE("recharge"),
        FOCUS_ENERGY("focus energy"),
        LASER_FOCUS("laser focus"),
        ROOST("roost"),
        GASTRO_ACID("gastro acid"),
        SUBSTITUTE("substitute"),
        LEECH_SEED("leech seed");

        final private String statusString;

        public String toString() {
            return statusString;
        }

        SubStatus(String statusString) {
            this.statusString = statusString;
        }
    }

    /**
     * Move categories which help group some of the moves. This is useful for moves that can or cannot affect Pokemon
     * in special conditions
     * @see MoveTemplate#moveCategory
     */
    public enum MoveCategory {
        NONE("None"),
        AURA_PULSE("Aura and pulse"),
        BALL_BOMB("Ball and bomb"),
        BITING("Biting"),
        DANCE("Dance"),
        EXPLOSIVE("Explosive"),
        POWDER_SPORE("Powder and Spore"),
        PUNCHING("Punching"),
        SLICING("Slicing"),
        SOUND_BASED("Sound-based"),
        WIND("Wind");

        final private String name;

        public String toString() {
            return name;
        }

        MoveCategory(String name) {
            this.name = name;
        }
    }

    /**
     * Contains conditions that affect the entire side of the field.
     */
    public enum BattlefieldCondition {
        NONE("No condition"),
        TAILWIND("Tailwind");

        final private String name;

        public String toString() {
            return name;
        }

        BattlefieldCondition(String name) {
            this.name = name;
        }
    }

    /**
     * Contains ground hazards that may be placed on one side of the field.
     * @see BattleLogic#allySpikes
     * @see BattleLogic#enemySpikes
     */
    public enum Spikes {
        NONE("No spikes"),
        TOXIC_SPIKES("Toxic Spikes");

        final private String name;

        public String toString() {
            return name;
        }

        Spikes(String name) {
            this.name = name;
        }
    }

    /**
     * Contains all existing weather effects that may occur on the battlefield.
     * @see BattleLogic#weatherEffect
     */
    public enum WeatherEffect {
        NONE("Regular weather"),
        RAIN("rain"),
        SANDSTORM("sandstorm");

        final private String name;

        public String toString() {
            return name;
        }

        WeatherEffect(String name) {
            this.name = name;
        }
    }

    /**
     * Contains all contexts for switching a Pokémon. It allows the <code>SwapPokemonController</code> to determine
     * which phase the turn is currently in.
     * @see SwapPokemonController#finalizeSwitchOut(List, int, TrainerAction)
     * @see SwapPokemonController#switchContext
     */
    public enum SwitchContext {
        SWITCH_FIRST,
        SWITCH_FIRST_MOVE,
        SWITCH_SECOND,
        SWITCH_SECOND_MOVE,
        SWITCH_FAINTED,
        SWITCH_BOTH_FAINTED_ONLINE;
    }

    /**
     * Contains all natures that can occur in a Pokémon. Natures give a boost to one of the statistic at the cost of
     * lowering others. Natures that don't boost any are boosting and lowering the same statistic.
     * @see Pokemon#nature
     */
    public enum Nature {       //nature list
        HARDY(0, "Hardy", 0, 0, 0, 0, 0),
        LONELY(1,"Lonely", 1, -1, 0, 0, 0),
        BRAVE(2,"Brave", 1, 0, 0, 0, -1),
        ADAMANT(3,"Adamant", 1, 0, -1, 0, 0),
        NAUGHTY(4,"Naughty", 1, 0, 0, -1, 0),
        BOLD(5,"Bold", -1, 1, 0, 0, 0),
        DOCILE(6,"Docile", 0, 0, 0, 0, 0),
        RELAXED(7,"Relaxed", 0, 1, 0, 0, -1),
        IMPISH(8,"Impish", 0, 0, 0, 0, 0),
        LAX(9,"Lax", 0, 1, 0, -1, 0),
        TIMID(10,"Timid", -1, 0, 0, 0, 1),
        HASTY(11,"Hasty", 0, -1, 0, 0, 1),
        SERIOUS(12,"Serious", 0, 0, 0, 0, 0),
        JOLLY(13,"Jolly", 0, 0, -1, 0, 1),
        NAIVE(14,"Naive", 0, 0, 0, -1, 1),
        MODEST(15,"Modest", -1, 0, 1, 0, 0),
        MILD(16,"Mild", 0, -1, 1, 0, 0),
        QUIET(17,"Quiet", -1, 0, 1, 0, 0),
        BASHFUL(18,"Bashful", 0, 0, 0, 0, 0),
        RASH(19,"Rash", 0, 0, 1, -1, 0),
        CALM(20,"Calm", -1, 0, 0, 1, 0),
        GENTLE(21,"Gentle", 0, -1, 0, 1, 0),
        SASSY(22,"Sassy", 0, 0, 0, 1, -1),
        CAREFUL(23,"Careful", 0, 0, -1, 1, 0),
        QUIRKY(24, "Quirky", 0, 0, 0, 0, 0);

        final private int value;
        final private String nature;
        final private int[] statTab = {0, 0, 0, 0, 0};
        private static HashMap<Object, Nature> map = new HashMap<>();

        static {
            for (Nature nature : Nature.values()) {
                map.put(nature.value, nature);
            }
        }

        public static Nature valueOf(int nature) {
            return (Nature) map.get(nature);
        }

        public String toString(){
            return nature;
        }

        public int getValue(){
            return value;
        }

        public int[] getStatTab() {
            return statTab;
        }

        Nature(int value ,String nature, int a, int d, int sa, int sd, int s) {
            this.nature = nature;
            statTab[0] = a;
            statTab[1] = d;
            statTab[2] = sa;
            statTab[3] = sd;
            statTab[4] = s;
            this.value = value;
        }
    }

    /**
     * Contains states in which the Pokémon can be in. These usually are used for marking Pokémon that are locked into executing
     * a move or Pokémon which may benefit from executing a move repeatedly.
     * @see Pokemon#state
     */
    public enum States{
        NONE,
        TWOTURN,
        MULTITURN,
        RAGE;
    }

    /**
     * Contains trainer types which can be assigned to NPC trainers.
     * @see NpcTrainer
     */
    public enum TrainerTypes{
        NONE(""),
        ACE_TRAINER("Ace Trainer"),
        YOUNGSTER("Youngster");

        final private String typeString;

        public String toString() {
            return typeString;
        }

        TrainerTypes(String typeString) {
            this.typeString = typeString;
        }
    }

    /**
     * Contains action types that might be performed by a trainer.
     * @see BattleLogic#processPlayerAction(TrainerAction)
     */
    public enum ActionTypes{
        USE_MOVE,
        RECHARGE_PHASE,
        OUT_OF_MOVES,
        USE_BAG_ITEM,
        SWITCH_POKEMON,
    }

    /**
     * Contains all item types that can be used by a trainer during battle.
     * @see SwapPokemonController#useItem(Pokemon, HBox, int, ProgressBar, Label)
     * @see BattleLogic#processEnemyUseItem(List, TrainerAction)
     */
    public enum ItemType{
        HP_RESTORE,
        PP_RESTORE,
        STATUS_HEALING,
        X_ITEMS;
    }
}