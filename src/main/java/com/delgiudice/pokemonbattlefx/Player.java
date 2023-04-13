package com.delgiudice.pokemonbattlefx;

public class Player extends Trainer {

    public Player(String name, Pokemon pokemon)
    {
        super(name);
        player = true;
        addPokemon(pokemon);
    }
}
