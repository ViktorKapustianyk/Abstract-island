package org.example.entity.organism.plant;

public class Grass extends Plant{
    private double weight;
    private int maxNumPerCell;

    public Grass() {
    }

    public Grass(double weight, int maxNumPerCell) {
        this.weight = weight;
        this.maxNumPerCell = maxNumPerCell;
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
}
