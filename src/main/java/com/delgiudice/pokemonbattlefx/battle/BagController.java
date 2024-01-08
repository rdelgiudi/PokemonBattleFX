package com.delgiudice.pokemonbattlefx.battle;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.item.Item;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import com.delgiudice.pokemonbattlefx.trainer.Player;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BagController {

    private static final int FONT_SIZE = 15, ITEM_BUTTON_WIDTH = 125, ITEM_BUTTON_HEIGHT = 100;

    private static final String CATEGORY_BUTTON_STYLE_RELEASED = "-fx-background-color: linear-gradient(to top right, #C5283D, #2E2C2A)";
    private static final String CATEGORY_BUTTON_STYLE_PRESSED = "-fx-background-color: linear-gradient(to top right, #991f2f, #2E2C2A)";
    private static final String CATEGORY_BUTTON_STYLE_HOVER = "-fx-background-color: linear-gradient(to top right, #e62e46, #2E2C2A)";

    @FXML
    private GridPane itemGrid;
    @FXML
    private Label descriptionField;
    @FXML
    private Button categoryHpRestoreButton, categoryPpRestoreButton, categoryStatusHealingButton, categoryXItemsButton;
    @FXML
    private Button backButton;
    @FXML
    private HBox categoryBox;

    private Pane battlePane, swapPokemonPane;
    private BattleLogic battleLogic;
    private BattleController battleController;
    private SwapPokemonController swapPokemonController;
    private Player player;
    List<Pokemon> playerParty;

    private HashMap<Item, Integer> itemList;

    private Enums.ItemType currentType = Enums.ItemType.HP_RESTORE;

    public void initialize() {
        initButtonListeners();
    }

    public void initMenu(Player player, Pane swapPokemonPane, SwapPokemonController swapPokemonController,
                         Pane battlePane, BattleLogic logic, BattleController controller, List<Pokemon> party) {
        this.player = player;
        itemList = player.getItems();
        this.battlePane = battlePane;
        battleLogic = logic;
        battleController = controller;
        playerParty = party;
        this.swapPokemonPane = swapPokemonPane;
        this.swapPokemonController = swapPokemonController;
        gridDisplayHpRestoreItems();

        backButton.setOnMouseClicked(e -> {
            Scene scene = backButton.getScene();
            scene.setRoot(battlePane);
        });
    }

    private void enableAllButtons() {
        for (Node button : categoryBox.getChildren()) {
            button.setDisable(false);
        }
    }

    private void clearGrid() {
        enableAllButtons();
        itemGrid.getChildren().clear();
    }

    public void populateItemGrid() {

        descriptionField.setText("");
        itemGrid.getChildren().clear();

        int i = 0, j = 0;

        for (Map.Entry<Item, Integer> item : itemList.entrySet()) {
            Button itemButton = new Button();
            itemButton.setText(String.format("%s%nx%d", item.getKey().getName(), item.getValue()));
            itemButton.setFont(Font.font("Monospaced", FONT_SIZE));
            itemButton.setAlignment(Pos.CENTER);
            itemButton.setTextFill(Color.WHITE);
            itemButton.setStyle("-fx-background-color:  linear-gradient(to top right, #2d388a, #00aeef)");
            itemGrid.add(itemButton, i, j);

            itemButton.setPrefHeight(ITEM_BUTTON_HEIGHT);
            itemButton.setPrefWidth(ITEM_BUTTON_WIDTH);

            itemButton.setOnMouseEntered(e -> descriptionField.setText(item.getKey().getDescription()));
            itemButton.setOnMouseExited(e -> descriptionField.setText(""));

            i++;
            if (i > 8) {
                i = 0;
                j++;
            }

            itemButton.setOnAction(e -> {
                swapPokemonController.initVariablesBag(battlePane, (Pane) itemButton.getScene().getRoot(), battleLogic,
                        battleController, playerParty, player, item.getKey());
                Scene scene = itemButton.getScene();
                scene.setRoot(swapPokemonPane);
            });
        }
    }

    public void initButtonListeners() {
        for (Node button : categoryBox.getChildren()) {
            button.setOnMouseEntered(e -> button.setStyle(CATEGORY_BUTTON_STYLE_HOVER));
            button.setOnMousePressed(e -> button.setStyle(CATEGORY_BUTTON_STYLE_PRESSED));
            button.setOnMouseExited(e -> button.setStyle(CATEGORY_BUTTON_STYLE_RELEASED));
            button.setOnMouseReleased(e -> button.setStyle(CATEGORY_BUTTON_STYLE_RELEASED));
        }
    }

    @FXML
    public void gridDisplayHpRestoreItems() {
        clearGrid();
        categoryHpRestoreButton.setDisable(true);
        currentType = Enums.ItemType.HP_RESTORE;
        populateItemGrid();
    }

    @FXML
    public void gridDisplayPpRestoreItems() {
        clearGrid();
        categoryPpRestoreButton.setDisable(true);
        currentType = Enums.ItemType.PP_RESTORE;
        populateItemGrid();
    }

    @FXML
    public void gridDisplayStatusItems() {
        clearGrid();
        categoryStatusHealingButton.setDisable(true);
        currentType = Enums.ItemType.STATUS_HEALING;
        populateItemGrid();
    }

    @FXML
    public void gridDisplayXItems() {
        clearGrid();
        categoryXItemsButton.setDisable(true);
        currentType = Enums.ItemType.X_ITEMS;
        populateItemGrid();
    }
}
