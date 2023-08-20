package org.example.worker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.herbivore.Herbivore;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
public class Statistics {
    private GameField gameField;
    public Map<Type, Integer> getOrganismStatistics() {
        synchronized (gameField) {
            Cell[][] cells = gameField.getCells();

            Map<Type, Integer> organismStatistics = new HashMap<>();

            for (Cell[] row : cells) {
                for (Cell cell : row) {
                    Map<Type, Set<Organism>> residents = cell.getResidents();

                    for (Set<Organism> organisms : residents.values()) {
                        for (Organism organism : organisms) {

                            Type organismType = organism.targetType(organism);
                            organismStatistics.put(organismType, organismStatistics.getOrDefault(organismType, 0) + 1);

                        }
                    }
                }
            }

            return organismStatistics;
        }
    }

    public void drawHistogram(Map<Type, Integer> data) {
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
                            return true; // Если найден Herbivore, возвращаем true
                        }
                    }
                }
            }
        }
        return false; // Если не найдено Herbivore, возвращаем false
    }
}
