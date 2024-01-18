package com.delgiudice.pokemonbattlefx.network;


import com.delgiudice.pokemonbattlefx.battle.SwapPokemonController;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import javafx.animation.Timeline;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import static com.delgiudice.pokemonbattlefx.trainer.OnlineTrainer.sendAction;

public class SwitchDataSend extends SyncThread{

    private TrainerAction playerAction;
    private SwapPokemonController swapPokemonController;
    private List<Timeline> battleTimeLine;
    private int index;

    public SwitchDataSend(DataInputStream inputStream, DataOutputStream outputStream, TrainerAction playerAction,
                             SwapPokemonController swapPokemonController, List<Timeline> battleTimeLine, int index) {
        super(inputStream, outputStream);
        this.playerAction = playerAction;
        this.swapPokemonController = swapPokemonController;
        this.battleTimeLine = battleTimeLine;
        this.index = index;
    }

    @Override
    public void run() {
        sendAction(outputStream, playerAction);
        try {
            if (inputStream.readUTF().equals("OK"))
                swapPokemonController.finalizeSwitchOut(battleTimeLine, index, playerAction);
            else {
                System.out.println("Thread " + this.getClass().getName() + "failed!");
            }
        } catch (IOException e) {
            swapPokemonController.finalizeSwitchOut(battleTimeLine, index, playerAction);
            throw new RuntimeException(e);
        }
    }
}
