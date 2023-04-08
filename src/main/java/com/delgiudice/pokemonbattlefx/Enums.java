package com.delgiudice.pokemonbattlefx;

import java.util.HashMap;
import java.util.Map;

public class Enums {
    //All types
    enum Types {        //lista typów
        NORMAL("NORMAL"),       //0
        FIRE("FIRE"),           //1
        WATER("WATER"),         //2
        ELECTRIC("ELECTRIC"),   //3
        GRASS("GRASS"),         //4
        ICE("ICE"),             //5
        FIGHTING("FIGHTING"),   //6
        POISON("POISON"),       //7
        GROUND("GROUND"),       //8
        FLYING("FLYING"),       //9
        PSYCHIC("PSYCHIC"),     //10
        BUG("BUG"),             //11
        ROCK("ROCK"),           //12
        GHOST("GHOST"),         //13
        DRAGON("DRAGON"),       //14
        DARK("DARK"),           //15
        STEEL("STEEL"),         //16
        FAIRY("FAIRY"),         //17
        MISSING("NONE"),        //18
        NOTYPE("NO TYPE");      //19

        final private String typeString;

        public String toString() {
            return typeString;
        }

        Types(String typeString) {
            this.typeString = typeString;
        }
    }
    enum Subtypes {     //move subtype list
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
    enum StatusType {       //statistics to lower or increase
        ATTACK("Attack"),
        DEFENSE("Defense"),
        ACCURACY("Accuracy"),
        SPECIAL_ATTACK("Special Attack"),
        SPECIAL_DEFENSE("Special Defense"),
        SPEED("Speed"),
        EVASIVENESS("Evasiveness");

        final private String typeString;

        public String toString()
        {
            return typeString;
        }

        StatusType(String typeString) {
            this.typeString = typeString;
        }
    }
    enum Status {       //lista statusów
        NONE("NONE"),
        PARALYZED("PARALYZED"),
        POISONED("POISONED"),
        SLEEPING("SLEEPING"),
        BURNED("BURNED"),
        FROZEN("FROZEN");

        final private String statusString;

        public String toString() {
            return statusString;
        }

        Status(String statusString) {
            this.statusString = statusString;
        }
    }
    enum Nature {       //lista natur
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
        TIMID(10,"Timid", 1, 0, 0, 0, 1),
        HASTY(11,"Hasty", 0, -1, 0, 0, 1),
        SERIOUS(12,"Serious", 0, 0, 0, 0, 0),
        JOLLY(13,"Jolly", 0, 0, -1, 0, 1),
        NAIVE(14,"Naive", 0, 0, 0, -1, 1),
        MODEST(15,"Modest", 1, 0, 1, 0, 0),
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
        private static Map map = new HashMap<>();

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
    enum TrainerTypes{
        ACETRAINER("Ace Trainer"),
        YOUNGSTER("Youngster");

        final private String typeString;

        public String toString() {
            return typeString;
        }

        TrainerTypes(String typeString) {
            this.typeString = typeString;
        }
    }
    enum ItemType{
        HEALING,
        PPRESTORE;
    }
}