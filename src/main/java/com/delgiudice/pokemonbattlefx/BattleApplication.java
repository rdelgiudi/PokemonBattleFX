package com.delgiudice.pokemonbattlefx;

import com.delgiudice.pokemonbattlefx.teambuilder.TeamBuilderController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;

public class BattleApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BattleApplication.class.getResource("teambuilder-view.fxml"));
        Pane root = fxmlLoader.load();
        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("Pokemon Battle FX");
        stage.setScene(scene);
        stage.sizeToScene();

        letterbox(scene, root, scene.getWidth(), scene.getHeight());
        stage.show();

        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());
        stage.setWidth(stage.getWidth());
        stage.setHeight(stage.getHeight());
        double ratio = stage.getHeight() / stage.getWidth();
        stage.minHeightProperty().bind(stage.widthProperty().multiply(ratio));
        stage.maxHeightProperty().bind(stage.widthProperty().multiply(ratio));
        stage.setResizable(false);

        KeyCode fullscreenKey = KeyCode.F11;

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == fullscreenKey) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        stage.setFullScreenExitHint(String.format("Press %s or %s to exit fullscreen",
                KeyCode.ESCAPE.getName() ,fullscreenKey.getName()));
    }

    public static void main(String[] args) {
        launch();
    }

    public static void letterbox(final Scene scene, final Pane contentPane, final double initWidth, final double initHeight) {
        //final double initWidth  = scene.getWidth();
        //final double initHeight = scene.getHeight();
        final double ratio      = initWidth / initHeight;

        SceneSizeChangeListener sizeListener = new SceneSizeChangeListener(scene, ratio, initHeight, initWidth, contentPane);
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);
        ChangeListener<? super Parent> changeListener = (javafx.beans.value.ChangeListener<Parent>) (observableValue, parent, t1) -> sizeListener.scale();
        scene.rootProperty().addListener(changeListener);
    }
}