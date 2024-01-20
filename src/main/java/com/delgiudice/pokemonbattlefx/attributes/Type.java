package com.delgiudice.pokemonbattlefx.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class indicates the typing of move/Pokémon as well as contains type strengths and weaknesses.
 * @see Enums.Types
 */
public class Type {

    private static final HashMap<Enums.Types,Type> typeMap = new HashMap<>();

    /**
     * Used to get a specific <code>Type</code> object.
     * @param type enum corresponding to the requested <code>Type</code>
     * @return <code>Type</code> object from the map
     */
    public static Type getTypeMap(Enums.Types type) {
        return typeMap.get(type);
    }

    /**
     * Used to get the entire <code>Type</code> map.
     */
    public static HashMap<Enums.Types,Type> getTypeMap() {
        return typeMap;
    }

    private final Enums.Types type;
    private final List<Enums.Types> weakAgainst = new ArrayList<>();
    private final List<Enums.Types> strongAgainst = new ArrayList<>();
    private Enums.Types noEffectAgainst = Enums.Types.MISSING;

    /**
     * Class constructor.
     * @param type enum representing the <code>Type</code> to be created
     */
    public Type(Enums.Types type) {
        this.type = type;
        setEffectiveness();       //fills effectiveness
    }

    /**
     * Returns all <code>Enums.Types</code> which this <code>Type</code> is weak against.
     * @return <code>List</code> of all weaknesses
     */
    public List<Enums.Types> getWeakAgainst() {
        return weakAgainst;
    }
    /**
     * Returns all <code>Enums.Types</code> which this <code>Type</code> is strong against.
     * @return <code>List</code> of all strengths
     */
    public List<Enums.Types> getStrongAgainst() {
        return strongAgainst;
    }
    /**
     * Returns all <code>Enums.Types</code> which this <code>Type</code> has no effect against.
     * @return enum representing <code>Type</code> which isn't affected by this <code>Type</code>
     */
    public Enums.Types getNoEffectAgainst() {
        return noEffectAgainst;
    }

    /**
     * Getter for the <code>type</code> field
     * @return <code>type</code>
     */
    public Enums.Types getTypeEnum() {
        return type;
    }

    /**
     * Return name of the <code>Type</code>
     * @return name of the type in <code>String</code> form.
     */
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
