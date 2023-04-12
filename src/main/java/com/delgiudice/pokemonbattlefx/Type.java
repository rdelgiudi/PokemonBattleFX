package com.delgiudice.pokemonbattlefx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

///This class indicates the typing of move/Pokemon as well as contains type strengths and weaknesses
public class Type {

    private static final HashMap<Enums.Types,Type> typeMap = new HashMap<>();

    public static HashMap<Enums.Types,Type> getTypeMap() {
        return typeMap;
    }

    private final Enums.Types type;
    private final List<Enums.Types> weakAgainst = new LinkedList<>();
    private final List<Enums.Types> strongAgainst = new LinkedList<>();
    private Enums.Types noEffectAgainst = Enums.Types.MISSING;

    Type(Enums.Types type) {
        this.type = type;
        setEffectiveness();       //fills effectiveness
    }

    public List<Enums.Types> getWeakAgainst() {
        return weakAgainst;
    }

    public List<Enums.Types> getStrongAgainst() {
        return strongAgainst;
    }

    public Enums.Types getNoEffectAgainst() {
        return noEffectAgainst;
    }

    public Enums.Types getTypeEnum() {
        return type;
    }

    public String toString() {
        return type.toString();
    }

    //initializes list of available types
    public static void setTypeList()
    {
        for(Enums.Types type: Enums.Types.values())
            typeMap.put(type ,new Type(type));
    }

    ///Sets effectiveness of types (very effective, not very effective and no effect)
    public void setEffectiveness()
    {
        switch (type) {
            case NORMAL -> {
                weakAgainst.add(Enums.Types.ROCK);
                weakAgainst.add(Enums.Types.STEEL);
                noEffectAgainst = Enums.Types.GHOST;
            }
            case FIRE -> {
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.WATER);
                weakAgainst.add(Enums.Types.ROCK);
                weakAgainst.add(Enums.Types.DRAGON);
                strongAgainst.add(Enums.Types.GRASS);
                strongAgainst.add(Enums.Types.ICE);
                strongAgainst.add(Enums.Types.BUG);
                strongAgainst.add(Enums.Types.STEEL);
            }
            case WATER -> {
                weakAgainst.add(Enums.Types.WATER);
                weakAgainst.add(Enums.Types.GRASS);
                weakAgainst.add(Enums.Types.DRAGON);
                strongAgainst.add(Enums.Types.FIRE);
                strongAgainst.add(Enums.Types.GROUND);
                strongAgainst.add(Enums.Types.ROCK);
            }
            case ELECTRIC -> {
                weakAgainst.add(Enums.Types.ELECTRIC);
                weakAgainst.add(Enums.Types.GRASS);
                weakAgainst.add(Enums.Types.DRAGON);
                strongAgainst.add(Enums.Types.WATER);
                strongAgainst.add(Enums.Types.FLYING);
                noEffectAgainst = Enums.Types.GROUND;
            }
            case GRASS -> {
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.GRASS);
                weakAgainst.add(Enums.Types.POISON);
                weakAgainst.add(Enums.Types.FLYING);
                weakAgainst.add(Enums.Types.BUG);
                weakAgainst.add(Enums.Types.DRAGON);
                weakAgainst.add(Enums.Types.STEEL);
                strongAgainst.add(Enums.Types.WATER);
                strongAgainst.add(Enums.Types.GROUND);
                strongAgainst.add(Enums.Types.ROCK);
            }
            case ICE -> {
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.WATER);
                weakAgainst.add(Enums.Types.ICE);
                weakAgainst.add(Enums.Types.STEEL);
                strongAgainst.add(Enums.Types.GRASS);
                strongAgainst.add(Enums.Types.GROUND);
                strongAgainst.add(Enums.Types.FLYING);
                strongAgainst.add(Enums.Types.DRAGON);
            }
            case FIGHTING -> {
                weakAgainst.add(Enums.Types.POISON);
                weakAgainst.add(Enums.Types.FLYING);
                weakAgainst.add(Enums.Types.PSYCHIC);
                weakAgainst.add(Enums.Types.BUG);
                weakAgainst.add(Enums.Types.FAIRY);
                strongAgainst.add(Enums.Types.NORMAL);
                strongAgainst.add(Enums.Types.ICE);
                strongAgainst.add(Enums.Types.ROCK);
                strongAgainst.add(Enums.Types.DARK);
                strongAgainst.add(Enums.Types.STEEL);
                noEffectAgainst = Enums.Types.GHOST;
            }
            case POISON -> {
                weakAgainst.add(Enums.Types.POISON);
                weakAgainst.add(Enums.Types.GROUND);
                weakAgainst.add(Enums.Types.ROCK);
                weakAgainst.add(Enums.Types.GHOST);
                strongAgainst.add(Enums.Types.GRASS);
                strongAgainst.add(Enums.Types.FAIRY);
                noEffectAgainst = Enums.Types.STEEL;
            }
            case GROUND -> {
                weakAgainst.add(Enums.Types.GRASS);
                weakAgainst.add(Enums.Types.BUG);
                strongAgainst.add(Enums.Types.FIRE);
                strongAgainst.add(Enums.Types.ELECTRIC);
                strongAgainst.add(Enums.Types.POISON);
                strongAgainst.add(Enums.Types.ROCK);
                strongAgainst.add(Enums.Types.STEEL);
                noEffectAgainst = Enums.Types.FLYING;
            }
            case FLYING -> {
                weakAgainst.add(Enums.Types.ELECTRIC);
                weakAgainst.add(Enums.Types.ROCK);
                weakAgainst.add(Enums.Types.STEEL);
                strongAgainst.add(Enums.Types.GRASS);
                strongAgainst.add(Enums.Types.FIGHTING);
                strongAgainst.add(Enums.Types.BUG);
            }
            case PSYCHIC -> {
                weakAgainst.add(Enums.Types.PSYCHIC);
                weakAgainst.add(Enums.Types.STEEL);
                strongAgainst.add(Enums.Types.FIGHTING);
                strongAgainst.add(Enums.Types.POISON);
                noEffectAgainst = Enums.Types.DARK;
            }
            case BUG -> {
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.FIGHTING);
                weakAgainst.add(Enums.Types.POISON);
                weakAgainst.add(Enums.Types.FLYING);
                weakAgainst.add(Enums.Types.GHOST);
                weakAgainst.add(Enums.Types.STEEL);
                weakAgainst.add(Enums.Types.FAIRY);
                strongAgainst.add(Enums.Types.GRASS);
                strongAgainst.add(Enums.Types.PSYCHIC);
                strongAgainst.add(Enums.Types.DARK);
            }
            case ROCK -> {
                weakAgainst.add(Enums.Types.FIGHTING);
                weakAgainst.add(Enums.Types.GROUND);
                weakAgainst.add(Enums.Types.STEEL);
                strongAgainst.add(Enums.Types.FIRE);
                strongAgainst.add(Enums.Types.ICE);
                strongAgainst.add(Enums.Types.FLYING);
                strongAgainst.add(Enums.Types.BUG);
            }
            case GHOST -> {
                weakAgainst.add(Enums.Types.DARK);
                strongAgainst.add(Enums.Types.PSYCHIC);
                strongAgainst.add(Enums.Types.GHOST);
                noEffectAgainst = Enums.Types.NORMAL;
            }
            case DRAGON -> {
                weakAgainst.add(Enums.Types.STEEL);
                strongAgainst.add(Enums.Types.DRAGON);
                noEffectAgainst = Enums.Types.FAIRY;
            }
            case DARK -> {
                weakAgainst.add(Enums.Types.FIGHTING);
                weakAgainst.add(Enums.Types.DRAGON);
                weakAgainst.add(Enums.Types.FAIRY);
                strongAgainst.add(Enums.Types.PSYCHIC);
                strongAgainst.add(Enums.Types.GHOST);
            }
            case STEEL -> {
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.WATER);
                weakAgainst.add(Enums.Types.ELECTRIC);
                weakAgainst.add(Enums.Types.STEEL);
                strongAgainst.add(Enums.Types.ICE);
                strongAgainst.add(Enums.Types.ROCK);
                strongAgainst.add(Enums.Types.FAIRY);
            }
            case FAIRY -> {
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.POISON);
                weakAgainst.add(Enums.Types.STEEL);
                strongAgainst.add(Enums.Types.FIGHTING);
                strongAgainst.add(Enums.Types.DRAGON);
                strongAgainst.add(Enums.Types.DARK);
            }
        }
    }
}
