package org.example.entity.organism.animal;

import lombok.*;
import org.example.creators.EatProbability;
import org.example.creators.OrganismFactory;
import org.example.creators.OrganismInfo;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.interfaces.Eatable;
import org.example.interfaces.Movable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
public abstract class Animal extends Organism implements Movable, Eatable {

    private OrganismFactory organismFactory = new OrganismFactory();
    private Map<Type, Double> eatProbabilities = new HashMap<>();
    private final Lock animalLock = new ReentrantLock();

    public void setEatProbability(Type type, double probability) {
        eatProbabilities.put(type, probability);
    }

    public double getEatProbability(Type type) {
        return eatProbabilities.getOrDefault(type, 0.0);
    }
    public Lock getLock() {return animalLock;}

    public void move(GameField gameField) {

        Cell currentCell = this.getCell();
        if (currentCell == null) {
            return;
        }

        Random random = ThreadLocalRandom.current();

        int direction = random.nextInt(4);

        int speed = getSpeed();
        int newX = calculateNewX(direction, this.getCurrentX(), speed, gameField);
        int newY = calculateNewY(direction, this.getCurrentY(), speed, gameField);

        if (newX < 0 || newX >= gameField.getWidth() || newY < 0 || newY >= gameField.getHeight()) {
            return;
        }

        Cell newCell = gameField.getCells()[newX][newY];

        Lock residentsLock = newCell.getResidentsLock();
        if (residentsLock == null) {
            return;
        }
        boolean residentsLockAcquired = false;
        try {
            residentsLockAcquired = residentsLock.tryLock();
            if (residentsLockAcquired) {

            Organism organism = this.reproduce();
            Map<Type, Set<Organism>> residents = newCell.getResidents();
            Type targetType = targetType(organism);

                Lock newCellLock = newCell.getLock();
                boolean newCellLockAcquired = false;

                try {
                    newCellLockAcquired = newCellLock.tryLock();
                    if (newCellLockAcquired) {

            Map<Type, Integer> typeCounts = typeCounts(newCell);
            int count = typeCounts.getOrDefault(targetType, 0);

            if (count < this.getMaxNumPerCell()) {
                residents.get(targetType).add(organism);
                count++;

                Lock currentCellLock = currentCell.getLock();
                boolean currentCellLockAcquired = false;

                try {
                    currentCellLockAcquired = currentCellLock.tryLock();
                    if (currentCellLockAcquired) {

                    Map<Type, Set<Organism>> currentResidents = currentCell.getResidents();
                    this.killOrganism();
                    currentResidents.values().forEach(organisms -> organisms.remove(this));
                    }
                } finally {
                    if (currentCellLockAcquired) {
                        currentCellLock.unlock();
                    }
                }
            }
                    }
                } finally {
                    if (newCellLockAcquired) {
                        newCellLock.unlock();
                    }
                }
            }
        } finally {
            if (residentsLockAcquired) {
                residentsLock.unlock();
            }
        }
    }

    public void eat() {

        Cell currentCell = this.getCell();
        if (currentCell == null) {
            return;
        }
        Lock residentsLock = currentCell.getResidentsLock();
        if (residentsLock == null) {
            return;
        }
        boolean residentsLockAcquired = false;
        try {
            residentsLockAcquired = residentsLock.tryLock();
            if (residentsLockAcquired) {

            Map<Type, Set<Organism>> residents = currentCell.getResidents();
            Random random = ThreadLocalRandom.current();
            int foodEnough = 0;

            List<Organism> potentialPreys = new ArrayList<>();
            for (Map.Entry<Type, Set<Organism>> entry : residents.entrySet()) {
                Type type = entry.getKey();
                Set<Organism> preySet = entry.getValue();
                potentialPreys.addAll(preySet);
            }

            List<Organism> deadPreys = new ArrayList<>();

            for (Organism prey : potentialPreys) {
                double chance = random.nextDouble() * 100;
                Double preyProbability = eatProbabilities.get(Type.getTypeFromClass(prey.getClass()));

                if (preyProbability > 0.0 && chance <= preyProbability && prey.isAlive() && foodEnough <= getFoodNeed()) {

                                prey.killOrganism();
                                foodEnough += prey.getWeight();

                                if (!prey.isAlive()) {
                                    deadPreys.add(prey);
                                }
                }
            }
            for (Type type : residents.keySet()) {
                Set<Organism> organisms = residents.get(type);
                organisms.removeAll(deadPreys);
            }
            }
        } finally {
            if (residentsLockAcquired) {
                residentsLock.unlock();
            }
        }
    }

    public void reproduceTribe() {
        Cell currentCell = this.getCell();
        if (currentCell == null) {
            return;
        }
        Lock residentsLock = currentCell.getResidentsLock();
        if (residentsLock == null) {
            return;
        }
        boolean residentsLockAcquired = false;
        try {
            residentsLockAcquired = residentsLock.tryLock();
            if (residentsLockAcquired) {

            Map<Type, Set<Organism>> residents = currentCell.getResidents();
            Set<Organism> newOrganisms = new HashSet<>();

            Map<Type, Integer> typeIntegerMap = typeCounts(currentCell);
            Type typeOfOrganism = targetType(this);

            int count = typeIntegerMap.getOrDefault(typeOfOrganism, 0);

            if (count >= 2) {
                if (this instanceof Animal && this.isAlive()) {
                    Organism newOrganism = this.reproduce();
                    if (this.getMaxNumPerCell() > count) {
                            newOrganisms.add(newOrganism);
                    }
                }
            }
            for (Organism organism : newOrganisms) {
                residents.get(typeOfOrganism).add(organism);
            }
            }
        } finally {
            if (residentsLockAcquired) {
                residentsLock.unlock();
            }
        }
    }

    public Organism reproduce() {
        OrganismInfo organismInfo = new OrganismInfo();
        organismInfo.setType(this.getClass().getSimpleName());
        organismInfo.setWeight(this.getWeight());
        organismInfo.setMaxNumPerCell(this.getMaxNumPerCell());
        organismInfo.setSpeed(this.getSpeed());
        organismInfo.setFoodNeed(this.getFoodNeed());
        organismInfo.setAlive(true);

        Animal animal = this;
        List<EatProbability> eatProbabilities = new ArrayList<>();
        for (Type eatType : animal.getEatProbabilities().keySet()) {
            EatProbability eatProbability = new EatProbability();
            eatProbability.setType(eatType);
            eatProbability.setProbability(animal.getEatProbabilities().get(eatType));
            eatProbabilities.add(eatProbability);
        }
        organismInfo.setEatProbabilities(eatProbabilities);

        try {
            return organismFactory.createOrganism(organismInfo);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private int calculateNewX(int direction, int currentX, int speed, GameField gameField) {
        switch (direction) {
            case 0:
            case 1:
                return currentX;
            case 2:
                return Math.max(0, currentX - speed);
            case 3:
                return Math.min(gameField.getWidth() - 1, currentX + speed);
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    private int calculateNewY(int direction, int currentY, int speed, GameField gameField) {
        switch (direction) {
            case 0:
                return Math.max(0, currentY - speed);
            case 1:
                return Math.min(gameField.getHeight() - 1, currentY + speed);
            case 2:
            case 3:
                return currentY;
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }
}

