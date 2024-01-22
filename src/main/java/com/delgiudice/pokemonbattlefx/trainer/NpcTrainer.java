package com.delgiudice.pokemonbattlefx.trainer;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class that represents an AI trainer.
 */
public class NpcTrainer extends EnemyTrainer {
    public static HashMap<String, NpcTrainer> trainerList = new LinkedHashMap<>();
    private Enums.TrainerTypes trainerType;

    /**
     * Class constructor.
     */
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

    }*/

    @Override
    public Enums.TrainerTypes getTrainerType() {
        return trainerType;
    }

    public static void setTrainerList(){

    }

    /**
     * Generates the enemy action. For now only attacks with randomly generated move, gym leader/Elite Four AI
     * implementation planned in the future.
     * @param trainerAction unused
     * @return the action decided by the enemy
     */
    @Override
    public TrainerAction getEnemyAction(TrainerAction trainerAction) {
        Pokemon enemyPokemon = partyBattleOrder.get(0);
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

    /**
     * Gets next Pokémon to be sent out by the enemy.
     * @return the action decided by the enemy
     */
    @Override
    public TrainerAction getEnemySwitchOut() {

        int newPokemonIndex = -1;
        for (int i=1; i<partyBattleOrder.size(); i++) {
            if (partyBattleOrder.get(i).getHp() > 0) {
                newPokemonIndex = i;
                break;
            }
        }
        //switchPokemon(false, newPokemonIndex);
        return new TrainerAction(Enums.ActionTypes.SWITCH_POKEMON, String.valueOf(newPokemonIndex));
    }
}
