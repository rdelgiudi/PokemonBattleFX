package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.BattleApplication;
import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.attributes.Type;
import com.delgiudice.pokemonbattlefx.graphics.ParticleAnimationCreator;
import com.delgiudice.pokemonbattlefx.graphics.ParticleGroup;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.move.MoveEnum;
import com.delgiudice.pokemonbattlefx.move.MoveTemplate;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import com.delgiudice.pokemonbattlefx.pokemon.PokemonSpecie;
import com.delgiudice.pokemonbattlefx.teambuilder.AddPokemonController;
import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.util.Duration;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import javax.sound.sampled.*;
import javax.swing.event.ChangeListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
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
    private HBox allyPokemonPartyBox, enemyPokemonPartyBox;
    @FXML
    private ImageView allyPokemonSprite, enemyPokemonSprite, allyBattlePlatform, enemyBattlePlatform, textArrowView;
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

    private final Image pokemonIndicatorEmpty, pokemonIndicatorNormal;

    private boolean turboMode = false;

    private final static String FIGHT_BUTTON_PRESSED = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: darkred;";
    private final static String FIGHT_BUTTON_RELEASE = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: firebrick;";
    private final static String FIGHT_BUTTON_HOVER = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: crimson;";

    private final static String POKEMON_BUTTON_PRESSED = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: darkgreen;";
    private final static String POKEMON_BUTTON_RELEASE = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: forestgreen;";
    private final static String POKEMON_BUTTON_HOVER = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: limegreen;";

    private final static String BAG_BUTTON_PRESSED = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #cc7722;";
    private final static String BAG_BUTTON_RELEASE = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: goldenrod;";
    private final static String BAG_BUTTON_HOVER = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f4c430;";

    private final static String RUN_BUTTON_PRESSED = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #1773cd;";
    private final static String RUN_BUTTON_RELEASE = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: dodgerblue;";
    private final static String RUN_BUTTON_HOVER = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #75b3f0;";

    private final static String WEATHER_NONE = "-fx-background-color: linear-gradient(to bottom, lightskyblue, darkorange);";
    private final static String WEATHER_RAIN = "-fx-background-color: linear-gradient(to bottom, lightgray, darkblue);";
    private final static String WEATHER_SANDSTORM = "-fx-background-color: linear-gradient(to bottom, #C4A484, #5C4033);";

    public final static int SCREEN_WIDTH = 1280, SCREEN_HEIGHT = 720;
    private double ALLY_INFO_DEFAULT_Y, ALLY_SPRITE_DEFAULT_X, ALLY_SPRITE_DEFAULT_Y, ENEMY_SPRITE_DEFAULT_X, ENEMY_SPRITE_DEFAULT_Y;
    private double ALLY_SPRITE_DEFAULT_WIDTH, ALLY_SPRITE_DEFAULT_HEIGHT, ENEMY_SPRITE_DEFUALT_WIDTH,
            ENEMY_SPRITE_DEFAULT_HEIGHT;

    private InvalidationListener allyAnimatedSpriteListener, enemyAnimatedSpriteListener;

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

    public void setTurboMode(boolean turboMode) {
        this.turboMode = turboMode;
    }

    /**
     * Class constructor
     */
    public BattleController(){
        // Looped battle intro, requires main loop and intro in two separate audio files
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sound/battle_theme_loop.wav");
        if (inputStream != null) {
            battleThemeLoopInputStream = new BufferedInputStream(inputStream);
            try {
                configBattleTheme();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | URISyntaxException e) {
                System.out.println("[WARN] Loading battle theme failed " + e);
            }
        }
        else
            battleThemeLoopInputStream = null;

        // Looped victory intro, requires main loop and intro in two seperate audio files
        inputStream = getClass().getClassLoader().getResourceAsStream("sound/victory_theme_loop.wav");
        if (inputStream != null) {
            victoryThemeInputStream = new BufferedInputStream(inputStream);
            try {
                configVictoryTheme();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | URISyntaxException e) {
                System.out.println("[WARN] Loading victory theme failed " + e);
            }
        }
        else
            victoryThemeInputStream = null;

        inputStream = getClass().getClassLoader().getResourceAsStream("sound/pokemon_low_hp.wav");

        if (inputStream != null) {
            allyLowHpInputStream = new BufferedInputStream(inputStream);
            try {
                configLowHpEffect();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.out.println("[WARN] Loading low HP effect failed " + e);
            }
        }
        else
            allyLowHpInputStream = null;

        pokemonIndicatorEmpty = new Image("/sprites/indicator_empty.png");
        pokemonIndicatorNormal = new Image("/sprites/indicator_normal.png");
    }

    /**
     * Configures UI elements.
     */
    public void initialize() {
        battleText.setMouseTransparent(true);
        battleText.setFocusTraversable(false);
        battleTextFull.setMouseTransparent(true);
        battleTextFull.setFocusTraversable(false);

        idleAnimation = getBattleIdleAnimation();

        ALLY_INFO_DEFAULT_Y = allyPokemonInfo.getLayoutY();
        ALLY_SPRITE_DEFAULT_X = allyPokemonSprite.getLayoutX();
        ALLY_SPRITE_DEFAULT_Y = allyPokemonSprite.getLayoutY();
        ALLY_SPRITE_DEFAULT_WIDTH = allyPokemonSprite.getFitWidth();
        ALLY_SPRITE_DEFAULT_HEIGHT = allyPokemonSprite.getFitHeight();

        ENEMY_SPRITE_DEFAULT_X = enemyPokemonSprite.getLayoutX();
        ENEMY_SPRITE_DEFAULT_Y = enemyPokemonSprite.getLayoutY();
        ENEMY_SPRITE_DEFUALT_WIDTH = enemyPokemonSprite.getFitWidth();
        ENEMY_SPRITE_DEFAULT_HEIGHT = enemyPokemonSprite.getFitHeight();

        Rectangle rect = new Rectangle(SCREEN_WIDTH, SCREEN_HEIGHT);
        mainPane.setClip(rect);

        setupButtons();

        final double middleXAlly = allyPokemonSprite.getLayoutX() + allyPokemonSprite.getFitWidth() / 2.0;
        final double middleYAlly = allyPokemonSprite.getLayoutY() + 50 + allyPokemonSprite.getFitHeight();
        final double middleXEnemy = enemyPokemonSprite.getLayoutX() + enemyPokemonSprite.getFitWidth() / 2.0;
        final double middleYEnemy = enemyPokemonSprite.getLayoutY() + 50 + enemyPokemonSprite.getFitHeight();

        allyAnimatedSpriteListener = new InvalidationListener() {
            @Override
            public void invalidated(Observable e) {
                allyPokemonSprite.setFitWidth(0);
                allyPokemonSprite.setFitHeight(0);
                double newX = middleXAlly - allyPokemonSprite.getImage().getWidth() / 2.0;
                double newY = middleYAlly - allyPokemonSprite.getImage().getHeight();
                allyPokemonSprite.setLayoutX(newX);
                allyPokemonSprite.setLayoutY(newY);
            }
        };
        enemyAnimatedSpriteListener = new InvalidationListener() {
            @Override
            public void invalidated(Observable e) {
                enemyPokemonSprite.setFitWidth(0);
                enemyPokemonSprite.setFitHeight(0);
                double newX = middleXEnemy - enemyPokemonSprite.getImage().getWidth() / 2.0;
                double newY = middleYEnemy - enemyPokemonSprite.getImage().getHeight();
                enemyPokemonSprite.setLayoutX(newX);
                enemyPokemonSprite.setLayoutY(newY);
            }
        };
    }

    /**
     * One of the methods responsible for modifying element positions depending on using animated sprites or not. Using
     * animated sprites requires lowering some elements down the screen and modifying sizes.
     * @see AddPokemonController#processSpriteModeSwitch()
     * @see SummaryController#processSpriteModeSwitch()
     */
    public void processSpriteModeSwitch() {
        if (BattleApplication.isUseInternetSprites() || BattleApplication.isUseLocalAnimSprites()) {
            allyPokemonSprite.setLayoutY(allyPokemonSprite.getLayoutY() + 50);
            enemyPokemonSprite.setLayoutY(enemyPokemonSprite.getLayoutY() + 50);
            enemyBattlePlatform.setLayoutY(enemyBattlePlatform.getLayoutY() + 50);

            allyPokemonSprite.imageProperty().addListener(allyAnimatedSpriteListener);

            enemyPokemonSprite.imageProperty().addListener(enemyAnimatedSpriteListener);
        }
        else {
            allyPokemonSprite.imageProperty().removeListener(allyAnimatedSpriteListener);
            enemyPokemonSprite.imageProperty().removeListener(enemyAnimatedSpriteListener);

            allyPokemonSprite.setLayoutY(allyPokemonSprite.getLayoutY() - 50);
            enemyPokemonSprite.setLayoutY(enemyPokemonSprite.getLayoutY() - 50);
            enemyBattlePlatform.setLayoutY(enemyBattlePlatform.getLayoutY() - 50);

            allyPokemonSprite.setFitWidth(ALLY_SPRITE_DEFAULT_WIDTH);
            allyPokemonSprite.setFitHeight(ALLY_SPRITE_DEFAULT_HEIGHT);
            allyPokemonSprite.setLayoutX(ALLY_SPRITE_DEFAULT_X);
            allyPokemonSprite.setLayoutY(ALLY_SPRITE_DEFAULT_Y);

            enemyPokemonSprite.setFitWidth(ENEMY_SPRITE_DEFUALT_WIDTH);
            enemyPokemonSprite.setFitHeight(ENEMY_SPRITE_DEFAULT_HEIGHT);
            enemyPokemonSprite.setLayoutX(ENEMY_SPRITE_DEFAULT_X);
            enemyPokemonSprite.setLayoutY(ENEMY_SPRITE_DEFAULT_Y);
        }
    }

    /**
     * Resets screen to default state.
     */
    public void resetState() {
        battleTextFull.setVisible(true);
        battleText.setVisible(false);
        allyPokemonSprite.setVisible(false);
        allyPokemonInfo.setVisible(false);
        enemyPokemonSprite.setVisible(false);
        enemyPokemonInfo.setVisible(false);
    }

    //https://stackoverflow.com/questions/40514910/set-volume-of-java-clip

    /**
     * Method for checking volume. Converts Clip's logarithmic audio volume controls to a linear scale,between 0 and 1,
     * for ease of use.
     * @param clip Clip whose volume should be checked
     * @return volume, in values between 0 and 1
     */
    public float getVolume(Clip clip) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }

    /**
     * Method responsible for changing volume. Converts Clip's logarithmic audio volume controls to a linear scale,
     * between 0 and 1, for ease of use.
     * @param clip Clip whose volume should be changed
     * @param volume value of volume, between 0 and 1
     */
    public void setVolume(Clip clip, float volume) {
        if (volume < 0f || volume > 1f)
            throw new IllegalArgumentException("Volume not valid: " + volume);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    /**
     * Configurations related to battle theme playback.
     */
    private void configBattleTheme()
            throws UnsupportedAudioFileException, IOException, LineUnavailableException, URISyntaxException {

        AudioInputStream audioStreamLoop = AudioSystem.getAudioInputStream(battleThemeLoopInputStream);
        AudioFormat audioFormatLoop = audioStreamLoop.getFormat();
        DataLine.Info infoLoop = new DataLine.Info(Clip.class, audioFormatLoop);
        battleThemeLoopAudioClip = (Clip) AudioSystem.getLine(infoLoop);
        battleThemeLoopAudioClip.open(audioStreamLoop);
        setVolume(battleThemeLoopAudioClip, 0.25f);

        Media media = new Media(Objects.requireNonNull(
                getClass().getClassLoader().getResource("sound/battle_theme_intro.wav")).toURI().toString());
        introPlayer = new MediaPlayer(media);
        introPlayer.setVolume(0.25f);
        introPlayer.setOnEndOfMedia(() -> {
            battleThemeLoopAudioClip.setFramePosition(0);
            battleThemeLoopAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
        });
    }
    
    /**
     * Configurations related to victory theme playback.
     */
    private void configVictoryTheme()
            throws URISyntaxException, UnsupportedAudioFileException, IOException, LineUnavailableException {

        AudioInputStream audioStreamLoop = AudioSystem.getAudioInputStream(victoryThemeInputStream);
        AudioFormat audioFormatLoop = audioStreamLoop.getFormat();
        DataLine.Info infoLoop = new DataLine.Info(Clip.class, audioFormatLoop);
        victoryThemeLoopAudioClip = (Clip) AudioSystem.getLine(infoLoop);
        victoryThemeLoopAudioClip.open(audioStreamLoop);
        setVolume(victoryThemeLoopAudioClip, 0.25f);

        Media media = new Media(Objects.requireNonNull(
                getClass().getClassLoader().getResource("sound/victory_theme_intro.wav")).toURI().toString());
        victoryIntroPlayer = new MediaPlayer(media);
        victoryIntroPlayer.setVolume(0.25f);
        victoryIntroPlayer.setOnEndOfMedia(() -> {
            victoryThemeLoopAudioClip.setFramePosition(0);
            victoryThemeLoopAudioClip.loop(Clip.LOOP_CONTINUOUSLY);
        });
    }

    /**
     * Configurations related to low hp effect playback.
     */
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

    /**
     * Method which prepares the sound effect that should be played during hit animation.
     * @param typeEffect type effectiveness of used move
     * @return <code>InputStream</code> of requested sound effect
     * @see BattleLogic#calculateTypeEffect(Move, Type, Type) 
     */
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

    /**
     * Method which prepares the sound effect that should be played during stat changes.
     * @param statChange value of stat change that is occurring to a Pokémon on screen
     * @return <code>InputStream</code> of requested sound effect
     * @see BattleLogic#processStatChange(List, Move, Pokemon, boolean) 
     */
    private InputStream prepareStatChangeEffect(float statChange) {
        InputStream inputStream;

        if (statChange > 0)
            inputStream = getClass().getClassLoader().getResourceAsStream("sound/stat_raised.wav");
        else
            inputStream = getClass().getClassLoader().getResourceAsStream("sound/stat_fell.wav");

        return inputStream;
    }

    /**
     * Method which loads requested sound effect into the <code>audioEffectsClip</code> variable. This is the final
     * preparation before playing back the sound.
     * @param inputStream <code>InputStream</code> of the requested sound
     */
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

    /**
     * Method that plays the requested sound effect.
     * @param inputStream <code>InputStream</code> of the requested sound
     */
    private void playEffect(InputStream inputStream) {
        try {
            prepareEffectClip(inputStream);
            audioEffectsClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            System.out.println("[WARN] Failed playing sound effect " + ex);
        }
    }

    /**
     * Method that starts looped playback of the low HP sound effect (if it exists).
     */
    private void playLowHpEffect()  {
        if (allyLowHpClip != null) {
            if (!allyLowHpClip.isActive()) {
                allyLowHpClip.setFramePosition(0);
                allyLowHpClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    /**
     * Method that stops playback of the low HP sound effect (if it exists).
     */
    public void stopLowHpEffect() {
        if (allyLowHpClip != null)
            allyLowHpClip.stop();
    }

    /**
     * Method that prepares a Timeline object that begins playback of a hit effect. This most often should be 
     * played right before hit animations.
     * @param typeEffect type effectiveness of used move
     * @return <code>Timeline</code> object that executes sound playback when played
     * @see BattleLogic#calculateTypeEffect(Move, Type, Type) 
     */
    public Timeline getHitEffectClipPlayback(float typeEffect) {

        KeyFrame kf = new KeyFrame(Duration.millis(1), e -> {
            InputStream inputStream = prepareHitEffect(typeEffect);
            if (inputStream != null) {
                playEffect(inputStream);
            }
        });

        return new Timeline(kf);
    }

    /**
     * Method that prepares a Timeline object that begins playback of a stat change effect. This most often should be
     * played right before stat change animations
     * @param statChange value of stat change that is occurring to a Pokémon on screen
     * @return <code>Timeline</code> object that executes sound playback when played
     * @see BattleLogic#processStatChange(List, Move, Pokemon, boolean)
     */
    public Timeline getStatChangeClipPlayback(int statChange) {
        KeyFrame kf = new KeyFrame(Duration.millis(1), e -> {
            InputStream inputStream = prepareStatChangeEffect(statChange);
            if (inputStream != null) {
                playEffect(inputStream);
            }
        });

        return new Timeline(kf);
    }

    /**
     * Method that prepares a <code>KeyFrame</code> object that begins playback of a faint animation effect. This is used
     * as the first <code>KeyFrame</code> that is played during the faint animation
     * @return <code>KeyFrame</code> object which executes sound playback when played
     */
    private KeyFrame getPokemonFaintedClipPlayback() {
        return new KeyFrame(Duration.millis(1), e-> {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sound/pokemon_fainted.wav");
            if (inputStream != null) {
                playEffect(inputStream);
            }
        });
    }

    /**
     * Method that starts playing the victory theme (if it exists).
     */
    public void startVictoryThemePlayback() {
        if (victoryIntroPlayer != null)
            victoryIntroPlayer.play();
    }

    /**
     * Method that stops playing the victory theme (if it exists).
     */
    public void endVictoryThemePlayback() {
        if (victoryIntroPlayer != null)
            victoryIntroPlayer.stop();
        if (victoryThemeLoopAudioClip != null)
            victoryThemeLoopAudioClip.stop();
    }

    /**
     * Method that starts playing the battle theme (if it exists).
     */
    public void startBattleThemePlayback() {
        if (introPlayer != null)
            introPlayer.play();
    }

    /**
     * Method that stops playing the battle theme (if it exists).
     */
    public void endBattleThemePlayback() {
        if (introPlayer != null)
            introPlayer.stop();
        if (battleThemeLoopAudioClip != null)
            battleThemeLoopAudioClip.stop();
    }

    /**
     * Method that calls multiple visual configurations of buttons. This also handles custom button focus.
     */
    private void setupButtons() {
        setFightButton();
        setPokemonButton();
        setBagButton();
        setRunButton();

        optionGrid.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case LEFT:
                    bagButton.requestFocus();
                    break;
                case UP:
                    fightButton.requestFocus();
                    break;
                case RIGHT:
                    pokemonButton.requestFocus();
                    break;
                case DOWN:
                    runButton.requestFocus();
                    break;
            }
        });
    }

    /**
     * Generates rain animation.
     * @return <code>ParticleAnimationCreator</code> object containing the requested animation
     */
    public ParticleAnimationCreator generateRainAnimation() {
        int lowerBoundX = -300;
        int lowerBoundY = -1000;
        int upperBoundX = 1280;
        int upperBoundY = -20;
        int[] bounds = new int[]{lowerBoundX, lowerBoundY, upperBoundX, upperBoundY};

        ParticleGroup particleGroup = new ParticleGroup(new Image("sprites/particle_rain_drop.png"), 20, 20);
        particleGroup.addMultipleParticles(250);
        particleGroup.setVisible(false);
        particleGroup.addParticleGroupToPane(mainPane, 4);

        ParticleAnimationCreator particleAnimationCreator = new ParticleAnimationCreator(particleGroup);

        particleAnimationCreator.initRainAnimation(bounds, mainPane.getWidth(), mainPane.getHeight());
        particleAnimationCreator.setOnEnded(e -> {
            particleAnimationCreator.endRainAnimation(lowerBoundY, (int)mainPane.getHeight());
        });

        return particleAnimationCreator;
    }

    /**
     * Method responsible for setting up an option button visual effects
     * @param button one of the option buttons
     * @param index index of the option button; 0:Fight, 1:Pokémon, 2:Bag, 3:Run
     */
    private static void setButton(Button button, int index) {
        String styleRelease, stylePressed, styleHover;
        switch (index) {
            case 0:
                styleRelease = FIGHT_BUTTON_RELEASE;
                stylePressed = FIGHT_BUTTON_PRESSED;
                styleHover = FIGHT_BUTTON_HOVER;
                break;
            case 1:
                styleRelease = POKEMON_BUTTON_RELEASE;
                stylePressed = POKEMON_BUTTON_PRESSED;
                styleHover = POKEMON_BUTTON_HOVER;
                break;
            case 2:
                styleRelease = BAG_BUTTON_RELEASE;
                stylePressed = BAG_BUTTON_PRESSED;
                styleHover = BAG_BUTTON_HOVER;
                break;
            case 3:
                styleRelease = RUN_BUTTON_RELEASE;
                stylePressed = RUN_BUTTON_PRESSED;
                styleHover = RUN_BUTTON_HOVER;
                break;
            default:
                throw new ValueException("Unexpected index value: " + index);
        }

        button.setOnMousePressed(e -> {
            button.setStyle(stylePressed);
        });

        button.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE)
                button.setStyle(stylePressed);
        });

        button.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.SPACE)
                button.setStyle(styleRelease);
        });

        button.setOnMouseReleased(e -> {
            button.setStyle(styleRelease);
        });

        button.setOnMouseEntered(e -> {
            button.requestFocus();
            button.setText(">" + button.getText().substring(1));
            button.setStyle(styleHover);
        });

        button.focusedProperty().addListener(observable -> {
            if (button.isFocused()) {
                button.setText(">" + button.getText().substring(1));
                button.setStyle(styleHover);
            }
            else {
                button.setText(" " + button.getText().substring(1));
                button.setStyle(styleRelease);
            }
        });
    }

    /**
     * Method that sets up the Fight button
     */
    private void setFightButton() {
        setButton(fightButton, 0);
    }
    /**
     * Method that sets up the Pokémon button
     */
    private void setPokemonButton() {
        setButton(pokemonButton, 1);
    }
    /**
     * Method that sets up the Bag button
     */
    private void setBagButton() {
        setButton(bagButton, 2);
    }

    /**
     * Method that sets up the Run button
     */
    private void setRunButton() {
        setButton(runButton, 3);
    }

    /**
     * Method called to obtain an idle animation, which is played when waiting for player choice. It loops until it is
     * stopped manually.
     * @return <code>Timeline</code> object containing requested idle animation
     */
    private Timeline getBattleIdleAnimation() {

        double infoLayoutYPos = allyPokemonInfo.getLayoutY();
        double infoLayoutYMov = infoLayoutYPos - 3;

        double spriteLayoutYPos = allyPokemonSprite.getLayoutY();
        double spriteLayoutYMov = spriteLayoutYPos - 3;

        final KeyFrame kf1 = new KeyFrame(Duration.ZERO, e -> {
            if (!BattleApplication.isUseInternetSprites() && !BattleApplication.isUseLocalAnimSprites())
                allyPokemonSprite.setLayoutY(spriteLayoutYPos);
            allyPokemonInfo.setLayoutY(infoLayoutYPos);
        });
        final KeyFrame kf2 = new KeyFrame(Duration.seconds(0.3), e -> {
            if (!BattleApplication.isUseInternetSprites() && !BattleApplication.isUseLocalAnimSprites())
                allyPokemonSprite.setLayoutY(spriteLayoutYMov);
            allyPokemonInfo.setLayoutY(infoLayoutYMov);
        });

        Timeline timeline = new Timeline(kf1, kf2);
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.setOnFinished(e -> {
            if (!BattleApplication.isUseInternetSprites() && !BattleApplication.isUseLocalAnimSprites())
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

    /**
     * Sets the screen into choice or display mode. Choice mode allows the player to input the next command. Display
     * mode is used to display turn outcome.
     * @param choice when <code>true</code> enters choice mode, otherwise enters display mode
     */
    public void switchToPlayerChoice(boolean choice) {
        battleTextFull.setVisible(!choice);
        battleText.setVisible(choice);
        optionGrid.setVisible(choice);
        if (choice) {
            idleAnimation.play();
        }
        else {
            idleAnimation.stop();
            if (!BattleApplication.isUseInternetSprites() && !BattleApplication.isUseLocalAnimSprites())
                allyPokemonSprite.setLayoutY(ALLY_SPRITE_DEFAULT_Y);
            allyPokemonInfo.setLayoutY(ALLY_INFO_DEFAULT_Y);
        }
    }

    /**
     * Method used to signal when text should be wiped. Wipes text from both the full text display and the small text
     * display, known in code as <code>battleText</code> and <code>battleTextFull</code>.
     * @param inTimeline if <code>true</code> only returns text wipe action as <code>Timeline</code>, otherwise wipes 
     *                   text as soon as the method is called
     * @return <code>Timeline</code> object containing the action of text wipe
     */
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

    /**
     * Method called to put text into the textbox. All text appears as soon as the <code>Timeline</code> is played.
     * @param text <code>String</code> containing the text that should appear
     * @param full if <code>true</code> display text on <code>battleTextFull</code>, otherwise displays on
     *             <code>battleText</code>
     * @return <code>Timeline</code> object containing the action of filling screen with text
     * @see #getBattleTextAnimation(String, boolean) 
     */
    public Timeline getBattleText(String text, boolean full) {
        KeyFrame kf;
        if (full)
            kf = new KeyFrame(Duration.millis(1), e -> battleTextFull.setText(text));
        else
            kf = new KeyFrame(Duration.millis(1), e -> battleText.setText(text));

        return new Timeline(kf);
    }

    /**
     * Method called to put text into a textbox. Text appears letter by letter at a fixed speed. It can be sped up
     * when turbo mode is active.
     * @param text <code>String</code> containing the text that should appear
     * @param full if <code>true</code> display text on <code>battleTextFull</code>, otherwise displays on
     *             <code>battleText</code>
     * @return <code>Timeline</code> object containing the animation of filling screen with text letter by letter
     * @see #getBattleText(String, boolean)
     */
    public Timeline getBattleTextAnimation(String text, boolean full) {
        final List<KeyFrame> characterList = new ArrayList<>();
        final double modifier = turboMode ? 4 : 1;
        for (int i = 0; i <= text.length(); i++) {
            int finalI = i;
            final KeyFrame kf;
            if (full)
                kf = new KeyFrame(Duration.millis(20 / modifier * i), e -> battleTextFull.setText(text.substring(0, finalI)));
            else
                kf = new KeyFrame(Duration.millis(20 / modifier * i), e -> battleText.setText(text.substring(0, finalI)));
            characterList.add(kf);
        }

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(characterList);

        final KeyFrame kf;
        double ms = 20 / modifier * text.length() + 25;
        if (full)
            kf = new KeyFrame(Duration.millis(ms), e -> battleTextFull.setText(text));
        else
            kf = new KeyFrame(Duration.millis(ms), e -> battleText.setText(text));

        timeline.getKeyFrames().add(kf);

        return timeline;
    }

    /**
     * Makes the transition between two text animations be triggered by user input.
     * @param text <code>Timeline</code> object containing the pre transition text animation
     * @param nextText <code>Timeline</code> object containing the after transition text animation
     * @see #getBattleTextAnimation(String, boolean)
     */
    public void battleTextAdvanceByUserInput(Timeline text, Timeline nextText) {

        text.setOnFinished(e -> {

            final Timeline timeline = getTextArrowAnimation();

            mainPane.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    textArrowView.setVisible(false);
                    nextText.play();
                    mainPane.setOnMouseClicked(null);
                    battleTextFull.setOnKeyPressed(null);
                    timeline.stop();
                }
            });
            battleTextFull.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    textArrowView.setVisible(false);
                    nextText.play();
                    mainPane.setOnMouseClicked(null);
                    battleTextFull.setOnKeyPressed(null);
                    timeline.stop();
                }
            });

            textArrowView.setVisible(true);
            timeline.play();
            });
    }

    /**
     * Method that generates the text arrow animation visible when text can be advanced by user input. Loops until
     * stopped manually.
     * @return <code>Timeline</code> object containing the arrow animation
     */
    private Timeline getTextArrowAnimation() {

        textArrowView.setLayoutY(SCREEN_HEIGHT - 30);
        KeyValue keyValue = new KeyValue(textArrowView.layoutYProperty(), SCREEN_HEIGHT - 39);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(225), keyValue);

        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(keyFrame);
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

    /**
     * Method used to display the slide animation introducing an allied Pokémon's ability. This animation is played every
     * time a Pokémon's ability effect is triggered.
     * @param pokemon the Pokémon whose ability has been triggered
     * @return <code>Timeline</code> object containing the requested animation
     */
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

    /**
     * Method used to display the slide animation introducing an enemy Pokémon's ability. This animation is played every
     * time a Pokémon's ability effect is triggered.
     * @param pokemon the Pokémon whose ability has been triggered
     * @return <code>Timeline</code> object containing the requested animation
     */
    public Timeline getEnemyAbilityInfoAnimation(Pokemon pokemon) {
        final List<KeyFrame> keyFrameList = new ArrayList<>();
        double width = enemyAbilityInfo.getPrefWidth();
        //double screenWidth = enemyAbilityInfo.getScene().getWidth();
        double screenWidth = mainPane.getWidth();

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
    /**
     * Shifts the colors displayed on an <code>ImageView</code> to the specified color.
     * @param imageView <code>ImageView</code> which should have its color shifted
     * @param opacity opacity of this effect, the higher the value, the more pronounced the effect
     * @param color <code>Color</code> object representing the desired color of the effect
     * @see #resetColorShiftEffect(ImageView)
     */
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

    /**
     * Resets any applied color shift effect. This can also reset other effects that have been applied.
     * @param imageView <code>ImageView</code> object which should be reset
     * @see #setColorShiftEffect(ImageView, double, Color)
     */
    private void resetColorShiftEffect(ImageView imageView) {
        imageView.setEffect(null);
        imageView.setClip(null);
    }

    /**
     * Generates an intro animation for the Pokémon. This type of animation is used when a Pokémon is being sent out.
     * @param pokemon Pokémon to be sent out
     * @param hp HP value of sent out Pokémon
     * @return <code>Timeline</code> object representing the requested animation
     */
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

        final double[] regularWidth = new double[1];
        final double[] regularHeight = new double[1];
        final double[] bottomY = new double[1];
        final double[] centerX = new double[1];


        final List<KeyFrame> keyFrameList = new ArrayList<>();
        final InputStream soundInputStream = getClass().getClassLoader().getResourceAsStream("sound/pokemon_sent_out.wav");

        final KeyFrame configPokemonSprite = new KeyFrame(Duration.millis(1), e -> {
            if (soundInputStream != null) playEffect(soundInputStream);

            if (ally) setAllyInformation(pokemon, hp);
            else setEnemyInformation(pokemon, hp);

            if (!BattleApplication.isUseInternetSprites() && !BattleApplication.isUseLocalAnimSprites()) {
                regularWidth[0] = sprite.getFitWidth();
                regularHeight[0] = sprite.getFitHeight();
                bottomY[0] = sprite.getLayoutY() + sprite.getFitHeight();
                centerX[0] = sprite.getLayoutX() + sprite.getFitWidth() / 2;
            }
            else {
                regularWidth[0] = sprite.getImage().getWidth();
                regularHeight[0] = sprite.getImage().getHeight();
                bottomY[0] = sprite.getLayoutY() + sprite.getImage().getHeight();
                centerX[0] = sprite.getLayoutX() + sprite.getImage().getWidth() / 2;
            }

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

                sprite.setFitHeight((regularHeight[0] * finalI) / maxI);
                sprite.setFitWidth((regularWidth[0] * finalI) / maxI);
                double calcX = centerX[0] - (sprite.getFitWidth() / 2);
                double calcY = bottomY[0] - sprite.getFitHeight();
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

    /**
     * Configures enemy information on screen. This includes sprite image and the HP box visible beside the sprite.
     * @param pokemon Pokémon whose info should be entered
     */
    public void setEnemyInformation(Pokemon pokemon) {
        enemyNameLabel.setText(pokemon.getName());
        enemyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        enemyPokemonSprite.setImage(pokemon.getSpecie().getFrontSpriteBattle());
        //centerImage(enemyPokemonSprite);

        setEnemyHpBar(pokemon.getHp(), pokemon.getMaxHP());
    }

    /**
     * Configures enemy information on screen. This includes sprite image and the HP box visible beside the sprite.
     * Overrides the HP value displayed.
     * @param pokemon Pokémon whose info should be entered
     * @param overrideHp HP value which should be displayed instead of the one contained in Pokémon information.
     */
    public void setEnemyInformation(Pokemon pokemon, int overrideHp) {
        enemyNameLabel.setText(pokemon.getName());
        enemyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        enemyPokemonSprite.setImage(pokemon.getSpecie().getFrontSpriteBattle());
        //centerImage(enemyPokemonSprite);

        setEnemyHpBar(overrideHp, pokemon.getMaxHP());
    }

    /**
     * Sets the enemy Pokémon's HP bar.
     * @param hp the HP value that the bar should be set to
     * @param maxhp the maximum amount of HP the current Pokémon can have
     * @see #getEnemyHpAnimation(int, int, int)
     */
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

    /**
     * Generates an animation of losing (or gaining) HP for the enemy.
     * @param from starting amount of HP
     * @param to the amount of HP the starting amount should be reduced to
     * @param max maximum possible amount of HP for this Pokémon
     * @return <code>Timeline</code> object containing the requested animation
     * @see #setEnemyHpBar(int, int)
     */
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

    /**
     * Generates an animation for getting hit with a move.
     * @param ally if <code>true</code> generates animation for allied Pokémon, otherwise generates animation for enemy
     * @return <code>Timeline</code> object containing the requested animation
     */
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

    /**
     * Configures the status label to display the requested status. Changes text and background color.
     * @param statusLabel label that should be formatted
     * @param status the status which should be displayed by the label
     */
    public static void setStatusStyle(Label statusLabel, Enums.Status status) {
        statusLabel.setTextFill(Color.WHITE);

        final String style = "-fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: ";
        String finalStyle = "";

        switch (status) {
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

        if (status != Enums.Status.NONE) {
            statusLabel.setStyle(finalStyle);
            statusLabel.setVisible(true);
        }
    }

    /**
     * Method that generates an animation responsible for displaying a Pokémon's status change.
     * @param ally sets ally's status label if <code>true</code>, the enemy's otherwise
     * @param status determines which status should be represented by the label
     * @return <code>Timeline</code> object containing the requested animation
     */
    public Timeline updateStatus(boolean ally, Enums.Status status) {

        final Label statusLabel;
        if (ally)
            statusLabel = allyStatusLabel;
        else
            statusLabel = enemyStatusLabel;

        final KeyFrame kf = new KeyFrame(Duration.millis(1), e-> {
            setStatusStyle(statusLabel, status);
        });

        return new Timeline(kf);
    }

    /**
     * Generates an animation that changes the weather effect present on the battlefield.
     * @param weatherEffect determines which weather should the field be changed to
     * @return <code>Timeline</code> object containing the requested animation
     */
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
            case SANDSTORM:
                kf = new KeyFrame(Duration.millis(1), e-> {
                    mainPane.setStyle(WEATHER_SANDSTORM);
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
    /**
     * Configures ally information on screen. This includes sprite image and the HP box visible beside the sprite.
     * @param pokemon Pokémon whose info should be entered
     */
    public void setAllyInformation(Pokemon pokemon) {
        allyNameLabel.setText(pokemon.getName());
        allyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        allyPokemonSprite.setImage(pokemon.getSpecie().getBackSpriteBattle());
        //centerImage(allyPokemonSprite);

        setAllyHpBar(pokemon.getHp(), pokemon.getMaxHP(), true);
    }

    /**
     * Configures ally information on screen. This includes sprite image and the HP box visible beside the sprite.
     * Overrides the HP value displayed.
     * @param pokemon Pokémon whose info should be entered
     * @param overrideHp HP value which should be displayed instead of the one contained in Pokémon information.
     */
    public void setAllyInformation(Pokemon pokemon, int overrideHp) {
        allyNameLabel.setText(pokemon.getName());
        allyLvLabel.setText(String.format("Lv. %d", pokemon.getLevel()));

        allyPokemonSprite.setImage(pokemon.getSpecie().getBackSpriteBattle());
        //centerImage(allyPokemonSprite);

        setAllyHpBar(overrideHp, pokemon.getMaxHP(), true);
    }

    /**
     * Sets the ally Pokémon's HP bar.
     * @param hp the HP value that the bar should be set to
     * @param maxhp the maximum amount of HP the current Pokémon can have
     * @param applySound set to <code>true</code> to check if low hp sound playback should be started or stopped
     * @see #getAllyHpAnimation(int, int, int)
     */
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

    /**
     * Generates an animation of losing (or gaining) HP for the ally.
     * @param from starting amount of HP
     * @param to the amount of HP the starting amount should be reduced to
     * @param max maximum possible amount of HP for this Pokémon
     * @return <code>Timeline</code> object containing the requested animation
     * @see #setAllyHpBar(int, int, boolean)
     */
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

    /**
     * Generates a fainted animation.
     * @param ally if <code>true</code> applies animation to ally, otherwise applies it to enemy
     * @return <code>Timeline</code> object containing the requested animation
     */
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
        double scale;
        if (!BattleApplication.isUseInternetSprites() && !BattleApplication.isUseLocalAnimSprites())
            scale = sprite.getFitHeight() / height;
        else
            scale = sprite.getImage().getHeight() / height;


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

    /**
     * Generates animation of Pokémon being recalled by the trainer.
     * @param ally if <code>true</code> applies animation to ally, otherwise applies it to enemy
     * @return <code>Timeline</code> object containing the requested animation
     */
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

    private static String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

    /**
     * Converts <code>Color</code> object to a hexadecimal value representation in <code>String</code> format.
     * @param value color that should be converted
     * @return converted color <code>String</code>
     */
    public static String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }

    /**
     * Method called to display information about the currently highlighted move. This includes type and PP.
     * @param move move which information should be displayed
     */
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

    /**
     * Sets move information into the specified button. This also initiates visual related listeners.
     * @param button button into which the information should be inserted
     * @param move the move which information should be inserted into the button
     */
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
            HBox typeBox = (HBox) moveTypeBox.getChildren().get(0);
            HBox ppBox = (HBox) moveTypeBox.getChildren().get(1);
            typeBox.getChildren().get(1).setVisible(false);
            ppBox.getChildren().get(1).setVisible(false);
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
            HBox typeBox = (HBox) moveTypeBox.getChildren().get(0);
            HBox ppBox = (HBox) moveTypeBox.getChildren().get(1);
            typeBox.getChildren().get(1).setVisible(true);
            ppBox.getChildren().get(1).setVisible(true);
        });
    }

    /**
     * Updates the information of available moves.
     * @param pokemon Pokémon which moves should be inserted into buttons.
     */
    public void updateAvailableMoves(Pokemon pokemon) {
        List<Move> moveList = pokemon.getMoveList();
        int size = moveList.size();

        String setColorNoMove = "-fx-font: 11 monospaced; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 5; " +
                "-fx-border-color: black; -fx-background-color: lightgray";

        for (int i=0; i<4; i++) {

            Button moveButton = (Button) moveGrid.getChildren().get(i);

            if (i < size) {
                moveButton.setDisable(false);
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

    /**
     * Updates the party boxes, which contain basic info about the party size and if each individual member is able
     * to battle.
     * @param allyPokemon list containing the ally's party
     * @param enemyPokemon list containing the enemy's party
     * @param enemySeen an array that tracks which enemy party members were seen by the player
     */
    public void updatePokemonPartyBox(List<Pokemon> allyPokemon, List<Pokemon> enemyPokemon, boolean[] enemySeen) {
        updateAllyPartyBox(allyPokemon);
        updateEnemyStatusBox(enemyPokemon, enemySeen);
    }

    /**
     * Updates the ally's party box, which contain basic info about the party size and if each individual member is able
     * to battle.
     * @param allyPokemon list containing the ally's party
     */
    private void updateAllyPartyBox(List<Pokemon> allyPokemon) {
        int allySize = allyPokemon.size();
        ArrayList<ImageView> allImageViews = new ArrayList<>();
        addAllImageViewFromBox(allyPokemonPartyBox, allImageViews);

        for (int i=0; i < 6; i++) {
            List<ImageView> subList = allImageViews.subList(i*2, 2+i*2);
            ImageView allyPokemonStatusBackground = subList.get(0);
            ImageView allyPokemonStatusForeground = subList.get(1);
            if (allySize > i) {
                allyPokemonStatusForeground.setImage(allyPokemon.get(i).getSpecie().getFrontSpriteThumbnail());
                allyPokemonStatusBackground.setImage(pokemonIndicatorNormal);
                if (allyPokemon.get(i).getStatus() == Enums.Status.FAINTED) {
                    setColorShiftEffect(allyPokemonStatusForeground, 1, Color.GRAY);
                    setColorShiftEffect(allyPokemonStatusBackground, 1, Color.GRAY);
                }
                else {
                    resetColorShiftEffect(allyPokemonStatusForeground);
                    resetColorShiftEffect(allyPokemonStatusBackground);
                }
            }
            else {
                allyPokemonStatusForeground.setImage(null);
                allyPokemonStatusBackground.setImage(pokemonIndicatorEmpty);
                resetColorShiftEffect(allyPokemonStatusBackground);
            }
        }
    }

    /**
     * Updates the ally's party box, which contain basic info about the party size and if each individual member is able
     * to battle.
     * @param enemyPokemon list containing the enemy's party
     * @param seenArray an array that tracks which enemy party members were seen by the player
     */
    private void updateEnemyStatusBox(List<Pokemon> enemyPokemon, boolean[] seenArray) {
        int enemySize = enemyPokemon.size();
        ArrayList<ImageView> allImageViews = new ArrayList<>();
        addAllImageViewFromBox(enemyPokemonPartyBox, allImageViews);

        for (int i=0; i < 6; i++) {
            List<ImageView> subList = allImageViews.subList((5-i)*2, 2+(5-i)*2);
            ImageView enemyPokemonStatusBackground = subList.get(0);
            ImageView enemyPokemonStatusForeground = subList.get(1);

            if (enemySize > i) {
                if (seenArray[i])
                    enemyPokemonStatusForeground.setImage(enemyPokemon.get(i).getSpecie().getFrontSpriteThumbnail());
                else
                    enemyPokemonStatusForeground.setImage(null);

                enemyPokemonStatusBackground.setImage(pokemonIndicatorNormal);

                if (enemyPokemon.get(i).getStatus() == Enums.Status.FAINTED) {
                    setColorShiftEffect(enemyPokemonStatusBackground, 1, Color.GRAY);
                    if (enemyPokemonStatusForeground.getImage() != null)
                        setColorShiftEffect(enemyPokemonStatusForeground, 1, Color.GRAY);
                }
                else {
                    resetColorShiftEffect(enemyPokemonStatusBackground);
                    resetColorShiftEffect(enemyPokemonStatusForeground);
                }
            }
            else {
                enemyPokemonStatusForeground.setImage(null);
                enemyPokemonStatusBackground.setImage(pokemonIndicatorEmpty);
                resetColorShiftEffect(enemyPokemonStatusBackground);
            }
        }
    }

    /**
     * Generates the animation that is displayed at the start of the battle. It makes each of the individual party icon
     * appear on screen.
     * @return <code>Timeline</code> object containing the requested animation
     */
    public Timeline getPokemonPartyBoxAnimation() {
        ArrayList<ImageView> allyStatusPanes = new ArrayList<>();
        addAllImageViewFromBox(allyPokemonPartyBox, allyStatusPanes);

        ArrayList<ImageView> enemyStatusPanes = new ArrayList<>();
        addAllImageViewFromBox(enemyPokemonPartyBox, enemyStatusPanes);

        KeyFrame setup = new KeyFrame(Duration.millis(1), e -> {
            for (ImageView node : allyStatusPanes) {
                node.setOpacity(0);
                node.setFitWidth(1);
                node.setFitHeight(1);
            }
            for (ImageView node : enemyStatusPanes) {
                node.setOpacity(0);
                node.setFitWidth(1);
                node.setFitHeight(1);
            }
        });

        Timeline timeline = new Timeline(setup);

        for (int j=0; j<6; j++) {

            List<ImageView> allyNodes = allyStatusPanes.subList(2*(5-j), 2+2*(5-j));
            List<ImageView> enemyNodes = enemyStatusPanes.subList(2*(5-j), 2+2*(5-j));

            for (int i=1; i<=100; i++) {
                final ImageView allyIconBackground = allyNodes.get(0);
                final ImageView allyIconForeground = allyNodes.get(1);
                final ImageView enemyIconBackground = enemyNodes.get(0);
                final ImageView enemyIconForeground = enemyNodes.get(1);

                double backgroundWidth = allyIconBackground.getFitWidth();
                double pokemonWidth = allyIconForeground.getFitWidth();


                final double newSize = backgroundWidth * i / 100.0;
                final double newSizePokemon = pokemonWidth * i / 100.0;
                final double newOpacity = i / 100.0;

                final KeyFrame kf = new KeyFrame(Duration.millis(i * 3 + j * 300), e -> {
                    modifySizeAndOpacity(allyIconForeground, allyIconBackground, newSize, newSizePokemon, newOpacity);
                    modifySizeAndOpacity(enemyIconForeground, enemyIconBackground, newSize, newSizePokemon, newOpacity);
                });
                timeline.getKeyFrames().add(kf);
            }
        }

        return timeline;
    }

    /**
     * Modifies size and opacity of a party box member to the specifies values.
     * @param iconForeground object containing the member sprite
     * @param iconBackground object containing the member background
     * @param newSize value of the size to which the background should be scaled
     * @param newSizePokemon value of the size which the sprite should be scaled
     * @param newOpacity value of the opacity that should be applied to the group
     */
    private static void modifySizeAndOpacity(ImageView iconForeground, ImageView iconBackground, double newSize,
                                             double newSizePokemon, double newOpacity) {
        iconBackground.setOpacity(newOpacity);
        iconForeground.setOpacity(newOpacity);

        iconBackground.setFitWidth(newSize);
        iconBackground.setFitHeight(newSize);

        iconForeground.setFitWidth(newSizePokemon);
        iconForeground.setFitHeight(newSizePokemon);
    }

    /**
     * Adds all found ImageView objects found among the children of the parent node. This also includes the children
     * of the found parent nodes.
     * @param parent parent node which should be searched
     * @param nodes list into which all found <code>ImageView</code> nodes are inserted
     */
    private static void addAllImageViewFromBox(Parent parent, ArrayList<ImageView> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof ImageView)
                nodes.add((ImageView) node);
            else if (node instanceof Parent)
                addAllImageViewFromBox((Parent)node, nodes);
        }
    }

    /**
     * Old function used for inserting Pokémon information into buttons, The Pokémon menu has been inserted into a
     * separate screen.
     * @param party list of player's party
     * @deprecated
     */
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

    /**
     * Generates an animation tied to the Substitute move. It fades away the user and places a substitute in place of it.
     * @param pokemon move user
     * @return <code>Timeline</code> object which contains the requested animation
     */
    public List<Timeline> generateSubstituteAppearAnimation(Pokemon pokemon) {
        final boolean isPlayer = pokemon.getOwner().isPlayer();

        List<Timeline> substituteTimeline = new ArrayList<>();

        Timeline pokemonSpriteFadeTimeline = new Timeline();

        ImageView pokemonSprite = isPlayer ? allyPokemonSprite : enemyPokemonSprite;
        final double spriteX = pokemonSprite.getLayoutX();
        final double spriteY = pokemonSprite.getLayoutY();
        final double spriteHeight;
        final double spriteWidth;

        if (BattleApplication.isUseInternetSprites() || BattleApplication.isUseLocalAnimSprites()) {
            spriteHeight = pokemonSprite.getImage().getHeight();
            spriteWidth = pokemonSprite.getImage().getWidth();
        }
        else {
            spriteHeight = pokemonSprite.getFitHeight();
            spriteWidth = pokemonSprite.getFitWidth();
        }

        KeyFrame setupSprite = new KeyFrame(Duration.millis(0.1), e -> {
            pokemonSprite.setPreserveRatio(false);
        });

        pokemonSpriteFadeTimeline.getKeyFrames().add(setupSprite);

        for (int i=1; i < spriteWidth; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i * 0.5), e -> {
                pokemonSprite.setFitWidth(spriteWidth - finalI);
                pokemonSprite.setLayoutX(spriteX + (finalI / 2.0));
                pokemonSprite.setFitHeight(spriteHeight + (finalI / 2.5));
                pokemonSprite.setLayoutY(spriteY - (finalI / 2.5));
            });
            pokemonSpriteFadeTimeline.getKeyFrames().add(kf);
        }

        KeyFrame makeSpriteInvisible = new KeyFrame(Duration.millis(spriteWidth / 2), e -> {
            pokemonSprite.setVisible(false);
            pokemonSprite.setLayoutX(spriteX);
            pokemonSprite.setLayoutY(spriteY);
            pokemonSprite.setPreserveRatio(true);
            pokemonSprite.setFitWidth(spriteWidth);
            pokemonSprite.setFitHeight(spriteHeight);
        });

        pokemonSpriteFadeTimeline.getKeyFrames().add(makeSpriteInvisible);

        Timeline substituteAppearTimeline = new Timeline();

        double distance = Math.abs(-spriteHeight - spriteY);

        Image substituteImage;
        String spritePath = isPlayer ? "/sprites/substitute_back.png" : "/sprites/substitute_front.png";
        URL frontSpriteUrl = getClass().getResource(spritePath);

        if (frontSpriteUrl != null)
            substituteImage = new Image(spritePath);
        else
            substituteImage = new Image("sprites/default.png");


        substituteImage = PokemonSpecie.alignBottom(substituteImage);
        substituteImage = PokemonSpecie.resample(substituteImage, 5);

        final Image processedSubstituteImage = substituteImage;

        KeyFrame setSubstituteSprite = new KeyFrame(Duration.millis(0.1), e -> {
            pokemonSprite.setImage(processedSubstituteImage);
            if (BattleApplication.isUseInternetSprites() || BattleApplication.isUseLocalAnimSprites()) {
                pokemonSprite.setFitWidth(spriteWidth);
                pokemonSprite.setFitHeight(spriteHeight);
                pokemonSprite.setLayoutX(spriteX);
                pokemonSprite.setLayoutY(-spriteHeight);
            }
            pokemonSprite.setVisible(true);
        });

        substituteAppearTimeline.getKeyFrames().add(setSubstituteSprite);

        for (int i=1; i < distance + 600; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(0.5 * i), e -> {
                if (finalI <= distance)
                    pokemonSprite.setLayoutY(-spriteHeight + finalI);
                else
                    // Bounce animation calculation, jumps for 150 pixels, then falls back down to original position
                    // sine function allows to observe a sense of momentum when rising and falling back down
                    pokemonSprite.setLayoutY(spriteY - (150 * Math.sin((finalI - distance) * Math.PI / 600)));
            });

            substituteAppearTimeline.getKeyFrames().add(kf);
        }

        final KeyFrame cleanup = new KeyFrame(Duration.millis((distance + 600) * 0.5), e -> {
            pokemonSprite.setLayoutY(spriteY);
        });

        substituteAppearTimeline.getKeyFrames().add(cleanup);


        substituteTimeline.add(pokemonSpriteFadeTimeline);
        substituteTimeline.add(generatePause(500));
        substituteTimeline.add(substituteAppearTimeline);
        return substituteTimeline;
    }

    /**
     * Generates the animation of a substitute being destroyed, and the user returning to the battlefield.
     * @param pokemon move user
     * @return <code>Timeline</code> object which contains the requested animation
     */
    public List<Timeline> generateSubstituteFadeAnimation(Pokemon pokemon) {
        final boolean isPlayer = pokemon.getOwner().isPlayer();

        List<Timeline> substituteTimeline = new ArrayList<>();

        Timeline substituteFadeTimeline = new Timeline();

        ImageView pokemonSprite = isPlayer ? allyPokemonSprite : enemyPokemonSprite;
        final double spriteX = pokemonSprite.getLayoutX();
        //final double spriteY = pokemonSprite.getLayoutY();
        //final double spriteHeight = pokemonSprite.getFitHeight();
        final double spriteWidth;

        if (BattleApplication.isUseInternetSprites() || BattleApplication.isUseLocalAnimSprites()) {
            spriteWidth = pokemonSprite.getImage().getWidth();
        }
        else {
            spriteWidth = pokemonSprite.getFitWidth();
        }

        for (int i=1; i < 100; i++) {
            int finalI = i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i*2), e -> {
                pokemonSprite.setOpacity((100 - finalI) / 100.0);
            });
            substituteFadeTimeline.getKeyFrames().add(kf);
        }

        final double hideX = isPlayer ? (-spriteWidth) : (mainPane.getWidth() + spriteWidth);

        KeyFrame hideSprite = new KeyFrame(Duration.millis(100 * 2), e -> {
            pokemonSprite.setVisible(false);
            pokemonSprite.setOpacity(1);
            pokemonSprite.setImage(isPlayer ? pokemon.getSpecie().getBackSpriteBattle()
                    : pokemon.getSpecie().getFrontSpriteBattle());
            pokemonSprite.setLayoutX(hideX);
            pokemonSprite.setVisible(true);
        });

        substituteFadeTimeline.getKeyFrames().add(hideSprite);

        Timeline showSpriteBack;

        final double distance;
        if (isPlayer)
            distance = Math.abs(spriteX - hideX);
        else
            distance = Math.abs(hideX - spriteX);

//        for (int i=0; i < distance; i++) {
//            int finalI = i;
//            final KeyFrame kf = new KeyFrame(Duration.millis(i), e -> {
//               if (isPlayer)
//                   pokemonSprite.setLayoutX(hideX + finalI);
//               else
//                   pokemonSprite.setLayoutX(hideX - finalI);
//            });
//            showSpriteBack.getKeyFrames().add(kf);
//        }
        if (isPlayer)
            showSpriteBack = moveRight(pokemonSprite, distance, 1);
        else
            showSpriteBack = moveRight(pokemonSprite, -distance, 1);

        final KeyFrame cleanup = new KeyFrame(Duration.millis(distance+1), e -> {
            pokemonSprite.setLayoutX(spriteX);
        });

        showSpriteBack.getKeyFrames().add(cleanup);

        substituteTimeline.add(substituteFadeTimeline);
        substituteTimeline.add(generatePause(500));
        substituteTimeline.add(showSpriteBack);
        return substituteTimeline;
    }

    /**
     * Moves a node down by the specified amount
     * @param node node which should be moved
     * @param distance distance by which the node should be moved down, if negative moves the node up
     * @param frameTime determines how long each frame should last
     * @return <code>Timeline</code> object which contains the requested animation
     */
    private static Timeline moveDown(Node node, double distance, double frameTime) {

        Timeline output = new Timeline();

        final double[] currentPosition = new double[1];

        for (int i=1; i <= Math.abs(distance); i++) {
            int finalI = distance > 0 ? i : -i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i*frameTime), e -> {
                if (finalI == 1 || finalI == -1)
                    currentPosition[0] = node.getLayoutY();
                node.setLayoutY(currentPosition[0] + finalI);
            });
            output.getKeyFrames().add(kf);
        }

        return output;
    }

    /**
     * Moves a node right by the specified amount
     * @param node node which should be moved
     * @param distance distance by which the node should be moved right, if negative moves the node left
     * @param frameTime determines how long each frame should last
     * @return <code>Timeline</code> object which contains the requested animation
     */
    private static Timeline moveRight(Node node, double distance, double frameTime) {

        Timeline output = new Timeline();
        final double[] currentPosition = new double[1];

        for (int i=1; i <= Math.abs(distance); i++) {
            int finalI = distance > 0 ? i : -i;
            final KeyFrame kf = new KeyFrame(Duration.millis(i*frameTime), e -> {
                if (finalI == 1 || finalI == -1)
                    currentPosition[0] = node.getLayoutX();
                node.setLayoutX(currentPosition[0] + finalI);
            });
            output.getKeyFrames().add(kf);
        }

        return output;
    }

    /**
     * Generates a pause in animation. This pause is greatly shortened when turbo mode is active.
     * @param millis amount of milliseconds that the pause should last
     * @return <code>Timeline</code> object that represents the requested pause
     */
    public Timeline generatePause(double millis) {
        double modifier = turboMode ? millis : 1;
        return new Timeline(new KeyFrame(Duration.millis(millis / modifier)));
    }

    /**
     * Method that is executed when the Fight button is pressed
     * @param pokemon Pokémon which moves should appear on the screen
     */
    public void fightButtonPressed(Pokemon pokemon) {

        updateAvailableMoves(pokemon);

        optionGrid.setVisible(false);
        moveGrid.setVisible(true);
    }

    /**
     * Method that is executed when the back (when browsing available moves) button is pressed
     */
    public void backButtonPressed() {
        optionGrid.setVisible(true);
        moveGrid.setVisible(false);
    }

    /**
     * Method that is executed when the Pokémon button is pressed. Unused because of the changes to the Pokémon menu.
     * @deprecated
     */
    public void pokemonButtonPressed(List<Pokemon> party) {
        optionGrid.setVisible(false);
        pokemonGrid.setVisible(true);

        updateSelectPokemonButtons(party);

        //updateAvailablePokemon(party);
    }

    /**
     * Returns the node of specified row and column position.
     * @param gridPane <code>GridPane</code> from which the node should be returned
     * @param col node's column position
     * @param row node's row position
     * @return requested node
     */
    public Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }
}