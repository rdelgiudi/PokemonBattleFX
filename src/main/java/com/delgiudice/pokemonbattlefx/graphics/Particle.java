package com.delgiudice.pokemonbattlefx.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Particle extends ImageView {

    Pane currentParentPane;

    public Particle(Image image) {
        super(image);
    }

    public void increaseSpriteSize(int widthIncrease, int heightIncrease) {
        setFitWidth(getFitWidth() + widthIncrease);
        setFitHeight(getFitHeight() + heightIncrease);
    }

    public void flipParticle() {
        setTranslateZ(getFitWidth() / 2.0);
        setRotationAxis(Rotate.Y_AXIS);
        int rotate = getRotate() == 0 ? 180 : 0;
        setRotate(rotate);
    }

    public double getMiddleX() {
        return getLayoutX() + getFitWidth() / 2.0;
    }

    public double getMiddleY() {
        return getLayoutY() + getFitHeight() / 2.0;
    }

    public void setMiddleX(double x) {
        setLayoutX(x - getFitWidth() / 2.0);
    }

    public void setMiddleY(double y) {
        setLayoutY(y - getFitHeight() / 2.0);
    }

    public void moveRight(int amount) {
        setLayoutX(getLayoutX() + amount);
    }

    public void moveDown(int amount) {
        setLayoutY(getLayoutY() + amount);
    }
}
