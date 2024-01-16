package com.delgiudice.pokemonbattlefx.trainer;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.item.Item;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class Trainer {
    private String name;
    private List<Pokemon> party = new ArrayList<>();
    private HashMap<Item, Integer> items = new LinkedHashMap<>();
    protected boolean player = false;

    public String getName() {
        return name;
    }

    public List<Pokemon> getParty() {
        return party;
    }

    public Pokemon getParty(int i) {
        return party.get(i);
    }

    public HashMap<Item, Integer> getItems() {
        return items;
    }

    public boolean isPlayer() {
        return player;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Trainer(String name, Pokemon pokemon) {
        this.name = name;
        addPokemon(pokemon);
    }

    public Trainer(String name) {
        this.name = name;
    }

    public void addPokemon (Pokemon pokemon) {
        if (party.size() <= 5) {
            pokemon.setOwner(this);
            party.add(pokemon);
        }
        else
            System.out.println("ERROR: Party full!");
    }

    public Enums.TrainerTypes getTrainerType() {
        return Enums.TrainerTypes.NONE;
    }

    abstract public TrainerAction getEnemyAction(Pokemon enemyPokemon);
}
