package com.delgiudice.pokemonbattlefx;

import com.delgiudice.pokemonbattlefx.network.NetworkThread;
import com.delgiudice.pokemonbattlefx.pokemon.PokemonSpecie;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Class that initiates application components and settings.
 */
public class BattleApplication extends Application {

    /**
     * List of running network threads.
     */
    public static List<NetworkThread> threadList = new ArrayList<>();

    // Experimental - to be completed after completing initial goals
    private static boolean USE_INTERNET_SPRITES = false;
    private static boolean USE_LOCAL_ANIM_SPRITES = false;
    //****************************************

    public static boolean isUseInternetSprites() {
        return USE_INTERNET_SPRITES;
    }

    public static boolean isUseLocalAnimSprites() {
        return USE_LOCAL_ANIM_SPRITES;
    }

    /**
     * Switches the use of animated sprites on or off.
     * @param useInternetSprites set <code>true</code> to use animated sprites, <code>false</code> otherwise
     */
    public static void setUseInternetSprites(boolean useInternetSprites) {
        if (useInternetSprites)
            PokemonSpecie.toggleAnimatedPokemonSprites();
        else
            PokemonSpecie.unloadNetImages();
        USE_INTERNET_SPRITES = useInternetSprites;
    }

    public static void setUseLocalAnimSprites(boolean useLocalAnimSprites) {
        USE_LOCAL_ANIM_SPRITES = useLocalAnimSprites;
    }

    /**
     * Configures UI and starts the application.
     */
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
        //stage.setWidth(stage.getWidth());
        //stage.setHeight(stage.getHeight());
        //double ratio = stage.getHeight() / stage.getWidth();
        //stage.minHeightProperty().bind(stage.widthProperty().multiply(ratio));
        //stage.maxHeightProperty().bind(stage.widthProperty().multiply(ratio));
        //stage.setResizable(false);

        KeyCode fullscreenKey = KeyCode.F11;

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == fullscreenKey) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
        stage.setFullScreenExitHint(String.format("Press %s or %s to exit fullscreen",
                KeyCode.ESCAPE.getName() ,fullscreenKey.getName()));

        stage.setOnCloseRequest(e -> {
            endNetworkThread();
        });
    }

    /**
     * Ends all running network threads present on the list.
     */
    public static void endNetworkThread() {
        if (!threadList.isEmpty())
            System.out.println("Closing network threads...");
        for (NetworkThread thread : threadList) {
            thread.closeConnection();
            System.out.println("Ending connection for thread " + thread.getName());
        }
        threadList.clear();
    }

    public static void main(String[] args) {
        launch();
    }

    //https://stackoverflow.com/questions/16606162/javafx-fullscreen-resizing-elements-based-upon-screen-size
    /**
     * Allows to scale the scene to window size.
     * @param scene current scene
     * @param contentPane root Pane
     * @param initWidth width of the scene
     * @param initHeight height of the scene
     * @see SceneSizeChangeListener
     */
    public static void letterbox(final Scene scene, final Pane contentPane, final double initWidth, final double initHeight) {
        //final double initWidth  = scene.getWidth();
        //final double initHeight = scene.getHeight();
        final double ratio = initWidth / initHeight;

        SceneSizeChangeListener sizeListener = new SceneSizeChangeListener(scene, ratio, initHeight, initWidth, contentPane);
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);
        ChangeListener<? super Parent> changeListener =
                (javafx.beans.value.ChangeListener<Parent>) (observableValue, parent, t1) -> sizeListener.scale();
        scene.rootProperty().addListener(changeListener);
    }
}