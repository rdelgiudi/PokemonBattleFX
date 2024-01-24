package com.delgiudice.pokemonbattlefx.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleGroup {
    private double width, height;
    private Image particleImage;
    private List<Particle> particleGroup = new ArrayList<>();
    private Pane currentParentPane;

    public ParticleGroup(Image particleImage, double width, double height) {
        this.particleImage = particleImage;
        this.width = width;
        this.height = height;
    }

    public Pane getCurrentParentPane() {
        return currentParentPane;
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
        Particle particleView = new Particle(particleImage);
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

    public void flipParticle(int index) {
        particleGroup.get(index).flipParticle();
    }

    public void moveParticlesDown(double amount) {
        for (int i = 0; i< getSize(); i++) {
            setParticlePosition(i, getParticleX(i),
                    getParticleY(i) + amount);
        }
    }

    public void moveParticlesRight(double amount) {
        for (int i = 0; i< getSize(); i++) {
            setParticlePosition(i, getParticleX(i) + amount,
                    getParticleY(i));
        }
    }

    public void moveParticlesAwayFrom(double x, double y) {
        for (int i=0; i < getSize(); i++) {
            Particle particle = particleGroup.get(i);
            double newX;
            double newY;
            double particleX = particle.getLayoutX();
            double particleY = particle.getLayoutY();
            // y = ax + b
            if (particleX - x == 0)
                x = x - 0.001 * x;
            double a = (particleY - y) / (particleX - x);
            double b = y - (a * x);

            if (x > particleX) {
                newX = particleX - 1;
            }
            else {
                newX = particleX + 1;
            }
            newY = newX * a + b;
            particle.setLayoutX(newX);
            particle.setLayoutY(newY);
        }
    }

    public void increaseSpriteSize(int widthIncrease, int heightIncrease) {
        for (Particle particle : particleGroup) {
            particle.increaseSpriteSize(widthIncrease, heightIncrease);
            width += widthIncrease;
            height += heightIncrease;
        }
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
        for (Particle particle : particleGroup) {
            particle.setVisible(set);
        }
    }
}
