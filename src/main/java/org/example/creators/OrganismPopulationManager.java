package org.example.creators;

import org.example.entity.map.Cell;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrganismPopulationManager {
    private Cell[][] cells;
    public void populate(Cell[][] cells, List<OrganismInfo> organismInfoList) throws InstantiationException, IllegalAccessException {
        for (OrganismInfo organismInfo : organismInfoList) {
            Type type = Type.valueOf(organismInfo.getType().toUpperCase());

            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell cell = cells[i][j];
                    Map<Type, Set<Organism>> residents = cell.getResidents();
                    Set<Organism> tribe = residents.computeIfAbsent(type, k -> new HashSet<>());

                    for (int k = 0; k < organismInfo.getMaxNumPerCell() / 2; k++) {
                        Organism organism = createOrganism(organismInfo);
                        tribe.add(organism);
                    }
                }
            }
        }
    }

    private Organism createOrganism(OrganismInfo organismInfo) throws InstantiationException, IllegalAccessException {
        Type type = Type.valueOf(organismInfo.getType().toUpperCase());
        Organism organism = type.getOrganismClass().newInstance();

        organism.setWeight(organismInfo.getWeight());
        organism.setMaxNumPerCell(organismInfo.getMaxNumPerCell());
        organism.setSpeed(organismInfo.getSpeed());

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
