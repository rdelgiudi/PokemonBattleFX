package com.delgiudice.pokemonbattlefx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

public class TeamBuilderController {

    @FXML
    private HBox playerPartyBox, enemyPartyBox;
    @FXML
    private GridPane pokemonGrid;
    @FXML
    private Button playerSettingsButton, enemySettingsButton, startBattleButton;
    @FXML
    private ScrollPane scrollPane;

    List<Pokemon> playerParty = new ArrayList<>(), enemyParty = new ArrayList<>();

    ArrayList<Pokemon> sortedPokemon = new ArrayList<>();

    String playerName = "Red", enemyName = "Joey";

    Enums.TrainerTypes trainerType = Enums.TrainerTypes.YOUNGSTER;

    FXMLLoader addPokemonLoader;
    Scene addPokemonScene;

    private int FONT_SIZE = 13, POKEMON_BUTTON_WIDTH = 125, POKEMON_BUTTON_HEIGHT = 100, POKEMON_PANE_SIZE = 450;
    private int PARTY_BUTTON_WIDTH = 100, PARTY_BUTTON_HEIGHT = 100;

    public TeamBuilderController() {
        Pokemon.generatePokemonExamples();
    }

    public void initialize() {

        for (Map.Entry<PokemonEnum, Pokemon> entry : Pokemon.getPokemonExamples().entrySet()) {
            sortedPokemon.add(entry.getValue());
        }

        sortedPokemon.sort(new Comparator<Pokemon>() {
            @Override
            public int compare(Pokemon o1, Pokemon o2) {
                return o1.getPokedexNumber() - o2.getPokedexNumber();
            }
        });

        populatePokemonGrid();
        refreshParties();
        setButtonEffect(playerSettingsButton);
        setButtonEffect(enemySettingsButton);
        setButtonEffect(startBattleButton);
        preparePartyButtonEffects();
        preparePartyButtons(playerPartyBox, playerParty, true);
        preparePartyButtons(enemyPartyBox, enemyParty, false);

        addPokemonLoader = new FXMLLoader(BattleApplication.class.getResource("addpokemon-view.fxml"));
        try {
            addPokemonScene = new Scene(addPokemonLoader.load(), 1280, 720);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setUiResizeListener() {
        Scene scene = startBattleButton.getScene();

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            FONT_SIZE = (int)Math.round(scene.getWidth() / 98.46);
            POKEMON_BUTTON_WIDTH = (int)Math.round(scene.getWidth() / 10.24);
            //POKEMON_BUTTON_HEIGHT = (int)Math.round(scene.getWidth() / 12.8);
            PARTY_BUTTON_WIDTH = (int)Math.round(scene.getWidth() / 12.8);
            //POKEMON_PANE_SIZE = (int)Math.round(scene.getWidth() / 2.84);
            refreshUi();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            POKEMON_BUTTON_HEIGHT = (int)Math.round(scene.getHeight() / 7.2);
            POKEMON_PANE_SIZE = (int)Math.round(scene.getHeight() / 1.6);
            PARTY_BUTTON_HEIGHT = (int)Math.round(scene.getHeight() / 7.2);
            refreshUi();
        });
    }

    private void refreshUi() {
        populatePokemonGrid();
        refreshParties();
        scrollPane.setPrefHeight(POKEMON_PANE_SIZE);
    }

    public void preparePartyButtons(HBox partyBox, List<Pokemon> party, boolean player) {
        for (int i=0; i < partyBox.getChildren().size(); i++) {
            Button button = (Button) partyBox.getChildren().get(i);
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editPokemon = new MenuItem("Edit");
            MenuItem deletePokemon = new MenuItem("Delete");
            MenuItem movePokemon = new MenuItem("Move");
            MenuItem cancel = new MenuItem("Cancel");

            int finalI = i;
            editPokemon.setOnAction(e -> {
                editPokemon(finalI, party);
            });
            deletePokemon.setOnAction(e -> {
                party.remove(finalI);
                refreshParties();
            });
            cancel.setOnAction(e -> {
                contextMenu.hide();
            });
            movePokemon.setOnAction(e -> {
                HBox oppositeBox = player ? enemyPartyBox : playerPartyBox;
                oppositeBox.setDisable(true);
                Timeline timeline = setButtonsFlashingEffect(partyBox, player);
                timeline.play();
                for (int j=0; j < party.size(); j++) {
                    Button button1 = (Button) partyBox.getChildren().get(j);
                    int finalJ = j;
                    button1.setContextMenu(null);
                    button1.setOnMouseClicked(event -> {
                        Pokemon pokemon = party.get(finalI);
                        party.remove(finalI);
                        party.add(finalJ, pokemon);
                        preparePartyButtons(partyBox, party, player);
                        refreshParties();
                        timeline.stop();
                        oppositeBox.setDisable(false);
                    });
                }
            });
            contextMenu.getItems().addAll(editPokemon, deletePokemon, movePokemon, cancel);
            button.setContextMenu(contextMenu);
        }
    }

    private Timeline setButtonsFlashingEffect(HBox partyBox, boolean player) {
        Timeline timeline;
        String color1 = player ? "#87CEEB": "lightcoral";
        String color2 = player ? "powderblue": "lightpink";
        String styleString = "-fx-background-color: ";

        final KeyFrame kf = new KeyFrame(Duration.millis(200), e -> {
            for (int i=0; i < partyBox.getChildren().size(); i++) {
                Button button = (Button) partyBox.getChildren().get(i);
                button.setStyle(styleString + color1);
            }
        });

        final KeyFrame kf2 = new KeyFrame(Duration.millis(400), e -> {
            for (int i=0; i < partyBox.getChildren().size(); i++) {
                Button button = (Button) partyBox.getChildren().get(i);
                button.setStyle(styleString + color2);
            }
        });

        timeline = new Timeline(kf, kf2);

        timeline.setOnFinished(e -> {
            for (int i=0; i < partyBox.getChildren().size(); i++) {
                Button button = (Button) partyBox.getChildren().get(i);
                button.setStyle(styleString + color1);
            }
        });

        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    public void preparePartyButtonEffects() {

        for (int i=0; i < playerPartyBox.getChildren().size(); i++) {
            Button button = (Button) playerPartyBox.getChildren().get(i);
            setButtonEffect(button);
        }

        for (int i=0; i < enemyPartyBox.getChildren().size(); i++) {
            Button button = (Button) enemyPartyBox.getChildren().get(i);
            setButtonEffectEnemy(button);
        }
    }

    public void populatePokemonGrid() {

        pokemonGrid.getChildren().clear();

        int i = 0, j = 0;

        for (Pokemon pokemon : sortedPokemon) {
            Button pokemonButton = new Button();
            pokemonButton.setText(pokemon.getOriginalName().toString());
            pokemonButton.setFont(Font.font("Monospaced", FONT_SIZE));
            pokemonGrid.add(pokemonButton, i, j);

            pokemonButton.setPrefHeight(POKEMON_BUTTON_HEIGHT);
            pokemonButton.setPrefWidth(POKEMON_BUTTON_WIDTH);

            i++;
            if (i > 8) {
                i = 0;
                j++;
            }

            pokemonButton.setOnAction(e -> {
                Stage stage = (Stage) startBattleButton.getScene().getWindow();
                //Pokemon pokemonExample = Pokemon.getPokemonExamples().get(pokemon.getOriginalName());
                AddPokemonController controller = addPokemonLoader.getController();
                controller.setAddData(pokemon, playerParty, enemyParty, startBattleButton.getScene(), this);
                controller.enterAddMode();
                stage.setTitle("Add Pokemon!");
                stage.setScene(addPokemonScene);
            });
        }
    }

    private void editPokemon(int index, List<Pokemon> party) {
        Stage stage = (Stage) startBattleButton.getScene().getWindow();
        AddPokemonController controller = addPokemonLoader.getController();
        controller.setAddData(party.get(index), playerParty, enemyParty, startBattleButton.getScene(), this);
        stage.setTitle("Edit Pokemon!");
        controller.enterEditMode(playerParty.equals(party), index);
        stage.setScene(addPokemonScene);
    }

    public void refreshParties() {
        playerPartyBox.setPrefSize((PARTY_BUTTON_WIDTH +5) * 6, PARTY_BUTTON_HEIGHT);
        enemyPartyBox.setPrefSize((PARTY_BUTTON_WIDTH +5) * 6, PARTY_BUTTON_HEIGHT);
        for (int i=0; i < playerPartyBox.getChildren().size(); i++) {
            Button playerPokemonButton = (Button) playerPartyBox.getChildren().get(i);
            Button enemyPokemonButton = (Button) enemyPartyBox.getChildren().get(i);
            playerPokemonButton.setFont(Font.font("Monospaced", FONT_SIZE));
            enemyPokemonButton.setFont(Font.font("Monospaced", FONT_SIZE));
            playerPokemonButton.setPrefSize(PARTY_BUTTON_WIDTH, PARTY_BUTTON_HEIGHT);
            enemyPokemonButton.setPrefSize(PARTY_BUTTON_WIDTH, PARTY_BUTTON_HEIGHT);
            if (i < playerParty.size()) {
                playerPokemonButton.setText(playerParty.get(i).getName());
                playerPokemonButton.setDisable(false);

                playerPokemonButton.setOnMouseClicked(e -> {
                    playerPokemonButton.getContextMenu().show(playerPokemonButton, e.getScreenX(), e.getScreenY());
                });
            }
            else {
                playerPokemonButton.setText("");
                playerPokemonButton.setDisable(true);
            }

            if (i < enemyParty.size()) {
                enemyPokemonButton.setText(enemyParty.get(i).getName());
                enemyPokemonButton.setDisable(false);

                enemyPokemonButton.setOnMouseClicked(e -> {
                    enemyPokemonButton.getContextMenu().show(enemyPokemonButton, e.getScreenX(), e.getScreenY());
                });
            }

            else {
                enemyPokemonButton.setText("");
                enemyPokemonButton.setDisable(true);
            }
        }
        startBattleButton.setDisable(playerParty.size() < 1 || enemyParty.size() < 1);
    }

    public void setButtonEffect(Button button) {
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #87CEEB"));
        button.setOnMouseReleased(e -> button.setStyle("-fx-background-color: #87CEEB"));

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: powderblue"));
        button.setOnMousePressed(e -> button.setStyle("-fx-background-color: steelblue"));
    }

    public void setButtonEffectEnemy(Button button) {
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: lightcoral"));
        button.setOnMouseReleased(e -> button.setStyle("-fx-background-color: lightcoral"));

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: lightpink"));
        button.setOnMousePressed(e -> button.setStyle("-fx-background-color: indianred"));
    }

    @FXML
    public void startBattle() throws IOException {
        Player player = new Player(playerName, playerParty.get(0));
        NpcTrainer enemy = new NpcTrainer(enemyName, trainerType, enemyParty.get(0));

        for (int i=1; i < playerParty.size(); i++)
            player.addPokemon(playerParty.get(i));
        for (int i=1; i < enemyParty.size(); i++)
            enemy.addPokemon(enemyParty.get(i));

        FXMLLoader fxmlLoader = new FXMLLoader(BattleApplication.class.getResource("battle-view.fxml"));
        Scene teamBuilderScene = startBattleButton.getScene();
        Stage stage = (Stage) startBattleButton.getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Battle!");
        stage.setScene(scene);

        BattleController controller = fxmlLoader.getController();
        BattleLogic logic = new BattleLogic(controller, player, enemy, teamBuilderScene);
    }
}
