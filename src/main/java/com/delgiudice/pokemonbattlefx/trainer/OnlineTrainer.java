package com.delgiudice.pokemonbattlefx.trainer;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.TrainerAction;
import com.delgiudice.pokemonbattlefx.pokemon.Ability;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class OnlineTrainer extends Trainer{

    public OnlineTrainer(String name, Pokemon pokemon) {
        super(name, pokemon);
    }

    @Override
    public TrainerAction getEnemyAction(Pokemon enemyPokemon) {
        return null;
    }

    public static void sendAction(DataOutputStream outputStream, TrainerAction trainerAction) {
        StringBuilder builder = new StringBuilder();
        String separator = "--";
        builder.append("HELLO").append(separator);
        builder.append(trainerAction.actionType.ordinal()).append(separator);
        builder.append(trainerAction.actionName).append(separator);
        builder.append(trainerAction.target).append(separator);
        builder.append(trainerAction.ppRestoreTarget).append(separator);
        builder.append("GOODBYE");
        try {
            outputStream.writeUTF(builder.toString());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TrainerAction parseAction(String message) {
        String[] info = message.split("--");
        if (!info[0].equals("HELLO"))
            return null;
        Enums.ActionTypes actionType = Enums.ActionTypes.values()[Integer.parseInt(info[1])];
        String actionName = info[2];
        int target = Integer.parseInt(info[3]);
        int ppRestoreTarget = Integer.parseInt(info[4]);
        if (info[5].equals("GOODBYE"))
            return new TrainerAction(actionType, actionName, target, ppRestoreTarget);
        else
            return null;
    }
    @Override
    public TrainerAction getEnemyActionClient(TrainerAction trainerAction, DataOutputStream outputStream, DataInputStream inputStream) {
        sendAction(outputStream, trainerAction);
        String enemyAction;
        try {
            enemyAction = inputStream.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parseAction(enemyAction);
    }

    @Override
    public TrainerAction getEnemyActionServer(TrainerAction trainerAction, DataOutputStream outputStream, DataInputStream inputStream) {
        String enemyAction;
        try {
            enemyAction = inputStream.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendAction(outputStream, trainerAction);
        return parseAction(enemyAction);
    }

    @Override
    public TrainerAction getEnemySwitchOut(List<Pokemon> enemyParty) {
        return null;
    }

    public TrainerAction getEnemySwitchOut(DataInputStream inputStream, DataOutputStream outputStream) {
        String enemyAction;
        try {
            enemyAction = inputStream.readUTF();
            outputStream.writeUTF("OK");
            outputStream.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parseAction(enemyAction);
    }
}
