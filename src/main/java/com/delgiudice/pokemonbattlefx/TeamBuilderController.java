package com.delgiudice.pokemonbattlefx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TeamBuilderController {

    @FXML
    private HBox playerPartyBox, enemyPartyBox;
    @FXML
    private GridPane pokemonGrid;
    @FXML
    private Button playerSettingsButton, enemySettingsButton, startBattleButton;

    List<Pokemon> playerParty = new ArrayList<>(), enemyParty = new ArrayList<>();

    String playerName = "Red", enemyName = "Joey";

    Enums.TrainerTypes trainerType = Enums.TrainerTypes.YOUNGSTER;

    public TeamBuilderController() {
        Pokemon.generatePokemonExamples();
    }

    public void initialize() {
        populatePokemonGrid();
    }

    public void populatePokemonGrid() {
        TreeMap<PokemonEnum, PokemonSpecie> sortedPokemon = new TreeMap<>(PokemonSpecie.getPokemonMap());
        int i = 0, j = 0;

        for (PokemonSpecie pokemon : sortedPokemon.values()) {
            Button pokemonButton = new Button();
            pokemonButton.setText(pokemon.getName().toString());
            pokemonGrid.add(pokemonButton, i, j);

            pokemonButton.setPrefHeight(100);
            pokemonButton.setPrefWidth(125);

            i++;
            if (i > 8) {
                i = 0;
                j++;
            }

            pokemonButton.setOnAction(e -> {
                FXMLLoader fxmlLoader = new FXMLLoader(BattleApplication.class.getResource("addpokemon-view.fxml"));
                Stage stage = (Stage) startBattleButton.getScene().getWindow();
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load(), 1280, 720);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Pokemon pokemonExample = Pokemon.getPokemonExamples().get(pokemon.getName());
                AddPokemonController controller = fxmlLoader.getController();
                controller.setData(pokemonExample, startBattleButton.getScene());
                stage.setTitle("Add Pokemon!");
                stage.setScene(scene);
            });
        }
    }

    @FXML
    public void startBattle() throws IOException {
        Player player = new Player(playerName, playerParty.get(0));
        NpcTrainer trainer = new NpcTrainer(enemyName, trainerType, enemyParty.get(0));

        FXMLLoader fxmlLoader = new FXMLLoader(BattleApplication.class.getResource("battle-view.fxml"));
        Stage stage = (Stage) startBattleButton.getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Battle!");
        stage.setScene(scene);

        BattleController controller = fxmlLoader.getController();
        BattleLogic logic = new BattleLogic(controller, player, trainer);

        stage.show();
    }
}
