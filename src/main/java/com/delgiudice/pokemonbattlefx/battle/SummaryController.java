package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class SummaryController {

    @FXML
    private Label pokemonNameLabel, levelLabel, itemLabel, abilityLabel, abilityDescriptionLabel, natureLabel, hpLabel,
                    statusLabel;
    @FXML
    private Button backButton, nextButton, previousButton, closeMoveInfoButton;
    @FXML
    private ImageView pokemonPortrait;
    @FXML
    private HBox typeBox, baseStatBox, ivBox, totalStatBox, firstMoveBox, secondMoveBox, thirdMoveBox, fourthMoveBox;
    @FXML
    private VBox moveBox, moveInfoBox;

    private List<Pokemon> party;
    private Scene previousScene;
    private int currentIndex;

    public void initialize() {
        setListeners();
    }

    public void setParty(List<Pokemon> party) {
        this.party = party;
    }

    public void setPreviousScene(Scene previousScene) {
        this.previousScene = previousScene;
    }

    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }

    public void displayPokemonStat(int index) {

        moveInfoBox.setVisible(false);
        closeMoveInfoButton.setVisible(false);

        currentIndex = index;
        Pokemon pokemon = party.get(index);

        nextButton.setDisable(index + 1 == party.size());
        previousButton.setDisable(index == 0);

        pokemonNameLabel.setText(pokemon.getName());
        levelLabel.setText(String.format("Lv. %3d", pokemon.getLevel()));
        itemLabel.setText("None");
        abilityLabel.setText(pokemon.getAbility().toString());
        abilityDescriptionLabel.setText(pokemon.getAbility().getDescription());

        setPokemonTypes(pokemon);
        hpLabel.setText(String.format("%3d / %-3d", pokemon.getHp(), pokemon.getMaxHP()));

        setPokemonStatistics(pokemon);

        natureLabel.setText(pokemon.getNature().toString());
        setMoves(pokemon);

        pokemonPortrait.setImage(pokemon.getSpecie().getFrontSprite());

        BattleController.setStatusStyle(pokemon, statusLabel);
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

    private void setPokemonStatistics(Pokemon pokemon) {
        for (int i=1; i < baseStatBox.getChildren().size(); i++) {
            TextField baseStat = (TextField) baseStatBox.getChildren().get(i);
            TextField ivStat = (TextField) ivBox.getChildren().get(i);
            TextField totalStat = (TextField) totalStatBox.getChildren().get(i);

            if (i > 1) {
                if (pokemon.getNature().getStatTab()[i - 2] == 0)
                    totalStat.setStyle("-fx-text-fill: black");
                else if (pokemon.getNature().getStatTab()[i - 2] == 1)
                    totalStat.setStyle("-fx-text-fill: red");
                else
                    totalStat.setStyle("-fx-text-fill: blue");
            }

            baseStat.setText(pokemon.getSpecie().getBaseStats().get(Enums.StatType.getFromBaseStatId(i-1)).toString());
            ivStat.setText(Integer.toString(pokemon.getIvs()[i-1]));
            totalStat.setText(Integer.toString(pokemon.getStats(Enums.StatType.getFromBaseStatId(i-1))));
        }
    }

    private void setMoves(Pokemon pokemon) {
        String styleString = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: ";

        for (int i=0; i < moveBox.getChildren().size(); i++) {
            HBox moveHBox = (HBox) moveBox.getChildren().get(i);
            Label moveType = (Label) moveHBox.getChildren().get(0);
            Label moveName = (Label) moveHBox.getChildren().get(1);
            Label ppLabel = (Label) moveHBox.getChildren().get(3);
            if (i < pokemon.getMoveList().size()) {
                moveType.setVisible(true);
                Color moveTypeColor = pokemon.getMoveList(i).getType().getTypeEnum().getTypeColor();
                String colorHex = toHexString(moveTypeColor);
                moveType.setStyle(styleString + colorHex);
                moveType.setText(pokemon.getMoveList(i).getType().getTypeEnum().toString());

                moveName.setText(pokemon.getMoveList(i).getName().toString());

                ppLabel.setText(String.format("%2d / %-2d", pokemon.getMoveList(i).getPp(), pokemon.getMoveList(i).getMaxpp()));

                int finalI = i;
                moveHBox.setOnMouseClicked(e -> {
                    prepareMoveInfo(pokemon.getMoveList(finalI));
                    moveInfoBox.setVisible(true);
                    closeMoveInfoButton.setVisible(true);
                });

                moveHBox.setOnMouseEntered(e -> moveHBox.setStyle("-fx-background-color: lightgray;" +
                        "-fx-border-color: black"));
                moveHBox.setOnMouseExited(e -> moveHBox.setStyle("-fx-background-color: transparent;" +
                        "-fx-border-color: black"));
            }
            else {
                moveType.setVisible(false);
                moveName.setText("---");
                ppLabel.setText("-- / --");
                moveHBox.setOnMouseClicked(null);
                moveHBox.setOnMouseEntered(null);
                moveHBox.setOnMouseExited(null);
            }
        }
    }

    private void prepareMoveInfo(Move move) {
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

        ppLabel.setText(String.format("%2d / %-2d", move.getPp(), move.getMaxpp()));

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

    private void setListeners() {
        backButton.setOnAction(e -> {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(previousScene);
            //stage.show();
        });

        nextButton.setOnAction(e -> {
            currentIndex++;
            displayPokemonStat(currentIndex);
        });

        previousButton.setOnAction(e -> {
            currentIndex--;
            displayPokemonStat(currentIndex);
        });

        closeMoveInfoButton.setOnAction(e -> {
            moveInfoBox.setVisible(false);
            closeMoveInfoButton.setVisible(false);
        });

        moveInfoBox.setOnMouseClicked(e -> {
            moveInfoBox.setVisible(false);
            closeMoveInfoButton.setVisible(false);
        });

    }
}
