package com.delgiudice.pokemonbattlefx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;

public class BattleController {

    @FXML
    private TextArea battleText, battleTextFull;
    @FXML
    private GridPane optionGrid;
    @FXML
    private Button fightButton, bagButton, pokemonButton, runButton;
    @FXML
    private VBox allyPokemonInfo, enemyPokemonInfo;
    @FXML
    private ImageView allyPokemonSprite, enemyPokemonSprite;
    @FXML
    private Label enemyNameLabel, enemyLvLabel, allyNameLabel, allyHpLabel, allyLvLabel;
    @FXML
    private ProgressBar enemyHpBar, allyHpBar;

    public void initialize() {

    }

    public Timeline getBattleTextAnimation(String text, boolean full) {
        final List<KeyFrame> characterList = new LinkedList<>();
        for (int i=0; i<=text.length(); i++) {
            int finalI = i;
            final KeyFrame kf;
            if (full)
                kf = new KeyFrame(Duration.millis(50*i), e -> battleTextFull.setText(text.substring(0, finalI)));
            else
                kf = new KeyFrame(Duration.millis(50*i), e -> battleText.setText(text.substring(0, finalI)));
            characterList.add(kf);
        }

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(characterList);
        return timeline;
    }

    public Timeline getAllyInfoAnimation() {
        allyPokemonInfo.setLayoutX(1281);
        allyPokemonInfo.setVisible(true);
        final List<KeyFrame> keyFrameList = new LinkedList<>();

        for (int i=1; i<=323; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> allyPokemonInfo.setLayoutX(1281-finalI));
            keyFrameList.add(kf);
        }

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public Timeline getEnemyInfoAnimation() {
        enemyPokemonInfo.setLayoutX(-312);
        enemyPokemonInfo.setVisible(true);
        final List<KeyFrame> keyFrameList = new LinkedList<>();

        for (int i=1; i<=336; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> enemyPokemonInfo.setLayoutX(-312+finalI));
            keyFrameList.add(kf);
        }

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public void setEnemyInformation(Pokemon pokemon) {
        enemyNameLabel.setText(pokemon.getName());
        enemyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        enemyHpBar.setProgress((double) pokemon.getHp() / (100 * pokemon.getStats("Max HP")));
    }
}