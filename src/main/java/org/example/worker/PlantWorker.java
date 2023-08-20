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
import org.example.entity.organism.plant.Plant;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

@Getter
@Setter
@AllArgsConstructor
public class PlantWorker implements Runnable {
    private GameField gameField;
    private Statistics statistics; // Объявите поле

    //    private CountDownLatch stepLatch;
    public PlantWorker(GameField gameField) {
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

                for (Type type : residents.keySet()) {
                    Set<Organism> organisms = new HashSet<>(residents.get(type));

                    for (Organism organism : organisms) {
                        if (organism instanceof Plant plant) {
//                            System.out.println("________Plant reproduce ________");
                            plant.reproducePlant();
                        }
                    }
                }
            }
        }
    }
}
