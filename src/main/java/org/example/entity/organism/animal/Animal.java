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

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;

@Getter
public abstract class Animal extends Organism implements Movable, Eatable {

    private OrganismFactory organismFactory = new OrganismFactory();
    ;

    private Map<Type, Double> eatProbabilities = new HashMap<>();

    public void setEatProbability(Type type, double probability) {
        eatProbabilities.put(type, probability);
    }

    public double getEatProbability(Type type) {
        return eatProbabilities.getOrDefault(type, 0.0);
    }

    public void move(Cell currentCell, GameField gameField, int currentX, int currentY) {

        int speed = getSpeed();
        int newX = currentX;
        int newY = currentY;

        Random random = ThreadLocalRandom.current();

        int direction = random.nextInt(4);

        switch (direction) {
            case 0:
                newY = Math.max(0, currentY - speed);
                break;
            case 1:
                newY = Math.min(gameField.getHeight() - 1, currentY + speed);
                break;
            case 2:
                newX = Math.max(0, currentX - speed);
                break;
            case 3:
                newX = Math.min(gameField.getWidth() - 1, currentX + speed);
                break;
        }

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

//            Lock currentCellLock = currentCell.getLock();
//            currentCellLock.lock();
//            try {
//                // Получаем организмы текущей ячейки, которые будут перемещены
//                Map<Type, Set<Organism>> currentResidents = currentCell.getResidents();
//                this.killOrganism();
//                List<Organism> deadPreys = new ArrayList<>();
//                deadPreys.add(this);
//
//                for (Type type : currentResidents.keySet()) {
//                    Set<Organism> organisms = currentResidents.get(type);
//                    organisms.removeAll(deadPreys);
//                }
//
//            } finally {
//                currentCellLock.unlock();
//            }
        }
    }

    public void eat(Cell currentCell) {

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

    public void reproduceTribe(Cell currentCell) {
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
    private Map<Type, Integer> typeCounts(Cell currentCell) {
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

