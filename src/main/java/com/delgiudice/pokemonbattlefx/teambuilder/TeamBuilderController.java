package com.delgiudice.pokemonbattlefx.teambuilder;

import com.delgiudice.pokemonbattlefx.*;
import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.BattleController;
import com.delgiudice.pokemonbattlefx.battle.BattleLogic;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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

    AddPokemonController addPokemonController;

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

    public TeamBuilderController() throws IOException {
        Pokemon.generatePokemonExamples();

        battleLoader = new FXMLLoader(BattleApplication.class.getResource("battle-view.fxml"));
        //Pane teamBuilderPane = (Pane) startBattleButton.getScene().getRoot();
        battlePane = battleLoader.load();

        Item.setItemMap();

        battleController = battleLoader.getController();
        logic = new BattleLogic(battleController);
    }

    public void initialize() {

        for (Map.Entry<PokemonEnum, Pokemon> entry : Pokemon.getPokemonExamples().entrySet()) {
            sortedPokemon.add(entry.getValue());
        }

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

        addPokemonController = addPokemonLoader.getController();

        setupStringField(playerNameField, 15);
    }
    @FXML
    private void openPlayerSettings() {
        playerSettingsBox.setVisible(true);
        disableInput(true);
    }

    @FXML
    private void closePlayerSettings() {
        playerSettingsBox.setVisible(false);
        updatePlayerName();
        disableInput(false);
    }

    @FXML
    private void updatePlayerName() {
        playerName = playerNameField.getText();
    }

    @FXML
    private void toggleTurboMode() {
        turboMode = turboModeCheckBox.isSelected();
    }

    @FXML
    private void toggleAnimatedMode() {
        BattleApplication.setUseInternetSprites(animatedModeCheckBox.isSelected());
        battleController.processSpriteModeSwitch();
        addPokemonController.processSpriteModeSwitch();
    }

    private static void setupStringField(TextField textField, int characterLimit) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*")) {
                textField.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
            if (newValue.length() > characterLimit)
                textField.setText(newValue.substring(0, characterLimit));
        });
    }

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

    private void refreshUi() {
        populatePokemonGrid();
        refreshParties();
        scrollPane.setPrefHeight(POKEMON_PANE_SIZE);
    }

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

    public void populatePokemonGrid() {

        pokemonGrid.getChildren().clear();

        int i = 0, j = 0;

        for (Pokemon pokemon : sortedPokemon) {
            Button pokemonButton = new Button();
            pokemonButton.setText(String.format("No. %03d%n%s", pokemon.getPokedexNumber(), pokemon.getOriginalName().toString()));
            pokemonButton.setFont(Font.font("Monospaced", FONT_SIZE));
            pokemonGrid.add(pokemonButton, i, j);

            pokemonButton.setPrefHeight(POKEMON_BUTTON_HEIGHT);
            pokemonButton.setPrefWidth(POKEMON_BUTTON_WIDTH);

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

    public void setButtonEffect(Button button) {
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #87CEEB"));
        button.setOnMouseReleased(e -> button.setStyle("-fx-background-color: #87CEEB"));

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: powderblue"));
        button.setOnMousePressed(e -> button.setStyle("-fx-background-color: steelblue"));
    }

    public void setButtonEffectEnemy(Button button) {
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: lightcoral"));
        button.setOnMouseReleased(e -> button.setStyle("-fx-background-color: lightcoral"));

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: lightpink"));
        button.setOnMousePressed(e -> button.setStyle("-fx-background-color: indianred"));
    }

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

    private void disableInput(boolean value) {
        startBattleButton.setDisable(value);
        scrollPane.setDisable(value);
        playerPartyBox.setDisable(value);
        multiplayerOptionsButton.setDisable(value);
        enemySettingsButton.setDisable(value);
        playerSettingsButton.setDisable(value);
    }

    public void displayConnectionBlock(boolean block) {
        disableInput(block);
        waitingForConnectionLabel.setVisible(block);
    }

    private void startOnlineBattleServer() {
        displayConnectionBlock(true);
        inputStream = null;
        outputStream = null;
        int port = 1234;
        connectionThread = new ServerThread(port, this);
        connectionThread.start();
    }

    private void startOnlineBattleClient() {
        inputStream = null;
        outputStream = null;
        int port = 1234;
        connectionThread = new ClientThread(ipAddressField.getText(), port, this);
        connectionThread.start();
    }

    private StringBuilder createTeamInfo() {
        StringBuilder teamComposition = new StringBuilder();
        final String separator = "__";
        final String teamSeperator = "--";
        teamComposition.append("HELLO").append(teamSeperator);
        teamComposition.append(playerName).append(teamSeperator);
        for (Pokemon pokemon : playerParty) {
            teamComposition.append(pokemon.getName()).append(separator);
            teamComposition.append(pokemon.getNature().getValue()).append(separator);
            teamComposition.append(pokemon.getAbility()).append(separator);
            for (int iv : pokemon.getIvs())
                teamComposition.append(iv).append(separator);
            for (Move move : pokemon.getMoveList())
                teamComposition.append(move.getName()).append(separator);
            teamComposition.append(pokemon.getLevel()).append(teamSeperator);
        }
        teamComposition.append("GOODBYE").append(teamSeperator);
        return teamComposition;
    }

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
            String pokemonName = splitInfo[0];
            int pokemonNature = Integer.parseInt(splitInfo[1]);
            String pokemonAbility = splitInfo[2];
            int[] ivs = new int[6];
            int level = 0;
            List<String> moveNames = new ArrayList<>();
            for (int i=3; i<=8; i++)
                ivs[i-3] = Integer.parseInt(splitInfo[i]);
            for (int i=9; i<=13; i++) {
                if (splitInfo[i].matches("[0-9]+") && splitInfo[i].length() <= 2) {
                    level = Integer.parseInt(splitInfo[i]);
                    break;
                }
                moveNames.add(splitInfo[i]);
            }
            Move firstMove = new Move(MoveTemplate.getMove(MoveEnum.findByName(moveNames.remove(0))));
            Ability ability = Ability.findByName(pokemonAbility);
            Pokemon pokemon = new Pokemon(PokemonSpecie.getPokemonMap().get(PokemonEnum.findByName(pokemonName)),
                    level, ability,firstMove);
            for (String move : moveNames)
                pokemon.addMove(new Move(MoveTemplate.getMove(MoveEnum.findByName(move))));
            pokemon.setNature(Enums.Nature.valueOf(pokemonNature));
            pokemon.setIvs(ivs);
            pokemon.calculateStats();
            enemyTeam.add(pokemon);
        }

        this.enemyName = enemyName;
        this.enemyParty = enemyTeam;
    }

    public void sendBattleInfoClient() throws IOException {
        StringBuilder teamInfo = createTeamInfo();
        outputStream.writeUTF(teamInfo.toString());
        outputStream.flush();
        readBattleInfoClient();
    }

    private void readBattleInfoClient() throws IOException {
        String teamInfo = inputStream.readUTF();
        System.out.println("Server response: " + teamInfo);
        parseTrainerInfo(teamInfo);
        startOnlineBattle();
    }

    public void readBattleInfoServer() throws IOException {
        String teamInfo = inputStream.readUTF();
        System.out.println("Client response: " + teamInfo);
        parseTrainerInfo(teamInfo);
        sendBattleInfoServer();
    }

    private void sendBattleInfoServer() throws IOException {
        StringBuilder teamInfo = createTeamInfo();
        outputStream.writeUTF(teamInfo.toString());
        outputStream.flush();
        startOnlineBattle();
    }

    private void startOnlineBattle() {
        Player player = new Player(playerName, playerParty.get(0));
        OnlineTrainer enemy = new OnlineTrainer(enemyName, enemyParty.get(0));


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
            logic.startOnlineBattle(player, enemy, turboMode, teamBuilderPane, gameMode, inputStream, outputStream, connectionThread);
        });
    }
}
