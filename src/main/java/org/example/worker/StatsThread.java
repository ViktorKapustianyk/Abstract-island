package org.example.worker;

import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.herbivore.Herbivore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsThread implements Runnable {
    private final GameField gameField;

    public StatsThread(GameField gameField) {
        this.gameField = gameField;
    }

    @Override
    public void run() {
        while (true) {
            // Вывод статистики о текущем состоянии gameField
            synchronized (gameField) {
                Map<Type, Integer> organismStatistics = getOrganismStatistics(gameField);
                drawHistogram(organismStatistics);
            }
            try {
                Thread.sleep(5000); // Задержка между выводами статистики
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public Map<Type, Integer> getOrganismStatistics(GameField gameField) {

        Map<Type, Integer> statistics = new HashMap<>();
        Cell[][] cells = gameField.getCells();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.getResidentsLock().lock();
                try {
                    for (Set<Organism> organisms : cell.getResidents().values()) {
                        for (Organism organism : organisms) {
                            Type type = Type.getTypeFromClass(organism.getClass());
                            statistics.put(type, statistics.getOrDefault(type, 0) + 1);
                        }
                    }
                } finally {
                    cell.getResidentsLock().unlock();
                }
            }
        }
        return statistics;
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
