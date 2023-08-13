package org.example;

import org.example.creators.Tribe;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;

import java.util.*;

import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;
import org.example.entity.organism.animal.herbivore.Rabbit;
import org.example.entity.organism.animal.predator.Boa;
import org.example.entity.organism.plant.Grass;
import org.example.entity.organism.plant.Plant;

public class Main {
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
        simulationMove(gameField);
        //removeDeadOrganisms(gameField);
        printCellArrays(gameField);
//        printRabbitHashCodes(gameField);

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
                        int canMaxAdd = 10 - count;
                        if (count >= 2) { // Перевіряємо, чи є більше двох об'єктів даного типу
                            Set<Organism> organisms = residents.get(type);
                            for (Organism organism : organisms) {
                                if (organism instanceof Animal && organism.isAlive()) {
                                    Animal animal = (Animal) organism;
                                    Organism newOrganism = animal.reproduce();
                                    System.out.println("****Chosen REPRODUCE: ****** " + newOrganism.getClass().getSimpleName()
                                            + " " + newOrganism.hashCode()
                                            + " " + newOrganism.getMaxNumPerCell()
                                            + " " + newOrganism.getWeight()
                                            + " " + newOrganism.getFoodNeed());
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
            }
        }
    }

    public static void simulationMove(GameField gameField) {
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
                            if (organism instanceof Animal) {
                                Animal animal = (Animal) organism;
                                if (animal.isAlive()) {
                                    animal.move(currentCell, gameField);
                                }
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
}