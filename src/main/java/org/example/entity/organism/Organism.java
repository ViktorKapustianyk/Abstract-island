package org.example.entity.organism;
import lombok.*;
import org.example.entity.map.Cell;
import org.example.entity.organism.animal.Animal;
import org.example.interfaces.Reproducible;

import java.util.*;
import java.util.concurrent.locks.Lock;

@Getter
@Setter
public abstract class Organism implements Reproducible{
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
        Lock residentsLock = currentCell.getResidentsLock();
        boolean residentsLockAcquired = false;

        try {
            residentsLockAcquired = residentsLock.tryLock();
            if (residentsLockAcquired) {
                Map<Type, Set<Organism>> residents = currentCell.getResidents();
                Map<Type, Integer> typeCounts = new HashMap<>();

                Set<Type> types = new HashSet<>(residents.keySet());

                for (Type type : types) {
                    Set<Organism> organisms = residents.get(type);
                    int count = 0;

                    for (Organism organism : organisms) {
                        if (organism instanceof Animal && organism.isAlive()) {
                            count++;
                        }
                    }
                    typeCounts.put(type, count);
                }
                return typeCounts;
            } else {
                return Collections.emptyMap();
            }
        } finally {
            if (residentsLockAcquired) {
                residentsLock.unlock();
            }
        }
    }

}
