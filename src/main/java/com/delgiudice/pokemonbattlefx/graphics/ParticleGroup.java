package com.delgiudice.pokemonbattlefx.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleGroup {
    private double width, height;
    private Image particleImage;
    private List<ImageView> particleGroup = new ArrayList<>();
    private Pane currentParentPane;

    public ParticleGroup(Image particleImage, double width, double height) {
        this.particleImage = particleImage;
        this.width = width;
        this.height = height;
    }

    public int getSize() {
        return particleGroup.size();
    }

    public double getParticleWidth() {
        return width;
    }

    public double getParticleHeight() {
        return height;
    }

    public void addParticle() {
        ImageView particleView = new ImageView(particleImage);
        particleView.setFitHeight(height);
        particleView.setFitWidth(width);
        particleGroup.add(particleView);
    }

    public void removeParticle(int index) {
        particleGroup.remove(index);
    }

    public void addMultipleParticles(int num) {
        for (int i=0; i < num; i++)
            addParticle();
    }

    public void addParticleGroupToPane(Pane pane) {
        if (currentParentPane == null) {
            pane.getChildren().addAll(particleGroup);
            currentParentPane = pane;
        }
        else {
            throw new IllegalStateException("Particle group already belong to a Pane!");
        }
    }

    public void addParticleGroupToPane(Pane pane, int index) {
        if (currentParentPane == null) {
            pane.getChildren().addAll(index ,particleGroup);
            currentParentPane = pane;
        }
        else {
            throw new IllegalStateException("Particle group already belongs to a Pane!");
        }
    }

    public void removeParticleGroupFromPane() {
        if (currentParentPane != null) {
            currentParentPane.getChildren().removeAll(particleGroup);
            currentParentPane = null;
        }
    }

    public double getParticleX(int index) {
        ImageView particle = particleGroup.get(index);
        return particle.getLayoutX();
    }

    public double getParticleY(int index) {
        ImageView particle = particleGroup.get(index);
        return particle.getLayoutY();
    }

    public void setParticlePosition(int index, double x, double y) {
        ImageView particle = particleGroup.get(index);
        particle.setLayoutX(x);
        particle.setLayoutY(y);
    }

    public void setParticlePositionRandom(int index ,int lowerBoundX, int upperBoundX, int lowerBoundY, int upperBoundY) {
        Random random = new Random();
        double newX = random.nextInt(upperBoundX - lowerBoundX) + lowerBoundX;
        double newY = random.nextInt(upperBoundY - lowerBoundY) + lowerBoundY;
        setParticlePosition(index, newX, newY);
    }

    public void setParticleGroupPositionsRandom(int lowerBoundX, int upperBoundX, int lowerBoundY, int upperBoundY) {
        Random random = new Random();
        for (int i=0; i<particleGroup.size(); i++) {
            double newX = random.nextInt(upperBoundX - lowerBoundX) + lowerBoundX;
            double newY = random.nextInt(upperBoundY - lowerBoundY) + lowerBoundY;
            setParticlePosition(i, newX, newY);
        }
    }

    public void setVisible(boolean set) {
        for (ImageView particle : particleGroup) {
            particle.setVisible(set);
        }
    }
}
