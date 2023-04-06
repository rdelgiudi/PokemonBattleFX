package com.delgiudice.pokemonbattlefx;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class PokemonSpecie {
    private String name;
    private Type[] type = {new Type(Enums.Types.NOTYPE), new Type(Enums.Types.NOTYPE)};
    private LinkedHashMap<String, Integer> baseStats = new LinkedHashMap<>();
    private static HashMap<String, PokemonSpecie> pokemonList = new HashMap<>();

    public static HashMap<String, PokemonSpecie> getPokemonList() {
        return pokemonList;
    }

    PokemonSpecie(String name, Type type1, Type type2, int maxHp, int attack, int defense, int spAttack, int spDefense, int speed){
        this.name = name;
        this.type[0] = type1;
        this.type[1] = type2;
        baseStats.put("Max HP", maxHp);
        baseStats.put("Attack", attack);
        baseStats.put("Defense", defense);
        baseStats.put("Special Attack", spAttack);
        baseStats.put("Special Defense", spDefense);
        baseStats.put("Speed", speed);
    }

    PokemonSpecie(String name, Type type1, int maxHp, int attack, int defense, int spAttack, int spDefense, int speed){
        this.name = name;
        this.type[0] = type1;
        baseStats.put("Max HP", maxHp);
        baseStats.put("Attack", attack);
        baseStats.put("Defense", defense);
        baseStats.put("Special Attack", spAttack);
        baseStats.put("Special Defense", spDefense);
        baseStats.put("Speed", speed);
    }

    public PokemonSpecie(PokemonSpecie original) {
        this.name = original.name;
        this.type = original.type;
        this.baseStats = original.baseStats;
    }

    public String getName() {
        return name;
    }

    public Type[] getType() {
        return type;
    }

    public HashMap<String, Integer> getBaseStats() {
        return baseStats;
    }

    public static void setPokemonList(){        //fills pokemon list, maybe some alternatives on how to execute this?
        Move.setMoveList(); //first we initialize movelist
        PokemonSpecie newpkmn = new PokemonSpecie("Bulbasaur", Type.typeList.get(4), Type.typeList.get(7),
                45, 49, 49, 65, 65, 45);
        pokemonList.put(newpkmn.getName(), newpkmn);
        newpkmn = new PokemonSpecie("Charmander", Type.typeList.get(1),
                39, 52, 43, 60, 50, 65);
        pokemonList.put(newpkmn.getName(), newpkmn);
        newpkmn = new PokemonSpecie("Rattata", Type.typeList.get(0),
                30, 56, 35, 25, 35, 72);
        pokemonList.put(newpkmn.getName(), newpkmn);
    }
}
