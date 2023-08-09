package org.example.entity.organism;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Organism {
    private double weight;
    private int maxNumPerCell;
    private int speed;
    private double foodNeed;
    public boolean isAlive = true;
    public void reproduce() {}
}
