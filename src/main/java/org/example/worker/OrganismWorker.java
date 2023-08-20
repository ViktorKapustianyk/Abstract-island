package org.example.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@AllArgsConstructor
public class OrganismWorker implements Runnable {
    private GameField gameField;
    private Statistics statistics; // Объявите поле
//    private CountDownLatch stepLatch;

    public OrganismWorker(GameField gameField) {
        this.gameField = gameField;
    }

    @Override
    public void run() {
        Cell[][] cells = gameField.getCells();
        processCell(cells);
//        while (statistics.hasLivingHerbivores()) {
//            int numSteps = 10000;
//            for (int step = 1; step <= numSteps; step++) {
//                System.out.println("Step " + step + ":");
//                processCell(cells);
//            }
//        }
    }

    private void processCell(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell currentCell = cells[i][j];
                Map<Type, Set<Organism>> residents = currentCell.getResidents();
                // Создаем копии множеств организмов для безопасной итерации
                for (Type type : Type.values()) {
                    Set<Organism> organismsCopy = new HashSet<>(residents.getOrDefault(type, new HashSet<>()));
                    for (Organism organism : organismsCopy) {
                        if (organism instanceof Animal && organism.isAlive()) {
                            Animal animal = (Animal) organism;
                            animal.eat();
//                            System.out.println("________Animal eat ________");
                            animal.reproduceTribe();
//                            System.out.println("________Animal reproduce ________");
                            animal.move(gameField);
//                            System.out.println("________Animal move ________");
                        }
                    }
                }
            }
        }
    }
}
