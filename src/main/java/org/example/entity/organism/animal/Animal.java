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

    public void move(Cell currentCell, GameField gameField) {
//        int speed = getSpeed();
//        int newX = currentX;
//        int newY = currentY;

        // Генерируем случайное направление перемещения (вверх, вниз, влево, вправо)
        Random random = ThreadLocalRandom.current();
        int speed = getSpeed();
        int newX = random.nextInt(gameField.getWidth());
        System.out.print("newX :" + newX);
        int newY = random.nextInt(gameField.getHeight());
        System.out.println(" newY :" + newY);

//        int direction = random.nextInt(4); // 0 - вверх, 1 - вниз, 2 - влево, 3 - вправо
//
//        // В зависимости от направления меняем координаты
//        switch (direction) {
//            case 0:
//                newY = Math.max(0, currentY - speed);
//                break;
//            case 1:
//                newY = Math.min(gameField.getHeight() - 1, currentY + speed);
//                break;
//            case 2:
//                newX = Math.max(0, currentX - speed);
//                break;
//            case 3:
//                newX = Math.min(gameField.getWidth() - 1, currentX + speed);
//                break;
//        }
        if (newX > 0 && newX <= gameField.getWidth() && newY > 0 && newY <= gameField.getHeight()) {
            // Получаем новую клетку и перемещаемся в нее, если она свободна
            Cell newCell = gameField.getCells()[newX][newY];
            reproduceTribe(newCell);
        }
        Map<Type, Set<Organism>> residents = currentCell.getResidents();
        List<Organism> dropOut = new ArrayList<>();

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

        List<Organism> newOrganisms = new ArrayList<>(); // Список для зберігання нових об'єктів

        for (Type type : residents.keySet()) {
            int count = typeCounts.getOrDefault(type, 0);
            int canMaxAdd = getMaxNumPerCell() - count;
            if (count >= 2) { // Перевіряємо, чи є більше двох об'єктів даного типу
                Set<Organism> organisms = residents.get(type);
                for (Organism organism : organisms) {
                    if (organism instanceof Animal && organism.isAlive()) {
                        Animal animal = (Animal) organism;
                        Organism newOrganism = animal.reproduce();
                        if (canMaxAdd > 0) {
                            newOrganisms.add(newOrganism);
                            canMaxAdd--;
                        }
                    }
                }
            }
        }

        for (Organism organism : newOrganisms) {
            String fullClassName = organism.getClass().getName(); // Отримуємо повне ім'я класу (включаючи пакет)
            String[] parts = fullClassName.split("\\."); // Розбиваємо ім'я на частини за роздільником "."
            String simpleClassName = parts[parts.length - 1]; // Остання частина є простим іменем класу

            Type targetType = Type.valueOf(simpleClassName.toUpperCase()); // Перетворюємо на Enum Type
            residents.get(targetType).add(organism);
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
}

