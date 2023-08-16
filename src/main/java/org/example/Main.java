package org.example;

import org.example.creators.Tribe;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;

import java.util.*;
import java.util.concurrent.locks.Lock;

import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;
import org.example.entity.organism.animal.herbivore.Rabbit;
import org.example.entity.organism.animal.predator.Boa;
import org.example.entity.organism.plant.Grass;
import org.example.entity.organism.plant.Plant;

public class Main {
    private static Animal animal;

    public static void main(String[] args) throws Exception {

//        Rabbit rabbit = new Rabbit();
//        rabbit.setWeight(1.0);
//        rabbit.setMaxNumPerCell(5);
//        rabbit.setSpeed(2);
//        rabbit.setFoodNeed(0.5);
//        rabbit.setAlive(true);
//
//        Organism newRabbit = rabbit.reproduce();
//
//        System.out.println("Original Rabbit: " + rabbit);
//        System.out.println("Newly Reproduced Rabbit: " + newRabbit);
//
//        Grass grass = new Grass();
//        grass.setWeight(1.0);
//        grass.setMaxNumPerCell(5);
//        grass.setSpeed(2);
//        grass.setFoodNeed(0.5);
//        grass.setAlive(true);
//
//        Organism newGrass = grass.reproduce();
//
//        System.out.println("Original Grass: " + grass);
//        System.out.println("Newly Reproduced Grass: " + newGrass);

        // Створення об'єкту класу GameField
        GameField gameField = GameField.readGameFieldConfigFile("src/main/resources/game_field_config.yaml");
        Tribe tribe = new Tribe(gameField);

        // Виклик методу для створення об'єктів типу Rabbit та Grass
        try {
            tribe.createTribe();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // Виклик методу для виведення масивів до терміналу
        printCellArrays(gameField);
        simulationEat(gameField);
        printCellArrays(gameField);
        simulationReproduce(gameField);
        printCellArrays(gameField);
        simulationMove(gameField);
        printCellArrays(gameField);

//        simulationCunt(gameField);

//        // Получаем случайную ячейку для проверки
//        int randomX = 0; // задайте случайное значение X
//        int randomY = 0; // задайте случайное значение Y
//        Cell randomCell = gameField.getCells()[randomX][randomY];
//
//        // Проверяем перемещение одного из организмов в случайную ячейку
//        Set<Organism> organisms = randomCell.getResidents().get(Type.RABBIT); // предположим, это кролики
//        if (organisms != null && !organisms.isEmpty()) {
//            Animal rabbitToMove = (Animal) organisms.iterator().next(); // берем первого попавшегося кролика
//            rabbitToMove.move(randomCell, gameField, randomX, randomY);
//        }


        // Повторно выводим состояние масивів до терміналу
//        printCellArrays(gameField);


        //simulationMove(gameField);
        //removeDeadOrganisms(gameField);
        //printCellArrays(gameField);
        //printRabbitHashCodes(gameField);

    }

    public static void printCellArrays(GameField gameField) {
        Cell[][] cells = gameField.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                Map<Type, Set<Organism>> residents = cell.getResidents();

                System.out.println("Cell[" + i + "][" + j + "]:");
                for (Type type : residents.keySet()) {
                    Set<Organism> organisms = residents.get(type);
                    System.out.println("  " + type.name() + ": " + organisms.size() + " organisms");
                }
            }
        }
    }

    public static void printRabbitHashCodes(GameField gameField) {
        Cell[][] cells = gameField.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                Set<Organism> rabbits = cell.getResidents().get(Type.BOA);
                if (rabbits != null) {
                    System.out.println("Rabbits in Cell[" + i + "][" + j + "]:");
                    for (Organism rabbit : rabbits) {

                        System.out.println("  HashCode: " + rabbit.hashCode());
                        double weight = rabbit.getWeight();
                        int maxNumPerCell = rabbit.getMaxNumPerCell();
                        int speed = rabbit.getSpeed();
                        double foodNeed = rabbit.getFoodNeed();
                        boolean alive = rabbit.isAlive();
                        System.out.println(weight + " " + maxNumPerCell + " " + speed + " " + foodNeed + " " + alive);
                    }
                }
            }
        }
    }

    // Виконуємо симуляцію протягом деякої кількості кроків
    public static void simulationEat(GameField gameField) throws InstantiationException, IllegalAccessException {
        Cell[][] cells = gameField.getCells();
        int numSteps = 1;
        for (int step = 1; step <= numSteps; step++) {
            System.out.println("Step " + step + ":");
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell currentCell = cells[i][j];
                    Map<Type, Set<Organism>> residents = currentCell.getResidents();

                    for (Type type : residents.keySet()) {
                        Set<Organism> organisms = residents.get(type);
                        for (Organism organism : organisms) {
                            if (organism instanceof Animal animal) {
                                System.out.println("_____ Animal hunt _____");
                                animal.eat(currentCell);
                            }
                        }
                    }
                }
            }
        }
    }
    public static void simulationMove(GameField gameField) {
        Cell[][] cells = gameField.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell currentCell = cells[i][j];
                Map<Type, Set<Organism>> residents = currentCell.getResidents();
                Lock currentCellLock = currentCell.getLock();
                currentCellLock.lock();
                try {
                    // Создаем копии множеств организмов для безопасной итерации
                    for (Type type : Type.values()) {
                        Set<Organism> organismsCopy = new HashSet<>(residents.getOrDefault(type, new HashSet<>()));
                        for (Organism organism : organismsCopy) {
                            if (organism instanceof Animal && organism.isAlive()) {
                                Animal animal = (Animal) organism;
                                System.out.println("________Animal move ________");
                                animal.move(currentCell, gameField, i, j);
                            }
                        }
                    }
                } finally {
                    currentCellLock.unlock();
                }
            }
        }
    }
    public static void simulationCunt(GameField gameField) throws InstantiationException, IllegalAccessException {
        Cell[][] cells = gameField.getCells();
        int numSteps = 1;
        for (int step = 1; step <= numSteps; step++) {
            System.out.println("Step " + step + ":");
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell currentCell = cells[i][j];
                    Map<Type, Integer> typeIntegerMap = typeCounts(currentCell);
                    System.out.println(typeIntegerMap);

                    Map<Type, Set<Organism>> residents = currentCell.getResidents();
                    for (Type type : residents.keySet()){
                        int organisms = countOrganismsOfType(currentCell, type);
                        System.out.println("organism : " + organisms + " " + type);
                    }
                }
            }
        }
    }

    public static void simulationReproduce(GameField gameField) throws InterruptedException {
        Cell[][] cells = gameField.getCells();
        int numSteps = 1;

        for (int step = 1; step <= numSteps; step++) {
            System.out.println("Step " + step + ":");

            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell currentCell = cells[i][j];

                        Map<Type, Set<Organism>> residents = currentCell.getResidents();

                        for (Type type : residents.keySet()) {
                            Set<Organism> organisms = new HashSet<>(residents.get(type));

                            for (Organism organism : organisms) {
                                if (organism instanceof Animal animal) {
                                    System.out.println("________Animal reproduce ________");
                                    animal.reproduceTribe(currentCell);
                                }
                            }
                        }
                    }
                }
            }
    }
    public static void removeDeadOrganisms(GameField gameField) {
        Cell[][] cells = gameField.getCells();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Cell cell = cells[i][j];
                Map<Type, Set<Organism>> residents = cell.getResidents();

                for (Type type : residents.keySet()) {
                    Set<Organism> organisms = residents.get(type);
                    Iterator<Organism> iterator = organisms.iterator();
                    while (iterator.hasNext()) {
                        Organism organism = iterator.next();
                        if (!organism.isAlive()) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    public static int countOrganismsOfType(Cell cell, Type type) {
        int count = 0;
        Map<Type, Set<Organism>> residents = cell.getResidents();

        for (Set<Organism> organisms : residents.values()) {
            for (Organism organism : organisms) {
                if (organism.isAlive()) {
                    Type organismType = Type.getTypeFromClass(organism.getClass());
                    if (organismType == type) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    private static Map<Type, Integer> typeCounts(Cell currentCell) {
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