package com.delgiudice.pokemonbattlefx;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class NpcTrainer extends Trainer {
    public static HashMap<String, NpcTrainer> trainerList = new LinkedHashMap<>();
    private Enums.TrainerTypes trainerType;

    NpcTrainer(String name, Enums.TrainerTypes type, Pokemon pokemon)
    {
        super(name, pokemon);
        trainerType = type;
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

    }

}
