package com.delgiudice.pokemonbattlefx;

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
    private Label pokemonNameLabel, levelLabel, itemLabel, abilityLabel, abilityDescriptionLabel, natureLabel, hpLabel;
    @FXML
    private Button backButton, nextButton, previousButton;
    @FXML
    private ImageView pokemonPortrait;
    @FXML
    private HBox typeBox, baseStatBox, ivBox, totalStatBox, firstMoveBox, secondMoveBox, thirdMoveBox, fourthMoveBox;
    @FXML
    private VBox moveBox;

    private List<Pokemon> party;
    private Scene previousScene;
    private int currentIndex;

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
        currentIndex = index;
        Pokemon pokemon = party.get(index);

        nextButton.setDisable(index + 1 == party.size());
        previousButton.setDisable(index == 0);

        pokemonNameLabel.setText(pokemon.getName());
        levelLabel.setText(String.format("Lv. %3d", pokemon.getLevel()));
        itemLabel.setText("None");
        abilityLabel.setText(pokemon.getAbility().toString());
        abilityDescriptionLabel.setText("No description");

        setPokemonTypes(pokemon);
        hpLabel.setText(String.format("%3d / %-3d", pokemon.getHp(), pokemon.getMaxHP()));

        setPokemonStatistics(pokemon);

        natureLabel.setText(pokemon.getNature().toString());
        setMoves(pokemon);

        pokemonPortrait.setImage(pokemon.getSpecie().getFrontSprite());

        setListeners();
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

            baseStat.setText(pokemon.getSpecie().getBaseStats().get(Enums.StatType.fromBaseStatId(i-1)).toString());
            ivStat.setText(Integer.toString(pokemon.getIvs()[i-1]));
            totalStat.setText(Integer.toString(pokemon.getStats(Enums.StatType.fromBaseStatId(i-1))));
        }
    }

    private void setMoves(Pokemon pokemon) {
        String styleString = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: ";

        for (int i=0; i < moveBox.getChildren().size(); i++) {
            HBox moveHBox = (HBox) moveBox.getChildren().get(i);
            Label moveType = (Label) moveHBox.getChildren().get(0);
            Color moveTypeColor = pokemon.getMoveList(i).getType().getTypeEnum().getTypeColor();
            String colorHex = toHexString(moveTypeColor);
            moveType.setStyle(styleString + colorHex);
            moveType.setText(pokemon.getMoveList(i).getType().getTypeEnum().toString());

            Label moveName = (Label) moveHBox.getChildren().get(1);
            moveName.setText(pokemon.getMoveList(i).getName().toString());

            Label ppLabel = (Label) moveHBox.getChildren().get(3);
            ppLabel.setText(String.format("%2d / %-2d", pokemon.getMoveList(i).getPp(), pokemon.getMoveList(i).getMaxpp()));
        }
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
    }
}
