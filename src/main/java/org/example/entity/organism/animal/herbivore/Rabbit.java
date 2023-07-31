package org.example.entity.organism.animal.herbivore;

import org.example.entity.organism.animal.Animal;

public class Rabbit extends Animal {
    private double weight = 2;
    private int maxNumPerCell = 15;
    private double probability = 1;
    private int speed = 2;
    private double foodNeed = 0.45;
    public Rabbit() {
    }

    public Rabbit(double weight, int maxNumPerCell, double probability, int speed, double foodNeed) {
        this.weight = weight;
        this.maxNumPerCell = maxNumPerCell;
        this.probability = probability;
        this.speed = speed;
        this.foodNeed = foodNeed;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getMaxNumPerCell() {
        return maxNumPerCell;
    }

    public void setMaxNumPerCell(int maxNumPerCell) {
        this.maxNumPerCell = maxNumPerCell;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public double getFoodNeed() {
        return foodNeed;
    }

    public void setFoodNeed(double foodNeed) {
        this.foodNeed = foodNeed;
    }
}
