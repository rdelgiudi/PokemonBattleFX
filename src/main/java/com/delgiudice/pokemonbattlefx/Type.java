package com.delgiudice.pokemonbattlefx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

///This class indicates the typing of move/Pokemon as well as contains type strengths and weaknesses
public class Type {
    private Enums.Types type;
    private List<Enums.Types> weakAgainst = new LinkedList<>();
    private List<Enums.Types> strongAgainst = new LinkedList<>();
    private Enums.Types noEffectAgainst = Enums.Types.MISSING;

    Type(Enums.Types type) {
        this.type = type;
        checkEffectiveness();       //fills effectiveness
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

    public static List<Type> typeList = new ArrayList<>();

    //initializes list of available types
    public static void setTypeList()
    {
        for(Enums.Types type: Enums.Types.values())
            typeList.add(new Type(type));
    }

    ///Sets effectiveness of types (very effective, not very effective and no effect)
    public void checkEffectiveness()
    {
        switch(type){
            case NORMAL:
                weakAgainst.add(Enums.Types.ROCK);
                weakAgainst.add(Enums.Types.STEEL);

                noEffectAgainst = Enums.Types.GHOST;
                break;
            case FIRE:
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.WATER);
                weakAgainst.add(Enums.Types.ROCK);
                weakAgainst.add(Enums.Types.DRAGON);

                strongAgainst.add(Enums.Types.GRASS);
                strongAgainst.add(Enums.Types.ICE);
                strongAgainst.add(Enums.Types.BUG);
                strongAgainst.add(Enums.Types.STEEL);
                break;
            case WATER:
                weakAgainst.add(Enums.Types.WATER);
                weakAgainst.add(Enums.Types.GRASS);
                weakAgainst.add(Enums.Types.DRAGON);

                strongAgainst.add(Enums.Types.FIRE);
                strongAgainst.add(Enums.Types.GROUND);
                strongAgainst.add(Enums.Types.ROCK);
                break;
            case ELECTRIC:
                weakAgainst.add(Enums.Types.ELECTRIC);
                weakAgainst.add(Enums.Types.GRASS);
                weakAgainst.add(Enums.Types.DRAGON);

                strongAgainst.add(Enums.Types.WATER);
                strongAgainst.add(Enums.Types.FLYING);

                noEffectAgainst = Enums.Types.GROUND;
                break;
            case GRASS:
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
                break;
            case ICE:
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.WATER);
                weakAgainst.add(Enums.Types.ICE);
                weakAgainst.add(Enums.Types.STEEL);

                strongAgainst.add(Enums.Types.GRASS);
                strongAgainst.add(Enums.Types.GROUND);
                strongAgainst.add(Enums.Types.FLYING);
                strongAgainst.add(Enums.Types.DRAGON);
                break;
            case FIGHTING:
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
                break;
            case POISON:
                weakAgainst.add(Enums.Types.POISON);
                weakAgainst.add(Enums.Types.GROUND);
                weakAgainst.add(Enums.Types.ROCK);
                weakAgainst.add(Enums.Types.GHOST);

                strongAgainst.add(Enums.Types.GRASS);
                strongAgainst.add(Enums.Types.FAIRY);

                noEffectAgainst = Enums.Types.STEEL;
                break;
            case GROUND:
                weakAgainst.add(Enums.Types.GRASS);
                weakAgainst.add(Enums.Types.BUG);

                strongAgainst.add(Enums.Types.FIRE);
                strongAgainst.add(Enums.Types.ELECTRIC);
                strongAgainst.add(Enums.Types.POISON);
                strongAgainst.add(Enums.Types.ROCK);
                strongAgainst.add(Enums.Types.STEEL);

                noEffectAgainst = Enums.Types.FLYING;
                break;
            case FLYING:
                weakAgainst.add(Enums.Types.ELECTRIC);
                weakAgainst.add(Enums.Types.ROCK);
                weakAgainst.add(Enums.Types.STEEL);

                strongAgainst.add(Enums.Types.GRASS);
                strongAgainst.add(Enums.Types.FIGHTING);
                strongAgainst.add(Enums.Types.BUG);
                break;
            case PSYCHIC:
                weakAgainst.add(Enums.Types.PSYCHIC);
                weakAgainst.add(Enums.Types.STEEL);

                strongAgainst.add(Enums.Types.FIGHTING);
                strongAgainst.add(Enums.Types.POISON);

                noEffectAgainst = Enums.Types.DARK;
                break;
            case BUG:
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
                break;
            case ROCK:
                weakAgainst.add(Enums.Types.FIGHTING);
                weakAgainst.add(Enums.Types.GROUND);
                weakAgainst.add(Enums.Types.STEEL);

                strongAgainst.add(Enums.Types.FIRE);
                strongAgainst.add(Enums.Types.ICE);
                strongAgainst.add(Enums.Types.FLYING);
                strongAgainst.add(Enums.Types.BUG);
                break;
            case GHOST:
                weakAgainst.add(Enums.Types.DARK);

                strongAgainst.add(Enums.Types.PSYCHIC);
                strongAgainst.add(Enums.Types.GHOST);

                noEffectAgainst = Enums.Types.NORMAL;
                break;
            case DRAGON:
                weakAgainst.add(Enums.Types.STEEL);

                strongAgainst.add(Enums.Types.DRAGON);

                noEffectAgainst = Enums.Types.FAIRY;
                break;
            case DARK:
                weakAgainst.add(Enums.Types.FIGHTING);
                weakAgainst.add(Enums.Types.DRAGON);
                weakAgainst.add(Enums.Types.FAIRY);

                strongAgainst.add(Enums.Types.PSYCHIC);
                strongAgainst.add(Enums.Types.GHOST);
                break;
            case STEEL:
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.WATER);
                weakAgainst.add(Enums.Types.ELECTRIC);
                weakAgainst.add(Enums.Types.STEEL);

                strongAgainst.add(Enums.Types.ICE);
                strongAgainst.add(Enums.Types.ROCK);
                strongAgainst.add(Enums.Types.FAIRY);
                break;
            case FAIRY:
                weakAgainst.add(Enums.Types.FIRE);
                weakAgainst.add(Enums.Types.POISON);
                weakAgainst.add(Enums.Types.STEEL);

                strongAgainst.add(Enums.Types.FIGHTING);
                strongAgainst.add(Enums.Types.DRAGON);
                strongAgainst.add(Enums.Types.DARK);
                break;
        }
    }
}
