package org.example.entity.organism.plant;

import lombok.Getter;
import org.example.creators.OrganismFactory;
import org.example.creators.OrganismInfo;
import org.example.entity.map.Cell;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;

import java.util.*;
import java.util.concurrent.locks.Lock;

@Getter
public abstract class Plant extends Organism {

    private OrganismFactory organismFactory = new OrganismFactory();

    public void reproducePlant() {

        Cell currentCell = this.getCell();
        if (currentCell == null) {
            return;
        }

        Lock residentsLock = currentCell.getResidentsLock();
        if (residentsLock == null) {
            return;
        }
        boolean residentsLockAcquired = false;
        try {
            residentsLockAcquired = residentsLock.tryLock();
            if (residentsLockAcquired) {
            Map<Type, Set<Organism>> residents = currentCell.getResidents();

            Type typeOfOrganism = targetType(this);

            Set<Organism> organisms = residents.getOrDefault(typeOfOrganism, new HashSet<>());
            List<Organism> newOrganisms = new ArrayList<>();
            int count = organisms.size();

                for (Organism organism : organisms) {
                    if (organism instanceof Plant plant) {
                        Organism newOrganism = plant.reproduce();
                        int maxNumPerCell = organism.getMaxNumPerCell();

                        if (count < maxNumPerCell) {
                            newOrganisms.add(newOrganism);
                            count++;
                        }
                    }
                }

                for (Organism organism : newOrganisms) {
                    residents.get(targetType(organism)).add(organism);
                }
            }
        } finally {
            if (residentsLockAcquired) {
                residentsLock.unlock();
            }
        }
    }
    @Override
    public Organism reproduce() {
        OrganismInfo organismInfo = new OrganismInfo();
        organismInfo.setType(this.getClass().getSimpleName());
        organismInfo.setWeight(this.getWeight());
        organismInfo.setMaxNumPerCell(this.getMaxNumPerCell());
        organismInfo.setSpeed(this.getSpeed());
        organismInfo.setFoodNeed(this.getFoodNeed());
        organismInfo.setAlive(true);

        try {
            return organismFactory.createOrganism(organismInfo);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
