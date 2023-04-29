package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
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
    private ImageView allyPokemonSprite, enemyPokemonSprite, textArrowView;
    @FXML
    private Label enemyNameLabel, enemyLvLabel, allyNameLabel, allyHpLabel, allyLvLabel, allyStatusLabel,
                    enemyStatusLabel, allyAbilityInfo, enemyAbilityInfo;
    @FXML
    private ProgressBar enemyHpBar, allyHpBar;

    private final static String FIGHT_BUTTON_PRESSED = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: darkred;";
    private final static String FIGHT_BUTTON_RELEASE = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: firebrick;";
    private final static String FIGHT_BUTTON_HOVER = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: crimson;";

    private final static String POKEMON_BUTTON_PRESSED = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: darkgreen;";
    private final static String POKEMON_BUTTON_RELEASE = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: forestgreen;";
    private final static String POKEMON_BUTTON_HOVER = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: limegreen;";

    private Scene scene;

    public ImageView getAllyPokemonSprite() {
        return allyPokemonSprite;
    }

    public ImageView getEnemyPokemonSprite() {
        return enemyPokemonSprite;
    }

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
        battleText.setMouseTransparent(true);
        battleText.setFocusTraversable(false);
        battleTextFull.setMouseTransparent(true);
        battleTextFull.setFocusTraversable(false);

        setupButtons();
    }

    private void setupButtons() {
        setFightButton();
        setPokemonButton();
    }

    private void setFightButton() {
        fightButton.setOnMousePressed(e -> {
            fightButton.setStyle(FIGHT_BUTTON_PRESSED);
        });

        fightButton.setOnMouseReleased(e -> {
            fightButton.setStyle(FIGHT_BUTTON_RELEASE);
        });

        fightButton.setOnMouseEntered(e -> {
            fightButton.setText(">" + fightButton.getText().substring(1));
            fightButton.setStyle(FIGHT_BUTTON_HOVER);
        });

        fightButton.setOnMouseExited(e -> {
            fightButton.setText(" " + fightButton.getText().substring(1));
            fightButton.setStyle(FIGHT_BUTTON_RELEASE);
        });
    }

    private void setPokemonButton() {
        pokemonButton.setOnMousePressed(e -> {
            pokemonButton.setStyle(POKEMON_BUTTON_PRESSED);
        });

        pokemonButton.setOnMouseReleased(e -> {
            pokemonButton.setStyle(POKEMON_BUTTON_RELEASE);
        });

        pokemonButton.setOnMouseEntered(e -> {
            pokemonButton.setText(">" + pokemonButton.getText().substring(1));
            pokemonButton.setStyle(POKEMON_BUTTON_HOVER);
        });

        pokemonButton.setOnMouseExited(e -> {
            pokemonButton.setText(" " + pokemonButton.getText().substring(1));
            pokemonButton.setStyle(POKEMON_BUTTON_RELEASE);
        });
    }

    public void switchToPlayerChoice(boolean choice) {
        battleTextFull.setVisible(!choice);
        battleText.setVisible(choice);
        optionGrid.setVisible(choice);
    }

    public Timeline wipeText(boolean inTimeline) {
        KeyFrame kf = new KeyFrame(Duration.millis(1), e -> {
            battleText.setText("");
            battleTextFull.setText("");
        });

        Timeline timeline = new Timeline(kf);

        if (!inTimeline)
            timeline.play();

        return timeline;
    }

    public Timeline getBattleTextAnimation(String text, boolean full) {
        final List<KeyFrame> characterList = new ArrayList<>();
        for (int i = 0; i <= text.length(); i++) {
            int finalI = i;
            final KeyFrame kf;
            if (full)
                kf = new KeyFrame(Duration.millis(25 * i), e -> battleTextFull.setText(text.substring(0, finalI)));
            else
                kf = new KeyFrame(Duration.millis(25 * i), e -> battleText.setText(text.substring(0, finalI)));
            characterList.add(kf);
        }

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(characterList);

        final KeyFrame kf;
        if (full)
            kf = new KeyFrame(Duration.millis(25 * text.length() + 25), e -> battleTextFull.setText(text));
        else
            kf = new KeyFrame(Duration.millis(25 * text.length() + 25), e -> battleText.setText(text));

        timeline.getKeyFrames().add(kf);

        return timeline;
    }

    public void battleTextAdvanceByUserInput(Timeline text, Timeline nextText) {

        Scene scene = battleTextFull.getScene();

        text.setOnFinished(e -> {

            final Timeline timeline = getTextArrowAnimation();

            scene.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    textArrowView.setVisible(false);
                    nextText.play();
                    scene.setOnMouseClicked(null);
                    scene.setOnKeyPressed(null);
                    timeline.stop();
                }
            });
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    textArrowView.setVisible(false);
                    nextText.play();
                    scene.setOnMouseClicked(null);
                    scene.setOnKeyPressed(null);
                    timeline.stop();
                }
            });

            textArrowView.setVisible(true);
            timeline.play();
            });
    }

    private Timeline getTextArrowAnimation() {

        List<KeyFrame> keyFrameList = new ArrayList<>();

        for (int i=10; i < 20; i++) {
            double finalI = i;
            KeyFrame kf = new KeyFrame(Duration.millis((i-10) * 25), e -> AnchorPane.setBottomAnchor(textArrowView, finalI));
            keyFrameList.add(kf);
        }

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);

        return timeline;
    }

    public Timeline getAllyInfoAnimation(Pokemon pokemon, int hpStatus) {

        double width = allyPokemonSprite.getFitWidth();
        double height = allyPokemonSprite.getFitHeight();

        final List<KeyFrame> keyFrameList = new ArrayList<>();

        final double[] outside = new double[1];

        final KeyFrame configPokemonSprite = new KeyFrame(Duration.ZERO, e -> {
            setAllyInformation(pokemon, hpStatus);

            allyPokemonSprite.setFitWidth(1);
            allyPokemonSprite.setFitHeight(1);
            allyPokemonSprite.setVisible(true);

            outside[0] = anchorPane.getWidth();

            AnchorPane.setRightAnchor(allyPokemonInfo, null);
            allyPokemonInfo.setLayoutX(outside[0]);
            allyPokemonInfo.setVisible(true);
        });

        keyFrameList.add(configPokemonSprite);

        double maxI = allyPokemonInfo.getPrefWidth() + 15;

        for (int i = 1; i <= maxI; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> {
                allyPokemonInfo.setLayoutX(outside[0] - finalI);
                allyPokemonSprite.setFitWidth((width * finalI) / maxI);
                allyPokemonSprite.setFitHeight((height * finalI) / maxI);
            });
            keyFrameList.add(kf);
        }

        final KeyFrame resetAnchor = new KeyFrame(Duration.millis(maxI + 1), e -> {
            AnchorPane.setRightAnchor(allyPokemonInfo, 15.0);
            centerImage(allyPokemonSprite);
        });
        keyFrameList.add(resetAnchor);

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public Timeline getAllyAbilityInfoAnimation(Pokemon pokemon) {
        final List<KeyFrame> keyFrameList = new ArrayList<>();
        double width = allyAbilityInfo.getPrefWidth();

        final KeyFrame configAbilityLabel = new KeyFrame(Duration.ZERO, e -> {
            allyAbilityInfo.setVisible(false);
            allyAbilityInfo.setText(String.format("%s's%n%s", pokemon.getName(), pokemon.getAbility()));

            allyAbilityInfo.setLayoutX(-width);
            allyAbilityInfo.setVisible(true);
        });

        keyFrameList.add(configAbilityLabel);

        for (int i = 0; i <= width; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> {
                allyAbilityInfo.setLayoutX(finalI - width);
            });
            keyFrameList.add(kf);
        }

        Timeline timeline = new Timeline();
        timeline.setAutoReverse(true);
        timeline.setCycleCount(2);
        timeline.getKeyFrames().addAll(keyFrameList);
        timeline.getKeyFrames().addAll(generatePause(1000).getKeyFrames());
        return timeline;
    }

    public Timeline getEnemyAbilityInfoAnimation(Pokemon pokemon) {
        final List<KeyFrame> keyFrameList = new ArrayList<>();
        double width = enemyAbilityInfo.getPrefWidth();
        double screenWidth = enemyAbilityInfo.getScene().getWidth();

        final KeyFrame configAbilityLabel = new KeyFrame(Duration.ZERO, e -> {
            enemyAbilityInfo.setVisible(false);
            enemyAbilityInfo.setText(String.format("%s's%n%s", pokemon.getName(), pokemon.getAbility()));

            enemyAbilityInfo.setLayoutX(screenWidth);
            enemyAbilityInfo.setVisible(true);
        });

        keyFrameList.add(configAbilityLabel);

        for (int i = 0; i <= width; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> {
                enemyAbilityInfo.setLayoutX(screenWidth - finalI);
            });
            keyFrameList.add(kf);
        }

        Timeline timeline = new Timeline();
        timeline.setAutoReverse(true);
        timeline.setCycleCount(2);
        timeline.getKeyFrames().addAll(keyFrameList);
        timeline.getKeyFrames().addAll(generatePause(1000).getKeyFrames());
        return timeline;
    }

    public Timeline getEnemyInfoAnimation(Pokemon pokemon, int hpStatus) {

        double width = enemyPokemonSprite.getFitWidth();
        double height = enemyPokemonSprite.getFitHeight();

        final List<KeyFrame> keyFrameList = new ArrayList<>();

        final KeyFrame configPokemonSprite = new KeyFrame(Duration.ZERO, e -> {

            setEnemyInformation(pokemon, hpStatus);

            enemyPokemonSprite.setFitWidth(1);
            enemyPokemonSprite.setFitHeight(1);
            enemyPokemonSprite.setVisible(true);

            AnchorPane.setLeftAnchor(enemyPokemonInfo, null);
            enemyPokemonInfo.setLayoutX(-enemyPokemonInfo.getPrefWidth());
            enemyPokemonInfo.setVisible(true);
        });

        keyFrameList.add(configPokemonSprite);

        double maxI = enemyPokemonInfo.getPrefWidth() + 15;

        for (int i = 1; i <= maxI; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> {
                enemyPokemonInfo.setLayoutX(-enemyPokemonInfo.getPrefWidth() + finalI);
                enemyPokemonSprite.setFitWidth((width * finalI) / maxI);
                enemyPokemonSprite.setFitHeight((height * finalI) / maxI);
            });
            keyFrameList.add(kf);
        }

        final KeyFrame resetAnchor = new KeyFrame(Duration.millis(maxI + 1), e -> {
           AnchorPane.setLeftAnchor(enemyPokemonInfo, 15.0);
           centerImage(enemyPokemonSprite);
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
        //centerImage(enemyPokemonSprite);

        setEnemyHpBar(pokemon.getHp(), pokemon.getMaxHP());
    }

    public void setEnemyInformation(Pokemon pokemon, int overrideHp) {
        enemyNameLabel.setText(pokemon.getName());
        enemyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        enemyPokemonSprite.setImage(pokemon.getSpecie().getFrontSprite());
        //centerImage(enemyPokemonSprite);

        setEnemyHpBar(overrideHp, pokemon.getMaxHP());
    }

    public void setEnemyHpBar(int hp, int maxhp) {
        double currentHpPercentage = (double) hp / maxhp;
        if (currentHpPercentage < 0.03 && hp != 0)
            currentHpPercentage = 0.03;

        enemyHpBar.setProgress(currentHpPercentage);

        if (currentHpPercentage > 0.56)
            enemyHpBar.setStyle("-fx-accent: green");
        else if (currentHpPercentage > 0.21)
            enemyHpBar.setStyle("-fx-accent: yellow");
        else
            enemyHpBar.setStyle("-fx-accent: red");
    }

    public Timeline getEnemyHpAnimation(int from, int to, int max) {
        final List<KeyFrame> keyFrameList = new ArrayList<>();

        if (from == to)
            return null;

        boolean descending = from > to;
        int frameTime = 1000 / max;

        for (int i = 1; i <= Math.abs(from - to); i++) {
            final KeyFrame kf;
            final int finalI = i;
            if (descending)
                kf = new KeyFrame(Duration.millis(frameTime * i), e -> setEnemyHpBar(from - finalI, max));
            else
                kf = new KeyFrame(Duration.millis(frameTime * i), e -> setEnemyHpBar(from + finalI, max));
            keyFrameList.add(kf);
        }

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public static void setStatusStyle(Pokemon pokemon, Label statusLabel) {
        statusLabel.setTextFill(Color.WHITE);

        final String style = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: ";
        String finalStyle = "";

        switch (pokemon.getStatus()) {
            case NONE:
                statusLabel.setVisible(false);
                break;
            case PARALYZED:
                statusLabel.setText("PAR");
                finalStyle = style + "#B8B818";
                break;
            case POISONED:
                statusLabel.setText("PSN");
                finalStyle = style + "purple";
                break;
            case BADLY_POISONED:
                statusLabel.setText("PSN");
                statusLabel.setTextFill(Color.LIGHTPINK);
                finalStyle = style + "purple";
                break;
            case SLEEPING:
                statusLabel.setText("SLP");
                finalStyle = style + "gray";
                break;
            case BURNED:
                statusLabel.setText("BRN");
                finalStyle = style + "chocolate";
                break;
            case FROZEN:
                statusLabel.setText("FRZ");
                finalStyle = style + "dodgerblue";
                break;
        }

        if (pokemon.getHp() == 0) {
            statusLabel.setVisible(true);
            statusLabel.setText("FNT");
            finalStyle = style + "maroon";
            statusLabel.setStyle(finalStyle);
        }

        if (pokemon.getStatus() != Enums.Status.NONE) {
            statusLabel.setStyle(finalStyle);
            statusLabel.setVisible(true);
        }
    }

    public Timeline updateStatus(Pokemon pokemon, boolean ally) {

        final Label statusLabel;
        if (ally)
            statusLabel = allyStatusLabel;
        else
            statusLabel = enemyStatusLabel;

        final KeyFrame kf = new KeyFrame(Duration.millis(1), e-> {
            setStatusStyle(pokemon, statusLabel);
        });

        return new Timeline(kf);
    }

    //https://stackoverflow.com/questions/32781362/centering-an-image-in-an-imageview
    public void centerImage(ImageView imageView) {
        Image img = imageView.getImage();

        if (img != null) {
            double w;
            double h;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reducCoeff = Math.min(ratioX, ratioY);

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageView.setTranslateX((imageView.getFitWidth() - w) / 2);
            imageView.setTranslateY((imageView.getFitHeight() - h));

            //imageView.setImage(img);
        }
    }

    public void setAllyInformation(Pokemon pokemon) {
        allyNameLabel.setText(pokemon.getName());
        allyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        allyPokemonSprite.setImage(pokemon.getSpecie().getBackSprite());
        //centerImage(allyPokemonSprite);

        setAllyHpBar(pokemon.getHp(), pokemon.getMaxHP());
    }

    public void setAllyInformation(Pokemon pokemon, int overrideHp) {
        allyNameLabel.setText(pokemon.getName());
        allyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        allyPokemonSprite.setImage(pokemon.getSpecie().getBackSprite());
        //centerImage(allyPokemonSprite);

        setAllyHpBar(overrideHp, pokemon.getMaxHP());
    }

    public void setAllyHpBar(int hp, int maxhp) {
        double currentHpPercentage = (double) hp / maxhp;
        if (currentHpPercentage < 0.03 && hp != 0)
            currentHpPercentage = 0.03;

        allyHpBar.setProgress(currentHpPercentage);
        allyHpLabel.setText(String.format("%3d/%-3d", hp, maxhp));

        if (currentHpPercentage > 0.56)
            allyHpBar.setStyle("-fx-accent: green");
        else if (currentHpPercentage > 0.21)
            allyHpBar.setStyle("-fx-accent: yellow");
        else
            allyHpBar.setStyle("-fx-accent: red");
    }

    public Timeline getAllyHpAnimation(int from, int to, int max) {
        final List<KeyFrame> keyFrameList = new ArrayList<>();

        if (from == to || max < to || max < from)
            return null;

        boolean descending = from > to;

        double frameTime = 1000.0 / max;
        int frameCount = Math.abs(from - to);

        for (int i = 1; i <= frameCount; i++) {
            final KeyFrame kf;
            final int finalI = i;

            if (descending)
                kf = new KeyFrame(Duration.millis(frameTime * i), e -> setAllyHpBar(from - finalI, max));
            else
                kf = new KeyFrame(Duration.millis(frameTime * i), e -> setAllyHpBar(from + finalI, max));
            keyFrameList.add(kf);
        }

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public Timeline getPokemonFaintedAnimation(boolean ally) {

        final List<KeyFrame> keyFrameList = new ArrayList<>();

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

    private String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    public String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }

    private void setMoveInformation(Button button, Move move) {

        String ppInfo = "%s%nPP: %-2d/%2d%nType: %s";
        String type;
        type = move.getType().getTypeEnum().toString();

        button.setText(String.format(ppInfo, move.getName(), move.getPp(),
                move.getMaxpp(), type));
        button.setTextAlignment(TextAlignment.CENTER);

        button.setFont(Font.font("Monospaced", 11));
        Color buttonBorderColor = move.getType().getTypeEnum().getTypeColor();
        Color buttonColor = Color.GAINSBORO;
        String colorHex = toHexString(buttonBorderColor);
        String colorHexEnd = toHexString(buttonColor);
        String setColorIntro = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 5; " +
                "-fx-border-color: ";
        String setColorEnd = "; -fx-background-color: ";
        button.setStyle(setColorIntro + colorHex + setColorEnd + colorHexEnd);
        button.setOnMouseExited(e -> button.setStyle(setColorIntro + colorHex + setColorEnd + colorHexEnd));
        button.setOnMouseReleased(e -> button.setStyle(setColorIntro + colorHex + setColorEnd + colorHexEnd));

        Color buttonBorderColorPressed = buttonBorderColor.darker();
        Color buttonColorPressed = buttonColor.darker();
        String colorHexPressed = toHexString(buttonBorderColorPressed);
        String colorHexPressedEnd = toHexString(buttonColorPressed);
        button.setOnMousePressed(e -> button.setStyle(setColorIntro + colorHexPressed + setColorEnd + colorHexPressedEnd));

        Color buttonBorderColorHover = buttonBorderColor.brighter();
        Color buttonColorHover = buttonColor.brighter();
        String colorHexHover = toHexString(buttonBorderColorHover);
        String colorHexHoverEnd = toHexString(buttonColorHover);
        button.setOnMouseEntered(e -> button.setStyle(setColorIntro + colorHexHover + setColorEnd + colorHexHoverEnd));

        // looks good like this so far, needs more testing
        Color textColorRegular = Color.BLACK;
        Color textColorWarning = Color.GOLDENROD;
        Color textColorLow = Color.DARKORANGE;
        Color textColorNoPP = Color.FIREBRICK;

        float percentage = (float)move.getPp() / move.getMaxpp();
        if (percentage > 0.5)
            button.setTextFill(textColorRegular);
        else if (percentage > 0.25)
            button.setTextFill(textColorWarning);
        else if (percentage > 0)
            button.setTextFill(textColorLow);
        else
            button.setTextFill(textColorNoPP);
    }

    public void updateAvailableMoves(Pokemon pokemon) {
        List<Move> moveList = pokemon.getMoveList();

        setMoveInformation(firstMoveButton, moveList.get(0));

        String setColorNoMove = "-fx-font: 11 monospaced; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 5; " +
                "-fx-border-color: black; -fx-background-color: lightgray";

        if (moveList.size() > 1) {
            setMoveInformation(secondMoveButton, moveList.get(1));
        }
        else {
            secondMoveButton.setTextFill(Color.BLACK);
            secondMoveButton.setText("");
            secondMoveButton.setDisable(true);
            secondMoveButton.setStyle(setColorNoMove);
        }
        if (moveList.size() > 2)
            setMoveInformation(thirdMoveButton, moveList.get(2));
        else {
            thirdMoveButton.setTextFill(Color.BLACK);
            thirdMoveButton.setText("");
            thirdMoveButton.setDisable(true);
            thirdMoveButton.setStyle(setColorNoMove);
        }

        if (moveList.size() > 3)
            setMoveInformation(fourthMoveButton, moveList.get(3));
        else {
            fourthMoveButton.setTextFill(Color.BLACK);
            fourthMoveButton.setText("");
            fourthMoveButton.setDisable(true);
            fourthMoveButton.setStyle(setColorNoMove);
        }

    }

    public void updateSelectPokemonButtons(List<Pokemon> party) {

        int partySize = party.size();

        String setColorIntro = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 7; " +
                "-fx-border-color: ";
        String setColorEnd = "; -fx-background-color: ";

        Color buttonColor = Color.LIGHTGRAY;
        String colorHexEnd = toHexString(buttonColor);
        Color buttonColorPressed = buttonColor.darker();
        String colorHexPressedEnd = toHexString(buttonColorPressed);
        Color buttonColorHover = buttonColor.brighter();
        String colorHexHoverEnd = toHexString(buttonColorHover);
        String halfAndHalf = "linear-gradient(from 0%% 50%% to 100%% 50%%, %s, %s)";

        for (int j=0; j <= 1; j++) {
            for (int i = 0; i <= 2; i++) {

                int partyIndex = i + (3 * j);

                Button button = (Button) getNodeFromGridPane(getPokemonGrid(), i, j);
                //button.setStyle("-fx-background-color: green");
                button.setTextFill(Color.DARKGREEN);
                button.setDisable(false);

                button.setTextAlignment(TextAlignment.CENTER);

                button.setFont(Font.font("Monospaced"));
                Color[] buttonBorderColor = new Color[2];
                Color[] buttonBorderColorPressed = new Color[2];


                if (partyIndex > partySize - 1) {
                    button.setText("");
                    button.setDisable(true);
                    button.setStyle(setColorIntro + "black" + setColorEnd + colorHexEnd);
                    continue;
                }
                Pokemon pokemon = party.get(partyIndex);
                button.setText(String.format("%s%nHP:%d/%d", pokemon.getName(), pokemon.getHp(), pokemon.getMaxHP()));

                buttonBorderColor[0] = pokemon.getType()[0].getTypeEnum().getTypeColor();

                if (pokemon.getType()[1].getTypeEnum() == Enums.Types.NO_TYPE) {
                    buttonBorderColor[1] = buttonBorderColor[0];
                }
                else {
                    buttonBorderColor[1] = pokemon.getType()[1].getTypeEnum().getTypeColor();
                }
                String[] colorHex = new String[2];
                colorHex[0] = toHexString(buttonBorderColor[0]);
                colorHex[1] = toHexString(buttonBorderColor[1]);
                String colorHexComplete = String.format(halfAndHalf, colorHex[0], colorHex[1]);
                button.setStyle(setColorIntro + colorHexComplete + setColorEnd + colorHexEnd);
                button.setOnMouseExited(e -> button.setStyle(setColorIntro + colorHexComplete + setColorEnd + colorHexEnd));
                button.setOnMouseReleased(e -> button.setStyle(setColorIntro + colorHexComplete + setColorEnd + colorHexEnd));

                buttonBorderColorPressed[0] = buttonBorderColor[0].darker();
                buttonBorderColorPressed[1] = buttonBorderColor[1].darker();
                String[] colorHexPressed = new String[2];
                colorHexPressed[0] = toHexString(buttonBorderColorPressed[0]);
                colorHexPressed[1] = toHexString(buttonBorderColorPressed[1]);
                String colorHexPressedComplete = String.format(halfAndHalf, colorHexPressed[0], colorHexPressed[1]);
                button.setOnMousePressed(e -> button.setStyle(setColorIntro + colorHexPressedComplete + setColorEnd + colorHexPressedEnd));

                Color[] buttonBorderColorHover = new Color[2];
                buttonBorderColorHover[0] = buttonBorderColor[0].brighter();
                buttonBorderColorHover[1] = buttonBorderColor[1].brighter();
                String[] colorHexHover = new String[2];
                colorHexHover[0] = toHexString(buttonBorderColorHover[0]);
                colorHexHover[1] = toHexString(buttonBorderColorHover[1]);
                String colorHexHoverComplete = String.format(halfAndHalf, colorHexHover[0], colorHexHover[1]);
                button.setOnMouseEntered(e -> button.setStyle(setColorIntro + colorHexHoverComplete + setColorEnd + colorHexHoverEnd));

                if (pokemon.getHp() == 0) {
                    //button.setStyle("-fx-background-color: red");
                    button.setTextFill(Color.DARKRED);
                    button.setDisable(true);
                }
            }
        }
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

        updateSelectPokemonButtons(party);

        //updateAvailablePokemon(party);
    }

    public Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
}