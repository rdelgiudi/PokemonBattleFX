package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.battle.BattleLogic;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.trainer.OnlineTrainer;
import com.delgiudice.pokemonbattlefx.trainer.Trainer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import org.omg.CORBA.TIMEOUT;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

// Client thread that swaps information with its server counterpart about trainer moves
public class ClientMoveSync extends SyncThread{
    private BattleLogic battleLogic;
    private TrainerAction playerAction;
    private Trainer enemy;
    List<Timeline> battleTimeLine;

    public ClientMoveSync(DataInputStream inputStream, DataOutputStream outputStream, BattleLogic battleLogic,
                          TrainerAction playerAction, Trainer enemy, List<Timeline> battleTimeLine) {
        super(inputStream, outputStream);
        this.battleLogic = battleLogic;
        this.playerAction = playerAction;
        this.enemy = enemy;
        this.battleTimeLine = battleTimeLine;
    }

    @Override
    public void run() {
        final TrainerAction enemyAction = enemy.getEnemyActionClient(playerAction, outputStream, inputStream);
        battleLogic.battleTurn(playerAction, enemyAction, battleTimeLine);
    }
}
