package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.BattleApplication;
import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SwapPokemonController {

    private static final String faintedStyle = "-fx-border-color: black; -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 15; " +
            "-fx-background-color: linear-gradient(to left, lightcoral, azure)";
    private static final String regularStyle = "-fx-border-color: black; -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 15; " +
            "-fx-background-color: linear-gradient(to left, lightskyblue, azure)";
    private static final String hoverStyle = "-fx-border-color: black; -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 15; " +
            "-fx-background-color: azure";

    private static final String optionsRegularStyle = "-fx-background-color: skyblue; -fx-border-color: black; -fx-border-width: 5";
    private static final String optionsHoverStyle = "-fx-background-color: lightblue; -fx-border-color: black; -fx-border-width: 5";
    private static final String optionsPressedStyle = "-fx-background-color: lightskyblue; -fx-border-color: black; -fx-border-width: 5";

    private Pane battlePane;
    private BattleLogic battleLogic;
    private BattleController battleController;

    private final FXMLLoader summaryLoader;
    private final Pane summaryPane;

    private List<Pokemon> party;

    private Enums.SwitchContext switchContext;
    private List<Pokemon> turnPokemon;
    private Move secondMove;

    @FXML
    private Pane mainPane;
    @FXML
    private HBox currentPokemonBox;
    @FXML
    private VBox pokemonBox, switchOptionsBox;
    @FXML
    private Label infoLabel, selectedPokemonLabel;
    @FXML
    private Button cancelButton, switchOutButton, summaryButton, closeButton;

    public SwapPokemonController() {
        summaryLoader = new FXMLLoader(BattleApplication.class.getResource("summary-view.fxml"));
        try {
            summaryPane = summaryLoader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        //BattleApplication.letterbox(summaryScene, pane);
    }

    public void initialize() {
        initListeners();
    }

    public void initVariables(Pane battlePane, BattleLogic logic, BattleController controller, List<Pokemon> party,
                              boolean force, Enums.SwitchContext switchContext, List<Pokemon> turnPokemon, Move secondMove) {
        this.battlePane = battlePane;
        battleLogic = logic;
        this.party = party;
        battleController = controller;
        initPokemonInfo(force);
        this.switchContext = switchContext;
        this.turnPokemon = turnPokemon;
        this.secondMove = secondMove;

        cancelButton.setOnAction(e -> {
            Scene scene = cancelButton.getScene();
            scene.setRoot(battlePane);
        });
    }

    public Timeline getInfoText(String text) {
        KeyFrame kf = new KeyFrame(Duration.millis(1), e -> infoLabel.setText(text));

        return new Timeline(kf);
    }

    private void initPokemonInfo(boolean force) {

        int currentAllyPokemon = 0;

        cancelButton.setDisable(party.get(currentAllyPokemon).getHp() == 0 || force);
        switchOptionsBox.setVisible(false);

        setPokemonInfo(currentPokemonBox, currentAllyPokemon, true);

        int partySize = party.size();

        for (int i = 1; i < 6; i++) {

            HBox box = (HBox) pokemonBox.getChildren().get(i-1);

            if (i >= partySize) {
                disableBox(box);
                continue;
            }

            setPokemonInfo(box, i, false);
        }
    }

    private void disableBox(HBox pokemonBox) {

        ImageView icon = (ImageView) pokemonBox.getChildren().get(0);
        VBox infoBox = (VBox) pokemonBox.getChildren().get(1);
        VBox hpBox = (VBox) pokemonBox.getChildren().get(2);

        icon.setVisible(false);
        infoBox.setVisible(false);
        hpBox.setVisible(false);

        String style = "-fx-border-color: black; -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 15; " +
                "-fx-background-color:  linear-gradient(to left, dodgerblue, lightgray)";

        pokemonBox.setStyle(style);

        pokemonBox.setOnMouseEntered(null);
        pokemonBox.setOnMouseExited(null);
        pokemonBox.setOnMouseClicked(null);
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

        if (pokemon.getHp() == 0)
            pokemonBox.setStyle(faintedStyle);
        else
            pokemonBox.setStyle(regularStyle);

        initBoxElementListener(pokemonBox, pokemon, index);
        icon.setVisible(true);
        infoBox.setVisible(true);
        hpBox.setVisible(true);
    }

    private void initListeners() {
        switchOutButton.setOnMouseEntered(e -> {
            switchOutButton.setStyle(optionsHoverStyle);
        });
        switchOutButton.setOnMouseExited(e -> {
            switchOutButton.setStyle(optionsRegularStyle);
        });
        switchOutButton.setOnMousePressed(e -> {
            switchOutButton.setStyle(optionsPressedStyle);
        });
        switchOutButton.setOnMouseReleased(e -> {
            switchOutButton.setStyle(optionsRegularStyle);
        });

        summaryButton.setOnMouseEntered(e -> {
            summaryButton.setStyle(optionsHoverStyle);
        });
        summaryButton.setOnMouseExited(e -> {
            summaryButton.setStyle(optionsRegularStyle);
        });
        summaryButton.setOnMousePressed(e -> {
            summaryButton.setStyle(optionsPressedStyle);
        });
        summaryButton.setOnMouseReleased(e -> {
            summaryButton.setStyle(optionsRegularStyle);
        });

        closeButton.setOnMouseEntered(e -> {
            closeButton.setStyle(optionsHoverStyle);
        });
        closeButton.setOnMouseExited(e -> {
            closeButton.setStyle(optionsRegularStyle);
        });
        closeButton.setOnMousePressed(e -> {
            closeButton.setStyle(optionsPressedStyle);
        });
        closeButton.setOnMouseReleased(e -> {
            closeButton.setStyle(optionsRegularStyle);
        });

        cancelButton.setOnMouseEntered(e -> {
            cancelButton.setStyle(optionsHoverStyle);
        });
        cancelButton.setOnMouseExited(e -> {
            cancelButton.setStyle(optionsRegularStyle);
        });
        cancelButton.setOnMousePressed(e -> {
            cancelButton.setStyle(optionsPressedStyle);
        });
        cancelButton.setOnMouseReleased(e -> {
            cancelButton.setStyle(optionsRegularStyle);
        });
    }

    private void initBoxElementListener(HBox pokemonBox, Pokemon pokemon, int index) {
        pokemonBox.setOnMouseClicked(e -> {

            // Adding parent layout location applies a correction for the elements in a VBox, since without correction
            // it doesn't account for the offset of it
            double correctionX = pokemonBox.getParent().getClass() == VBox.class ? pokemonBox.getParent().getLayoutX() : 0;
            double correctionY = pokemonBox.getParent().getClass() == VBox.class ? pokemonBox.getParent().getLayoutY() : 0;

            double mouseLayoutX = e.getX() + pokemonBox.getLayoutX() + correctionX;
            double mouseLayoutY = e.getY() + pokemonBox.getLayoutY() + correctionY;

            double boxWidth = switchOptionsBox.getWidth();
            double boxHeight = switchOptionsBox.getHeight();

            if (mouseLayoutX + boxWidth > mainPane.getWidth())
                switchOptionsBox.setLayoutX(mouseLayoutX - boxWidth);
            else
                switchOptionsBox.setLayoutX(mouseLayoutX);

            if (mouseLayoutY + boxHeight > mainPane.getHeight())
                switchOptionsBox.setLayoutY(mouseLayoutY - boxHeight);
            else
                switchOptionsBox.setLayoutY(mouseLayoutY);

            initSwapMenuListener(pokemonBox ,pokemon, index);
        });

        pokemonBox.setOnMouseEntered(e -> pokemonBox.setStyle(hoverStyle));
        pokemonBox.setOnMouseExited(e -> {
            if (pokemon.getHp() == 0)
                pokemonBox.setStyle(faintedStyle);
            else
                pokemonBox.setStyle(regularStyle);
        });
    }

    private void initSwapMenuListener(HBox pokemonBox, Pokemon pokemon, int index) {

        int currentAllyPokemon = 0;
        boolean allyFainted = party.get(currentAllyPokemon).getHp() == 0;

        selectedPokemonLabel.setText(String.format("%d: %-12s", index+1, pokemon.getName()));

        String setColorIntro = "-fx-border-radius: 10; -fx-background-radius: 15; -fx-border-width: 5; " +
                "-fx-border-color: ";
        String halfAndHalf = "linear-gradient(from 0%% 50%% to 100%% 50%%, %s, %s)";
        Color[] buttonBorderColor = new Color[2];
        buttonBorderColor[0] = pokemon.getType()[0].getTypeEnum().getTypeColor();
        if (pokemon.getType()[1].getTypeEnum() == Enums.Types.NO_TYPE) {
            buttonBorderColor[1] = buttonBorderColor[0];
        }
        else {
            buttonBorderColor[1] = pokemon.getType()[1].getTypeEnum().getTypeColor();
        }
        String[] colorHex = new String[2];
        colorHex[0] = BattleController.toHexString(buttonBorderColor[0]);
        colorHex[1] = BattleController.toHexString(buttonBorderColor[1]);
        String colorHexComplete = String.format(halfAndHalf, colorHex[0], colorHex[1]);
        selectedPokemonLabel.setStyle(setColorIntro + colorHexComplete);

        switchOutButton.setOnAction(event -> {
            if (index == currentAllyPokemon) {
                Timeline alreadyInBattleInfo;
                if (!allyFainted)
                    alreadyInBattleInfo = getInfoText(String.format(
                            "%s is already in battle!", pokemon.getBattleName()));
                else
                    alreadyInBattleInfo = getInfoText(String.format(
                            "%s has just fainted!", pokemon.getBattleName()));

                Timeline resetText = getInfoText("Choose a POKÉMON.");
                resetText.setDelay(Duration.seconds(1));
                alreadyInBattleInfo.setOnFinished(actionEvent -> resetText.play());
                alreadyInBattleInfo.play();
                return;
            }

            if (pokemon.getHp() == 0) {
                Timeline faintedInfo = getInfoText(String.format(
                        "%s is fainted! Can't switch!",
                        pokemon.getName()));
                Timeline resetText = getInfoText("Choose a POKÉMON.");
                resetText.setDelay(Duration.seconds(1));
                faintedInfo.setOnFinished(actionEvent -> resetText.play());
                faintedInfo.play();
                return;
            }

            if (party.get(currentAllyPokemon).getTrapMove() != null && !allyFainted
                    && switchContext != Enums.SwitchContext.SWITCH_FIRST_MOVE
                    && switchContext != Enums.SwitchContext.SWITCH_SECOND_MOVE) {
                Timeline trappedInfo = getInfoText(String.format(
                        "%s is trapped! Can't switch!",
                        party.get(currentAllyPokemon).getName()));
                Timeline resetText = getInfoText("Choose a POKÉMON.");
                resetText.setDelay(Duration.seconds(1));
                trappedInfo.setOnFinished(actionEvent -> resetText.play());
                trappedInfo.play();
                return;
            }

            String currentAllyName = party.get(currentAllyPokemon).getBattleName();

            battleController.switchToPlayerChoice(false);

            List<Timeline> battleTimeLine = new ArrayList<>();

            if (!allyFainted) {
                if (switchContext != Enums.SwitchContext.SWITCH_SECOND_MOVE
                        && switchContext != Enums.SwitchContext.SWITCH_FIRST_MOVE) {
                    Timeline allyPokemonReturnText = battleController.getBattleTextAnimation(String.format(
                            "That's enough %s,%ncome back!", currentAllyName), true);
                    allyPokemonReturnText.setDelay(Duration.seconds(0.1));
                    battleTimeLine.add(allyPokemonReturnText);
                }

                Timeline allyPokemonReturn = battleController.getPokemonReturnAnimation(true);
                allyPokemonReturn.setDelay(Duration.seconds(1));
                battleTimeLine.add(allyPokemonReturn);
                battleTimeLine.add(battleController.generatePause(1000));
            }

            battleLogic.switchPokemon(true, index);
            int newCurrentAllyPokemon = 0;

            //controller.setAllyInformation(player.getParty(currentAllyPokemon));
            Timeline allyPokemonIntro = battleController.getBattleTextAnimation(String.format(BattleLogic.POKEMON_SENT_OUT_STRING,
                    party.get(newCurrentAllyPokemon).getBattleName()), true);
            battleTimeLine.add(allyPokemonIntro);

            Timeline updateStatus = battleController.updateStatus(party.get(newCurrentAllyPokemon), true);
            battleTimeLine.add(updateStatus);

            Timeline allyInfoAnimation = battleController.getIntroAnimation(party.get(newCurrentAllyPokemon),
                    party.get(newCurrentAllyPokemon).getHp());
            battleTimeLine.add(allyInfoAnimation);

            Scene scene = pokemonBox.getScene();
            scene.setRoot(battlePane);

            if (switchContext == Enums.SwitchContext.SWITCH_FIRST_MOVE) {
                if (turnPokemon == null || secondMove == null)
                    throw new ValueException("Values of turnPokemon and secondMove expected to be not null in this context");
                battleLogic.applySentOutEffects(battleTimeLine);
                battleLogic.processSecondMove(battleTimeLine, party.get(newCurrentAllyPokemon), turnPokemon.get(1), secondMove);
            }
            else if (switchContext == Enums.SwitchContext.SWITCH_FIRST) {
                battleLogic.applySentOutEffects(battleTimeLine);
                battleLogic.initAnimationQueue(battleTimeLine);
                battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
                        battleLogic.battleTurn(null);
                });
                battleTimeLine.get(0).play();
            }
            else if (switchContext == Enums.SwitchContext.SWITCH_FAINTED || switchContext == Enums.SwitchContext.SWITCH_SECOND_MOVE ||
                    switchContext == Enums.SwitchContext.SWITCH_SECOND) {
                battleLogic.battleTurnEnd(battleTimeLine);
            }
        });

        summaryButton.setOnAction(event -> {
            Scene scene = pokemonBox.getScene();

            SummaryController summaryController = summaryLoader.getController();
            summaryController.setParty(party);
            summaryController.displayPokemonStat(index);
            summaryController.setPreviousPane((Pane) scene.getRoot());

            scene.setRoot(summaryPane);
            switchOptionsBox.setVisible(false);
        });

        closeButton.setOnAction(event -> switchOptionsBox.setVisible(false));

        switchOptionsBox.setVisible(true);
    }
}
