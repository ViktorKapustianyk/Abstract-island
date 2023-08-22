package org.example.worker;

import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;
import org.example.entity.organism.plant.Grass;
import org.example.entity.organism.plant.Plant;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PlantThread implements Runnable {
    private final GameField gameField;

    public PlantThread(GameField gameField) {
        this.gameField = gameField;
    }

    @Override
    public void run() {
        while (true) {
            // Обработка Plant: reproducePlant
            Cell[][] cells = gameField.getCells();
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell currentCell = cells[i][j];
                    currentCell.getResidentsLock().lock();
                    try {
                        Set<Organism> animals = currentCell.getResidents().get(Type.getTypeFromClass(Animal.class));
                        if (animals != null && !animals.isEmpty()) {
                            Optional<Organism> randomAnimal = animals.stream()
                                    .skip(ThreadLocalRandom.current().nextInt(animals.size()))
                                    .findFirst();

                            randomAnimal.ifPresent(organism -> {
                                if (organism instanceof Animal) {
                                    ((Animal) organism).eat();
                                }
                            });
                        }

                    } finally {
                        currentCell.getResidentsLock().unlock();
                    }
                }
//                try {
//                    Thread.sleep(200); // Задержка между итерациями
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}
