package com.delgiudice.pokemonbattlefx;

import java.util.ArrayList;
import java.util.List;

public class Trainer {
    private String name;
    private List<Pokemon> party = new ArrayList<>();
    private List<Item> items = new ArrayList<>();

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

    public void setName(String name) {
        this.name = name;
    }

    public void addPokemon (Pokemon pokemon) {
        if (party.size() <= 5)
            party.add(pokemon);
        else
            System.out.println("ERROR: Party full!");
    }

}
