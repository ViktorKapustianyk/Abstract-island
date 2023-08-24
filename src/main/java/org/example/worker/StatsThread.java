package org.example.worker;

import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class StatsThread implements Runnable {
    private final GameField gameField;
    private final ReentrantLock lock = new ReentrantLock();

    public StatsThread(GameField gameField) {
        this.gameField = gameField;
    }

    @Override
    public void run() {
            try {
                lock.lock();
                Map<Type, Integer> organismStatistics = getOrganismStatistics(gameField);
                drawHistogram(organismStatistics);
            } finally {
                lock.unlock();
            }
    }
    public Map<Type, Integer> getOrganismStatistics(GameField gameField) {
        Map<Type, Integer> statistics = new HashMap<>();
        Cell[][] cells = gameField.getCells();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                try {
                    cell.getResidentsLock().lock();
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
        int scale = 50;

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
}
