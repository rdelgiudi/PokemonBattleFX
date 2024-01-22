package com.delgiudice.pokemonbattlefx.trainer;

import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;

import java.util.List;

/**
 * Base class of all enemy trainers.
 */
public abstract class EnemyTrainer extends Trainer{

    /**
     * Enemy party sorted using the battle order.
     */
    protected List<Pokemon> partyBattleOrder;

    public void setPartyBattleOrder(List<Pokemon> partyBattleOrder) {
        this.partyBattleOrder = partyBattleOrder;
    }

    public EnemyTrainer(String name, Pokemon pokemon) {
        super(name, pokemon);
    }

    public EnemyTrainer(String name) {
        super(name);
    }

    abstract public TrainerAction getEnemyAction(TrainerAction trainerAction);

    abstract public TrainerAction getEnemySwitchOut();
}
