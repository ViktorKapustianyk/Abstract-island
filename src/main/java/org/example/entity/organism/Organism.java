package org.example.entity.organism;
import lombok.*;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.animal.Animal;
import org.example.interfaces.Eatable;
import org.example.interfaces.Movable;
import org.example.interfaces.OrganismTask;
import org.example.interfaces.Reproducible;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public abstract class Organism implements Reproducible, OrganismTask {
    private Cell cell;
    private int currentX;
    private int currentY;

    private double weight;
    private int maxNumPerCell;
    private int speed;
    private double foodNeed;
    private boolean isAlive;
    public void killOrganism() {
        isAlive = false;
    }
    public abstract Organism reproduce();
    public Type targetType(Organism organism) {
        String fullClassName = organism.getClass().getName();
        String[] parts = fullClassName.split("\\.");
        String simpleClassName = parts[parts.length - 1];
        Type targetType = Type.valueOf(simpleClassName.toUpperCase());

        return targetType;
    }
    public Map<Type, Integer> typeCounts(Cell currentCell) {
        Map<Type, Set<Organism>> residents = currentCell.getResidents();
        Map<Type, Integer> typeCounts = new HashMap<>(); // Мапа для зберігання лічильників кількості об'єктів кожного типу

        for (Type type : residents.keySet()) {
            Set<Organism> organisms = residents.get(type);
            int count = 0; // Лічильник кількості об'єктів даного типу

            for (Organism organism : organisms) {
                if (organism instanceof Animal && organism.isAlive()) {
                    count++;
                }
            }

            typeCounts.put(type, count);
        }
        return typeCounts;
    }
}
