package org.example.entity.organism.animal;

import lombok.*;
import org.example.creators.EatProbability;
import org.example.creators.OrganismFactory;
import org.example.creators.OrganismInfo;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.herbivore.Herbivore;
import org.example.interfaces.Eatable;
import org.example.interfaces.Movable;
import org.example.interfaces.OrganismTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;

@Getter
@Setter
public abstract class Animal extends Organism implements Movable, Eatable, OrganismTask {

    private OrganismFactory organismFactory = new OrganismFactory();
    private Map<Type, Double> eatProbabilities = new HashMap<>();
    public void setEatProbability(Type type, double probability) {eatProbabilities.put(type, probability);}
    public double getEatProbability(Type type) {
        return eatProbabilities.getOrDefault(type, 0.0);
    }

    public void execute(GameField gameField){
        eat();
        reproduceTribe();
        move(gameField);
    }

    public void move(GameField gameField) {

        Cell currentCell = this.getCell();
        if (currentCell == null) {
            return; // Организм не знает, в какой клетке он находится
        }

        Random random = ThreadLocalRandom.current();

        int direction = random.nextInt(4);

        int speed = getSpeed();
        int newX = calculateNewX(direction, this.getCurrentX(), speed, gameField);
        int newY = calculateNewY(direction, this.getCurrentY(), speed, gameField);

        if (newX >= 0 && newX < gameField.getWidth() && newY >= 0 && newY < gameField.getHeight()) {
            // Создаем временную коллекцию для хранения информации о перемещении
            Lock newCellLock = gameField.getCells()[newX][newY].getLock();
            newCellLock.lock();
            try {
                // Получаем новую клетку и перемещаемся в нее, если она свободна
                Cell newCell = gameField.getCells()[newX][newY];
                Organism organism = this.reproduce();
                Map<Type, Set<Organism>> residents = newCell.getResidents();

                Map<Type, Integer> typeIntegerMap = typeCounts(newCell);

                int count = typeIntegerMap.getOrDefault(targetType(organism), 0);
                if (count < this.getMaxNumPerCell()) {
                    residents.get(targetType(organism)).add(organism);
                    ++count;

                    Lock currentCellLock = currentCell.getLock();
                    currentCellLock.lock();
                    try {
                        // Получаем организмы текущей ячейки, которые будут перемещены
                        Map<Type, Set<Organism>> currentResidents = currentCell.getResidents();
                        this.killOrganism();
                        List<Organism> deadPreys = new ArrayList<>();
                        deadPreys.add(this);

                        for (Type type : currentResidents.keySet()) {
                            Set<Organism> organisms = currentResidents.get(type);
                            organisms.removeAll(deadPreys);
                        }

                    } finally {
                        currentCellLock.unlock();
                    }

                }

            } finally {
                newCellLock.unlock();
            }
        }
    }

    public void eat() {

        Cell currentCell = this.getCell();
        if (currentCell == null) {
            return; // Организм не знает, в какой клетке он находится, поэтому не может есть
        }

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

    public void reproduceTribe() {
        Cell currentCell = this.getCell();
        if (currentCell == null) {
            return; // Организм не знает, в какой клетке он находится, поэтому не может есть
        }

        Map<Type, Set<Organism>> residents = currentCell.getResidents();
        Set<Organism> newOrganisms = new HashSet<>(); // Список для зберігання нових об'єктів

        Map<Type, Integer> typeIntegerMap = typeCounts(currentCell);

        Type typeOfOrganism = targetType(this);

        int count = typeIntegerMap.getOrDefault(typeOfOrganism, 0);

        if (count >= 2) { // Перевіряємо, чи є більше двох об'єктів даного типу
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

