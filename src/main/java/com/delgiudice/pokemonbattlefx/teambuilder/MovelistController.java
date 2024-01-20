

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
import static com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController.setupStringFieldMove;

/**
 * This class defines the controller which is tied to the Movelist menu.
 */
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

    /**
     * Configures UI elements as well as sorts the Movelist.
     * @see #initMenu(TextField, Pane)
     */
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

        setupStringFieldMove(moveNameSearchField, 15);
    }

    /**
     * Initializes the Movelist menu. This should always be executed before moving to the Movelist screen.
     * @param currentMoveSelectTextField defines to which text field should the selected move be inserted to
     * @param addPokemonPane defines the Pane from which this menu was entered
     */
    public void initMenu(TextField currentMoveSelectTextField, Pane addPokemonPane) {
        this.currentMoveSelectTextField = currentMoveSelectTextField;
        gridDisplayAllMoves();
        this.addPokemonPane = addPokemonPane;

        backButton.setOnMouseClicked(e -> {
            Scene scene = backButton.getScene();
            scene.setRoot(this.addPokemonPane);
        });
    }

    /**
     * Initializes the typeChoiceBox, which allows to display moves of only one Type.
     */
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

    /**
     * Configures a label to represent a specific type. This method changes the background properties of the label
     * as well as sets the appropriate text.
     * @param typeLabel label to be modified
     * @param moveTemplate move template from which the type should be extracted
     * @see #setMoveInfo(MoveTemplate)
     */
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

    /**
     * Sets move information in the move preview section at the bottom of the screen.
     * @param moveTemplate move template from which the information is extracted
     *
     */
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

    /**
     * Resets the move information to default in the move preview section at the bottom of the screen.
     * @see #setMoveType(Label, MoveTemplate)
     */
    private void resetMoveInfo() {
        setMoveType(moveTypeLabel, MoveTemplate.getHiddenMoveMap().get(MoveEnum.STRUGGLE));
        moveNameLabel.setText("Move");
        movePPLabel.setText("??");
        moveCategoryLabel.setText("???");
        movePowerLabel.setText("???");
        moveAccuracyLabel.setText("???");
        moveDescriptionLabel.setText("");
    }

    /**
     * Checks if str2 is contained inside str1. Case-insensitive.
     * @param str1
     * @param str2
     * @return <code>true</code> if str2 is contained inside str1, <code>false</code> otherwise
     */
    public static boolean containsIgnoreCase(String str1, String str2) {
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();
        return str1.contains(str2);
    }

    /**
     * Populates the grid with moves that match the requirements set by the other settings, such as Subtype or Type.
     */
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

    /**
     * Initializes button listeners, which defines visual changes to the button depending on user action.
     */
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

    /**
     * Enables all category buttons.
     */
    private void enableAllButtons() {
        for (Node button : categoryBox.getChildren()) {
            button.setDisable(false);
        }
    }

    /**
     * Refreshes grid. Sets to display moves of all subtypes.
     * @see #populateMoveGrid()
     */
    @FXML
    public void gridDisplayAllMoves() {
        enableAllButtons();
        categoryAllMoves.setDisable(true);
        currentType = null;
        populateMoveGrid();
    }
    /**
     * Refreshes grid. Sets to display physical moves.
     * @see #populateMoveGrid()
     */
    @FXML
    public void gridDisplayPhysicalMoves() {
        enableAllButtons();
        categoryPhysicalMoves.setDisable(true);
        currentType = Enums.Subtypes.PHYSICAL;
        populateMoveGrid();
    }
    /**
     * Refreshes grid. Sets to display special moves.
     * @see #populateMoveGrid()
     */
    @FXML
    public void gridDisplaySpecialMoves() {
        enableAllButtons();
        categorySpecialMoves.setDisable(true);
        currentType = Enums.Subtypes.SPECIAL;
        populateMoveGrid();
    }
    /**
     * Refreshes grid. Sets to display status moves.
     * @see #populateMoveGrid()
     */
    @FXML
    public void gridDisplayStatusMoves() {
        enableAllButtons();
        categoryStatusMoves.setDisable(true);
        currentType = Enums.Subtypes.STATUS;
        populateMoveGrid();
    }
}
