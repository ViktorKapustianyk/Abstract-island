package org.example.worker;

import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;
import org.example.entity.organism.animal.herbivore.Rabbit;
import org.example.entity.organism.animal.predator.Boa;
import org.example.entity.organism.plant.Grass;
import org.example.entity.organism.plant.Plant;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AnimalThread implements Runnable {
    private final GameField gameField;

    public AnimalThread(GameField gameField) {
        this.gameField = gameField;
    }

    @Override
    public void run() {
        while (true) {
            // Обработка Animal: eat, move, reproduceTribe
            Cell[][] cells = gameField.getCells();
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell currentCell = cells[i][j];
                    Map<Type, Set<Organism>> residentsCopy;
                    currentCell.getResidentsLock().lock();
                    try {
                        Set<Organism> animal = currentCell.getResidents().get(Type.getTypeFromClass(Boa.class));
                        if (animal != null && !animal.isEmpty()) {
                            Organism randomAnimal = animal.stream().skip(ThreadLocalRandom.current().nextInt(animal.size())).findFirst().orElse(null);
                            if (randomAnimal instanceof Animal) {
                                ((Animal) randomAnimal).eat();
                            }
                        }
                    } finally {
                        currentCell.getResidentsLock().unlock();
                    }
                }
//                try {
//                    Thread.sleep(100); // Задержка между итерациями
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}
