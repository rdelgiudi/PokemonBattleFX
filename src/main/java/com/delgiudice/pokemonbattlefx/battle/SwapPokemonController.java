package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.BattleApplication;
import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.item.Item;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.network.SwitchDataReceive;
import com.delgiudice.pokemonbattlefx.network.SwitchDataSend;
import com.delgiudice.pokemonbattlefx.network.SyncThread;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import com.delgiudice.pokemonbattlefx.trainer.Player;
import com.sun.istack.internal.NotNull;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SwapPokemonController {

    private static final String FAINTED_STYLE = "-fx-border-color: black; -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 15; " +
            "-fx-background-color: linear-gradient(to left, lightcoral, azure)";
    private static final String REGULAR_STYLE = "-fx-border-color: black; -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 15; " +
            "-fx-background-color: linear-gradient(to left, lightskyblue, azure)";
    private static final String HOVER_STYLE = "-fx-border-color: black; -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 15; " +
            "-fx-background-color: azure";

    private static final String OPTIONS_REGULAR_STYLE = "-fx-background-color: skyblue; -fx-border-color: black; -fx-border-width: 5";
    private static final String OPTIONS_HOVER_STYLE = "-fx-background-color: lightblue; -fx-border-color: black; -fx-border-width: 5";
    private static final String OPTIONS_PRESSED_STYLE = "-fx-background-color: lightskyblue; -fx-border-color: black; -fx-border-width: 5";

    private Pane battlePane;
    private BattleLogic battleLogic;
    private BattleController battleController;

    private final FXMLLoader summaryLoader;
    private final Pane summaryPane;

    private List<Pokemon> party;
    private Player player;

    private boolean useItem;
    private Item item;
    private Enums.SwitchContext switchContext;
    private List<Pokemon> turnPokemon;
    private Move secondMove;

    private List<InvalidationListener> animatedSpriteListeners = new ArrayList<>();
    private List<Double> maxFitWidth = new ArrayList<>();
    private List<Double> maxFitHeight = new ArrayList<>();

    @FXML
    private Pane mainPane;
    @FXML
    private HBox currentPokemonBox;
    @FXML
    private VBox pokemonBox, switchOptionsBox;
    @FXML
    private Label infoLabel, selectedPokemonLabel;
    @FXML
    private Button cancelButton, switchOutButton, summaryButton, closeButton;

    /**
     * Class constructor
     */
    public SwapPokemonController() {
        summaryLoader = new FXMLLoader(BattleApplication.class.getResource("summary-view.fxml"));
        try {
            summaryPane = summaryLoader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        //BattleApplication.letterbox(summaryScene, pane);
    }

    /**
     * Configures UI elements
     */
    public void initialize() {
        initListeners();

        Rectangle rect = new Rectangle(BattleController.SCREEN_WIDTH, BattleController.SCREEN_HEIGHT);
        mainPane.setClip(rect);

        HBox primaryViewBox = (HBox) currentPokemonBox.getChildren().get(0);
        ImageView primaryView = (ImageView) primaryViewBox.getChildren().get(0);
        maxFitWidth.add(primaryView.getFitWidth());
        maxFitHeight.add(primaryView.getFitHeight());
        animatedSpriteListeners.add(new InvalidationListener() {
            @Override
            public void invalidated(Observable e) {
                primaryView.setFitWidth(primaryView.getImage().getWidth() / 3.5 / 110 * maxFitWidth.get(0));
                primaryView.setFitHeight(primaryView.getImage().getWidth() / 3.5 / 110 * maxFitHeight.get(0));
            }
        });

        for (int i=0; i<5; i++) {
            HBox partyMemberBox = (HBox) pokemonBox.getChildren().get(i);
            HBox partyMemberBoxBox = (HBox) partyMemberBox.getChildren().get(0);
            ImageView partyMemberView = (ImageView) partyMemberBoxBox.getChildren().get(0);
            maxFitWidth.add(partyMemberView.getFitWidth());
            maxFitHeight.add(partyMemberView.getFitHeight());
            int finalI = i;
            animatedSpriteListeners.add(new InvalidationListener() {
                @Override
                public void invalidated(Observable e) {
                    partyMemberView.setFitWidth(partyMemberView.getImage().getWidth() / 3.5 / 110 * maxFitWidth.get(finalI +1));
                    partyMemberView.setFitHeight(partyMemberView.getImage().getWidth() / 3.5 / 110 * maxFitHeight.get(finalI +1));
                }
            });
        }
    }

    /**
     * Passes along information to the <code>SummaryController</code> object that the sprite mode has been switched
     * @see SummaryController#processSpriteModeSwitch()
     */
    public void processSpriteModeSwitch() {
        SummaryController summaryController = summaryLoader.getController();
        summaryController.processSpriteModeSwitch();

        HBox primaryViewBox = (HBox) currentPokemonBox.getChildren().get(0);
        ImageView primaryView = (ImageView) primaryViewBox.getChildren().get(0);

        if (BattleApplication.isUseInternetSprites() || BattleApplication.isUseLocalAnimSprites()) {
            primaryView.imageProperty().addListener(animatedSpriteListeners.get(0));
        }
        else {
            primaryView.imageProperty().removeListener(animatedSpriteListeners.get(0));
            primaryView.setFitWidth(maxFitWidth.get(0));
            primaryView.setFitHeight(maxFitHeight.get(0));
        }

        for (int i=0; i<5; i++) {
            HBox partyMemberBox = (HBox) pokemonBox.getChildren().get(i);
            HBox partyMemberBoxBox = (HBox) partyMemberBox.getChildren().get(0);
            ImageView partyMemberView = (ImageView) partyMemberBoxBox.getChildren().get(0);

            if (BattleApplication.isUseInternetSprites() || BattleApplication.isUseLocalAnimSprites()) {
                partyMemberView.imageProperty().addListener(animatedSpriteListeners.get(i+1));
            }
            else {
                partyMemberView.imageProperty().removeListener(animatedSpriteListeners.get(i+1));
                partyMemberView.setFitWidth(maxFitWidth.get(i+1));
                partyMemberView.setFitHeight(maxFitHeight.get(i+1));
            }
        }
    }

    /**
     * Initiates variables for opening this screen to select a Pokémon on which to use an item. This item was selected
     * in the previous screen.
     * @param battlePane screen from which the bag menu was initiated
     * @param bagPane screen from which the item was selected (previous screen to this)
     * @param logic <code>BattleLogic</code> object which handles all logic related to the battle itself
     * @param controller controller which handles UI changes related to the battle
     * @param party player party using the in battle order
     * @param player object that represents the player's trainer
     * @param item item selected in the previous screen
     */
    public void initVariablesBag(Pane battlePane, Pane bagPane, BattleLogic logic, BattleController controller,
                                 List<Pokemon> party, Player player, Item item) {
        useItem = true;
        this.item = item;
        this.battlePane = battlePane;
        battleLogic = logic;
        battleController = controller;
        this.party = party;
        this.player = player;

        infoLabel.setText("Choose a POKÉMON.");

        cancelButton.setOnAction(e -> {
            Scene scene = cancelButton.getScene();
            scene.setRoot(bagPane);
        });
        initPokemonInfo(false);
    }

    /**
     * Initiates variables for opening this screen to select a Pokémon to switch into.
     * @param battlePane screen from which the switch action is initiated
     * @param logic <code>BattleLogic</code> object which handles all logic related to the battle itself
     * @param controller controller which handles UI changes related to the battle
     * @param party player party using the in battle order
     * @param force if <code>true</code>, it doesn't allow the player to back out of the switch, this is used when the
     *              previous Pokémon has fainted or used a move that initiates a switch automatically
     * @param switchContext context of the switch
     * @param turnPokemon list of Pokémon currently taking part in the turn, ordered by attack order
     * @param secondMove next move to be used after the switch
     */
    public void initVariablesSwitch(Pane battlePane, BattleLogic logic, BattleController controller, List<Pokemon> party,
                                    boolean force, Enums.SwitchContext switchContext, List<Pokemon> turnPokemon, Move secondMove) {
        useItem = false;
        item = null;
        this.battlePane = battlePane;
        battleLogic = logic;
        this.party = party;
        battleController = controller;
        initPokemonInfo(force);
        this.switchContext = switchContext;
        this.turnPokemon = turnPokemon;
        this.secondMove = secondMove;

        infoLabel.setText("Choose a POKÉMON.");

        cancelButton.setOnAction(e -> {
            Scene scene = cancelButton.getScene();
            scene.setRoot(battlePane);
        });
    }

    /**
     * Generates an animation that sets the information text at the bottom of the screen. Makes the letters appear all
     * at once.
     * @param text nex text to be set
     * @return <code>Timeline</code> object containing the requested animation
     */
    public Timeline getInfoText(String text) {
        KeyFrame kf = new KeyFrame(Duration.millis(1), e -> infoLabel.setText(text));

        return new Timeline(kf);
    }

    /**
     * Sets the Pokémon information into their respective tabs.
     * @param force <code>true</code> if player is forced to switch, <code>false</code> otherwise
     */
    private void initPokemonInfo(boolean force) {

        int currentAllyPokemon = 0;

        cancelButton.setDisable(party.get(currentAllyPokemon).getHp() == 0 || force);
        switchOptionsBox.setVisible(false);

        setPokemonInfo(currentPokemonBox, currentAllyPokemon);

        int partySize = party.size();

        for (int i = 1; i < 6; i++) {

            HBox box = (HBox) pokemonBox.getChildren().get(i-1);

            if (i >= partySize) {
                disableBox(box);
                continue;
            }

            setPokemonInfo(box, i);
        }
    }

    /**
     * This method is called if the slot isn't filled with any Pokémon. It sets its state to empty, blocking any
     * interaction with this box.
     * @param pokemonBox box to which this setting is to be applied
     */
    private void disableBox(HBox pokemonBox) {

        HBox iconBox = (HBox) pokemonBox.getChildren().get(0);
        ImageView icon = (ImageView) iconBox.getChildren().get(0);
        VBox infoBox = (VBox) pokemonBox.getChildren().get(1);
        VBox hpBox = (VBox) pokemonBox.getChildren().get(2);

        icon.setVisible(false);
        infoBox.setVisible(false);
        hpBox.setVisible(false);

        String style = "-fx-border-color: black; -fx-border-width: 5; -fx-border-radius: 10; -fx-background-radius: 15; " +
                "-fx-background-color:  linear-gradient(to left, dodgerblue, lightgray)";

        pokemonBox.setStyle(style);

        pokemonBox.setOnMouseEntered(null);
        pokemonBox.setOnMouseExited(null);
        pokemonBox.setOnMouseClicked(null);
    }

    /**
     * Sets the HP bar to the desired amount.
     * @param hp value to which the bar should be set
     * @param maxhp maximum possible HP value
     * @param hpBar object representing the HP bar
     * @param hpLabel text label containing the numerical information about HP
     */
    private void setHpBar(int hp, int maxhp, ProgressBar hpBar, Label hpLabel) {
        double currentHpPercentage = (double) hp / maxhp;
        if (currentHpPercentage < 0.03 && hp != 0)
            currentHpPercentage = 0.03;

        hpBar.setProgress(currentHpPercentage);
        hpLabel.setText(String.format("%3d/%-3d", hp, maxhp));

        if (currentHpPercentage > 0.56)
            hpBar.setStyle("-fx-accent: green");
        else if (currentHpPercentage > 0.21)
            hpBar.setStyle("-fx-accent: yellow");
        else
            hpBar.setStyle("-fx-accent: red");
    }

    /**
     * Generates animation of increasing (or decreasing) the amount of HP on the selected HP bar.
     * @param from initial amount of HP
     * @param to target amount of HP
     * @param max maximum amount of HP
     * @param hpBar object representing the HP bar
     * @param hpLabel text label containing the numerical information about HP
     * @return <code>Timeline</code> object containing the requested animation
     */
    @NotNull
    private Timeline getHpAnimation(int from, int to, int max, ProgressBar hpBar, Label hpLabel) {
        final List<KeyFrame> keyFrameList = new ArrayList<>();

        if (from == to || max < to || max < from)
            throw new ValueException("The values of from and to can't be equal!");

        boolean descending = from > to;

        double frameTime = 1000.0 / max;
        int frameCount = Math.abs(from - to);

        for (int i = 1; i <= frameCount; i++) {
            final KeyFrame kf;
            final int finalI = i;

            if (descending)
                kf = new KeyFrame(Duration.millis(frameTime * i), e ->
                        setHpBar(from - finalI, max, hpBar, hpLabel));
            else
                kf = new KeyFrame(Duration.millis(frameTime * i), e ->
                        setHpBar(from + finalI, max, hpBar, hpLabel));
            keyFrameList.add(kf);
        }

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrameList);
        return timeline;
    }

    /**
     * Fills one of the boxes with information about a Pokémon.
     * @param pokemonBox selected box
     * @param index points to which of the player party Pokémon's info should be used
     */
    private void setPokemonInfo(HBox pokemonBox, int index) {

        Pokemon pokemon = party.get(index);

        HBox iconBox = (HBox) pokemonBox.getChildren().get(0);
        ImageView icon = (ImageView) iconBox.getChildren().get(0);
        icon.setImage(pokemon.getSpecie().getFrontSprite());

        VBox infoBox = (VBox) pokemonBox.getChildren().get(1);

        Label pokemonName = (Label) infoBox.getChildren().get(0);
        Label pokemonLevel = (Label) infoBox.getChildren().get(1);

        pokemonName.setText(pokemon.getName());
        pokemonLevel.setText("Lv. " + pokemon.getLevel());

        VBox hpBox;

        if (index == 0)
            hpBox = (VBox) infoBox.getChildren().get(2);
        else
            hpBox = (VBox) pokemonBox.getChildren().get(2);

        HBox hpBarBox = (HBox) hpBox.getChildren().get(0);
        ProgressBar hpBar = (ProgressBar) hpBarBox.getChildren().get(1);
        Label hpLabel = (Label) hpBox.getChildren().get(1);
        setHpBar(pokemon.getHp(), pokemon.getMaxHP(), hpBar, hpLabel);

        Label statusLabel = (Label) hpBox.getChildren().get(2);

        BattleController.setStatusStyle(statusLabel, pokemon.getStatus());

        if (pokemon.getHp() == 0)
            pokemonBox.setStyle(FAINTED_STYLE);
        else
            pokemonBox.setStyle(REGULAR_STYLE);

        initBoxElementListener(pokemonBox, pokemon, index, hpBar, hpLabel);
        icon.setVisible(true);
        infoBox.setVisible(true);
        hpBox.setVisible(true);
    }

    /**
     * Initiates visual listeners for the buttons
     */
    private void initListeners() {
        switchOutButton.setOnMouseEntered(e -> {
            switchOutButton.setStyle(OPTIONS_HOVER_STYLE);
        });
        switchOutButton.setOnMouseExited(e -> {
            switchOutButton.setStyle(OPTIONS_REGULAR_STYLE);
        });
        switchOutButton.setOnMousePressed(e -> {
            switchOutButton.setStyle(OPTIONS_PRESSED_STYLE);
        });
        switchOutButton.setOnMouseReleased(e -> {
            switchOutButton.setStyle(OPTIONS_REGULAR_STYLE);
        });

        summaryButton.setOnMouseEntered(e -> {
            summaryButton.setStyle(OPTIONS_HOVER_STYLE);
        });
        summaryButton.setOnMouseExited(e -> {
            summaryButton.setStyle(OPTIONS_REGULAR_STYLE);
        });
        summaryButton.setOnMousePressed(e -> {
            summaryButton.setStyle(OPTIONS_PRESSED_STYLE);
        });
        summaryButton.setOnMouseReleased(e -> {
            summaryButton.setStyle(OPTIONS_REGULAR_STYLE);
        });

        closeButton.setOnMouseEntered(e -> {
            closeButton.setStyle(OPTIONS_HOVER_STYLE);
        });
        closeButton.setOnMouseExited(e -> {
            closeButton.setStyle(OPTIONS_REGULAR_STYLE);
        });
        closeButton.setOnMousePressed(e -> {
            closeButton.setStyle(OPTIONS_PRESSED_STYLE);
        });
        closeButton.setOnMouseReleased(e -> {
            closeButton.setStyle(OPTIONS_REGULAR_STYLE);
        });

        cancelButton.setOnMouseEntered(e -> {
            cancelButton.setStyle(OPTIONS_HOVER_STYLE);
        });
        cancelButton.setOnMouseExited(e -> {
            cancelButton.setStyle(OPTIONS_REGULAR_STYLE);
        });
        cancelButton.setOnMousePressed(e -> {
            cancelButton.setStyle(OPTIONS_PRESSED_STYLE);
        });
        cancelButton.setOnMouseReleased(e -> {
            cancelButton.setStyle(OPTIONS_REGULAR_STYLE);
        });
    }

    /**
     * Method called when player selects a Pokémon after selecting an item from the bag. Checks for item type and
     * executes appropriate method.
     * @param pokemon target Pokémon of the item
     * @param pokemonSelectBox box in which te selected Pokémon's info are situated
     * @param index index of the selected Pokémon
     * @param hpBar object representing the selected Pokémon's HP bar
     * @param hpLabel text representing the numerical value of HP
     */
    private void useItem(Pokemon pokemon, HBox pokemonSelectBox, int index, ProgressBar hpBar, Label hpLabel) {
        currentPokemonBox.setDisable(true);
        this.pokemonBox.setDisable(true);
        cancelButton.setDisable(true);
        switch (item.getType()) {
            case HP_RESTORE:
                useHealingItem(pokemon, pokemonSelectBox, index, hpBar, hpLabel);
                break;
            case PP_RESTORE:
                break;
            case STATUS_HEALING:
                useStatusHealingItem(pokemon, pokemonSelectBox, index, hpBar, hpLabel);
                break;
            case X_ITEMS:
                break;
        }
    }

    /**
     * Method that initiates logical and visual listeners for the selected box. Behaves differently depending on the
     * mode in which the screen was initiated in.
     * @param pokemonSelectBox the box whose listeners are to be initiated
     * @param pokemon the party member contained in the box
     * @param index the position in the player's party of Pokémon contained in the box
     * @param hpBar object representing the HP bar of this Pokémon
     * @param hpLabel label representing the numerical value of HP
     */
    private void initBoxElementListener(HBox pokemonSelectBox, Pokemon pokemon, int index, ProgressBar hpBar, Label hpLabel) {
        if (!useItem) {
            pokemonSelectBox.setOnMouseClicked(e -> {

                // Adding parent layout location applies a correction for the elements in a VBox, since without correction
                // it doesn't account for the offset of it
                double correctionX = pokemonSelectBox.getParent().getClass() == VBox.class ? pokemonSelectBox.getParent().getLayoutX() : 0;
                double correctionY = pokemonSelectBox.getParent().getClass() == VBox.class ? pokemonSelectBox.getParent().getLayoutY() : 0;

                double mouseLayoutX = e.getX() + pokemonSelectBox.getLayoutX() + correctionX;
                double mouseLayoutY = e.getY() + pokemonSelectBox.getLayoutY() + correctionY;

                double boxWidth = switchOptionsBox.getWidth();
                double boxHeight = switchOptionsBox.getHeight();

                if (mouseLayoutX + boxWidth > mainPane.getWidth())
                    switchOptionsBox.setLayoutX(mouseLayoutX - boxWidth);
                else
                    switchOptionsBox.setLayoutX(mouseLayoutX);

                if (mouseLayoutY + boxHeight > mainPane.getHeight())
                    switchOptionsBox.setLayoutY(mouseLayoutY - boxHeight);
                else
                    switchOptionsBox.setLayoutY(mouseLayoutY);

                initSwapMenuListener(pokemonSelectBox, pokemon, index);
            });
        }
        else {
            pokemonSelectBox.setOnMouseClicked(e -> {
                useItem(pokemon, pokemonSelectBox, index, hpBar, hpLabel);
            });
        }

        pokemonSelectBox.setOnMouseEntered(e -> pokemonSelectBox.setStyle(HOVER_STYLE));
        pokemonSelectBox.setOnMouseExited(e -> {
            if (pokemon.getHp() == 0)
                pokemonSelectBox.setStyle(FAINTED_STYLE);
            else
                pokemonSelectBox.setStyle(REGULAR_STYLE);
        });
    }

    /**
     * Checks if healing item can be used on selected Pokémon
     * @param item selected item
     * @param target item target
     * @return <code>true</code> if item can be used, <code>false</code> otherwise
     */
    public static boolean checkHealingItemViable(Item item, Pokemon target) {
        boolean fullHealPokemonAbnormalNonFaintStatus = item.getStatusHeal().equals(Enums.Status.ANY) &&
                (!target.getStatus().equals(Enums.Status.NONE) || target.getSubStatuses().contains(Enums.SubStatus.CONFUSED))
                && !target.getStatus().equals(Enums.Status.FAINTED);

        return (target.getHp() != target.getMaxHP() && target.getStatus() != Enums.Status.FAINTED) ||
                fullHealPokemonAbnormalNonFaintStatus;
    }

    /**
     * Calculates how much health is restored by using the specified item.
     * @param item selected item
     * @param target item target
     * @return amount of health restored
     */
    public static int calculateHealingItemEffect(Item item, Pokemon target) {
        int healValue;
        int hpToMax = target.getMaxHP() - target.getHp();
        int itemValue = item.getValue() == -1 ? hpToMax : item.getValue();
        healValue = Math.min(hpToMax, itemValue);
        return healValue;
    }

    /**
     * Tries to use item on selected Pokémon. If item cannot be used on selected Pokémon, displays information to player
     * and returns to Pokémon selection.
     * @param target target of the selected item
     * @param pokemonSelectBox box containing information about the selected Pokémon
     * @param index index of the selected Pokémon in the player's party
     * @param hpBar object representing the Pokémon's HP bar
     * @param hpLabel text representing the numerical value of HP
     */
    private void useHealingItem(Pokemon target, HBox pokemonSelectBox, int index, ProgressBar hpBar, Label hpLabel) {
        if (!checkHealingItemViable(item, target)) {
            playIncompatibleItemMessage(target);
            return;
        }

        List<Timeline> battleTimeLine = new ArrayList<>();

        int healValue = calculateHealingItemEffect(item, target);
        Timeline healAnimation;
        if (healValue > 0)
            healAnimation = getHpAnimation(target.getHp(), target.getHp() + healValue, target.getMaxHP(),
                hpBar, hpLabel);
        else
            healAnimation = battleController.generatePause(1);
        Timeline healInfo = getInfoText(String.format(
                "%s restored %d HP.", target.getBattleName(), healValue));
        Timeline pause = battleController.generatePause(1000);

        healAnimation.setOnFinished(e -> healInfo.play());
        healInfo.setOnFinished(e -> pause.play());

        TrainerAction itemAction = new TrainerAction(Enums.ActionTypes.USE_BAG_ITEM, item.getName(), index);

        target.setHp(target.getHp() + healValue);

        if (item.getStatusHeal().equals(Enums.Status.ANY)) {
            target.setStatus(Enums.Status.NONE);
            target.getSubStatuses().remove(Enums.SubStatus.CONFUSED);
            target.setConfusionTimer(0);
            setPokemonInfo(pokemonSelectBox, index);
        }

        if (index == 0) {
            battleController.setAllyHpBar(target.getHp(), target.getMaxHP(), true);
            battleController.updateStatus(true, target.getStatus()).play();
        }
        reduceItemAmount();

        battleTimeLine.add(healAnimation);
        battleTimeLine.add(healInfo);
        battleTimeLine.add(pause);
        //healAnimation.play();
        returnToBattle(itemAction, battleTimeLine);
    }

    /**
     * Checks if status healing item can be used on selected target.
     * @param item selected item
     * @param target item target
     * @return <code>true</code> if item can be used, <code>false</code> otherwise
     */
    public static boolean checkStatusHealingItemViable(Item item, Pokemon target) {
        boolean fullHealPokemonAbnormalNonFaintStatus = item.getStatusHeal().equals(Enums.Status.ANY) &&
                (!target.getStatus().equals(Enums.Status.NONE) || target.getSubStatuses().contains(Enums.SubStatus.CONFUSED))
                && !target.getStatus().equals(Enums.Status.FAINTED);

        return target.getStatus().equals(item.getStatusHeal()) || fullHealPokemonAbnormalNonFaintStatus;
    }

    /**
     * Processes the effects of selected status healing item. If the item revives fainted Pokémon, it also restores HP.
     * @param item selected item
     * @param target item target
     * @return amount of health restored by the item, if item doesn't restore health returns 0
     */
    public static int processStatusHealingEffect(Item item, Pokemon target) {
        target.setStatus(Enums.Status.NONE);
        int healValue = 0;
        if (item.getStatusHeal() == Enums.Status.ANY) {
            target.getSubStatuses().remove(Enums.SubStatus.CONFUSED);
            target.setConfusionTimer(0);
        }
        if (item.getStatusHeal().equals(Enums.Status.FAINTED) && target.getHp() == 0) {
            healValue = item.getValue() == Item.MAX_HP ? target.getMaxHP() : (int) Math.floor(target.getMaxHP() / 2.0);
        }
        return healValue;
    }

    /**
     * Tries to use a status healing item on selected Pokémon. If item cannot be used on selected Pokémon,
     * displays information to player and returns to Pokémon selection.
     * @param target item target
     * @param pokemonSelectBox box containing the target's information
     * @param index index of the target in the player's party
     * @param hpBar object representing the HP bar of the target
     * @param hpLabel label representing the numerical value of target's HP.
     */
    private void useStatusHealingItem(Pokemon target, HBox pokemonSelectBox, int index, ProgressBar hpBar, Label hpLabel) {
        if (!checkStatusHealingItemViable(item, target)){
            playIncompatibleItemMessage(target);
            return;
        }

        List<Timeline> battleTimeLine = new ArrayList<>();

        int healValue = processStatusHealingEffect(item, target);
        setPokemonInfo(pokemonSelectBox, index);
        Timeline healAnimation = battleController.generatePause(1);

        if (item.getStatusHeal().equals(Enums.Status.FAINTED) && target.getHp() == 0) {
            healAnimation = getHpAnimation(0, healValue, target.getMaxHP(), hpBar, hpLabel);
            target.setHp(healValue);
        }

        if (index == 0)
            battleController.updateStatus(true, target.getStatus()).play();

        Timeline healInfo = getInfoText(String.format(
                "%s's status was cleared.", target.getBattleName()));
        Timeline pause = battleController.generatePause(1000);

        TrainerAction itemAction = new TrainerAction(Enums.ActionTypes.USE_BAG_ITEM, item.getName(), index);

        reduceItemAmount();
        battleTimeLine.add(healAnimation);
        battleTimeLine.add(healInfo);
        battleTimeLine.add(pause);
        returnToBattle(itemAction, battleTimeLine);
    }

    /**
     * Displays message that item cannot be used on selected target.
     * @param target selected Pokémon
     */
    private void playIncompatibleItemMessage(Pokemon target) {
        Timeline noUseItemMessage = getInfoText(String.format(
                "%s can't use this item!", target.getBattleName()));
        Timeline resetText = getInfoText("Choose a POKÉMON.");
        resetText.setDelay(Duration.seconds(1));
        noUseItemMessage.setOnFinished(actionEvent -> {
            resetText.play();
        });
        resetText.setOnFinished(e -> {
            this.pokemonBox.setDisable(false);
            currentPokemonBox.setDisable(false);
            cancelButton.setDisable(false);
        });
        noUseItemMessage.play();
    }

    /**
     * Reduces the amount of items of selected type by 1
     */
    private void reduceItemAmount() {
        int itemRemaining = player.getItems().get(item);
        if (itemRemaining == 1)
            player.getItems().remove(item);
        else {
            itemRemaining--;
            player.getItems().put(item, itemRemaining);
        }
    }

    /**
     * After successfully completing the selected item action, returns to the battle screen.
     * @param itemAction selected item represented by itemAction
     * @param battleTimeLine list containing all <code>Timeline</code> objects to be played during current turn
     */
    private void returnToBattle(TrainerAction itemAction, List<Timeline> battleTimeLine) {
        battleTimeLine.add(new Timeline(new KeyFrame(Duration.millis(1), e -> {
            Scene scene = cancelButton.getScene();
            scene.setRoot(battlePane);
            this.pokemonBox.setDisable(false);
            currentPokemonBox.setDisable(false);
            cancelButton.setDisable(false);
        })));

        battleLogic.waitEnemyAction(itemAction, battleTimeLine);
    }

    /**
     * Initiates listeners of the swap menu which appears if player presses on a Pokémon box when in swap mode.
     * @param pokemonBox selected box
     * @param pokemon selected party member
     * @param index index of party member in player's party
     */
    private void initSwapMenuListener(HBox pokemonBox, Pokemon pokemon, int index) {

        int currentAllyPokemon = 0;
        boolean allyFainted = party.get(currentAllyPokemon).getHp() == 0;

        selectedPokemonLabel.setText(String.format("%d: %-12s", index+1, pokemon.getName()));

        String setColorIntro = "-fx-border-radius: 10; -fx-background-radius: 15; -fx-border-width: 5; " +
                "-fx-border-color: ";
        String halfAndHalf = "linear-gradient(from 0%% 50%% to 100%% 50%%, %s, %s)";
        Color[] buttonBorderColor = new Color[2];
        buttonBorderColor[0] = pokemon.getType()[0].getTypeEnum().getTypeColor();
        if (pokemon.getType()[1].getTypeEnum() == Enums.Types.NO_TYPE) {
            buttonBorderColor[1] = buttonBorderColor[0];
        }
        else {
            buttonBorderColor[1] = pokemon.getType()[1].getTypeEnum().getTypeColor();
        }
        String[] colorHex = new String[2];
        colorHex[0] = BattleController.toHexString(buttonBorderColor[0]);
        colorHex[1] = BattleController.toHexString(buttonBorderColor[1]);
        String colorHexComplete = String.format(halfAndHalf, colorHex[0], colorHex[1]);
        selectedPokemonLabel.setStyle(setColorIntro + colorHexComplete);

        switchOutButton.setOnAction(event -> {
            if (index == currentAllyPokemon) {
                Timeline alreadyInBattleInfo;
                if (!allyFainted)
                    alreadyInBattleInfo = getInfoText(String.format(
                            "%s is already in battle!", pokemon.getBattleName()));
                else
                    alreadyInBattleInfo = getInfoText(String.format(
                            "%s has just fainted!", pokemon.getBattleName()));

                Timeline resetText = getInfoText("Choose a POKÉMON.");
                resetText.setDelay(Duration.seconds(1));
                alreadyInBattleInfo.setOnFinished(actionEvent -> resetText.play());
                alreadyInBattleInfo.play();
                return;
            }

            if (pokemon.getHp() == 0) {
                Timeline faintedInfo = getInfoText(String.format(
                        "%s is fainted! Can't switch!",
                        pokemon.getName()));
                Timeline resetText = getInfoText("Choose a POKÉMON.");
                resetText.setDelay(Duration.seconds(1));
                faintedInfo.setOnFinished(actionEvent -> resetText.play());
                faintedInfo.play();
                return;
            }

            if (party.get(currentAllyPokemon).getTrapMove() != null && !allyFainted
                    && switchContext != Enums.SwitchContext.SWITCH_FIRST_MOVE
                    && switchContext != Enums.SwitchContext.SWITCH_SECOND_MOVE) {
                Timeline trappedInfo = getInfoText(String.format(
                        "%s is trapped! Can't switch!",
                        party.get(currentAllyPokemon).getName()));
                Timeline resetText = getInfoText("Choose a POKÉMON.");
                resetText.setDelay(Duration.seconds(1));
                trappedInfo.setOnFinished(actionEvent -> resetText.play());
                trappedInfo.play();
                return;
            }

            String currentAllyName = party.get(currentAllyPokemon).getBattleName();

            battleController.switchToPlayerChoice(false);

            List<Timeline> battleTimeLine = new ArrayList<>();

            if (!allyFainted) {
                if (switchContext != Enums.SwitchContext.SWITCH_SECOND_MOVE
                        && switchContext != Enums.SwitchContext.SWITCH_FIRST_MOVE) {
                    Timeline allyPokemonReturnText = battleController.getBattleTextAnimation(String.format(
                            "That's enough %s,%ncome back!", currentAllyName), true);
                    allyPokemonReturnText.setDelay(Duration.seconds(0.1));
                    battleTimeLine.add(allyPokemonReturnText);
                }

                Timeline allyPokemonReturn = battleController.getPokemonReturnAnimation(true);
                allyPokemonReturn.setDelay(Duration.seconds(1));
                battleTimeLine.add(allyPokemonReturn);
                battleTimeLine.add(battleController.generatePause(1000));
            }

            //int newCurrentAllyPokemon = 0;

            //controller.setAllyInformation(player.getParty(currentAllyPokemon));
            Timeline allyPokemonIntro = battleController.getBattleTextAnimation(String.format(BattleLogic.POKEMON_SENT_OUT_STRING,
                    party.get(index).getBattleName()), true);
            battleTimeLine.add(allyPokemonIntro);

            Timeline updateStatus = battleController.updateStatus(true,
                    party.get(index).getStatus());
            battleTimeLine.add(updateStatus);

            Timeline allyInfoAnimation = battleController.getIntroAnimation(party.get(index),
                    party.get(index).getHp());
            battleTimeLine.add(allyInfoAnimation);

            Scene scene = pokemonBox.getScene();
            scene.setRoot(battlePane);

            TrainerAction playerAction = new TrainerAction(Enums.ActionTypes.SWITCH_POKEMON, String.valueOf(index));
            if (battleLogic.getGameMode() != Enums.GameMode.OFFLINE && switchContext != Enums.SwitchContext.SWITCH_FIRST
                && switchContext != Enums.SwitchContext.SWITCH_SECOND) {
                battleController.getBattleText(BattleLogic.AWAITING_SYNC, true).play();
                SwitchDataSend syncThread = new SwitchDataSend(battleLogic.getInputStream(), battleLogic.getOutputStream(),
                        playerAction, this, battleTimeLine, index);
                syncThread.start();
            }
            else
                finalizeSwitchOut(battleTimeLine, index, playerAction);
        });

        summaryButton.setOnAction(event -> {
            Scene scene = pokemonBox.getScene();

            SummaryController summaryController = summaryLoader.getController();
            summaryController.setParty(party);
            summaryController.displayPokemonStat(index);
            summaryController.setPreviousPane((Pane) scene.getRoot());

            scene.setRoot(summaryPane);
            switchOptionsBox.setVisible(false);
        });

        closeButton.setOnAction(event -> switchOptionsBox.setVisible(false));

        switchOptionsBox.setVisible(true);
    }

    /**
     * Method called when finalizing switching out. Depending on the switch context, sends player to different moments
     * of the turn.
     * @param battleTimeLine list containing all <code>Timeline</code> objects to be played during current turn
     * @param index index of the selected Pokémon
     * @param playerAction <code>TrainerAction</code> representing player choice
     */
    public void finalizeSwitchOut(List<Timeline> battleTimeLine, int index, TrainerAction playerAction) {
        if (switchContext == Enums.SwitchContext.SWITCH_FIRST_MOVE) {
            if (turnPokemon == null || secondMove == null)
                throw new ValueException("Values of turnPokemon and secondMove expected to be not null in this context");
            battleLogic.switchPokemon(true, index);
            battleLogic.applySentOutEffects(battleTimeLine);
            battleLogic.processSecondMove(battleTimeLine, party.get(0), turnPokemon.get(1), secondMove);
        }
        else if (switchContext == Enums.SwitchContext.SWITCH_FIRST) {
            //battleLogic.initAnimationQueue(battleTimeLine);
            //battleTimeLine.get(battleTimeLine.size() - 1).setOnFinished(e -> {
            //});
            //battleTimeLine.get(0).play();
            battleLogic.waitEnemyAction(playerAction, battleTimeLine);
        }
        else if (switchContext == Enums.SwitchContext.SWITCH_SECOND_MOVE || switchContext == Enums.SwitchContext.SWITCH_SECOND) {
            battleLogic.switchPokemon(true, index);
            battleLogic.applySentOutEffects(battleTimeLine);
            battleLogic.battleTurnEnd(battleTimeLine);
        }
        else if (switchContext == Enums.SwitchContext.SWITCH_FAINTED) {
            battleLogic.switchPokemon(true, index);
            battleLogic.applySentOutEffects(battleTimeLine);
            battleLogic.finalChecks(battleTimeLine);
        }
    }
}
