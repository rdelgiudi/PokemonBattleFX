package com.delgiudice.pokemonbattlefx;

public class Player extends Trainer {

    Player(String name, Pokemon pokemon)
    {
        setName(name);
        getParty().add(pokemon);
    }
}
