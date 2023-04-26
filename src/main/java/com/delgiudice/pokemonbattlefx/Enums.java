package com.delgiudice.pokemonbattlefx;

import javafx.scene.paint.Color;

import java.util.HashMap;

public class Enums {
    //All types
    public enum Types {        //lista typów
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
    public enum Subtypes {     //move subtype list
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
    public enum Status {       //status list
        NONE("NONE"),
        PARALYZED("paralyzed"),
        POISONED("poisoned"),
        BADLY_POISONED("badly poisoned"),
        SLEEPING("asleep"),
        BURNED("burned"),
        FROZEN("frozen");

        final private String statusString;

        public String toString() {
            return statusString;
        }

        Status(String statusString) {
            this.statusString = statusString;
        }
    }
    public enum SubStatus {
        NONE("no abnormal status"),
        CONFUSED("became confused"),
        FLINCHED("flinched"),
        ATTRACTED("fell in love"), //TODO: implementation of Pokemon genders
        RECHARGE("recharge"),
        FOCUS_ENERGY("focus energy"),
        LASER_FOCUS("laser focus"),
        ROOST("roost");

        final private String statusString;

        public String toString() {
            return statusString;
        }

        SubStatus(String statusString) {
            this.statusString = statusString;
        }
    }

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

    public enum Nature {       //lista natur
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
    public enum States{
        NONE,
        TWOTURN,
        MULTITURN,
        RAGE;
    }

    public enum TrainerTypes{
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
    public enum ItemType{
        HEALING,
        PP_RESTORE;
    }
}