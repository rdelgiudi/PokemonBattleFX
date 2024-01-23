package com.delgiudice.pokemonbattlefx.graphics;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.ArrayList;
import java.util.List;

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

    public void moveParticlesRight(double amount) {
        for (ParticleGroup particleGroup : particleGroupList) {
            for (int i = 0; i< particleGroup.getSize(); i++) {
                particleGroup.setParticlePosition(i, particleGroup.getParticleX(i) + amount,
                        particleGroup.getParticleY(i));
            }
        }
    }

    public void moveParticlesDown(double amount) {
        for (ParticleGroup particleGroup : particleGroupList) {
            for (int i = 0; i< particleGroup.getSize(); i++) {
                particleGroup.setParticlePosition(i, particleGroup.getParticleX(i),
                        particleGroup.getParticleY(i) + amount);
            }
        }
    }

    public void moveParticlesDown(double amount, int group) {
        for (int i = 0; i< particleGroupList.get(group).getSize(); i++) {
            particleGroupList.get(group).setParticlePosition(i, particleGroupList.get(group).getParticleX(i),
                    particleGroupList.get(group).getParticleY(i) + amount);
        }
    }

    public void moveParticlesRight(double amount, int group) {
        if (group >= particleGroupList.size())
            throw new ValueException("Group index outside of range");

        for (int i = 0; i< particleGroupList.get(group).getSize(); i++) {
            particleGroupList.get(group).setParticlePosition(i, particleGroupList.get(group).getParticleX(i) + amount,
                    particleGroupList.get(group).getParticleY(i));
        }
    }

    private KeyFrame getRainAnimationKeyframe(int[] bounds, double screenWidth, double screenHeight) {
        int lowerBoundX = bounds[0];
        int lowerBoundY = bounds[1];
        int upperBoundX = bounds[2];
        int upperBoundY = bounds[3];

        ParticleGroup rainGroup = particleGroupList.get(0);

        return new KeyFrame(Duration.millis(2), e -> {
            moveParticlesDown(1, 0);
            moveParticlesRight(0.5, 0);
            for (int i=0; i< particleGroupList.get(0).getSize(); i++) {
                if (rainGroup.getParticleX(i) > screenWidth + rainGroup.getParticleWidth()  ||
                        rainGroup.getParticleY(i) > screenHeight + rainGroup.getParticleHeight())
                    rainGroup.setParticlePositionRandom(i, lowerBoundX, upperBoundX, lowerBoundY, upperBoundY);
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
            moveParticlesDown(1, 0);
            moveParticlesRight(0.5, 0);
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
