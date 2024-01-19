package com.delgiudice.pokemonbattlefx.trainer;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.BattleLogic;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.pokemon.Ability;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

// Trainer variant responsible for handling multiplayer matches
// This class handles swapping actions between players
public class OnlineTrainer extends Trainer{

    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final BattleLogic battleLogic;

    public OnlineTrainer(String name, Pokemon pokemon, DataInputStream inputStream,
                         DataOutputStream outputStream, BattleLogic battleLogic) {
        super(name, pokemon);
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.battleLogic = battleLogic;
    }

    @Override
    public TrainerAction getEnemyAction(Pokemon enemyPokemon) {
        return null;
    }

    // Transforms player action into a message and sends it to the other player
    public static void sendAction(DataOutputStream outputStream, TrainerAction trainerAction) {
        StringBuilder builder = new StringBuilder();
        String separator = "--";
        builder.append("HELLO_ACTION").append(separator);
        builder.append(trainerAction.actionType.ordinal()).append(separator);
        builder.append(trainerAction.actionName).append(separator);
        builder.append(trainerAction.target).append(separator);
        builder.append(trainerAction.ppRestoreTarget).append(separator);
        builder.append("GOODBYE_ACTION");
        try {
            outputStream.writeUTF(builder.toString());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Parses action received from the other player
    public static TrainerAction parseAction(String message) {
        String[] info = message.split("--");
        if (!info[0].equals("HELLO_ACTION"))
            return null;
        Enums.ActionTypes actionType = Enums.ActionTypes.values()[Integer.parseInt(info[1])];
        String actionName = info[2];
        int target = Integer.parseInt(info[3]);
        int ppRestoreTarget = Integer.parseInt(info[4]);
        if (info[5].equals("GOODBYE_ACTION"))
            return new TrainerAction(actionType, actionName, target, ppRestoreTarget);
        else
            return null;
    }

    // Handles flow of communication of the client
    private TrainerAction getEnemyActionClient(TrainerAction trainerAction) {
        sendAction(outputStream, trainerAction);
        String enemyAction;
        try {
            enemyAction = inputStream.readUTF();
        } catch (IOException e) {
            Platform.runLater(() -> battleLogic.endOnlineGameDisconnected(false));
            throw new RuntimeException(e);
        }
        return parseAction(enemyAction);
    }

    // Handles flow of communication of the server
    private TrainerAction getEnemyActionServer(TrainerAction trainerAction) {
        String enemyAction;
        try {
            enemyAction = inputStream.readUTF();
        } catch (IOException e) {
            Platform.runLater(() -> battleLogic.endOnlineGameDisconnected(false));
            throw new RuntimeException(e);
        }
        sendAction(outputStream, trainerAction);
        return parseAction(enemyAction);
    }

    // Method executed to swap their actions between them
    @Override
    public TrainerAction getEnemyAction(TrainerAction trainerAction) {
        if (battleLogic.getGameMode() == Enums.GameMode.SERVER)
            return getEnemyActionServer(trainerAction);
        else
            return getEnemyActionClient(trainerAction);
    }

    @Override
    public TrainerAction getEnemySwitchOut(List<Pokemon> enemyParty) {
        return null;
    }

    // Reads information about the next Pokemon that the enemy decides to send
    public TrainerAction getEnemySwitchOut() {
        String enemyAction;
        TrainerAction parsedEnemyAction;
        try {
            enemyAction = inputStream.readUTF();
            parsedEnemyAction = parseAction(enemyAction);
            if (parsedEnemyAction != null) {
                outputStream.writeUTF("OK");
                outputStream.flush();
                return parsedEnemyAction;
            }

        } catch (IOException e) {
            Platform.runLater(() -> battleLogic.endOnlineGameDisconnected(false));
            throw new RuntimeException(e);
        }

        return null;
    }
}
