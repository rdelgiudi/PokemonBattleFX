package com.delgiudice.pokemonbattlefx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;

public class BattleController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextArea battleText, battleTextFull;
    @FXML
    private GridPane optionGrid, moveGrid, pokemonGrid;
    @FXML
    private Button fightButton, bagButton, pokemonButton, runButton, firstMoveButton, secondMoveButton, thirdMoveButton,
            fourthMoveButton, backMoveButton, pokemonBackButton;
    @FXML
    private VBox allyPokemonInfo, enemyPokemonInfo;
    @FXML
    private ImageView allyPokemonSprite, enemyPokemonSprite;
    @FXML
    private Label enemyNameLabel, enemyLvLabel, allyNameLabel, allyHpLabel, allyLvLabel, allyStatusLabel,
                    enemyStatusLabel;
    @FXML
    private ProgressBar enemyHpBar, allyHpBar;

    public Button getFightButton() {
        return fightButton;
    }

    public Button getBagButton() {
        return bagButton;
    }

    public Button getPokemonButton() {
        return pokemonButton;
    }

    public Button getRunButton() {
        return runButton;
    }

    public Button getFirstMoveButton() {
        return firstMoveButton;
    }

    public Button getSecondMoveButton() {
        return secondMoveButton;
    }

    public Button getThirdMoveButton() {
        return thirdMoveButton;
    }

    public Button getFourthMoveButton() {
        return fourthMoveButton;
    }

    public Button getBackMoveButton() {
        return backMoveButton;
    }

    public GridPane getMoveGrid() {
        return moveGrid;
    }

    public GridPane getPokemonGrid() {
        return pokemonGrid;
    }

    public Button getPokemonBackButton() {
        return pokemonBackButton;
    }

    public void initialize() {

    }

    public void switchToPlayerChoice(boolean choice) {
        battleTextFull.setVisible(!choice);
        battleText.setVisible(choice);
        optionGrid.setVisible(choice);
    }

    public Timeline getBattleTextAnimation(String text, boolean full) {
        final List<KeyFrame> characterList = new LinkedList<>();
        for (int i = 0; i <= text.length(); i++) {
            int finalI = i;
            final KeyFrame kf;
            if (full)
                kf = new KeyFrame(Duration.millis(50 * i), e -> battleTextFull.setText(text.substring(0, finalI)));
            else
                kf = new KeyFrame(Duration.millis(50 * i), e -> battleText.setText(text.substring(0, finalI)));
            characterList.add(kf);
        }

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(characterList);

        final KeyFrame kf;
        if (full)
            kf = new KeyFrame(Duration.millis(50 * text.length() + 50), e -> battleTextFull.setText(text));
        else
            kf = new KeyFrame(Duration.millis(50 * text.length() + 50), e -> battleText.setText(text));

        timeline.getKeyFrames().add(kf);

        return timeline;
    }

    public Timeline getAllyInfoAnimation(Pokemon pokemon, int hpStatus) {

        double width = allyPokemonSprite.getFitWidth();
        double height = allyPokemonSprite.getFitHeight();

        final List<KeyFrame> keyFrameList = new LinkedList<>();

        final double[] outside = new double[1];

        final KeyFrame configPokemonSprite = new KeyFrame(Duration.ZERO, e -> {
            setAllyInformation(pokemon, hpStatus);

            outside[0] = anchorPane.getWidth();

            allyPokemonSprite.setFitWidth(1);
            allyPokemonSprite.setFitHeight(1);
            allyPokemonSprite.setVisible(true);

            AnchorPane.setRightAnchor(allyPokemonInfo, null);
            allyPokemonInfo.setLayoutX(outside[0]);
            allyPokemonInfo.setVisible(true);
        });

        keyFrameList.add(configPokemonSprite);

        for (int i = 1; i <= 338; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> {
                allyPokemonInfo.setLayoutX(outside[0] - finalI);
                allyPokemonSprite.setFitWidth((width * finalI) / 333);
                allyPokemonSprite.setFitHeight((height * finalI) / 333);
            });
            keyFrameList.add(kf);
        }

        final KeyFrame resetAnchor = new KeyFrame(Duration.millis(339), e -> {
            AnchorPane.setRightAnchor(allyPokemonInfo, 15.0);
        });
        keyFrameList.add(resetAnchor);

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public Timeline getEnemyInfoAnimation(Pokemon pokemon, int hpStatus) {

        double width = enemyPokemonSprite.getFitWidth();
        double height = enemyPokemonSprite.getFitHeight();

        final List<KeyFrame> keyFrameList = new LinkedList<>();

        final KeyFrame configPokemonSprite = new KeyFrame(Duration.ZERO, e -> {

            setEnemyInformation(pokemon, hpStatus);

            enemyPokemonSprite.setFitWidth(1);
            enemyPokemonSprite.setFitHeight(1);
            enemyPokemonSprite.setVisible(true);

            AnchorPane.setLeftAnchor(enemyPokemonInfo, null);
            enemyPokemonInfo.setLayoutX(-312);
            enemyPokemonInfo.setVisible(true);
        });

        keyFrameList.add(configPokemonSprite);

        for (int i = 1; i <= 337; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> {
                enemyPokemonInfo.setLayoutX(-312 + finalI);
                enemyPokemonSprite.setFitWidth((width * finalI) / 336);
                enemyPokemonSprite.setFitHeight((height * finalI) / 336);
            });
            keyFrameList.add(kf);
        }

        final KeyFrame resetAnchor = new KeyFrame(Duration.millis(338), e -> {
           AnchorPane.setLeftAnchor(enemyPokemonInfo, 15.0);
        });

        keyFrameList.add(resetAnchor);
        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public void setEnemyInformation(Pokemon pokemon) {
        enemyNameLabel.setText(pokemon.getName());
        enemyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        enemyPokemonSprite.setImage(pokemon.getSpecie().getFrontSprite());

        setEnemyHpBar(pokemon.getHp(), pokemon.getMaxHP());
    }

    public void setEnemyInformation(Pokemon pokemon, int overrideHp) {
        enemyNameLabel.setText(pokemon.getName());
        enemyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        enemyPokemonSprite.setImage(pokemon.getSpecie().getFrontSprite());

        setEnemyHpBar(overrideHp, pokemon.getMaxHP());
    }

    public void setEnemyHpBar(int hp, int maxhp) {
        double currentHpPercentage = (double) hp / maxhp;
        enemyHpBar.setProgress(currentHpPercentage);

        if (currentHpPercentage > 0.56)
            enemyHpBar.setStyle("-fx-accent: green");
        else if (currentHpPercentage > 0.21)
            enemyHpBar.setStyle("-fx-accent: yellow");
        else
            enemyHpBar.setStyle("-fx-accent: red");
    }

    public Timeline getEnemyHpAnimation(int from, int to, int max) {
        final List<KeyFrame> keyFrameList = new LinkedList<>();

        if (from == to)
            return null;

        boolean descending = from > to;

        for (int i = 1; i <= Math.abs(from - to); i++) {
            final KeyFrame kf;
            final int finalI = i;
            if (descending)
                kf = new KeyFrame(Duration.millis(50 * i), e -> setEnemyHpBar(from - finalI, max));
            else
                kf = new KeyFrame(Duration.millis(50 * i), e -> setEnemyHpBar(from + finalI, max));
            keyFrameList.add(kf);
        }

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public Timeline updateStatus(Pokemon pokemon, boolean ally) {

        final Label statusLabel;
        if (ally)
            statusLabel = allyStatusLabel;
        else
            statusLabel = enemyStatusLabel;

        final String style = "-fx-border-radius: 10; -fx-background-radius: 10";
        final String[] finalStyle = new String[1];

        final KeyFrame kf = new KeyFrame(Duration.millis(1), e-> {

            statusLabel.setTextFill(Color.WHITE);

            switch (pokemon.getStatus()) {
                case NONE -> {
                    statusLabel.setVisible(false);
                }
                case PARALYZED -> {
                    statusLabel.setText("PAR");
                    finalStyle[0] = style + "; -fx-background-color: #B8B818";
                }
                case POISONED -> {
                    statusLabel.setText("PSN");
                    finalStyle[0] = style + "; -fx-background-color: purple";
                }
                case BADLY_POISONED -> {
                    statusLabel.setText("PSN");
                    statusLabel.setTextFill(Color.LIGHTPINK);
                    finalStyle[0] = style + "; -fx-background-color: purple";
                }
                case SLEEPING -> {
                    statusLabel.setText("SLP");
                    finalStyle[0] = style + "; -fx-background-color: gray";
                }
                case BURNED -> {
                    statusLabel.setText("BRN");
                    finalStyle[0] = style + "; -fx-background-color: chocolate";
                }
                case FROZEN -> {
                    statusLabel.setText("FRZ");
                    finalStyle[0] = style + "; -fx-background-color: dodgerblue";
                }
            }
            if (pokemon.getStatus() != Enums.Status.NONE) {
                statusLabel.setStyle(finalStyle[0]);
                statusLabel.setVisible(true);
            }
        });

        return new Timeline(kf);
    }

    public void setAllyInformation(Pokemon pokemon) {
        allyNameLabel.setText(pokemon.getName());
        allyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        allyPokemonSprite.setImage(pokemon.getSpecie().getBackSprite());

        setAllyHpBar(pokemon.getHp(), pokemon.getMaxHP());
    }

    public void setAllyInformation(Pokemon pokemon, int overrideHp) {
        allyNameLabel.setText(pokemon.getName());
        allyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        allyPokemonSprite.setImage(pokemon.getSpecie().getBackSprite());

        setAllyHpBar(overrideHp, pokemon.getMaxHP());
    }

    public void setAllyHpBar(int hp, int maxhp) {
        double currentHpPercentage = (double) hp / maxhp;

        allyHpBar.setProgress(currentHpPercentage);
        allyHpLabel.setText(String.format("%d\t / \t%d", hp, maxhp));

        if (currentHpPercentage > 0.56)
            allyHpBar.setStyle("-fx-accent: green");
        else if (currentHpPercentage > 0.21)
            allyHpBar.setStyle("-fx-accent: yellow");
        else
            allyHpBar.setStyle("-fx-accent: red");
    }

    public Timeline getAllyHpAnimation(int from, int to, int max) {
        final List<KeyFrame> keyFrameList = new LinkedList<>();

        if (from == to || max < to || max < from)
            return null;

        boolean descending = from > to;

        int frameCount = Math.abs(from - to);

        for (int i = 1; i <= frameCount; i++) {
            final KeyFrame kf;
            final int finalI = i;

            double duration = (double) (500 * i) / frameCount;

            if (descending)
                kf = new KeyFrame(Duration.millis(duration), e -> setAllyHpBar(from - finalI, max));
            else
                kf = new KeyFrame(Duration.millis(duration), e -> setAllyHpBar(from + finalI, max));
            keyFrameList.add(kf);
        }

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public Timeline getPokemonFaintedAnimation(boolean ally) {

        final List<KeyFrame> keyFrameList = new LinkedList<>();

        double height;

        if (ally)
            height = allyPokemonSprite.getFitHeight();
        else
            height = enemyPokemonSprite.getFitHeight();


        for (int i = 1; i < height; i++) {
            final int finalI = i;
            final KeyFrame kf;
            if (ally)
                kf = new KeyFrame(Duration.millis(finalI), e -> allyPokemonSprite.setFitHeight(height - finalI));
            else
                kf = new KeyFrame(Duration.millis(finalI), e -> enemyPokemonSprite.setFitHeight(height - finalI));

            keyFrameList.add(kf);
        }

        final KeyFrame cleanUp = new KeyFrame(Duration.millis(height), e -> {
            if (ally) {
                allyPokemonInfo.setVisible(false);
                allyPokemonSprite.setVisible(false);
                allyPokemonSprite.setFitHeight(height);
            } else {
                enemyPokemonInfo.setVisible(false);
                enemyPokemonSprite.setVisible(false);
                enemyPokemonSprite.setFitHeight(height);
            }
        });

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().addAll(keyFrameList);

        timeline.getKeyFrames().add(cleanUp);

        return timeline;

    }

    public void updateAvailableMoves(Pokemon pokemon) {
        List<Move> moveList = pokemon.getMoveList();

        String ppInfo = "%s%nPP: %-2d\t/\t%2d";

        firstMoveButton.setText(String.format(ppInfo, moveList.get(0).getName(), moveList.get(0).getPp(),
                moveList.get(0).getMaxpp()));
        if (moveList.size() > 1)
            secondMoveButton.setText(String.format(ppInfo, moveList.get(1).getName(), moveList.get(1).getPp(),
                    moveList.get(1).getMaxpp()));
        else {
            secondMoveButton.setText("");
            secondMoveButton.setDisable(true);
        }
        if (moveList.size() > 2)
            thirdMoveButton.setText(String.format(ppInfo, moveList.get(2).getName(), moveList.get(2).getPp(),
                    moveList.get(2).getMaxpp()));
        else {
            thirdMoveButton.setText("");
            thirdMoveButton.setDisable(true);
        }
        if (moveList.size() > 3)
            fourthMoveButton.setText(String.format(ppInfo, moveList.get(2).getName(), moveList.get(2).getPp(),
                    moveList.get(2).getMaxpp()));
        else {
            fourthMoveButton.setText("");
            fourthMoveButton.setDisable(true);
        }
    }

    public void updateAvailablePokemon(List<Pokemon> pokemon) {

    }

    public Timeline generatePause(double millis) {
        return new Timeline(new KeyFrame(Duration.millis(millis)));
    }

    public void fightButtonPressed(Pokemon pokemon) {

        updateAvailableMoves(pokemon);

        optionGrid.setVisible(false);
        moveGrid.setVisible(true);
    }

    public void backButtonPressed() {
        optionGrid.setVisible(true);
        moveGrid.setVisible(false);
    }

    public void pokemonButtonPressed(List<Pokemon> party) {
        optionGrid.setVisible(false);
        pokemonGrid.setVisible(true);

        //updateAvailablePokemon(party);
    }
}