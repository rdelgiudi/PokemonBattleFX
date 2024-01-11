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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BagController {

    private static final int FONT_SIZE = 15, ITEM_BUTTON_WIDTH = 145, ITEM_BUTTON_HEIGHT = 120;

    private static final String CATEGORY_BUTTON_STYLE_RELEASED = "-fx-background-color: linear-gradient(to top right, #C5283D, #2E2C2A)";
    private static final String CATEGORY_BUTTON_STYLE_PRESSED = "-fx-background-color: linear-gradient(to top right, #991f2f, #2E2C2A)";
    private static final String CATEGORY_BUTTON_STYLE_HOVER = "-fx-background-color: linear-gradient(to top right, #e62e46, #2E2C2A)";

    @FXML
    private Pane mainPane;
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

        Rectangle rect = new Rectangle(BattleController.SCREEN_WIDTH, BattleController.SCREEN_HEIGHT);
        mainPane.setClip(rect);
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

            if (item.getKey().getType() == currentType) {
                Button itemButton = new Button();

                itemButton.setText(String.format("%s%nx%d", item.getKey().getName(), item.getValue()));
                itemButton.setFont(Font.font("Monospaced", FONT_SIZE));
                itemButton.textAlignmentProperty().set(TextAlignment.CENTER);
                itemButton.setTextFill(Color.WHITE);
                itemButton.setStyle("-fx-background-color: #2d388a");


                Image sprite = item.getKey().getSprite();
                ImageView graphic = new ImageView(sprite);
                graphic.preserveRatioProperty().set(true);
                graphic.setFitWidth(60);
                graphic.setFitHeight(60);
                itemButton.setGraphic(graphic);

                itemButton.setContentDisplay(ContentDisplay.TOP);
                itemButton.setAlignment(Pos.CENTER);

                itemButton.setPrefHeight(ITEM_BUTTON_HEIGHT);
                itemButton.setPrefWidth(ITEM_BUTTON_WIDTH);

                itemButton.setOnMouseEntered(e -> {
                    descriptionField.setText(item.getKey().getDescription());
                    itemButton.setStyle("-fx-background-color: #3a48b5");
                });
                itemButton.setOnMouseExited(e -> {
                    descriptionField.setText("");
                    itemButton.setStyle("-fx-background-color: #2d388a");
                });

                itemButton.setOnMousePressed(e -> {
                    itemButton.setStyle("-fx-background-color: #1e265c");
                });

                itemButton.setOnMouseReleased(e -> {
                    itemButton.setStyle("-fx-background-color: #2d388a");
                });

                itemButton.setOnAction(e -> {
                    swapPokemonController.initVariablesBag(battlePane, (Pane) itemButton.getScene().getRoot(), battleLogic,
                            battleController, playerParty, player, item.getKey());
                    Scene scene = itemButton.getScene();
                    scene.setRoot(swapPokemonPane);
                });

                itemGrid.add(itemButton, i, j);

                i++;
                if (i > 7) {
                    i = 0;
                    j++;
                }
            }
        }
    }

    public void initButtonListeners() {
        for (Node button : categoryBox.getChildren()) {
            button.setOnMouseEntered(e -> button.setStyle(CATEGORY_BUTTON_STYLE_HOVER));
            button.setOnMousePressed(e -> button.setStyle(CATEGORY_BUTTON_STYLE_PRESSED));
            button.setOnMouseExited(e -> button.setStyle(CATEGORY_BUTTON_STYLE_RELEASED));
            button.setOnMouseReleased(e -> button.setStyle(CATEGORY_BUTTON_STYLE_RELEASED));
        }

        backButton.setOnMouseEntered(e -> backButton.setStyle(CATEGORY_BUTTON_STYLE_HOVER));
        backButton.setOnMousePressed(e -> backButton.setStyle(CATEGORY_BUTTON_STYLE_PRESSED));
        backButton.setOnMouseExited(e -> backButton.setStyle(CATEGORY_BUTTON_STYLE_RELEASED));
        backButton.setOnMouseReleased(e -> backButton.setStyle(CATEGORY_BUTTON_STYLE_RELEASED));
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
