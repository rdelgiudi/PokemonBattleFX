package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class SwapPokemonController {

    private Scene battleScene;
    private BattleLogic battleLogic;

    List<Pokemon> party;

    @FXML
    HBox currentPokemonBox;
    @FXML
    VBox pokemonBox;

    public void initVariables(Scene battleScene, BattleLogic logic, List<Pokemon> party) {
        this.battleScene = battleScene;
        battleLogic = logic;
        this.party = party;
        initPokemonInfo();
    }

    private void initPokemonInfo() {

        int currentAllyPokemon = battleLogic.getCurrentAllyPokemon();

        setPokemonInfo(currentPokemonBox, currentAllyPokemon, true);

        int index = 0;
        int partySize = party.size();

        for (int i = 0; i < 6; i++) {
            if (i == currentAllyPokemon)
                continue;

            if (i == partySize) {
                break;
            }
            HBox box = (HBox) pokemonBox.getChildren().get(index);
            setPokemonInfo(box, i, false);
            index++;
        }
    }

    private void setHpBar(int hp, int maxhp, ProgressBar hpBar, Label hpLabel) {
        double currentHpPercentage = (double) hp / maxhp;
        if (currentHpPercentage < 0.03 && hp != 0)
            currentHpPercentage = 0.03;

        hpBar.setProgress(currentHpPercentage);
        hpLabel.setText(String.format("%3d/%-3d", hp, maxhp));

        if (currentHpPercentage > 0.56)
            hpBar.setStyle("-fx-accent: green");
        else if (currentHpPercentage > 0.21)
            hpBar.setStyle("-fx-accent: yellow");
        else
            hpBar.setStyle("-fx-accent: red");
    }

    private void setPokemonInfo(HBox pokemonBox, int index, boolean current) {
        Pokemon pokemon = party.get(index);

        ImageView icon = (ImageView) pokemonBox.getChildren().get(0);
        icon.setImage(pokemon.getSpecie().getFrontSprite());

        VBox infoBox = (VBox) pokemonBox.getChildren().get(1);

        Label pokemonName = (Label) infoBox.getChildren().get(0);
        Label pokemonLevel = (Label) infoBox.getChildren().get(1);

        pokemonName.setText(pokemon.getName());
        pokemonLevel.setText("Lv. " + pokemon.getLevel());

        VBox hpBox;

        if (current)
            hpBox = (VBox) infoBox.getChildren().get(2);
        else
            hpBox = (VBox) pokemonBox.getChildren().get(2);

        HBox hpBarBox = (HBox) hpBox.getChildren().get(0);
        ProgressBar hpBar = (ProgressBar) hpBarBox.getChildren().get(1);
        Label hpLabel = (Label) hpBox.getChildren().get(1);
        setHpBar(pokemon.getHp(), pokemon.getMaxHP(), hpBar, hpLabel);

        Label statusLabel = (Label) hpBox.getChildren().get(2);

        BattleController.setStatusStyle(pokemon, statusLabel);

    }

    private void initListener(HBox pokemonBox) {

    }
}
