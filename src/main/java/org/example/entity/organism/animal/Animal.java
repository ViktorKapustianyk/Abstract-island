package org.example.entity.organism.animal;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.creators.OrganismCreator;
import org.example.creators.OrganismPopulationManager;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.interfaces.Eatable;
import org.example.interfaces.Movable;
import org.example.interfaces.Reproducible;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public abstract class Animal extends Organism implements Movable, Eatable{
    private OrganismPopulationManager populationManager;

    private Map<Type, Double> eatProbabilities = new HashMap<>();

    public void setEatProbability(Type type, double probability) {
        eatProbabilities.put(type, probability);
    }

    public double getEatProbability(Type type) {
        return eatProbabilities.getOrDefault(type, 0.0);
    }

    public void move(Cell currentCell, Integer currentX, Integer currentY, GameField gameField) {
        int speed = getSpeed();
        int newX = currentX;
        int newY = currentY;

        // Генерируем случайное направление перемещения (вверх, вниз, влево, вправо)
        Random random = ThreadLocalRandom.current();
        int direction = random.nextInt(4); // 0 - вверх, 1 - вниз, 2 - влево, 3 - вправо

        // В зависимости от направления меняем координаты
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

        // Получаем новую клетку и перемещаемся в нее, если она свободна
        Cell newCell = gameField.getCells()[newX][newY];
        Organism reproduce = this.reproduce();


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
}

