package org.example.creators;

import org.example.entity.organism.Organism;
import org.example.entity.organism.animal.Animal;
import org.example.entity.organism.Type;

import java.util.List;

public class OrganismFactory {
    public Organism createOrganism(OrganismInfo organismInfo) throws InstantiationException, IllegalAccessException {
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
}
