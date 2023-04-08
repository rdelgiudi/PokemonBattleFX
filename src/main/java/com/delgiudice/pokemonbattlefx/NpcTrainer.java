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
        Pokemon tmppokemon = new Pokemon(PokemonSpecie.getPokemonMap().get("Rattata"), 5,
                new Move(MoveTemplate.getMoveMap().get("Scratch")), new Move(MoveTemplate.getMoveMap().get("Tail Whip")),
                new Move(MoveTemplate.getMoveMap().get("Quick Attack")));
        NpcTrainer newtrain = new NpcTrainer("Joey", Enums.TrainerTypes.YOUNGSTER, tmppokemon);
        tmppokemon = new Pokemon(PokemonSpecie.getPokemonMap().get("Bulbasaur"), 5,
                new Move(MoveTemplate.getMoveMap().get("Tackle")), new Move(MoveTemplate.getMoveMap().get("Growl")),
                new Move(MoveTemplate.getMoveMap().get("Vine Whip")));
        newtrain.addPokemon(tmppokemon);
        trainerList.put(newtrain.getName(), newtrain);
    }

}
