package com.delgiudice.pokemonbattlefx;

import java.util.ArrayList;
import java.util.List;

public class Trainer {
    private String name;
    private List<Pokemon> party = new ArrayList<>();
    private List<Item> items = new ArrayList<>();
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

    public List<Item> getItems() {
        return items;
    }

    public boolean isPlayer() {
        return player;
    }

    public void setName(String name) {
        this.name = name;
    }

    Trainer(String name, Pokemon pokemon) {
        this.name = name;
        addPokemon(pokemon);
    }

    Trainer(String name) {
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

}
