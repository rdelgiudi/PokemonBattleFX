package com.delgiudice.pokemonbattlefx;

import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BattleApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BattleApplication.class.getResource("teambuilder-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        //stage.minHeightProperty().bind(stage.widthProperty().multiply(0.5625));
        //stage.maxHeightProperty().bind(stage.widthProperty().multiply(0.5625));
        stage.setTitle("Pokemon Battle FX");
        stage.setScene(scene);
        TeamBuilderController controller = fxmlLoader.getController();
        controller.setUiResizeListener();

        //BattleController controller = fxmlLoader.getController();
        //BattleLogic logic = new BattleLogic(controller);

        stage.show();
        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
        stage.setMaxHeight(stage.getHeight());
        stage.setMaxWidth(stage.getWidth());
    }

    public static void main(String[] args) {
        launch();
    }
}