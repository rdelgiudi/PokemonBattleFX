package com.delgiudice.pokemonbattlefx.teambuilder;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.move.MoveEnum;
import com.delgiudice.pokemonbattlefx.move.MoveTemplate;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public class AddPokemonController {
    @FXML
    private Label pokemonNameLabel, itemLabel, abilityLabel, abilityDescriptionLabel, firstTypeLabel, secondTypeLabel;
    @FXML
    private Button backButton, addPlayerButton, addEnemyButton, closeMoveInfoButton;
    @FXML
    private ImageView pokemonPortrait;
    @FXML
    private HBox typeBox, baseStatBox, ivBox, totalStatBox, firstMoveBox, secondMoveBox, thirdMoveBox, fourthMoveBox;
    @FXML
    private VBox moveBox, statTab, moveInfoBox;
    @FXML
    private Spinner<Integer> pokemonLevel;
    @FXML
    private ChoiceBox<String> natureChoiceBox;

    private Pokemon currentPokemon;
    private Pane previousPane;
    private List<Pokemon> playerParty, enemyParty;
    private TeamBuilderController previousController;
    private boolean editModeAlly;
    private int editModeIndex = -1;

    public void initialize() {
        setStatisticsListeners();
    }

    public void setAddData(Pokemon pokemon, List<Pokemon> playerParty, List<Pokemon> enemyParty,
                           TeamBuilderController previousController, Pane pane) {
        currentPokemon = new Pokemon(pokemon);
        previousPane = pane;
        this.playerParty = playerParty;
        this.enemyParty = enemyParty;
        this.previousController = previousController;
        addPlayerButton.setDisable(playerParty.size() >= 6);
        addEnemyButton.setDisable(enemyParty.size() >= 6);
        moveInfoBox.setVisible(false);
        closeMoveInfoButton.setVisible(false);
    }

    public void enterAddMode() {
        editModeIndex = -1;
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
            MoveTemplate moveTemplate = MoveTemplate.getMove(MoveEnum.findByName(moveField.getText()));
            if (moveTemplate != null) {
                Move move = new Move(MoveTemplate.getMove(MoveEnum.findByName(moveField.getText())));
                currentPokemon.getMoveList().add(move);
            }
        }
    }

    @FXML
    public void returnToBuilder() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setTitle("Pokemon Battle FX");
        backButton.getScene().setRoot(previousPane);
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
        abilityDescriptionLabel.setText(currentPokemon.getAbility().getDescription());

        setPokemonTypes();
        //setStatisticsListeners();
        refreshPokemonStatistics();
        //setNatureChoiceBox();
        pokemonPortrait.setImage(currentPokemon.getSpecie().getFrontSprite());
        setPokemonMoves();
    }

    private void setPokemonTypes() {
        String styleString = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: ";
        firstTypeLabel.setText(currentPokemon.getType()[0].getTypeEnum().toString());
        Color firstTypeColor = currentPokemon.getType()[0].getTypeEnum().getTypeColor();
        String colorHex = toHexString(firstTypeColor);
        firstTypeLabel.setStyle(styleString + colorHex);
        if (currentPokemon.getType()[1].getTypeEnum() != Enums.Types.NO_TYPE) {
            if (!typeBox.getChildren().contains(secondTypeLabel))
                typeBox.getChildren().add(secondTypeLabel);
            secondTypeLabel.setText(currentPokemon.getType()[1].getTypeEnum().toString());
            Color secondTypeColor = currentPokemon.getType()[1].getTypeEnum().getTypeColor();
            String secondColorHex = toHexString(secondTypeColor);
            secondTypeLabel.setStyle(styleString + secondColorHex);
            secondTypeLabel.setVisible(true);
        }
        else {
            secondTypeLabel.setVisible(false);
            typeBox.getChildren().remove(secondTypeLabel);
        }
    }

    private void setNatureChoiceBox() {
        for (Enums.Nature nature : Enums.Nature.values()) {
            natureChoiceBox.getItems().add(nature.toString());
        }

        //natureChoiceBox.setValue(currentPokemon.getNature().toString());

        natureChoiceBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            currentPokemon.setNature(Enums.Nature.valueOf(natureChoiceBox.getSelectionModel().getSelectedIndex()));
            refreshPokemonStatistics();
        });
    }

    private void refreshPokemonStatistics() {

        pokemonLevel.getValueFactory().setValue(currentPokemon.getLevel());

        for (int i=1; i < baseStatBox.getChildren().size(); i++) {
            TextField baseStat = (TextField) baseStatBox.getChildren().get(i);
            TextField totalStat = (TextField) totalStatBox.getChildren().get(i);
            Spinner<Integer> ivStat = (Spinner<Integer>) ivBox.getChildren().get(i);

            if (i > 1) {
               if (currentPokemon.getNature().getStatTab()[i - 2] == 0)
                   totalStat.setStyle("-fx-text-fill: black");
               else if (currentPokemon.getNature().getStatTab()[i - 2] == 1)
                   totalStat.setStyle("-fx-text-fill: red");
               else
                   totalStat.setStyle("-fx-text-fill: blue");
            }

            baseStat.setText(currentPokemon.getSpecie().getBaseStats().get(Enums.StatType.getFromBaseStatId(i-1)).toString());
            totalStat.setText(Integer.toString(currentPokemon.getStats(Enums.StatType.getFromBaseStatId(i-1))));

            ivStat.getValueFactory().setValue(currentPokemon.getIvs()[i-1]);
        }

        natureChoiceBox.setValue(currentPokemon.getNature().toString());
    }

    public void setStatisticsListeners() {
        setNatureChoiceBox();

        SpinnerValueFactory<Integer> levelValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        pokemonLevel.setValueFactory(levelValueFactory);

        pokemonLevel.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            statTab.setDisable(true);
            pokemonLevel.setDisable(true);
            currentPokemon.setLevel(newValue);
            currentPokemon.calculateStats();
            refreshPokemonStatistics();
            statTab.setDisable(false);
            pokemonLevel.setDisable(false);
        });

        closeMoveInfoButton.setOnAction(e -> {
            moveInfoBox.setVisible(false);
            closeMoveInfoButton.setVisible(false);
        });

        for (int i=1; i < baseStatBox.getChildren().size(); i++) {
            Spinner<Integer> ivStat = (Spinner<Integer>) ivBox.getChildren().get(i);

            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 31, 0);
            ivStat.setValueFactory(valueFactory);

            int finalI = i;
            ivStat.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                statTab.setDisable(true);
                pokemonLevel.setDisable(true);
                currentPokemon.getIvs()[finalI - 1] = newValue;
                currentPokemon.calculateStats();
                refreshPokemonStatistics();
                statTab.setDisable(false);
                pokemonLevel.setDisable(false);
            });
        }

        for (int i=0; i < moveBox.getChildren().size(); i++) {
            HBox hBox = (HBox) moveBox.getChildren().get(i);
            TextField moveField = (TextField) hBox.getChildren().get(0);
            Label moveOkLabel = (Label) hBox.getChildren().get(1);
            Button moveInfoButton = (Button) hBox.getChildren().get(2);

            moveField.textProperty().addListener((observable, oldValue, newValue) -> {

                MoveTemplate moveTemplate = MoveTemplate.getMove(MoveEnum.findByName(newValue));

                if (moveTemplate != null ||
                        Objects.equals(newValue, "")) {
                    moveOkLabel.setText("ok");
                    moveInfoButton.setDisable(Objects.equals(newValue, ""));
                    moveOkLabel.setTextFill(Color.GREEN);
                } else {
                    moveOkLabel.setText("not found");
                    moveInfoButton.setDisable(true);
                    moveOkLabel.setTextFill(Color.RED);
                }

                addPlayerButton.setDisable(!(checkOkPlayer() && (editModeAlly || editModeIndex == -1)));
                addEnemyButton.setDisable(!(checkEnemyOk() && (!editModeAlly || editModeIndex == -1)));
            });
        }
    }

    private void prepareMoveInfo(MoveTemplate move) {
        String styleString = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: ";
        HBox generalInfoBox = (HBox) moveInfoBox.getChildren().get(0);
        Label typeLabel = (Label) generalInfoBox.getChildren().get(0);
        Label nameLabel = (Label) generalInfoBox.getChildren().get(1);
        Label ppLabel = (Label) generalInfoBox.getChildren().get(3);

        Color moveTypeColor = move.getType().getTypeEnum().getTypeColor();
        String colorHex = toHexString(moveTypeColor);
        typeLabel.setStyle(styleString + colorHex);
        typeLabel.setText(move.getType().getTypeEnum().toString());

        nameLabel.setText(move.getName().toString());

        ppLabel.setText(String.format("%2d", move.getMaxpp()));

        HBox otherInfoBox = (HBox) moveInfoBox.getChildren().get(1);
        Label categoryLabel = (Label) otherInfoBox.getChildren().get(2);
        Label powerLabel = (Label) otherInfoBox.getChildren().get(4);
        Label accuracyLabel = (Label) otherInfoBox.getChildren().get(6);

        categoryLabel.setText(move.getSubtype().toString());

        if (move.getPower() > 0)
            powerLabel.setText(Integer.toString(move.getPower()));
        else
            powerLabel.setText("-");

        if (move.getAccuracy() > 0)
            accuracyLabel.setText(Integer.toString(move.getAccuracy()));
        else
            accuracyLabel.setText("-");

        Label descriptionLabel = (Label) moveInfoBox.getChildren().get(2);
        descriptionLabel.setText(move.getMoveDescription());
    }

    private boolean checkMovesOk() {
        boolean anyMove = false;
        for (int i=0; i < moveBox.getChildren().size(); i++) {
            HBox hBox = (HBox) moveBox.getChildren().get(i);
            Label moveOkLabel = (Label) hBox.getChildren().get(1);
            TextField moveField = (TextField) hBox.getChildren().get(0);
            if (!Objects.equals(moveOkLabel.getText(), "ok"))
                return false;
            if (!Objects.equals(moveField.getText(), ""))
                anyMove = true;
        }
        return anyMove;
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

    private void setPokemonMoves() {
        for (int i=0; i < moveBox.getChildren().size(); i++) {
            HBox hBox = (HBox) moveBox.getChildren().get(i);
            TextField moveField = (TextField) hBox.getChildren().get(0);
            Label moveOkLabel = (Label) hBox.getChildren().get(1);
            Button moveInfoButton = (Button) hBox.getChildren().get(2);
            moveOkLabel.setTextFill(Color.GREEN);
            if (i >= currentPokemon.getMoveList().size()) {
                moveField.setText("");
            }
            else {
                moveField.setText(currentPokemon.getMoveList(i).getName().toString());
            }

            moveInfoButton.setOnMouseClicked(e -> {
                prepareMoveInfo(MoveTemplate.getMove(MoveEnum.findByName(moveField.getText())));
                moveInfoBox.setVisible(true);
                closeMoveInfoButton.setVisible(true);
            });
        }
    }
}
