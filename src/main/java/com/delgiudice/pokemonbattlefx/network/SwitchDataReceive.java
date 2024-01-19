package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.BattleLogic;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import com.delgiudice.pokemonbattlefx.trainer.OnlineTrainer;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;

// Thread that handles receiving data about the next Pokemon sent out by the online enemy
public class SwitchDataReceive extends SyncThread{

    private OnlineTrainer enemy;
    private BattleLogic logic;
    private Pokemon firstPokemon, secondPokemon;
    private Move secondMove;
    private Enums.SwitchContext switchContext;

    public SwitchDataReceive(DataInputStream inputStream, DataOutputStream outputStream, OnlineTrainer enemy,
                             BattleLogic logic, Pokemon firstPokemon, Pokemon secondPokemon, Move secondMove,
                             Enums.SwitchContext switchContext) {
        super(inputStream, outputStream);
        this.enemy = enemy;
        this.logic = logic;
        this.firstPokemon = firstPokemon;
        this.secondPokemon = secondPokemon;
        this.secondMove = secondMove;
        this.switchContext = switchContext;
    }

    @Override
    public void run() {
        TrainerAction enemyAction = enemy.getEnemySwitchOut();
        // Not necessary to Platform.runLater()
        logic.processEnemySwitchOut(firstPokemon, secondPokemon, secondMove, switchContext, enemyAction);
    }
}
