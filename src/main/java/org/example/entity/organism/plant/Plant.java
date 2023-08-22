package org.example.entity.organism.plant;

import lombok.Getter;
import org.example.creators.OrganismFactory;
import org.example.creators.OrganismInfo;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;

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
            return; // Или другие действия, если блокировка недоступна
        } // Захватываем блокировку на уровне ячейки
        residentsLock.lock();
        try {
            Map<Type, Set<Organism>> residents = currentCell.getResidents();

            Type typeOfOrganism = targetType(this);

            Set<Organism> organisms = residents.getOrDefault(typeOfOrganism, new HashSet<>());
            List<Organism> newOrganisms = new ArrayList<>();
            int count = organisms.size();

            Lock typeLock = currentCell.getTypeLock(typeOfOrganism);
            typeLock.lock(); // Захватываем блокировку на уровне типа организма

            try {
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

                // Важно обновить residents только после завершения итерации и модификации данных
                for (Organism organism : newOrganisms) {
                    residents.get(targetType(organism)).add(organism);
                }
            } finally {
                typeLock.unlock(); // Освобождаем блокировку на уровне типа организма
            }
        } finally {
            residentsLock.unlock(); // Освобождаем блокировку на уровне ячейки
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

    public void execute(GameField gameField){
        reproducePlant();

    }
}
