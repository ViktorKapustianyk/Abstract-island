package org.example.worker;

import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.plant.Grass;
import org.example.entity.organism.plant.Plant;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;


public class PlantThread implements Runnable {
    private final GameField gameField;

    public PlantThread(GameField gameField) {

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
        Lock residentsLock = currentCell.getResidentsLock();
        residentsLock.lock();
        try {
            Set<Organism> plants = currentCell.getResidents().get(Type.getTypeFromClass(Grass.class));
            if (plants != null && !plants.isEmpty()) {
                processPlants(currentCell, plants);
            }
        } finally {
            residentsLock.unlock();
        }
    }

    private void processPlants(Cell currentCell, Set<Organism> plants) {
        Optional<Organism> randomPlant = getRandomPlant(plants);
        randomPlant.ifPresent(organism -> {
            if (organism instanceof Plant) {
                processPlantReproduction(currentCell, (Plant) organism);
            }
        });
    }

    private Optional<Organism> getRandomPlant(Set<Organism> plants) {
        return plants.stream()
                .skip(ThreadLocalRandom.current().nextInt(plants.size()))
                .findFirst();
    }

    private void processPlantReproduction(Cell currentCell, Plant plant) {
        Lock residentsLock = currentCell.getResidentsLock();
        if (residentsLock.tryLock()) {
            try {
                plant.reproducePlant();
            } finally {
                residentsLock.unlock();
            }
        }
    }
}
