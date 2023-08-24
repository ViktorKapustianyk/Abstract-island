package org.example.worker;

import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AnimalThread implements Runnable {
    private final GameField gameField;

    public AnimalThread(GameField gameField) {

        this.gameField = gameField;
    }

    public void run() {
        Cell[][] cells = gameField.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                processCell(cells[i][j]);
            }
        }
    }

    private void processCell(Cell currentCell) {
        if (currentCell.getResidentsLock().tryLock()) {
            try {
                Map<Type, Set<Organism>> residentsCopy = new HashMap<>(currentCell.getResidents());
                for (Type type : residentsCopy.keySet()) {
                    processOrganisms(currentCell, type);
                }
            } finally {
                currentCell.getResidentsLock().unlock();
            }
        }
    }

    private void processOrganisms(Cell currentCell, Type type) {
        Set<Organism> organisms = currentCell.getResidents().get(type);
        if (organisms != null && !organisms.isEmpty()) {
            Organism randomOrganism = getRandomOrganism(organisms);
            if (randomOrganism instanceof Animal) {
                processAnimal((Animal) randomOrganism);
            }
        }
    }

    private Organism getRandomOrganism(Set<Organism> organisms) {
        return organisms.stream()
                .skip(ThreadLocalRandom.current().nextInt(organisms.size()))
                .findFirst()
                .orElse(null);
    }

    private void processAnimal(Animal animal) {
        if (animal.getLock().tryLock()) {
            try {
                animal.eat();
            } finally {
                animal.getLock().unlock();
            }
        }
    }
}

