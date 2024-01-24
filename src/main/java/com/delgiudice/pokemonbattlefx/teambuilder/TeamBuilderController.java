package com.delgiudice.pokemonbattlefx.teambuilder;

import com.delgiudice.pokemonbattlefx.*;
import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.BattleController;
import com.delgiudice.pokemonbattlefx.battle.BattleLogic;
import com.delgiudice.pokemonbattlefx.battle.SummaryController;
import com.delgiudice.pokemonbattlefx.item.Item;
import com.delgiudice.pokemonbattlefx.move.Move;
import com.delgiudice.pokemonbattlefx.move.MoveEnum;
import com.delgiudice.pokemonbattlefx.move.MoveTemplate;
import com.delgiudice.pokemonbattlefx.network.ClientThread;
import com.delgiudice.pokemonbattlefx.network.ServerThread;
import com.delgiudice.pokemonbattlefx.pokemon.Ability;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import com.delgiudice.pokemonbattlefx.pokemon.PokemonEnum;
import com.delgiudice.pokemonbattlefx.pokemon.PokemonSpecie;
import com.delgiudice.pokemonbattlefx.trainer.NpcTrainer;
import com.delgiudice.pokemonbattlefx.trainer.OnlineTrainer;
import com.delgiudice.pokemonbattlefx.trainer.Player;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class TeamBuilderController {

    private static String PLAYER_PARTY_STYLE = "-fx-background-color: #87CEEB";
    private static String ENEMY_PARTY_STYLE = "-fx-background-color: lightcoral";
    @FXML
    private HBox playerPartyBox, enemyPartyBox;
    @FXML
    private VBox playerSettingsBox;
    @FXML
    private GridPane pokemonGrid;
    @FXML
    private Button playerSettingsButton, enemySettingsButton, startBattleButton, multiplayerOptionsButton;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField ipAddressField, playerNameField;
    @FXML
    private Label waitingForConnectionLabel;
    @FXML
    private CheckBox turboModeCheckBox, animatedModeCheckBox;

    List<Pokemon> playerParty = new ArrayList<>(), enemyParty = new ArrayList<>();

    ArrayList<Pokemon> sortedPokemon = new ArrayList<>();

    String playerName = "Red", enemyName = "Joey";

    Enums.TrainerTypes trainerType = Enums.TrainerTypes.YOUNGSTER;

    FXMLLoader addPokemonLoader, battleLoader;
    Pane addPokemonPane, battlePane;

    BattleController battleController;
    BattleLogic logic;

    private int FONT_SIZE = 13, POKEMON_BUTTON_WIDTH = 125, POKEMON_BUTTON_HEIGHT = 100, POKEMON_PANE_SIZE = 450;
    private int PARTY_BUTTON_WIDTH = 100, PARTY_BUTTON_HEIGHT = 100;
    private boolean turboMode = false;

    private Enums.GameMode gameMode = Enums.GameMode.OFFLINE;

    private Thread connectionThread;

    private DataOutputStream outputStream;
    private DataInputStream inputStream;


    public void setOutputStream(DataOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setInputStream(DataInputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Class constructor
     */
    public TeamBuilderController() {
        Pokemon.generatePokemonExamples();

        battleLoader = new FXMLLoader(BattleApplication.class.getResource("battle-view.fxml"));
        //Pane teamBuilderPane = (Pane) startBattleButton.getScene().getRoot();
        try {
            battlePane = battleLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Item.setItemMap();

        battleController = battleLoader.getController();
        logic = new BattleLogic(battleController);
    }

    /**
     * Configures UI elements and creates a list of available Pokémon, sorted by Pokédex number.
     */
    public void initialize() {

        sortedPokemon.addAll(Pokemon.getPokemonExamples().values());

        sortedPokemon.sort(new Comparator<Pokemon>() {
            @Override
            public int compare(Pokemon o1, Pokemon o2) {
                return o1.getPokedexNumber() - o2.getPokedexNumber();
            }
        });

        populatePokemonGrid();
        refreshParties();
        setButtonEffect(playerSettingsButton);
        setButtonEffect(enemySettingsButton);
        setButtonEffect(startBattleButton);
        preparePartyButtonEffects();
        preparePartyButtons(playerPartyBox, playerParty, true);
        preparePartyButtons(enemyPartyBox, enemyParty, false);

        addPokemonLoader = new FXMLLoader(BattleApplication.class.getResource("addpokemon-view.fxml"));
        addPokemonPane = null;
        try {
            addPokemonPane = addPokemonLoader.load();
            //addPokemonScene = new Scene(addPokemonPane, 1280, 720);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        setupStringField(playerNameField, 15);
    }

    /**
     * Opens player settings menu.
     */
    @FXML
    private void openPlayerSettings() {
        playerSettingsBox.setVisible(true);
        disableInput(true);
    }

    /**
     * Closes player settings menu.
     */
    @FXML
    private void closePlayerSettings() {
        playerSettingsBox.setVisible(false);
        updatePlayerName();
        disableInput(false);
    }

    /**
     * Updates the player name variable.
     * @see #playerName
     */
    @FXML
    private void updatePlayerName() {
        playerName = playerNameField.getText();
    }

    /**
     * Changes the <code>turboMode</code> variable to the state in the <code>CheckBox</code>
     * @see #turboModeCheckBox
     */
    @FXML
    private void toggleTurboMode() {
        turboMode = turboModeCheckBox.isSelected();
    }

    /**
     * Toggles the option to use animated sprites downloaded from the internet.
     * @see BattleController#processSpriteModeSwitch()
     * @see AddPokemonController#processSpriteModeSwitch()
     * @see SummaryController#processSpriteModeSwitch()
     */
    @FXML
    private void toggleAnimatedMode() {
        BattleApplication.setUseInternetSprites(animatedModeCheckBox.isSelected());
        battleController.processSpriteModeSwitch();
        AddPokemonController addPokemonController = addPokemonLoader.getController();
        addPokemonController.processSpriteModeSwitch();
        logic.processSpriteModeSwitch();
    }

    /**
     * Method that makes the selected text field accept only letters and also limits the amount of characters.
     * @param textField selected text field
     * @param characterLimit limit of characters to be set
     */
    protected static void setupStringField(TextField textField, int characterLimit) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*")) {
                textField.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
            if (newValue.length() > characterLimit)
                textField.setText(newValue.substring(0, characterLimit));
        });
    }

    /**
     * Method that makes the selected text field accept only characters that occur in move names and also limits the
     * amount of characters.
     * @param textField selected text field
     * @param characterLimit limit of characters to be set
     */
    protected static void setupStringFieldMove(TextField textField, int characterLimit) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z-*")) {
                textField.setText(newValue.replaceAll("[^\\sa-zA-Z-]", ""));
            }
            if (newValue.length() > characterLimit)
                textField.setText(newValue.substring(0, characterLimit));
        });
    }

    /**
     * Old method that controlled automatic resizing of UI elements. Unused
     * @deprecated
     */
    public void setUiResizeListener() {
        Scene scene = startBattleButton.getScene();

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            FONT_SIZE = (int)Math.round(scene.getWidth() / 98.46);
            POKEMON_BUTTON_WIDTH = (int)Math.round(scene.getWidth() / 10.24);
            //POKEMON_BUTTON_HEIGHT = (int)Math.round(scene.getWidth() / 12.8);
            PARTY_BUTTON_WIDTH = (int)Math.round(scene.getWidth() / 12.8);
            //POKEMON_PANE_SIZE = (int)Math.round(scene.getWidth() / 2.84);
            refreshUi();
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            POKEMON_BUTTON_HEIGHT = (int)Math.round(scene.getHeight() / 7.2);
            POKEMON_PANE_SIZE = (int)Math.round(scene.getHeight() / 1.6);
            PARTY_BUTTON_HEIGHT = (int)Math.round(scene.getHeight() / 7.2);
            refreshUi();
        });
    }

    /**
     * Changes the mode in which the battle will run.
     * @see com.delgiudice.pokemonbattlefx.attributes.Enums.GameMode
     */
    @FXML
    private void changeGameMode() {

        int gameModeValue = gameMode.ordinal();
        if (gameModeValue < 2)
            gameModeValue++;
        else
            gameModeValue = 0;

        gameMode = Enums.GameMode.values()[gameModeValue];
        refreshParties();

        ipAddressField.setVisible(gameMode == Enums.GameMode.CLIENT);
        enemyPartyBox.setVisible(gameMode == Enums.GameMode.OFFLINE);

        switch (gameMode) {
            case OFFLINE:
                multiplayerOptionsButton.setText("Offline");
                break;
            case SERVER:
                multiplayerOptionsButton.setText("Server");
                break;
            case CLIENT:
                multiplayerOptionsButton.setText("Client");
                break;
        }
    }

    /**
     * Refreshes the UI.
     */
    private void refreshUi() {
        populatePokemonGrid();
        refreshParties();
        scrollPane.setPrefHeight(POKEMON_PANE_SIZE);
    }

    /**
     * Prepares the button used to represent party members.
     * @param partyBox the box that contains party member buttons
     * @param party the party whose information is to be inserted
     * @param player <code>true</code> if the party to be inserted is the player's party, false <code>otherwise</code>
     */
    public void preparePartyButtons(HBox partyBox, List<Pokemon> party, boolean player) {
        for (int i=0; i < partyBox.getChildren().size(); i++) {
            Button button = (Button) partyBox.getChildren().get(i);
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editPokemon = new MenuItem("Edit");
            MenuItem deletePokemon = new MenuItem("Delete");
            MenuItem movePokemon = new MenuItem("Move");
            MenuItem cancel = new MenuItem("Cancel");

            int finalI = i;
            editPokemon.setOnAction(e -> {
                editPokemon(finalI, party);
            });
            deletePokemon.setOnAction(e -> {
                party.remove(finalI);
                refreshParties();
            });
            cancel.setOnAction(e -> {
                contextMenu.hide();
            });
            movePokemon.setOnAction(e -> {
                HBox oppositeBox = player ? enemyPartyBox : playerPartyBox;
                oppositeBox.setDisable(true);
                Timeline timeline = setButtonsFlashingEffect(partyBox, player);
                timeline.play();
                for (int j=0; j < party.size(); j++) {
                    Button button1 = (Button) partyBox.getChildren().get(j);
                    int finalJ = j;
                    button1.setContextMenu(null);
                    button1.setOnMouseClicked(event -> {
                        Pokemon pokemon = party.get(finalI);
                        party.remove(finalI);
                        party.add(finalJ, pokemon);
                        preparePartyButtons(partyBox, party, player);
                        refreshParties();
                        timeline.stop();
                        oppositeBox.setDisable(false);
                    });
                }
            });
            contextMenu.getItems().addAll(editPokemon, deletePokemon, movePokemon, cancel);
            button.setContextMenu(contextMenu);
        }
    }

    /**
     * Configures an animation that makes the party's buttons flash. This is used when moving party members around.
     * @param partyBox box containing the party members' buttons
     * @param player <code>true</code> if the party to be inserted is the player's party, false <code>otherwise</code>
     * @return <code>Timeline</code> object containing the requested animation.
     */
    private Timeline setButtonsFlashingEffect(HBox partyBox, boolean player) {
        Timeline timeline;
        String color1 = player ? "#87CEEB": "lightcoral";
        String color2 = player ? "powderblue": "lightpink";
        String styleString = "-fx-background-color: ";

        final KeyFrame kf = new KeyFrame(Duration.millis(200), e -> {
            for (int i=0; i < partyBox.getChildren().size(); i++) {
                Button button = (Button) partyBox.getChildren().get(i);
                button.setStyle(styleString + color1);
            }
        });

        final KeyFrame kf2 = new KeyFrame(Duration.millis(400), e -> {
            for (int i=0; i < partyBox.getChildren().size(); i++) {
                Button button = (Button) partyBox.getChildren().get(i);
                button.setStyle(styleString + color2);
            }
        });

        timeline = new Timeline(kf, kf2);

        timeline.setOnFinished(e -> {
            for (int i=0; i < partyBox.getChildren().size(); i++) {
                Button button = (Button) partyBox.getChildren().get(i);
                button.setStyle(styleString + color1);
            }
        });

        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    /**
     * Sets up visual listeners for all current party member's buttons.
     */
    public void preparePartyButtonEffects() {

        for (int i=0; i < playerPartyBox.getChildren().size(); i++) {
            Button button = (Button) playerPartyBox.getChildren().get(i);
            setButtonEffect(button);
        }

        for (int i=0; i < enemyPartyBox.getChildren().size(); i++) {
            Button button = (Button) enemyPartyBox.getChildren().get(i);
            setButtonEffectEnemy(button);
        }
    }

    /**
     * Populates the grid with all available Pokémon.
     */
    public void populatePokemonGrid() {

        pokemonGrid.getChildren().clear();

        int i = 0, j = 0;

        for (Pokemon pokemon : sortedPokemon) {
            Button pokemonButton = new Button();
            pokemonButton.setText(String.format("No. %03d%n%n%n%n%s", pokemon.getPokedexNumber(), pokemon.getOriginalName().toString()));
            pokemonButton.setFont(Font.font("Monospaced", FONT_SIZE));
            pokemonButton.setTextAlignment(TextAlignment.CENTER);
            pokemonButton.setAlignment(Pos.TOP_CENTER);
            pokemonGrid.add(pokemonButton, i, j);

            pokemonButton.setPrefHeight(POKEMON_BUTTON_HEIGHT);
            pokemonButton.setPrefWidth(POKEMON_BUTTON_WIDTH);

            setPokemonListButtonEffect(pokemonButton);

            i++;
            if (i > 8) {
                i = 0;
                j++;
            }

            pokemonButton.setOnAction(e -> {
                Stage stage = (Stage) startBattleButton.getScene().getWindow();
                Scene scene = startBattleButton.getScene();
                //Pokemon pokemonExample = Pokemon.getPokemonExamples().get(pokemon.getOriginalName());
                AddPokemonController controller = addPokemonLoader.getController();
                controller.setAddData(pokemon, playerParty, enemyParty, this,
                        (Pane) startBattleButton.getScene().getRoot());
                controller.enterAddMode();
                stage.setTitle("Add Pokemon!");
                scene.setRoot(addPokemonPane);
            });
        }
    }

    /**
     * Enters the Pokémon edit menu.
     * @param index party index of the Pokémon to be edited.
     * @param party list of current party members
     */
    private void editPokemon(int index, List<Pokemon> party) {
        Stage stage = (Stage) startBattleButton.getScene().getWindow();
        Scene scene = startBattleButton.getScene();
        AddPokemonController controller = addPokemonLoader.getController();
        controller.setAddData(party.get(index), playerParty, enemyParty,this,
                (Pane) startBattleButton.getScene().getRoot());
        stage.setTitle("Edit Pokemon!");
        controller.enterEditMode(playerParty.equals(party), index);
        scene.setRoot(addPokemonPane);
    }

    /**
     * Refreshes the information contained in each of the party boxes.
     */
    public void refreshParties() {
        //playerPartyBox.setPrefSize((PARTY_BUTTON_WIDTH +5) * 6, PARTY_BUTTON_HEIGHT);
        //enemyPartyBox.setPrefSize((PARTY_BUTTON_WIDTH +5) * 6, PARTY_BUTTON_HEIGHT);

        String buttonText = "%s%nLv. %-3d";

        for (int i=0; i < playerPartyBox.getChildren().size(); i++) {
            Button playerPokemonButton = (Button) playerPartyBox.getChildren().get(i);
            Button enemyPokemonButton = (Button) enemyPartyBox.getChildren().get(i);
            playerPokemonButton.setFont(Font.font("Monospaced", FONT_SIZE));
            enemyPokemonButton.setFont(Font.font("Monospaced", FONT_SIZE));
            playerPokemonButton.setPrefSize(PARTY_BUTTON_WIDTH, PARTY_BUTTON_HEIGHT);
            enemyPokemonButton.setPrefSize(PARTY_BUTTON_WIDTH, PARTY_BUTTON_HEIGHT);
            playerPokemonButton.setStyle(PLAYER_PARTY_STYLE);
            enemyPokemonButton.setStyle(ENEMY_PARTY_STYLE);
            if (i < playerParty.size()) {
                playerPokemonButton.setText(String.format(buttonText, playerParty.get(i).getName(), playerParty.get(i).getLevel()));
                playerPokemonButton.setDisable(false);

                playerPokemonButton.setOnMouseClicked(e -> {
                    playerPokemonButton.getContextMenu().show(playerPokemonButton, e.getScreenX(), e.getScreenY());
                });
            }
            else {
                playerPokemonButton.setText("");
                playerPokemonButton.setDisable(true);
            }

            if (i < enemyParty.size()) {
                enemyPokemonButton.setText(String.format(buttonText, enemyParty.get(i).getName(), enemyParty.get(i).getLevel()));
                enemyPokemonButton.setDisable(false);

                enemyPokemonButton.setOnMouseClicked(e -> {
                    enemyPokemonButton.getContextMenu().show(enemyPokemonButton, e.getScreenX(), e.getScreenY());
                });
            }

            else {
                enemyPokemonButton.setText("");
                enemyPokemonButton.setDisable(true);
            }
        }
        if (gameMode == Enums.GameMode.OFFLINE)
            startBattleButton.setDisable(playerParty.isEmpty() || enemyParty.isEmpty());
        else
            startBattleButton.setDisable(playerParty.isEmpty());
    }

    /**
     * Sets the button effects for a button representing a Pokémon from the center list.
     * @param button button to which the changes will be applied to
     */
    public void setPokemonListButtonEffect(Button button) {

        String styleBeginning = "-fx-text-fill: linear-gradient(white 50%, black 100%); -fx-border-radius: 10; " +
                "-fx-background-radius: 10; -fx-border-color: black; -fx-border-width: 2; -fx-background-color: ";

        button.setStyle(styleBeginning + "linear-gradient(#800020 40%, lightgray 60%, #EAEAEA 100%)");

        button.setOnMouseExited(e -> button.setStyle(styleBeginning + "linear-gradient(#800020 40%, lightgray 60%, #EAEAEA 100%)"));
        button.setOnMouseReleased(e -> button.setStyle(styleBeginning + "linear-gradient(#800020 40%, lightgray 60%, #EAEAEA 100%)"));

        button.setOnMouseEntered(e -> button.setStyle(styleBeginning +  "linear-gradient(#b26679 40%, #EAEAEA 60%, #f2f2f2 100%)"));
        button.setOnMousePressed(e -> button.setStyle(styleBeginning + "linear-gradient(#4c0013 40%, gray 60%, #8c8c8c 100%)"));
    }

    /**
     * Sets the button effects for a button representing the player's party member.
     * @param button button to which the changes will be applied to
     */
    public void setButtonEffect(Button button) {
        button.setOnMouseExited(e -> button.setStyle(PLAYER_PARTY_STYLE));
        button.setOnMouseReleased(e -> button.setStyle(PLAYER_PARTY_STYLE));

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: powderblue"));
        button.setOnMousePressed(e -> button.setStyle("-fx-background-color: steelblue"));
    }

    /**
     * Sets the button effects for a button representing the player's party member.
     * @param button button to which the changes will be applied to
     */
    public void setButtonEffectEnemy(Button button) {
        button.setOnMouseExited(e -> button.setStyle(ENEMY_PARTY_STYLE));
        button.setOnMouseReleased(e -> button.setStyle(ENEMY_PARTY_STYLE));

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: lightpink"));
        button.setOnMousePressed(e -> button.setStyle("-fx-background-color: indianred"));
    }

    /**
     * Method called when pressing the "Start battle" button. It calls the appropriate starting method depending on
     * game mode.
     */
    @FXML
    private void startBattle(){
        switch (gameMode) {
            case OFFLINE:
                startOfflineBattle();
                break;
            case SERVER:
                startOnlineBattleServer();
                break;
            case CLIENT:
                startOnlineBattleClient();
                break;
        }
    }

    /**
     * Starts offline battle.
     */
    private void startOfflineBattle() {
        Player player = new Player(playerName, playerParty.get(0));
        NpcTrainer enemy = new NpcTrainer(enemyName, trainerType, enemyParty.get(0));

        for (int i=1; i < playerParty.size(); i++)
            player.addPokemon(playerParty.get(i));
        for (int i=1; i < enemyParty.size(); i++)
            enemy.addPokemon(enemyParty.get(i));

        Stage stage = (Stage) startBattleButton.getScene().getWindow();
        Pane teamBuilderPane = (Pane) startBattleButton.getScene().getRoot();
        stage.setTitle("Battle!");
        startBattleButton.getScene().setRoot(battlePane);

        logic.startBattle(player, enemy, turboMode, teamBuilderPane);
    }

    /**
     * Disables the ability to interact with any UI elements.
     * @param value set <code>true</code> to disable all elements, <code>false</code> to enable
     */
    private void disableInput(boolean value) {
        startBattleButton.setDisable(value);
        scrollPane.setDisable(value);
        playerPartyBox.setDisable(value);
        multiplayerOptionsButton.setDisable(value);
        enemySettingsButton.setDisable(value);
        playerSettingsButton.setDisable(value);
        ipAddressField.setDisable(value);
    }

    /**
     * Blocks user input end displays that the game is waiting for a connection. Used when starting battle in server
     * mode.
     * @param block <code>true</code> to display the message, <code>false</code> to hide it
     * @see #disableInput(boolean)
     */
    public void displayConnectionBlock(boolean block) {
        disableInput(block);
        waitingForConnectionLabel.setVisible(block);
    }

    /**
     * Starts online battle in server mode.
     */
    private void startOnlineBattleServer() {
        displayConnectionBlock(true);
        inputStream = null;
        outputStream = null;
        int port = 1234;
        connectionThread = new ServerThread(port, this);
        connectionThread.start();
    }

    /**
     * Starts online battle in client mode.
     */
    private void startOnlineBattleClient() {
        inputStream = null;
        outputStream = null;
        int port = 1234;
        connectionThread = new ClientThread(ipAddressField.getText(), port, this);
        connectionThread.start();
    }

    /**
     * Generates an information <code>String</code> containing all necessary Pokémon information.
     * @param pokemon Pokémon to be turned into a <code>String</code>
     * @return <code>String</code> representation of the selected Pokémon
     * @see #parsePokemonInfo(String[])
     */
    private String createPokemonInfo(Pokemon pokemon) {
        StringBuilder pokemonComposition = new StringBuilder();
        final String separator = "__";

        pokemonComposition.append(pokemon.getSpecie().getName().ordinal()).append(separator);
        pokemonComposition.append(pokemon.getName()).append(separator);
        pokemonComposition.append(pokemon.getNature().getValue()).append(separator);
        pokemonComposition.append(pokemon.getAbility().ordinal()).append(separator);
        pokemonComposition.append(pokemon.getLevel());
        for (int iv : pokemon.getIvs())
            pokemonComposition.append(separator).append(iv);
        for (Move move : pokemon.getMoveList())
            pokemonComposition.append(separator).append(move.getName().ordinal());

        return pokemonComposition.toString();
    }

    /**
     * Creates a <code>String</code> containing all necessary information about the player and their party.
     * @return <code>StringBuilder</code> object containing party member information in <code>String</code> format
     * @see #parseTrainerInfo(String)
     */
    private StringBuilder createTeamInfo() {
        StringBuilder teamComposition = new StringBuilder();
        final String teamSeperator = "--";
        teamComposition.append("HELLO").append(teamSeperator);
        teamComposition.append(playerName).append(teamSeperator);
        for (Pokemon pokemon : playerParty) {
            teamComposition.append(createPokemonInfo(pokemon));
            teamComposition.append(teamSeperator);
        }
        teamComposition.append("GOODBYE");
        return teamComposition;
    }

    /**
     * Converts Pokémon information in <code>String</code> format into <code>Pokemon</code> object
     * @param splitInfo <code>String</code> array containing Pokémon information
     * @return <code>Pokemon</code> representation of inserted data
     * @see #createPokemonInfo(Pokemon)
     */
    private Pokemon parsePokemonInfo(String[] splitInfo) {
        int pokemonSpecie = Integer.parseInt(splitInfo[0]); // specie enum number
        String pokemonName = splitInfo[1]; // for custom name, currently unused
        int pokemonNature = Integer.parseInt(splitInfo[2]); // nature enum number
        int pokemonAbility = Integer.parseInt(splitInfo[3]); // ability enum number
        int level = Integer.parseInt(splitInfo[4]); // level
        int[] ivs = new int[6];
        for (int i=5; i<=10; i++)
            ivs[i-5] = Integer.parseInt(splitInfo[i]);

        List<Integer> moveValues = new ArrayList<>(); // move enum number
        for (int i=11; i<splitInfo.length; i++)
            moveValues.add(Integer.parseInt(splitInfo[i]));

        Move firstMove = new Move(MoveTemplate.getMove(MoveEnum.values()[moveValues.remove(0)]));
        Ability ability = Ability.values()[pokemonAbility];
        Pokemon pokemon = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.values()[pokemonSpecie]),
                level, ability,firstMove);
        for (int move : moveValues)
            pokemon.addMove(new Move(MoveTemplate.getMove(MoveEnum.values()[move])));
        pokemon.setNature(Enums.Nature.valueOf(pokemonNature));
        pokemon.setIvs(ivs);
        pokemon.calculateStats();
        return pokemon;
    }

    /**
     * Converts Pokémon and Trainer information into objects. The enemy name and party is then loaded into memory.
     * @param info <code>String</code> representation of the information about the trainer and their party
     * @see #createTeamInfo()
     */
    private void parseTrainerInfo(String info) {
        final String separator = "__";
        final String teamSeperator = "--";
        final String[] teamComposition = info.split(teamSeperator);
        if (!teamComposition[0].equals("HELLO")) {
            System.out.println("Transmission corrupted, aborting");
            return;
        }

        String enemyName = teamComposition[1];

        List<Pokemon> enemyTeam = new ArrayList<>();
        for (int a=2; a<teamComposition.length; a++) {
            String pokemonInfo = teamComposition[a];
            if (pokemonInfo.equals("GOODBYE"))
                break;
            String[] splitInfo = pokemonInfo.split(separator);
            enemyTeam.add(parsePokemonInfo(splitInfo));
        }

        this.enemyName = enemyName;
        this.enemyParty = enemyTeam;
    }

    /**
     * Sends trainer and party info to the server. Used in client mode.
     */
    public void sendBattleInfoClient() throws IOException {
        StringBuilder teamInfo = createTeamInfo();
        outputStream.writeUTF(teamInfo.toString());
        outputStream.flush();
        readBattleInfoClient();
    }

    /**
     * Receives trainer and party info from the server. Used in client mode.
     */
    private void readBattleInfoClient() throws IOException {
        String teamInfo = inputStream.readUTF();
        parseTrainerInfo(teamInfo);
        startOnlineBattle();
    }

    /**
     * Receives trainer and party info from the client. Used in server mode.
     */
    public void readBattleInfoServer() throws IOException {
        String teamInfo = inputStream.readUTF();
        parseTrainerInfo(teamInfo);
        sendBattleInfoServer();
    }

    /**
     * Sends trainer and party info to the client. Used in server mode.
     */
    private void sendBattleInfoServer() throws IOException {
        StringBuilder teamInfo = createTeamInfo();
        outputStream.writeUTF(teamInfo.toString());
        outputStream.flush();
        startOnlineBattle();
    }

    /**
     * After information swap is complete, starts online battle.
     */
    private void startOnlineBattle() {
        Player player = new Player(playerName, playerParty.get(0));
        OnlineTrainer enemy = new OnlineTrainer(enemyName, enemyParty.get(0), inputStream, outputStream, logic);


        for (int i=1; i < playerParty.size(); i++)
            player.addPokemon(playerParty.get(i));
        for (int i=1; i < enemyParty.size(); i++)
            enemy.addPokemon(enemyParty.get(i));

        Platform.runLater(() -> {
            Stage stage = (Stage) startBattleButton.getScene().getWindow();
            Pane teamBuilderPane = (Pane) startBattleButton.getScene().getRoot();
            stage.setTitle("Battle!");
            startBattleButton.getScene().setRoot(battlePane);
            displayConnectionBlock(false);
            logic.startOnlineBattle(player, enemy, turboMode, teamBuilderPane, gameMode, inputStream, outputStream);
        });
    }
}
