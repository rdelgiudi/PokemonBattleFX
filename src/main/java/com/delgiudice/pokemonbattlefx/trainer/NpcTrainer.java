package com.delgiudice.pokemonbattlefx.trainer;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class NpcTrainer extends Trainer {
    public static HashMap<String, NpcTrainer> trainerList = new LinkedHashMap<>();
    private Enums.TrainerTypes trainerType;

    public NpcTrainer(String name, Enums.TrainerTypes type, Pokemon pokemon)
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
*/  @Override
    public Enums.TrainerTypes getTrainerType() {
        return trainerType;
    }

    public static void setTrainerList(){

    }

    // For now enemy move is randomly generated, maybe gym leader AI implementation in the future
    @Override
    public TrainerAction getEnemyAction(Pokemon enemyPokemon) {
        Move enemyMove;

        if (enemyPokemon.getStateMove() != null && enemyPokemon.getStateCounter() > 0 &&
                enemyPokemon.getState() == Enums.States.MULTITURN) {
            enemyMove = enemyPokemon.getStateMove();
            return new TrainerAction(Enums.ActionTypes.USE_MOVE,
                    String.valueOf(enemyPokemon.getMoveList().indexOf(enemyMove)));
        }
        if (enemyPokemon.getStateMove() != null) {
            enemyMove = enemyPokemon.getStateMove();
            return new TrainerAction(Enums.ActionTypes.USE_MOVE,
                    String.valueOf(enemyPokemon.getMoveList().indexOf(enemyMove)));
        }
        if (enemyPokemon.getSubStatuses().contains(Enums.SubStatus.RECHARGE)) {
            return new TrainerAction(Enums.ActionTypes.RECHARGE_PHASE, TrainerAction.NO_ACTION_NAME);
        }
        boolean[] moveAvailable = enemyPokemon.checkAvailableMoves();
        List<Integer> availableIndices = new ArrayList<>();

        for (int i=0; i < moveAvailable.length; i++) {
            if (moveAvailable[i])
                availableIndices.add(i);
        }
        if (availableIndices.isEmpty()) {
            return new TrainerAction(Enums.ActionTypes.OUT_OF_MOVES, TrainerAction.NO_ACTION_NAME);
        }

        SecureRandom generator = new SecureRandom();
        int enemyMoveIndex = generator.nextInt(availableIndices.size());

        enemyMove = enemyPokemon.getMoveList(availableIndices.get(enemyMoveIndex));

        return new TrainerAction(Enums.ActionTypes.USE_MOVE,
                String.valueOf(enemyPokemon.getMoveList().indexOf(enemyMove)));
    }
}
