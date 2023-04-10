package com.delgiudice.pokemonbattlefx;

public class Player extends Trainer {

    Player(String name, Pokemon pokemon)
    {
        super(name);
        player = true;
        addPokemon(pokemon);
    }
}
