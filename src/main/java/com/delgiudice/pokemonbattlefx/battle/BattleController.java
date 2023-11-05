package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.attributes.Type;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BattleController {

    @FXML
    private TextArea battleText, battleTextFull;
    @FXML
    private GridPane optionGrid, moveGrid, pokemonGrid;
    @FXML
    private Button fightButton, bagButton, pokemonButton, runButton, firstMoveButton, secondMoveButton, thirdMoveButton,
            fourthMoveButton, backMoveButton, pokemonBackButton;
    @FXML
    private VBox allyPokemonInfo, enemyPokemonInfo, moveTypeBox;
    @FXML
    private ImageView allyPokemonSprite, enemyPokemonSprite, textArrowView;
    @FXML
    private Label enemyNameLabel, enemyLvLabel, allyNameLabel, allyHpLabel, allyLvLabel, allyStatusLabel,
                    enemyStatusLabel, allyAbilityInfo, enemyAbilityInfo, moveTypeLabel, movePPLabel;
    @FXML
    private ProgressBar enemyHpBar, allyHpBar;
    @FXML
    private Pane mainPane;

    List<Button> moveButtons;

    private Timeline idleAnimation;

    private final InputStream battleThemeLoopInputStream, victoryThemeInputStream, allyLowHpInputStream;

    MediaPlayer introPlayer, victoryIntroPlayer;

    private Clip battleThemeLoopAudioClip, victoryThemeLoopAudioClip;

    private Clip audioEffectsClip, allyLowHpClip;

    private final static String FIGHT_BUTTON_PRESSED = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: darkred;";
    private final static String FIGHT_BUTTON_RELEASE = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: firebrick;";
    private final static String FIGHT_BUTTON_HOVER = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: crimson;";

    private final static String POKEMON_BUTTON_PRESSED = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: darkgreen;";
    private final static String POKEMON_BUTTON_RELEASE = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: forestgreen;";
    private final static String POKEMON_BUTTON_HOVER = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: limegreen;";

    private final static String WEATHER_NONE = "-fx-background-color: linear-gradient(to bottom, lightskyblue, darkorange);";
    private final static String WEATHER_RAIN = "-fx-background-color: linear-gradient(to bottom, lightgray, darkblue);";

    public final int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720;
    private double ALLY_INFO_DEFAULT_Y, ALLY_SPRITE_DEFAULT_Y;

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

    public BattleController() throws UnsupportedAudioFileException, LineUnavailableException, IOException, URISyntaxException {
        // Looped battle intro, requires main loop and intro in two separate audio files
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sound/battle_theme_loop.wav");
        if (inputStream != null) {
            battleThemeLoopInputStream = new BufferedInputStream(inputStream);
            configBattleTheme();
        }
        else
            battleThemeLoopInputStream = null;

        // Looped victory intro, requires main loop and intro in two seperate audio files
        inputStream = getClass().getClassLoader().getResourceAsStream("sound/victory_theme_loop.wav");
        if (inputStream != null) {
            victoryThemeInputStream = new BufferedInputStream(inputStream);
            configVictoryTheme();
        }
        else
            victoryThemeInputStream = null;

        inputStream = getClass().getClassLoader().getResourceAsStream("sound/pokemon_low_hp.wav");

        if (inputStream != null) {
            allyLowHpInputStream = new BufferedInputStream(inputStream);
            configLowHpEffect();
        }
        else
            allyLowHpInputStream = null;
    }

    public void initialize() {
        battleText.setMouseTransparent(true);
        battleText.setFocusTraversable(false);
        battleTextFull.setMouseTransparent(true);
        battleTextFull.setFocusTraversable(false);

        idleAnimation = getBattleIdleAnimation();

        ALLY_INFO_DEFAULT_Y = allyPokemonInfo.getLayoutY();
        ALLY_SPRITE_DEFAULT_Y = allyPokemonSprite.getLayoutY();

        Rectangle rect = new Rectangle(SCREEN_WIDTH, SCREEN_HEIGHT);
        mainPane.setClip(rect);

        setupButtons();
    }

    public void resetState() {
        battleTextFull.setVisible(true);
        battleText.setVisible(false);
        allyPokemonSprite.setVisible(false);
        allyPokemonInfo.setVisible(false);
        enemyPokemonSprite.setVisible(false);
        enemyPokemonInfo.setVisible(false);
    }

    //https://stackoverflow.com/questions/40514910/set-volume-of-java-clip
    // Converts Clip's logarithmic audio volume controls to a linear scale, between 0 and 1, for ease of use
    public float getVolume(Clip clip) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }

    public void setVolume(Clip clip ,float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    private void configBattleTheme()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException, URISyntaxException {
        Media media = new Media(Objects.requireNonNull(
                getClass().getClassLoader().getResource("sound/battle_theme_intro.wav")).toURI().toString());
        introPlayer = new MediaPlayer(media);
        introPlayer.setVolume(0.25f);
        introPlayer.setOnEndOfMedia(() -> {
            battleThemeLoopAudioClip.setFramePosition(0);
            battleThemeLoopAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
        });

        AudioInputStream audioStreamLoop = AudioSystem.getAudioInputStream(battleThemeLoopInputStream);
        AudioFormat audioFormatLoop = audioStreamLoop.getFormat();
        DataLine.Info infoLoop = new DataLine.Info(Clip.class, audioFormatLoop);
        battleThemeLoopAudioClip = (Clip) AudioSystem.getLine(infoLoop);
        battleThemeLoopAudioClip.open(audioStreamLoop);
        setVolume(battleThemeLoopAudioClip, 0.25f);
    }

    private void configVictoryTheme()
            throws URISyntaxException, UnsupportedAudioFileException, IOException, LineUnavailableException {
        Media media = new Media(Objects.requireNonNull(
                getClass().getClassLoader().getResource("sound/victory_theme_intro.wav")).toURI().toString());
        victoryIntroPlayer = new MediaPlayer(media);
        victoryIntroPlayer.setVolume(0.25f);
        victoryIntroPlayer.setOnEndOfMedia(() -> {
            victoryThemeLoopAudioClip.setFramePosition(0);
            victoryThemeLoopAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
        });

        AudioInputStream audioStreamLoop = AudioSystem.getAudioInputStream(victoryThemeInputStream);
        AudioFormat audioFormatLoop = audioStreamLoop.getFormat();
        DataLine.Info infoLoop = new DataLine.Info(Clip.class, audioFormatLoop);
        victoryThemeLoopAudioClip = (Clip) AudioSystem.getLine(infoLoop);
        victoryThemeLoopAudioClip.open(audioStreamLoop);
        setVolume(victoryThemeLoopAudioClip, 0.25f);
    }

    private void configLowHpEffect()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException{

        BufferedInputStream bufferedInputStream = new BufferedInputStream(allyLowHpInputStream);
        AudioInputStream audioStreamLoop = AudioSystem.getAudioInputStream(bufferedInputStream);
        AudioFormat audioFormatLoop = audioStreamLoop.getFormat();
        DataLine.Info infoLoop = new DataLine.Info(Clip.class, audioFormatLoop);
        allyLowHpClip = (Clip) AudioSystem.getLine(infoLoop);
        allyLowHpClip.open(audioStreamLoop);
        setVolume(allyLowHpClip, 0.5f);
    }

    private InputStream prepareHitEffect(float typeEffect) {
        InputStream inputStream;

        if (typeEffect < 1)
            inputStream = getClass().getClassLoader().getResourceAsStream("sound/hit_effect_low.wav");
        else if (typeEffect == 1)
            inputStream = getClass().getClassLoader().getResourceAsStream("sound/hit_effect_regular.wav");
        else
            inputStream = getClass().getClassLoader().getResourceAsStream("sound/hit_effect_super.wav");

        return inputStream;
    }

    private InputStream prepareStatChangeEffect(float statChange) {
        InputStream inputStream;

        if (statChange > 0)
            inputStream = getClass().getClassLoader().getResourceAsStream("sound/stat_raised.wav");
        else
            inputStream = getClass().getClassLoader().getResourceAsStream("sound/stat_fell.wav");

        return inputStream;
    }

    private void prepareEffectClip(InputStream inputStream)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        AudioInputStream audioStreamLoop = AudioSystem.getAudioInputStream(bufferedInputStream);
        AudioFormat audioFormatLoop = audioStreamLoop.getFormat();
        DataLine.Info infoLoop = new DataLine.Info(Clip.class, audioFormatLoop);
        audioEffectsClip = (Clip) AudioSystem.getLine(infoLoop);
        audioEffectsClip.open(audioStreamLoop);
        setVolume(audioEffectsClip, 0.5f);
    }

    private void playEffect(InputStream inputStream) {
        try {
            prepareEffectClip(inputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            throw new RuntimeException("Error while processing sound effects: " + ex);
        }
        audioEffectsClip.start();
    }

    private void playLowHpEffect()  {
        if (allyLowHpClip != null) {
            if (!allyLowHpClip.isActive()) {
                allyLowHpClip.setFramePosition(0);
                allyLowHpClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public void stopLowHpEffect() {
        if (allyLowHpClip != null)
            allyLowHpClip.stop();
    }

    public Timeline getHitEffectClipPlayback(float typeEffect) {

        KeyFrame kf = new KeyFrame(Duration.millis(1), e -> {
            InputStream inputStream = prepareHitEffect(typeEffect);
            if (inputStream != null) {
                playEffect(inputStream);
            }
        });

        return new Timeline(kf);
    }

    public Timeline getStatChangeClipPlayback(int statChange) {
        KeyFrame kf = new KeyFrame(Duration.millis(1), e -> {
            InputStream inputStream = prepareStatChangeEffect(statChange);
            if (inputStream != null) {
                playEffect(inputStream);
            }
        });

        return new Timeline(kf);
    }

    private KeyFrame getPokemonFaintedClipPlayback() {
        return new KeyFrame(Duration.millis(1), e-> {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sound/pokemon_fainted.wav");
            if (inputStream != null) {
                playEffect(inputStream);
            }
        });
    }

    public void startVictoryThemePlayback() {
        if (victoryIntroPlayer != null)
            victoryIntroPlayer.play();
    }

    public void endVictoryThemePlayback() {
        if (victoryIntroPlayer != null)
            victoryIntroPlayer.stop();
        if (victoryThemeLoopAudioClip != null)
            victoryThemeLoopAudioClip.stop();
    }

    public void startBattleThemePlayback() {
        if (introPlayer != null)
            introPlayer.play();
    }

    public void endBattleThemePlayback() {
        if (introPlayer != null)
            introPlayer.stop();
        if (battleThemeLoopAudioClip != null)
            battleThemeLoopAudioClip.stop();
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

    private Timeline getBattleIdleAnimation() {

        double infoLayoutYPos = allyPokemonInfo.getLayoutY();
        double infoLayoutYMov = infoLayoutYPos - 3;

        double spriteLayoutYPos = allyPokemonSprite.getLayoutY();
        double spriteLayoutYMov = spriteLayoutYPos - 3;

        final KeyFrame kf1 = new KeyFrame(Duration.ZERO, e -> {
            allyPokemonSprite.setLayoutY(spriteLayoutYPos);
            allyPokemonInfo.setLayoutY(infoLayoutYPos);
        });
        final KeyFrame kf2 = new KeyFrame(Duration.seconds(0.3), e -> {
            allyPokemonSprite.setLayoutY(spriteLayoutYMov);
            allyPokemonInfo.setLayoutY(infoLayoutYMov);
        });

        Timeline timeline = new Timeline(kf1, kf2);
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.setOnFinished(e -> {
            allyPokemonSprite.setLayoutY(spriteLayoutYPos);
            allyPokemonInfo.setLayoutY(infoLayoutYPos);
        });

        return timeline;
    }

    public static FadeTransition getFadeTransition(Pane pane, boolean in) {

        Pane innerPane = (Pane) pane.getChildren().get(0);
        Transform scale = pane.getTransforms().stream()
                .filter(w -> w.getClass() == Scale.class)
                .findFirst()
                .orElse(null);

        Rectangle rect = new Rectangle(0, 0, innerPane.getWidth(), innerPane.getHeight());
        rect.setFill(Color.BLACK);
        if (scale != null)
            rect.getTransforms().addAll(scale);

        innerPane.getChildren().add(rect);

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), rect);

        if (in) {
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
        }
        else {
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
        }

        return fadeTransition;
    }

    public void switchToPlayerChoice(boolean choice) {
        battleTextFull.setVisible(!choice);
        battleText.setVisible(choice);
        optionGrid.setVisible(choice);
        if (choice) {
            idleAnimation.play();
        }
        else {
            idleAnimation.stop();
            allyPokemonSprite.setLayoutY(ALLY_SPRITE_DEFAULT_Y);
            allyPokemonInfo.setLayoutY(ALLY_INFO_DEFAULT_Y);
            //AnchorPane.setBottomAnchor(allyPokemonSprite, 63.0);
            //AnchorPane.setBottomAnchor(allyPokemonInfo, 220.0);
        }
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

    public Timeline getBattleText(String text, boolean full) {
        KeyFrame kf;
        if (full)
            kf = new KeyFrame(Duration.millis(1), e -> battleTextFull.setText(text));
        else
            kf = new KeyFrame(Duration.millis(1), e -> battleText.setText(text));

        return new Timeline(kf);
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

        Parent root = battleTextFull.getScene().getRoot();

        text.setOnFinished(e -> {

            final Timeline timeline = getTextArrowAnimation();

            root.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    textArrowView.setVisible(false);
                    nextText.play();
                    root.setOnMouseClicked(null);
                    root.setOnKeyPressed(null);
                    timeline.stop();
                }
            });
            root.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    textArrowView.setVisible(false);
                    nextText.play();
                    root.setOnMouseClicked(null);
                    root.setOnKeyPressed(null);
                    timeline.stop();
                }
            });

            textArrowView.setVisible(true);
            timeline.play();
            });
    }

    private Timeline getTextArrowAnimation() {

        List<KeyFrame> keyFrameList = new ArrayList<>();

        for (int i=30; i < 40; i++) {
            double finalI = i;
            KeyFrame kf = new KeyFrame(Duration.millis((i-30) * 25), e ->
                    textArrowView.setLayoutY(SCREEN_HEIGHT - finalI));
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

        final KeyFrame configPokemonSprite = new KeyFrame(Duration.ZERO, e -> {
            setAllyInformation(pokemon, hpStatus);

            allyPokemonSprite.setFitWidth(1);
            allyPokemonSprite.setFitHeight(1);
            allyPokemonSprite.setVisible(true);

            allyPokemonInfo.setLayoutX(SCREEN_WIDTH);
            allyPokemonInfo.setVisible(true);
        });

        keyFrameList.add(configPokemonSprite);

        double maxI = allyPokemonInfo.getPrefWidth() + 15;

        for (int i = 1; i <= maxI; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> {
                allyPokemonInfo.setLayoutX(SCREEN_WIDTH - finalI);
                allyPokemonSprite.setFitWidth((width * finalI) / maxI);
                allyPokemonSprite.setFitHeight((height * finalI) / maxI);
            });
            keyFrameList.add(kf);
        }

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

    //https://stackoverflow.com/questions/18124364/how-to-change-color-of-image-in-javafx
    private void setColorShiftEffect(ImageView imageView, double opacity, Color color) {

        ImageView copy = new ImageView(imageView.getImage());
        copy.setFitWidth(imageView.getFitWidth());
        copy.setFitHeight(imageView.getFitHeight());
        imageView.setClip(copy);
        opacity *= 1.5;

        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-opacity);

        ColorInput colorInput = new ColorInput(0, 0,
                imageView.getImage().getWidth(), imageView.getImage().getHeight(), color);

        Blend blush = new Blend(BlendMode.MULTIPLY, monochrome, colorInput);
        blush.setOpacity(opacity);

        imageView.setEffect(blush);
    }

    private void resetColorShiftEffect(ImageView imageView) {
        imageView.setEffect(null);
        imageView.setClip(null);
    }

    public Timeline getIntroAnimation(Pokemon pokemon, int hp) {

        final boolean ally = pokemon.getOwner().isPlayer();
        final ImageView sprite;
        final VBox info;

        if(ally) {
            sprite = allyPokemonSprite;
            info = allyPokemonInfo;
        }
        else {
            sprite = enemyPokemonSprite;
            info = enemyPokemonInfo;
        }

        final double regularWidth = sprite.getFitWidth();
        final double regularHeight = sprite.getFitHeight();

        final double bottomY = sprite.getLayoutY() + sprite.getFitHeight();
        final double centerX = sprite.getLayoutX() + (sprite.getFitWidth() / 2);

        final List<KeyFrame> keyFrameList = new ArrayList<>();

        final KeyFrame configPokemonSprite = new KeyFrame(Duration.millis(1), e -> {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sound/pokemon_sent_out.wav");
            if (inputStream != null) playEffect(inputStream);

            if (ally) setAllyInformation(pokemon, hp);
            else setEnemyInformation(pokemon, hp);

            sprite.setFitWidth(1);
            sprite.setFitHeight(1);
            setColorShiftEffect(sprite, 1, Color.RED);
            sprite.setVisible(true);

            if (ally) info.setLayoutX(SCREEN_WIDTH);
            else info.setLayoutX(-info.getPrefWidth());
            info.setVisible(true);
        });

        keyFrameList.add(configPokemonSprite);

        double maxI = info.getPrefWidth() + 15;

        for (int i = 1; i <= maxI; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> {
                if (ally) info.setLayoutX(SCREEN_WIDTH - finalI);
                else info.setLayoutX(-info.getPrefWidth() + finalI);
                sprite.setFitHeight((regularHeight * finalI) / maxI);
                sprite.setFitWidth((regularWidth * finalI) / maxI);

                double calcX = centerX - (sprite.getFitWidth() / 2);
                double calcY = bottomY - sprite.getFitHeight();
                sprite.setLayoutX(calcX);
                sprite.setLayoutY(calcY);

                double opacity = 1 - (finalI / maxI);
                setColorShiftEffect(sprite, opacity, Color.RED);
            });
            keyFrameList.add(kf);
        }

        final KeyFrame resetEffect = new KeyFrame(Duration.millis(maxI + 1), e -> {
            resetColorShiftEffect(sprite);
        });
        keyFrameList.add(resetEffect);

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
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

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public void setEnemyInformation(Pokemon pokemon) {
        enemyNameLabel.setText(pokemon.getName());
        enemyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        enemyPokemonSprite.setImage(pokemon.getSpecie().getFrontSpriteBattle());
        //centerImage(enemyPokemonSprite);

        setEnemyHpBar(pokemon.getHp(), pokemon.getMaxHP());
    }

    public void setEnemyInformation(Pokemon pokemon, int overrideHp) {
        enemyNameLabel.setText(pokemon.getName());
        enemyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        enemyPokemonSprite.setImage(pokemon.getSpecie().getFrontSpriteBattle());
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

    public Timeline getMoveDamageAnimation(boolean ally) {
        int animationDuration = 4;
        Timeline timeline = new Timeline();
        timeline.setCycleCount(animationDuration);

        ImageView sprite;
        VBox info;

        if (ally) {
            sprite = allyPokemonSprite;
            info = allyPokemonInfo;
        }
        else {
            sprite = enemyPokemonSprite;
            info = enemyPokemonInfo;
        }

        double infoPos = info.getLayoutY();
        double infoMov = infoPos - 3;

        KeyFrame kf1, kf2;

        kf1 = new KeyFrame(Duration.millis(50), e -> {
            sprite.setVisible(false);
            info.setLayoutY(infoMov);
        });

        timeline.getKeyFrames().add(kf1);

        kf2 = new KeyFrame(Duration.millis(100), e -> {
            sprite.setVisible(true);
            info.setLayoutY(infoPos);
        });

        timeline.getKeyFrames().add(kf2);

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
            case FAINTED:
                statusLabel.setText("FNT");
                finalStyle = style + "maroon";
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

    public Timeline updateFieldWeatherEffect(Enums.WeatherEffect weatherEffect) {

        final KeyFrame kf;

        switch (weatherEffect) {
            case NONE:
                kf = new KeyFrame(Duration.millis(1), e-> {
                    mainPane.setStyle(WEATHER_NONE);
                });
                break;
            case RAIN:
                kf = new KeyFrame(Duration.millis(1), e-> {
                    mainPane.setStyle(WEATHER_RAIN);
                });
                break;
            default:
                throw new IllegalStateException("Unhandled weather effect: " + weatherEffect);
        }
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

        allyPokemonSprite.setImage(pokemon.getSpecie().getBackSpriteBattle());
        //centerImage(allyPokemonSprite);

        setAllyHpBar(pokemon.getHp(), pokemon.getMaxHP(), true);
    }

    public void setAllyInformation(Pokemon pokemon, int overrideHp) {
        allyNameLabel.setText(pokemon.getName());
        allyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        allyPokemonSprite.setImage(pokemon.getSpecie().getBackSpriteBattle());
        //centerImage(allyPokemonSprite);

        setAllyHpBar(overrideHp, pokemon.getMaxHP(), true);
    }

    public void setAllyHpBar(int hp, int maxhp, boolean applySound) {
        double currentHpPercentage = (double) hp / maxhp;
        if (currentHpPercentage < 0.03 && hp != 0)
            currentHpPercentage = 0.03;

        allyHpBar.setProgress(currentHpPercentage);
        allyHpLabel.setText(String.format("%3d/%-3d", hp, maxhp));

        if (currentHpPercentage > 0.56) {
            allyHpBar.setStyle("-fx-accent: green");
            if (applySound) stopLowHpEffect();
        }
        else if (currentHpPercentage > 0.21) {
            allyHpBar.setStyle("-fx-accent: yellow");
            if (applySound) stopLowHpEffect();
        }
        else if (currentHpPercentage > 0) {
            allyHpBar.setStyle("-fx-accent: red");
            if (applySound) playLowHpEffect();
        }
        else if (applySound) {
            stopLowHpEffect();
        }
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
                kf = new KeyFrame(Duration.millis(frameTime * i), e ->
                        setAllyHpBar(from - finalI, max, false));
            else
                kf = new KeyFrame(Duration.millis(frameTime * i), e ->
                        setAllyHpBar(from + finalI, max, false));
            keyFrameList.add(kf);
        }

        double currentHpPercentage = (double) to / max;

        keyFrameList.add(new KeyFrame(Duration.millis(frameTime * frameCount + 1), e -> {
            if (currentHpPercentage <= 0.21 && currentHpPercentage > 0)
                playLowHpEffect();
            else
                stopLowHpEffect();
        }));


        Timeline timeline = new Timeline();

        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    public Timeline getPokemonFaintedAnimation(boolean ally) {
        final List<KeyFrame> keyFrameList = new ArrayList<>();

        keyFrameList.add(getPokemonFaintedClipPlayback());

        final double height, width;
        final double oldLayoutY;
        final ImageView sprite;
        final VBox info;

        if (ally) {
            sprite = allyPokemonSprite;
            info = allyPokemonInfo;
        }
        else {
            sprite = enemyPokemonSprite;
            info = enemyPokemonInfo;
        }

        height = sprite.getImage().getHeight();
        width = sprite.getImage().getWidth();
        oldLayoutY = sprite.getLayoutY();

        int frameNumber = (int) Math.floor(height / 5);
        double scale = sprite.getFitHeight() / height;


        for (int i = 1; i < frameNumber; i++) {
            final int finalI = i;
            final KeyFrame kf;
            kf = new KeyFrame(Duration.millis(finalI * 2), e -> {
                int multip = finalI * 5;
                int scaledMultip = (int) Math.round(multip * scale);
                Rectangle2D rectangle2D = new Rectangle2D(0, 0, width, (height - multip));
                sprite.setViewport(rectangle2D);
                sprite.setLayoutY(oldLayoutY + (scaledMultip));
            });

            keyFrameList.add(kf);
        }

        final KeyFrame cleanUp = new KeyFrame(Duration.millis(frameNumber * 2), e -> {
            info.setVisible(false);
            sprite.setVisible(false);
            sprite.setViewport(null);
            sprite.setLayoutY(oldLayoutY);
        });

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        timeline.getKeyFrames().add(cleanUp);

        return timeline;
    }

    public Timeline getPokemonReturnAnimation(boolean ally) {

        final List<KeyFrame> keyFrameList = new ArrayList<>();

        final ImageView sprite;
        final VBox info;

        if (ally) {
            sprite = allyPokemonSprite;
            info = allyPokemonInfo;
        }
        else {
            sprite = enemyPokemonSprite;
            info = enemyPokemonInfo;
        }

        final double regularHeight = sprite.getFitHeight();
        final double regularWidth = sprite.getFitWidth();
        final double regularX = sprite.getLayoutX();
        final double regularY = sprite.getLayoutY();

        final double centerX = sprite.getLayoutX() + (sprite.getFitWidth() / 2);
        final double bottomY = sprite.getLayoutY() + sprite.getFitHeight();

        final KeyFrame startSound = new KeyFrame(Duration.millis(1), e -> {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sound/pokemon_sent_out.wav");
            playEffect(inputStream);
        });

        keyFrameList.add(startSound);

        for (int i = 1; i < regularHeight; i++) {
            final int finalI = i;
            final KeyFrame kf;

            kf = new KeyFrame(Duration.millis(i), e -> {
                sprite.setFitHeight(regularHeight - finalI);
                sprite.setFitWidth(regularWidth - finalI);

                double calcX = centerX - (sprite.getFitWidth() / 2);
                double calcY = bottomY - sprite.getFitHeight();
                sprite.setLayoutX(calcX);
                sprite.setLayoutY(calcY);

                double opacity = (finalI / regularHeight);
                setColorShiftEffect(sprite, opacity, Color.RED);
            });

            keyFrameList.add(kf);
        }

        final KeyFrame cleanUp = new KeyFrame(Duration.millis(regularHeight), e -> {
            info.setVisible(false);
            sprite.setVisible(false);
            sprite.setFitHeight(regularHeight);
            sprite.setFitWidth(regularWidth);
            sprite.setLayoutX(regularX);
            sprite.setLayoutY(regularY);
            resetColorShiftEffect(sprite);
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

    private void setMoveTypeBox(Move move) {

        Type type = move.getType();

        String styleString = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: ";
        moveTypeLabel.setText(type.getTypeEnum().toString());
        Color firstTypeColor = type.getTypeEnum().getTypeColor();
        String colorHex = toHexString(firstTypeColor);
        moveTypeLabel.setStyle(styleString + colorHex);
        movePPLabel.setText(String.format("%-2d/%2d", move.getPp(), move.getMaxpp()));

        // looks good like this so far, needs more testing
        Color textColorRegular = Color.BLACK;
        Color textColorWarning = Color.GOLDENROD;
        Color textColorLow = Color.DARKORANGE;
        Color textColorNoPP = Color.FIREBRICK;

        float percentage = (float)move.getPp() / move.getMaxpp();
        if (percentage > 0.5)
            movePPLabel.setTextFill(textColorRegular);
        else if (percentage > 0.25)
            movePPLabel.setTextFill(textColorWarning);
        else if (percentage > 0)
            movePPLabel.setTextFill(textColorLow);
        else
            movePPLabel.setTextFill(textColorNoPP);
    }

    private void setMoveInformation(Button button, Move move) {

        //String ppInfo = "%s%nPP: %-2d/%2d%nType: %s";

        //button.setText(String.format(ppInfo, move.getName(), move.getPp(),
        //        move.getMaxpp(), type));
        button.setText(move.getName().toString());
        button.setTextAlignment(TextAlignment.CENTER);

        button.setFont(Font.font("Monospaced", 20));
        Color buttonBorderColor = move.getType().getTypeEnum().getTypeColor();
        Color buttonColor = Color.GAINSBORO;
        String colorHex = toHexString(buttonBorderColor);
        String colorHexEnd = toHexString(buttonColor);
        String setColorIntro = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 5; " +
                "-fx-border-color: ";
        String setColorEnd = "; -fx-background-color: ";
        button.setStyle(setColorIntro + colorHex + setColorEnd + colorHexEnd);
        button.setOnMouseExited(e -> {
            button.setStyle(setColorIntro + colorHex + setColorEnd + colorHexEnd);
            moveTypeBox.getChildren().get(0).setVisible(false);
            moveTypeBox.getChildren().get(1).setVisible(false);
        });
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
        button.setOnMouseEntered(e -> {
            button.setStyle(setColorIntro + colorHexHover + setColorEnd + colorHexHoverEnd);
            setMoveTypeBox(move);
            moveTypeBox.getChildren().get(0).setVisible(true);
            moveTypeBox.getChildren().get(1).setVisible(true);
        });
    }

    public void updateAvailableMoves(Pokemon pokemon) {
        List<Move> moveList = pokemon.getMoveList();
        int size = moveList.size();

        String setColorNoMove = "-fx-font: 11 monospaced; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 5; " +
                "-fx-border-color: black; -fx-background-color: lightgray";

        for (int i=0; i<4; i++) {

            Button moveButton = (Button) moveGrid.getChildren().get(i);

            if (i < size) {
                setMoveInformation(moveButton, moveList.get(i));
            }
            else {
                moveButton.setTextFill(Color.BLACK);
                moveButton.setText("");
                moveButton.setDisable(true);
                moveButton.setStyle(setColorNoMove);
            }
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

    public List<Timeline> generateSubstituteAppearAnimation(Pokemon pokemon) {
        final boolean isPlayer = pokemon.getOwner().isPlayer();

        List<Timeline> substituteTimeline = new ArrayList<>();

        Timeline pokemonSpriteFadeTimeline = new Timeline();

        ImageView pokemonSprite = isPlayer ? allyPokemonSprite : enemyPokemonSprite;
        final double spriteX = pokemonSprite.getX();
        final double spriteY = pokemonSprite.getY();
        final double spriteHeight = pokemonSprite.getFitHeight();
        final double spriteWidth = pokemonSprite.getFitWidth();

        KeyFrame setupSprite = new KeyFrame(Duration.millis(1), e -> {
            pokemonSprite.setPreserveRatio(false);
        });

        pokemonSpriteFadeTimeline.getKeyFrames().add(setupSprite);

        for (int i=1; i < spriteWidth; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(5*i), e -> {
                pokemonSprite.setFitWidth(spriteWidth - finalI);
                pokemonSprite.setX(spriteX + (finalI / 2.0));
            });
            pokemonSpriteFadeTimeline.getKeyFrames().add(kf);
        }

        KeyFrame makeSpriteInvisible = new KeyFrame(Duration.millis(spriteWidth*5), e -> {
            pokemonSprite.setVisible(false);
            //pokemonSprite.setX(spriteX);
            //pokemonSprite.setY(spriteY);
            pokemonSprite.setFitWidth(spriteWidth);
            pokemonSprite.setFitHeight(spriteHeight);
        });

        pokemonSpriteFadeTimeline.getKeyFrames().add(makeSpriteInvisible);

        Timeline substituteAppearTimeline = new Timeline();

        double distance = (-spriteHeight) - spriteY;

        KeyFrame setSubstituteSprite = new KeyFrame(Duration.millis(1), e -> {
            pokemonSprite.setY(distance);
        });


        substituteTimeline.add(pokemonSpriteFadeTimeline);
        return substituteTimeline;
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