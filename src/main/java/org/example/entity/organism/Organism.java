package org.example.entity.organism;
import lombok.*;
import org.example.creators.EatProbability;
import org.example.creators.OrganismInfo;
import org.example.entity.map.Cell;
import org.example.entity.organism.animal.Animal;
import org.example.interfaces.Reproducible;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public abstract class Organism implements Reproducible {
    private double weight;
    private int maxNumPerCell;
    private int speed;
    private double foodNeed;
    private boolean isAlive;
    public void killOrganism() {
        isAlive = false;
    }
    public abstract Organism reproduce();

    public Type targetType(Organism organism) {
        String fullClassName = organism.getClass().getName();
        String[] parts = fullClassName.split("\\.");
        String simpleClassName = parts[parts.length - 1];
        Type targetType = Type.valueOf(simpleClassName.toUpperCase());

        return targetType;
    }
}
