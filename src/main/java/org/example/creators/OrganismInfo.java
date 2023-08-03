package org.example.creators;

public class OrganismInfo {
    private String type;
    private double weight;
    private int maxNumPerCell;
    private double probability;
    private int speed;
    private double foodNeed;

    public OrganismInfo() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
