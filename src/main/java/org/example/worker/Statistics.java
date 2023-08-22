package org.example.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.herbivore.Herbivore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
public class Statistics {
    private GameField gameField;
    private Lock statisticsLock;

    public Statistics(GameField gameField) {
        this.gameField = gameField;
    }

    public Map<Type, Integer> getOrganismStatistics() {
        statisticsLock.lock();
        try {
            Cell[][] cells = gameField.getCells();

            Map<Type, AtomicInteger> organismStatistics = new HashMap<>();

            for (Cell[] row : cells) {
                for (Cell cell : row) {
                    Map<Type, Set<Organism>> residents = cell.getResidents();

                    for (Set<Organism> organisms : residents.values()) {
                        for (Organism organism : organisms) {

                            Type organismType = organism.targetType(organism);
                            organismStatistics
                                    .computeIfAbsent(organismType, type -> new AtomicInteger())
                                    .incrementAndGet();
                        }
                    }
                }
            }

            Map<Type, Integer> finalStatistics = new HashMap<>();
            organismStatistics.forEach((type, atomicInteger) -> finalStatistics.put(type, atomicInteger.get()));
            return finalStatistics;
        } finally {
            statisticsLock.unlock();
        }
    }

    public synchronized void drawHistogram(Map<Type, Integer> data) {
        int maxCount = data.values().stream().max(Integer::compareTo).orElse(0);
        int scale = 50; // Масштаб для отображения

        for (Map.Entry<Type, Integer> entry : data.entrySet()) {
            Type type = entry.getKey();
            int count = entry.getValue();
            int scaledCount = (int) ((double) count / maxCount * scale);
            String bar = repeatCharacter('#', scaledCount);
            System.out.printf("%-10s | %-3d | %s\n", type, count, bar);
        }
    }
    private String repeatCharacter(char character, int times) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; i++) {
            result.append(character);
        }
        return result.toString();
    }

    public boolean hasLivingHerbivores() {
        Cell[][] cells = gameField.getCells();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                Map<Type, Set<Organism>> residents = cell.getResidents();
                for (Set<Organism> organisms : residents.values()) {
                    for (Organism organism : organisms) {
                        if (organism instanceof Herbivore) {
//                            System.out.println("The program STOP : Predator don't have ate all Herbivore");
                            return true; // Если найден Herbivore, возвращаем true
                        }
                    }
                }
            }
        }
        System.out.println("The program STOP : Predator have ate all Herbivore");
        return false; // Если не найдено Herbivore, возвращаем false
    }
}
