package com.delgiudice.pokemonbattlefx.trainer;

import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;

import java.util.List;

/**
 * Class that represents the player. Automatically flags the trainer as player during constructor call.
 */
public class Player extends Trainer {

    public Player(String name, Pokemon pokemon)
    {
        super(name);
        player = true;
        addPokemon(pokemon);
    }
}
