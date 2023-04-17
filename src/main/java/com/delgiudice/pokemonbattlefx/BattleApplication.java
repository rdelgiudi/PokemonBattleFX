package com.delgiudice.pokemonbattlefx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class BattleApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BattleApplication.class.getResource("teambuilder-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        //stage.minHeightProperty().bind(stage.widthProperty().multiply(0.5625));
        //stage.maxHeightProperty().bind(stage.widthProperty().multiply(0.5625));
        stage.setTitle("Pokemon Battle FX");
        stage.setScene(scene);
        stage.setMinHeight(scene.getHeight());
        stage.setMinWidth(scene.getWidth());
        TeamBuilderController controller = fxmlLoader.getController();
        controller.setUiResizeListener();

        //BattleController controller = fxmlLoader.getController();
        //BattleLogic logic = new BattleLogic(controller);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}