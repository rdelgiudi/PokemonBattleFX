package com.delgiudice.pokemonbattlefx.graphics;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleAnimationCreator{

    public static int ALL_GROUPS = -1;

    private List<ParticleGroup> particleGroupList = new ArrayList<>();
    private Timeline animationTimeLine = new Timeline();

    private EventHandler<ActionEvent> onStopHandler;

    public ParticleAnimationCreator(ParticleGroup particleGroup) {
        particleGroupList.add(particleGroup);
    }

    public void addParticleGroup(ParticleGroup particleGroup) {
        particleGroupList.add(particleGroup);
    }

    public void removeParticleGroup(int index) {
        particleGroupList.remove(index);
    }

    public ObservableList<KeyFrame> getKeyFrames() {
        return animationTimeLine.getKeyFrames();
    }

    public void setCycleCount(int value) {
        animationTimeLine.setCycleCount(value);
    }

    public void setOnFinished(EventHandler<ActionEvent> handler) {
        animationTimeLine.setOnFinished(handler);
    }

    public void setOnEnded(EventHandler<ActionEvent> handler) {
        this.onStopHandler = handler;
    }

    public void play() {
        animationTimeLine.play();
    }

    public void stop() {
        animationTimeLine.stop();
        if (onStopHandler != null)
            onStopHandler.handle(new ActionEvent());
    }

    public void moveAllParticlesRight(double amount) {
        for (ParticleGroup particleGroup : particleGroupList) {
            for (int i = 0; i< particleGroup.getSize(); i++) {
                particleGroup.setParticlePosition(i, particleGroup.getParticleX(i) + amount,
                        particleGroup.getParticleY(i));
            }
        }
    }

    public void moveAllParticlesDown(double amount) {
        for (ParticleGroup particleGroup : particleGroupList) {
            for (int i = 0; i< particleGroup.getSize(); i++) {
                particleGroup.setParticlePosition(i, particleGroup.getParticleX(i),
                        particleGroup.getParticleY(i) + amount);
            }
        }
    }

    public void moveParticleGroupDown(double amount, int group) {
        if (group >= particleGroupList.size())
            throw new ValueException("Group index outside of range");

        particleGroupList.get(group).moveParticlesDown(amount);
    }

    public void moveParticleGroupRight(double amount, int group) {
        if (group >= particleGroupList.size())
            throw new ValueException("Group index outside of range");

        particleGroupList.get(group).moveParticlesRight(amount);
    }

    private Timeline getRainSplashAnimation(Pane parentPane, double x, double y) {
        Random generator = new Random();

        Particle rainSplash = new Particle(new Image("sprites/particle_rain_splash.png"));
        rainSplash.setFitWidth(2);
        rainSplash.setFitHeight(2);

        ParticleGroup rainSplashGroup = new ParticleGroup(new Image("sprites/particle_rain_drop.png"), 10,10);
        rainSplashGroup.addMultipleParticles(2);

        parentPane.getChildren().add(2, rainSplash);
        rainSplash.setLayoutX(x);
        rainSplash.setLayoutY(y);
        double middleX = x + rainSplash.getFitWidth() / 2;
        double middleY = y + rainSplash.getFitHeight() / 2;

        rainSplashGroup.addParticleGroupToPane(parentPane, 2);
        rainSplashGroup.setParticlePosition(0, x-2, y-(1 + generator.nextInt(3)));
        rainSplashGroup.setParticlePosition(1, x+2, y-(1 + generator.nextInt(3)));
        rainSplashGroup.flipParticle(1);

        KeyFrame kf = new KeyFrame(Duration.millis(8), e-> {
            rainSplash.increaseSpriteSize(1, 1);
            double newX = middleX - rainSplash.getFitWidth() / 2;
            double newY = middleY - rainSplash.getFitHeight() / 2;
            rainSplash.setLayoutX(newX);
            rainSplash.setLayoutY(newY);
            rainSplashGroup.moveParticlesAwayFrom(middleX, middleY);
        });

        Timeline timeline = new Timeline(kf);
        timeline.setCycleCount(generator.nextInt(150) + 20);
        timeline.setOnFinished(e -> {
            parentPane.getChildren().remove(rainSplash);
            rainSplashGroup.removeParticleGroupFromPane();
        });
        return timeline;
    }

    private KeyFrame getRainAnimationKeyframe(int[] bounds, double screenWidth, double screenHeight) {
        int lowerBoundX = bounds[0];
        int lowerBoundY = bounds[1];
        int upperBoundX = bounds[2];
        int upperBoundY = bounds[3];

        ParticleGroup rainGroup = particleGroupList.get(0);
        Random generator = new Random();

        return new KeyFrame(Duration.millis(2), e -> {
            moveParticleGroupDown(1, 0);
            moveParticleGroupRight(0.5, 0);
            for (int i=0; i< particleGroupList.get(0).getSize(); i++) {
                if (rainGroup.getParticleX(i) > screenWidth + rainGroup.getParticleWidth()  ||
                        rainGroup.getParticleY(i) > screenHeight + rainGroup.getParticleHeight())
                    rainGroup.setParticlePositionRandom(i, lowerBoundX, upperBoundX, lowerBoundY, upperBoundY);

                else if (rainGroup.getParticleY(i) > screenHeight - 500 && rainGroup.getParticleY(i) < screenHeight - 150
                        && generator.nextInt(2000) == 123) {
                    double splashX = rainGroup.getParticleX(i) + rainGroup.getParticleWidth() / 2;
                    double splashY = rainGroup.getParticleY(i) + rainGroup.getParticleHeight();
                    getRainSplashAnimation(rainGroup.getCurrentParentPane(), splashX,
                            splashY).play();
                    rainGroup.setParticlePositionRandom(i, lowerBoundX, upperBoundX, lowerBoundY, upperBoundY);
                }
            }
        });
    }

    public void initRainAnimation(int[] bounds, double screenWidth, double screenHeight) {
        KeyFrame kf = getRainAnimationKeyframe(bounds, screenWidth, screenHeight);
        getKeyFrames().clear();
        getKeyFrames().add(kf);
        setCycleCount(Animation.INDEFINITE);
    }

    private KeyFrame getRainAnimationEndKeyframe() {
        return new KeyFrame(Duration.millis(2), e -> {
            moveParticleGroupDown(1, 0);
            moveParticleGroupRight(0.5, 0);
        });
    }

    public void endRainAnimation(int lowerBoundY, int screenHeight) {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = getRainAnimationEndKeyframe();

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Math.abs(screenHeight - lowerBoundY));
        timeline.setOnFinished(e -> {particleGroupList.get(0).removeParticleGroupFromPane();});
        timeline.play();
    }

    public void clearAnimationAfterEnd() {
        setOnEnded(e -> {
            for (ParticleGroup particleGroup : particleGroupList)
                particleGroup.removeParticleGroupFromPane();
        });
    }
}
