package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.battle.BattleLogic;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.trainer.Trainer;
import javafx.animation.Timeline;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.List;

// Thread that initiates information swap between players about the move they are about to take
public class MoveSync extends SyncThread{
    private BattleLogic battleLogic;
    private TrainerAction playerAction;
    private Trainer enemy;
    List<Timeline> battleTimeLine;

    public MoveSync(DataInputStream inputStream, DataOutputStream outputStream, BattleLogic battleLogic,
                    TrainerAction playerAction, Trainer enemy, List<Timeline> battleTimeLine) {
        super(inputStream, outputStream);
        this.battleLogic = battleLogic;
        this.playerAction = playerAction;
        this.enemy = enemy;
        this.battleTimeLine = battleTimeLine;
    }

    @Override
    public void run() {
        final TrainerAction enemyAction = enemy.getEnemyAction(playerAction);
        battleLogic.battleTurn(playerAction, enemyAction, battleTimeLine);
    }
}
