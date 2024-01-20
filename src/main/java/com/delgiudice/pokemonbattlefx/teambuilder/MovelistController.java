package com.delgiudice.pokemonbattlefx.teambuilder;

import com.delgiudice.pokemonbattlefx.attributes.Enums;
import com.delgiudice.pokemonbattlefx.battle.BattleController;
import com.delgiudice.pokemonbattlefx.move.MoveEnum;
import com.delgiudice.pokemonbattlefx.move.MoveTemplate;
import com.delgiudice.pokemonbattlefx.pokemon.Pokemon;
import com.delgiudice.pokemonbattlefx.pokemon.PokemonEnum;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.delgiudice.pokemonbattlefx.teambuilder.AddPokemonController.toHexString;
import static com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController.setupStringField;

public class MovelistController {

    private static final int FONT_SIZE = 14, MOVE_BUTTON_WIDTH = 144, MOVE_BUTTON_HEIGHT = 60;

    private static final String CATEGORY_BUTTON_STYLE_RELEASED = "-fx-background-color: linear-gradient(to left, darkmagenta, indigo)";
    private static final String CATEGORY_BUTTON_STYLE_PRESSED = "-fx-background-color: linear-gradient(to top right, darkmagenta, darkmagenta)";
    private static final String CATEGORY_BUTTON_STYLE_HOVER = "-fx-background-color: linear-gradient(to top right, magenta, skyblue)";

    private static final String TYPE_INFO_STYLE = "-fx-background-color: %s; -fx-border-radius: 10; -fx-background-radius: 10";

    @FXML
    private Pane mainPane;
    @FXML
    private GridPane moveGrid;
    @FXML
    private Button categoryAllMoves, categoryPhysicalMoves, categorySpecialMoves, categoryStatusMoves;
    @FXML
    private Button backButton;
    @FXML
    private HBox categoryBox;
    @FXML
    private VBox moveInfoBox;
    @FXML
    private Label moveTypeLabel, moveNameLabel, movePPLabel, moveCategoryLabel, movePowerLabel, moveAccuracyLabel,
            moveDescriptionLabel;
    @FXML
    private ChoiceBox<Enums.Types> typeChoiceBox;
    @FXML
    private TextField moveNameSearchField;

    private List<MoveTemplate> sortedMoves = new ArrayList<>();

    private Pane addPokemonPane;
    
    private TextField currentMoveSelectTextField;

    private Enums.Subtypes currentType = null;

    public void initialize() {

        sortedMoves.addAll(MoveTemplate.getMoveMap().values());

        sortedMoves.sort(new Comparator<MoveTemplate>() {
            @Override
            public int compare(MoveTemplate o1, MoveTemplate o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.getName().toString(), o2.getName().toString());
            }
        });

        Rectangle rect = new Rectangle(BattleController.SCREEN_WIDTH, BattleController.SCREEN_HEIGHT);
        mainPane.setClip(rect);

        initButtonListeners();
        setTypeChoiceBox();

        moveNameSearchField.textProperty().addListener(observable -> {
            populateMoveGrid();
        });

        setupStringField(moveNameSearchField, 15);
    }

    public void initMenu(TextField currentMoveSelectTextField, Pane addPokemonPane) {
        this.currentMoveSelectTextField = currentMoveSelectTextField;
        gridDisplayAllMoves();
        this.addPokemonPane = addPokemonPane;

        backButton.setOnMouseClicked(e -> {
            Scene scene = backButton.getScene();
            scene.setRoot(this.addPokemonPane);
        });
    }

    private void setTypeChoiceBox() {
        typeChoiceBox.setValue(Enums.Types.ANY);

        for (Enums.Types types : Enums.Types.values()) {
            if (types != Enums.Types.NO_TYPE && types != Enums.Types.MISSING)
                typeChoiceBox.getItems().add(types);
        }

        typeChoiceBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            populateMoveGrid();
        });
    }

    private static void setMoveType(Label typeLabel, MoveTemplate moveTemplate) {
        typeLabel.setTextFill(Color.WHITE);
        Enums.Types typeEnum = moveTemplate.getType().getTypeEnum();
        if (typeEnum == Enums.Types.NO_TYPE)
            typeLabel.setText("???");
        else
            typeLabel.setText(typeEnum.toString());
        Color firstTypeColor = typeEnum.getTypeColor();
        String colorHex = toHexString(firstTypeColor);
        typeLabel.setStyle(String.format(TYPE_INFO_STYLE, colorHex));
    }

    private void setMoveInfo(MoveTemplate moveTemplate) {
        setMoveType(moveTypeLabel, moveTemplate);
        moveNameLabel.setText(moveTemplate.getName().toString());
        movePPLabel.setText(String.valueOf(moveTemplate.getMaxpp()));
        moveCategoryLabel.setText(moveTemplate.getSubtype().toString());
        if (moveTemplate.getPower() > 0)
            movePowerLabel.setText(String.valueOf(moveTemplate.getPower()));
        else
            movePowerLabel.setText("-");
        if (moveTemplate.getAccuracy() > 0)
            moveAccuracyLabel.setText(String.valueOf(moveTemplate.getAccuracy()));
        else
            moveAccuracyLabel.setText("-");

        moveDescriptionLabel.setText(moveTemplate.getMoveDescription());
    }

    private void resetMoveInfo() {
        setMoveType(moveTypeLabel, MoveTemplate.getHiddenMoveMap().get(MoveEnum.STRUGGLE));
        moveNameLabel.setText("Move");
        movePPLabel.setText("??");
        moveCategoryLabel.setText("???");
        movePowerLabel.setText("???");
        moveAccuracyLabel.setText("???");
        moveDescriptionLabel.setText("");
    }

    public static boolean containsIgnoreCase(String str1, String str2) {
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();
        return str1.contains(str2);
    }

    public void populateMoveGrid() {

        moveGrid.getChildren().clear();

        int i = 0, j = 0;

        for (MoveTemplate move : sortedMoves) {

            boolean moveSubTypeMatch = currentType == null || move.getSubtype() == currentType;
            boolean moveTypeMatch = move.getType().getTypeEnum() == typeChoiceBox.getValue() ||
                    typeChoiceBox.getValue() == Enums.Types.ANY;
            boolean moveNameMatch = containsIgnoreCase(move.getName().toString(), moveNameSearchField.getText());

            if ( moveSubTypeMatch && moveTypeMatch && moveNameMatch) {
                VBox moveButton = new VBox();
                Label moveButtonType = new Label();
                Label moveButtonText = new Label();
                moveButtonType.setPrefWidth(MOVE_BUTTON_WIDTH - 20);
                moveButtonType.setPrefHeight(MOVE_BUTTON_HEIGHT / 2.0);

                moveButtonText.setText(move.getName().toString());
                moveButtonText.setFont(Font.font("Monospaced", FONT_SIZE));
                moveButtonText.textAlignmentProperty().set(TextAlignment.CENTER);
                moveButtonText.setTextFill(Color.WHITE);
                moveButton.setStyle("-fx-background-color: #2d388a");

                moveButtonType.setAlignment(Pos.CENTER);
                moveButtonType.setFont(Font.font("Monospaced", FONT_SIZE));
                setMoveType(moveButtonType, move);

                moveButton.setAlignment(Pos.CENTER);

                moveButton.setPrefHeight(MOVE_BUTTON_HEIGHT);
                moveButton.setPrefWidth(MOVE_BUTTON_WIDTH);

                moveButton.setOnMouseEntered(e -> {
                    setMoveInfo(move);
                    moveButton.setStyle("-fx-background-color: #3a48b5");
                });
                moveButton.setOnMouseExited(e -> {
                    resetMoveInfo();
                    moveButton.setStyle("-fx-background-color: #2d388a");
                });

                moveButton.setOnMousePressed(e -> {
                    moveButton.setStyle("-fx-background-color: #1e265c");
                });

                moveButton.setOnMouseReleased(e -> {
                    moveButton.setStyle("-fx-background-color: #2d388a");
                });

                moveButton.setOnMouseClicked(e -> {
                    currentMoveSelectTextField.setText(move.getName().toString());
                    Scene scene = moveButton.getScene();
                    scene.setRoot(addPokemonPane);
                });

                moveButton.getChildren().add(moveButtonType);
                moveButton.getChildren().add(moveButtonText);

                moveGrid.add(moveButton, i, j);

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

    private void enableAllButtons() {
        for (Node button : categoryBox.getChildren()) {
            button.setDisable(false);
        }
    }

    private void clearGrid() {
        enableAllButtons();
        moveGrid.getChildren().clear();
    }

    @FXML
    public void gridDisplayAllMoves() {
        clearGrid();
        categoryAllMoves.setDisable(true);
        currentType = null;
        populateMoveGrid();
    }

    @FXML
    public void gridDisplayPhysicalMoves() {
        clearGrid();
        categoryPhysicalMoves.setDisable(true);
        currentType = Enums.Subtypes.PHYSICAL;
        populateMoveGrid();
    }

    @FXML
    public void gridDisplaySpecialMoves() {
        clearGrid();
        categorySpecialMoves.setDisable(true);
        currentType = Enums.Subtypes.SPECIAL;
        populateMoveGrid();
    }

    @FXML
    public void gridDisplayStatusMoves() {
        clearGrid();
        categoryStatusMoves.setDisable(true);
        currentType = Enums.Subtypes.STATUS;
        populateMoveGrid();
    }
}
