package com.delgiudice.pokemonbattlefx;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddPokemonController {
    @FXML
    private Label pokemonNameLabel, itemLabel, abilityLabel, abilityDescriptionLabel, natureLabel, hpLabel;
    @FXML
    private Button backButton, nextButton, previousButton, closeMoveInfoButton;
    @FXML
    private ImageView pokemonPortrait;
    @FXML
    private HBox typeBox, baseStatBox, ivBox, totalStatBox, firstMoveBox, secondMoveBox, thirdMoveBox, fourthMoveBox;
    @FXML
    private VBox moveBox, moveInfoBox;
    @FXML
    private TextField pokemonLevel;

    private Pokemon currentPokemon;
    private Scene previousScene;

    public void initialize() {
    }

    public void setData(Pokemon pokemon, Scene scene) {
        currentPokemon = pokemon;
        previousScene = scene;
        refreshStats();
    }

    @FXML
    public void returnToBuilder() {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(previousScene);
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

        moveInfoBox.setVisible(false);
        closeMoveInfoButton.setVisible(false);


        pokemonNameLabel.setText(currentPokemon.getName());
        pokemonLevel.setText(Integer.toString(currentPokemon.getLevel()));
        itemLabel.setText("None");
        abilityLabel.setText(currentPokemon.getAbility().toString());
        abilityDescriptionLabel.setText("No description");

        setPokemonTypes(currentPokemon);
        hpLabel.setText(String.format("%3d / %-3d", currentPokemon.getHp(), currentPokemon.getMaxHP()));

        setPokemonStatistics(currentPokemon);

        natureLabel.setText(currentPokemon.getNature().toString());

        pokemonPortrait.setImage(currentPokemon.getSpecie().getFrontSprite());
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
}
