package org.example.entity.organism;

import lombok.*;
import org.example.creators.EatProbability;
import org.example.creators.OrganismInfo;
import org.example.entity.organism.animal.Animal;
import org.example.interfaces.Reproducible;

import java.util.ArrayList;
import java.util.List;

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

    protected Organism createOrganism(OrganismInfo organismInfo) throws InstantiationException, IllegalAccessException {
        Type type = Type.valueOf(organismInfo.getType().toUpperCase());
        Organism organism = type.getOrganismClass().newInstance();

        organism.setWeight(organismInfo.getWeight());
        organism.setMaxNumPerCell(organismInfo.getMaxNumPerCell());
        organism.setSpeed(organismInfo.getSpeed());
        organism.setAlive(true);

        if (organism instanceof Animal) {
            Animal animal = (Animal) organism;
            animal.setFoodNeed(organismInfo.getFoodNeed());

            List<EatProbability> eatProbabilities = organismInfo.getEatProbabilities();
            for (EatProbability eatProbability : eatProbabilities) {
                Type eatType = Type.valueOf(eatProbability.getType().toString());
                animal.setEatProbability(eatType, eatProbability.getProbability());
            }
        }
        return organism;
    }
    public abstract Organism reproduce();

}
