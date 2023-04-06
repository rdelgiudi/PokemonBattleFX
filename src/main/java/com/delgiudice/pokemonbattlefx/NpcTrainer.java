package com.delgiudice.pokemonbattlefx;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class NpcTrainer extends Trainer {
    public static HashMap<String, NpcTrainer> trainerList = new LinkedHashMap<>();
    private Enums.TrainerTypes trainerType;

    NpcTrainer(String name, Enums.TrainerTypes type, Pokemon pokemon)
    {
        setName(name);
        trainerType = type;
        getParty().add(pokemon);
    }

    /*NpcTrainer(NpcTrainer original)           //Does not work yet, do not use!
    {
        setName(original.getName());
        trainerType = original.trainerType;
        for (Pokemon copypoke: original.getParty()) {
            getParty().add(new Pokemon(copypoke));
        }

    }
*/
    public Enums.TrainerTypes getTrainerType() {
        return trainerType;
    }

    public static void setTrainerList(){
        PokemonSpecie.setPokemonList(); //sets pokemon list to form parties
        Pokemon tmppokemon = new Pokemon(PokemonSpecie.getPokemonList().get("Rattata"), 5, new Move(Move.getMoveList().get("Scratch")), new Move(Move.getMoveList().get("Tail Whip")),
                new Move(Move.getMoveList().get("Quick Attack")));
        NpcTrainer newtrain = new NpcTrainer("Joey", Enums.TrainerTypes.YOUNGSTER, tmppokemon);
        tmppokemon = new Pokemon(PokemonSpecie.getPokemonList().get("Bulbasaur"), 5, new Move(Move.getMoveList().get("Tackle")),
                new Move(Move.getMoveList().get("Growl")), new Move(Move.getMoveList().get("Vine Whip")));
        newtrain.addPokemon(tmppokemon);
        trainerList.put(newtrain.getName(), newtrain);
    }

}
