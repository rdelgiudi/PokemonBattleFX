package com.delgiudice.pokemonbattlefx;

import javafx.animation.Timeline;

public class BattleLogic {

    private BattleController controller;

    private Player player;
    private NpcTrainer enemy;

    boolean inBattle;

    public BattleLogic(BattleController controller) {
        this.controller = controller;
        inBattle = true;
        setup();
        initBattleLoop();
    }

    private void setup() {
        PokemonSpecie.setPokemonList();
        Move.setMoveList();

        // For testing purposes only, delete later
        Pokemon allyPokemon = new Pokemon(PokemonSpecie.getPokemonList().get("Charmander"), 5,
                Move.getMoveList().get("Scratch"), Move.getMoveList().get("Growl"), Move.getMoveList().get("Ember"));
        player = new Player("Red",  allyPokemon);

        Pokemon enemyPokemon = new Pokemon(PokemonSpecie.getPokemonList().get("Rattata"), 3, Move.getMoveList().get("Tackle"),
                Move.getMoveList().get("Scratch"));

        enemy = new NpcTrainer("Joey", Enums.TrainerTypes.YOUNGSTER ,enemyPokemon);
    }

    private void initBattleLoop() {
        Timeline battleTextIntro = controller.getBattleTextAnimation(String.format("%s %s%nwants to battle!",
                enemy.getTrainerType().toString(), enemy.getName()), true);

        Timeline enemyPokemonIntro = controller.getBattleTextAnimation(String.format("%s %s%nsends out %s!",
                enemy.getTrainerType().toString(), enemy.getName(), enemy.getParty(0).getName()), true);

        battleTextIntro.setOnFinished(e -> enemyPokemonIntro.play());

        controller.setEnemyInformation(enemy.getParty(0));

        Timeline enemyInfoAnimation = controller.getEnemyInfoAnimation();

        enemyPokemonIntro.setOnFinished(e -> enemyInfoAnimation.play());

        battleTextIntro.play();

        //while(inBattle) {
        //
        //}
    }

}
