package com.delgiudice.pokemonbattlefx.network;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.List;

// Base class of a thread for comparing game state and waiting - checks if game state is identical between both players
abstract public class SyncThread extends Thread{

    protected static String ENTITY_SEPARATOR = "--";
    protected static String FIELD_SEPARATOR = "__";

    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;

    public SyncThread(DataInputStream inputStream, DataOutputStream outputStream) {
        super();
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    // Generates state of a Pokemon
    protected static String generatePokemonState(Pokemon pokemon) {
        StringBuilder builder = new StringBuilder();
        builder.append(pokemon.getSpecie().getName().ordinal()).append(FIELD_SEPARATOR);
        builder.append(pokemon.getHp()).append(FIELD_SEPARATOR);

        for (Move move : pokemon.getMoveList()) {
            builder.append(move.getPp()).append(FIELD_SEPARATOR);
        }

        builder.append(pokemon.getStatus().ordinal()).append(FIELD_SEPARATOR);

        return builder.toString();
    }

    // Generates state of Pokemon index 0 - currently sent out Pokemon
    protected static String generateCurrentPokemonState(Pokemon pokemon) {
        StringBuilder builder = new StringBuilder();
        builder.append(generatePokemonState(pokemon));

        for (Enums.StatType statType : Enums.StatType.values()) {
            if (pokemon.getStatModifiers().containsKey(statType))
                builder.append(pokemon.getStatModifiers().get(statType)).append(FIELD_SEPARATOR);
        }

        for (Enums.SubStatus subStatus : pokemon.getSubStatuses())
            builder.append(subStatus.ordinal()).append(FIELD_SEPARATOR);

        builder.append(pokemon.getConfusionTimer()).append(FIELD_SEPARATOR);
        builder.append(pokemon.getLeechSeedTimer()).append(FIELD_SEPARATOR);
        builder.append(pokemon.getTrappedTimer());

        return builder.toString();
    }

    // Generates state of one team: Pokemon, conditions and ground hazards (spikes)
    protected static String generateTeamGameState(List<Pokemon> team,
                                                  HashMap<Enums.BattlefieldCondition, Integer> teamConditions,
                                                  HashMap<Enums.Spikes, Integer> teamSpikes) {
        StringBuilder builder = new StringBuilder();

        builder.append(generateCurrentPokemonState(team.get(0))).append(ENTITY_SEPARATOR);

        for (int i = 1; i < team.size(); i++) {
            builder.append(generateCurrentPokemonState(team.get(i))).append(ENTITY_SEPARATOR);
        }

        for (Enums.BattlefieldCondition battlefieldCondition : Enums.BattlefieldCondition.values()) {
            if (teamConditions.containsKey(battlefieldCondition))
                builder.append(battlefieldCondition.ordinal()).append(":")
                        .append(teamConditions.get(battlefieldCondition)).append(FIELD_SEPARATOR);

        }

        for (Enums.Spikes spikes : Enums.Spikes.values()) {
            if (teamSpikes.containsKey(spikes))
                builder.append(spikes.ordinal()).append(":").append(teamSpikes.get(spikes)).append(FIELD_SEPARATOR);
        }

        int len = builder.length();
        if (builder.lastIndexOf(FIELD_SEPARATOR) == len - 2)
            builder.replace(len - 2, len, ENTITY_SEPARATOR);

        return builder.toString();
    }

    // Generates complete state of the game
    protected static String generateGameState(
            List<Pokemon> firstTeam, HashMap<Enums.BattlefieldCondition, Integer> firstTeamConditions,
            HashMap<Enums.Spikes, Integer> firstTeamSpikes,
            List<Pokemon> secondTeam, HashMap<Enums.BattlefieldCondition, Integer> secondTeamConditions,
            HashMap<Enums.Spikes, Integer> secondTeamSpikes, Pair<Enums.WeatherEffect, Integer> weatherEffect) {

        StringBuilder builder = new StringBuilder();
        builder.append("GAME_STATE_START").append(ENTITY_SEPARATOR);
        builder.append(weatherEffect.getKey()).append(":").append(weatherEffect.getValue()).append(ENTITY_SEPARATOR);
        builder.append(generateTeamGameState(firstTeam, firstTeamConditions, firstTeamSpikes));
        builder.append(generateTeamGameState(secondTeam, secondTeamConditions, secondTeamSpikes));
        builder.append("GAME_STATE_END");

        return builder.toString();
    }
}
