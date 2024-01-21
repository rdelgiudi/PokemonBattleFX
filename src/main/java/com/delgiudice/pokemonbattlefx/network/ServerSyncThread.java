package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.BattleLogic;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.application.Platform;
import javafx.util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Serverside thread that swaps information about the game state to confirm the games are in sync.
 */
public class ServerSyncThread extends SyncThread {
    Runnable runLater;

    private List<Pokemon> playerTeam;
    private HashMap<Enums.BattlefieldCondition, Integer> playerTeamConditions;
    private HashMap<Enums.Spikes, Integer> playerTeamSpikes;
    private List<Pokemon> enemyTeam;
    private HashMap<Enums.BattlefieldCondition, Integer> enemyTeamConditions;
    private HashMap<Enums.Spikes, Integer> enemyTeamSpikes;
    private Pair<Enums.WeatherEffect, Integer> weatherEffect;

    private BattleLogic battleLogic;

    public ServerSyncThread(DataInputStream inputStream, DataOutputStream outputStream, Runnable runLater,
                            List<Pokemon> playerTeam, HashMap<Enums.BattlefieldCondition, Integer> playerTeamConditions,
                            HashMap<Enums.Spikes, Integer> playerTeamSpikes, List<Pokemon> enemyTeam,
                            HashMap<Enums.BattlefieldCondition, Integer> enemyTeamConditions,
                            HashMap<Enums.Spikes, Integer> enemyTeamSpikes,
                            Pair<Enums.WeatherEffect, Integer> weatherEffect, BattleLogic battleLogic) {

        super(inputStream, outputStream);
        this.runLater = runLater;
        this.playerTeam = playerTeam;
        this.playerTeamConditions = playerTeamConditions;
        this.playerTeamSpikes = playerTeamSpikes;
        this.enemyTeam = enemyTeam;
        this.enemyTeamConditions = enemyTeamConditions;
        this.enemyTeamSpikes = enemyTeamSpikes;
        this.weatherEffect = weatherEffect;
        this.battleLogic = battleLogic;
    }

    @Override
    public void run() {
        String gameState = generateGameState(playerTeam, playerTeamConditions, playerTeamSpikes, enemyTeam,
                enemyTeamConditions, enemyTeamSpikes, weatherEffect);

        try {
            if (inputStream.readUTF().equals(gameState)) {
                outputStream.writeUTF(gameState);
                outputStream.flush();
                Platform.runLater(runLater);
            }
            else {
                Platform.runLater(() -> battleLogic.endOnlineGameDisconnected(true));
                throw new IllegalStateException("Online game not synchronized");
            }

        } catch (IOException e) {
            Platform.runLater(() -> battleLogic.endOnlineGameDisconnected(false));
            throw new RuntimeException(e);
        }
    }
}
