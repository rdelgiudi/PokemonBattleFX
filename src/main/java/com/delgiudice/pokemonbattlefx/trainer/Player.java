package com.delgiudice.pokemonbattlefx.trainer;

import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;

public class Player extends Trainer {

    public Player(String name, Pokemon pokemon)
    {
        super(name);
        player = true;
        addPokemon(pokemon);
    }
}
