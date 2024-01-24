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

    private double[] normalizeVector(double[] vector) {
        double[] result = new double[vector.length];
        double sum = 0;

        for (double d : vector)
            sum += Math.pow(d, 2);

        if (sum == 0)
            throw new ValueException("Cannot normalize a zero vector");

        sum = Math.sqrt(sum);

        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] / sum;
        }

        return result;
    }

    public void moveParticlesAwayFrom(double x, double y, double amount) {
        for (int i=0; i < getSize(); i++) {
            Particle particle = particleGroup.get(i);
            double newX;
            double newY;
            double particleX = particle.getMiddleX();
            double particleY = particle.getMiddleY();
            // vx = xx - x
            // vy = yy - y
            double vx = particleX - x; //vector away from x coord of point
            double vy = particleY - y; //vector away from y coord of point

            double[] vector = new double[]{vx, vy};
            vector = normalizeVector(vector);
            newX = particleX + vector[0] * amount;
            newY = particleY + vector[1] * amount;
            particle.setMiddleX(newX);
            particle.setMiddleY(newY);
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

    public void setParticlePositionMiddle(int index, double centerX, double centerY) {
        Particle particle = particleGroup.get(index);
        particle.setMiddleX(centerX);
        particle.setMiddleY(centerY);
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

    public void setOpacity(double opacity) {
        for (Particle particle : particleGroup)
            particle.setOpacity(opacity);
    }

    public void decreaseOpacity(double value) {
        for (Particle particle : particleGroup) {
            particle.setOpacity(particle.getOpacity() - value);
        }
    }
}
