package com.delgiudice.pokemonbattlefx;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class AddPokemonController {
    @FXML
    private Label pokemonNameLabel, itemLabel, abilityLabel, abilityDescriptionLabel;
    @FXML
    private Button backButton, addPlayerButton, addEnemyButton, closeMoveInfoButton;
    @FXML
    private ImageView pokemonPortrait;
    @FXML
    private HBox typeBox, baseStatBox, ivBox, totalStatBox, firstMoveBox, secondMoveBox, thirdMoveBox, fourthMoveBox;
    @FXML
    private VBox moveBox;
    @FXML
    private Spinner<Integer> pokemonLevel;
    @FXML
    private ChoiceBox<String> natureChoiceBox;

    private Pokemon currentPokemon;
    private Scene previousScene;
    private List<Pokemon> playerParty, enemyParty;
    private TeamBuilderController previousController;
    private boolean editModeAlly;
    private int editModeIndex = -1;

    public void initialize() {
    }

    public void setAddData(Pokemon pokemon, List<Pokemon> playerParty, List<Pokemon> enemyParty, Scene scene,
                           TeamBuilderController previousController) {
        currentPokemon = new Pokemon(pokemon);
        previousScene = scene;
        this.playerParty = playerParty;
        this.enemyParty = enemyParty;
        this.previousController = previousController;
        addPlayerButton.setDisable(playerParty.size() >= 6);
        addEnemyButton.setDisable(enemyParty.size() >= 6);
    }

    public void enterAddMode() {
        refreshStats();
    }

    public void enterEditMode(boolean ally, int index) {
        refreshStats();
        editModeAlly = ally;
        editModeIndex = index;
        addPlayerButton.setDisable(!ally);
        addEnemyButton.setDisable(ally);
    }

    private void updatePokemonMoveList() {
        currentPokemon.getMoveList().clear();

        for (int i=0; i < moveBox.getChildren().size(); i++) {
            HBox hBox = (HBox) moveBox.getChildren().get(i);
            TextField moveField = (TextField) hBox.getChildren().get(0);
            if (Objects.equals(moveField.getText(), ""))
                continue;

            Move move = new Move(MoveTemplate.getMove(MoveEnum.findByName(moveField.getText())));
            currentPokemon.getMoveList().add(move);
        }
    }

    @FXML
    public void returnToBuilder() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(previousScene);
    }

    @FXML
    public void addPlayerPokemon() {
        updatePokemonMoveList();
        if (editModeIndex == -1)
            playerParty.add(currentPokemon);
        else {
            playerParty.remove(editModeIndex);
            playerParty.add(editModeIndex, currentPokemon);
        }
        previousController.refreshParties();
        returnToBuilder();
    }

    @FXML
    public void addEnemyPokemon() {
        updatePokemonMoveList();
        if (editModeIndex == -1)
            enemyParty.add(currentPokemon);
        else {
            enemyParty.remove(editModeIndex);
            enemyParty.add(editModeIndex, currentPokemon);
        }
        previousController.refreshParties();
        returnToBuilder();
    }

    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }

    private void refreshStats() {
        pokemonNameLabel.setText(currentPokemon.getName());
        itemLabel.setText("None");
        abilityLabel.setText(currentPokemon.getAbility().toString());
        abilityDescriptionLabel.setText("No description");

        setPokemonTypes(currentPokemon);

        setPokemonStatistics(currentPokemon);

        setNatureChoiceBox(currentPokemon);

        pokemonPortrait.setImage(currentPokemon.getSpecie().getFrontSprite());

        setPokemonMoves(currentPokemon);
    }

    private void setPokemonTypes(Pokemon pokemon) {
        String styleString = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: ";
        Label firstType = (Label) typeBox.getChildren().get(0);
        firstType.setText(pokemon.getType()[0].getTypeEnum().toString());
        Color firstTypeColor = pokemon.getType()[0].getTypeEnum().getTypeColor();
        String colorHex = toHexString(firstTypeColor);
        firstType.setStyle(styleString + colorHex);
        Label secondType = (Label) typeBox.getChildren().get(1);
        if (pokemon.getType()[1].getTypeEnum() != Enums.Types.NO_TYPE) {
            secondType.setText(pokemon.getType()[1].getTypeEnum().toString());
            Color secondTypeColor = pokemon.getType()[1].getTypeEnum().getTypeColor();
            String secondColorHex = toHexString(secondTypeColor);
            secondType.setStyle(styleString + secondColorHex);
            secondType.setVisible(true);
        }
        else {
            secondType.setVisible(false);
        }
    }

    private void setNatureChoiceBox(Pokemon pokemon) {
        for (Enums.Nature nature : Enums.Nature.values()) {
            natureChoiceBox.getItems().add(nature.toString());
        }

        natureChoiceBox.setValue(pokemon.getNature().toString());

        natureChoiceBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            pokemon.setNature(Enums.Nature.valueOf(natureChoiceBox.getSelectionModel().getSelectedIndex()));
            setPokemonStatistics(pokemon);
        });
    }

    private void setPokemonStatistics(Pokemon pokemon) {
        SpinnerValueFactory<Integer> levelValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, currentPokemon.getLevel());
        pokemonLevel.setValueFactory(levelValueFactory);

        pokemonLevel.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            pokemon.setLevel(newValue);
            pokemon.calculateStats();
            setPokemonStatistics(pokemon);
        });

        for (int i=1; i < baseStatBox.getChildren().size(); i++) {
            TextField baseStat = (TextField) baseStatBox.getChildren().get(i);
            Spinner<Integer> ivStat = (Spinner<Integer>) ivBox.getChildren().get(i);
            TextField totalStat = (TextField) totalStatBox.getChildren().get(i);

            baseStat.setText(pokemon.getSpecie().getBaseStats().get(Enums.StatType.getFromBaseStatId(i-1)).toString());
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 31, pokemon.getIvs()[i-1]);
            ivStat.setValueFactory(valueFactory);
            totalStat.setText(Integer.toString(pokemon.getStats(Enums.StatType.getFromBaseStatId(i-1))));

            int finalI = i;
            ivStat.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                pokemon.getIvs()[finalI - 1] = newValue;
                pokemon.calculateStats();
                setPokemonStatistics(pokemon);
            });
        }
    }

    private boolean checkMovesOk() {
        for (int i=0; i < moveBox.getChildren().size(); i++) {
            HBox hBox = (HBox) moveBox.getChildren().get(i);
            Label moveOkLabel = (Label) hBox.getChildren().get(1);
            if (!Objects.equals(moveOkLabel.getText(), "ok"))
                return false;
        }
        return true;
    }

    private boolean checkOkPlayer() {
        if (!checkMovesOk())
            return false;
        if (playerParty.size() >= 6)
            return false;
        return true;
    }

    private boolean checkEnemyOk() {
        if (!checkMovesOk())
            return false;
        if (enemyParty.size() >= 6)
            return false;
        return true;
    }

    private void setPokemonMoves(Pokemon pokemon) {
        for (int i=0; i < moveBox.getChildren().size(); i++) {
            HBox hBox = (HBox) moveBox.getChildren().get(i);
            TextField moveField = (TextField) hBox.getChildren().get(0);
            Label moveOkLabel = (Label) hBox.getChildren().get(1);
            moveOkLabel.setTextFill(Color.GREEN);
            if (i >= pokemon.getMoveList().size()) {
                moveField.setText(" ");
            }
            else {
                moveField.setText(pokemon.getMoveList(i).getName().toString());
            }

            moveField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (MoveEnum.findByName(newValue) != null ||
                        Objects.equals(newValue, "")) {
                    moveOkLabel.setText("ok");
                    moveOkLabel.setTextFill(Color.GREEN);
                }
                else {
                    moveOkLabel.setText("not found");
                    moveOkLabel.setTextFill(Color.RED);
                }
                addPlayerButton.setDisable(!(checkOkPlayer() && editModeAlly));
                addEnemyButton.setDisable(!(checkEnemyOk() && !editModeAlly));
            });
        }
    }
}
