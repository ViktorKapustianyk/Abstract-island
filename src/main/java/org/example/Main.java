package org.example;

import org.example.creators.Tribe;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;
import org.example.entity.organism.animal.herbivore.Herbivore;
import org.example.entity.organism.animal.herbivore.Rabbit;
import org.example.entity.organism.animal.predator.Boa;
import org.example.entity.organism.animal.predator.Predator;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Hello world!");
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
        simulation(gameField);
        printCellArrays(gameField);



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
                        System.out.println(weight + " " + maxNumPerCell + " " + speed + " " + foodNeed);
                    }
                }
            }
        }
    }

       // Виконуємо симуляцію протягом деякої кількості кроків
    public static void simulation(GameField gameField) {
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
                                    System.out.println("****Chosen HUNT: ******" + animal.getClass().getSimpleName());
                                    animal.eat(currentCell);
                                }
                            }
                        }
                    }
                }
            }
        }

//                    // Рух тварин
//                    for (Organism organism : rabbits) {
//                        if (organism instanceof Animal) {
//                            Animal animal = (Animal) organism;
//                            if (animal.isAlive()) {
//                                animal.move(currentCell, cells);
//                            }
//                        }
//                    }
//
//                    // Харчування травоїдних
//                    for (Organism organism : boa) {
//                        if (organism instanceof Predator) {
//                            Predator predator = (Predator) organism;
//                            if (predator.isAlive()) {
//                                predator.eat(currentCell);
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
//    public static void removeDeadOrganisms(GameField gameField) {
//        Cell[][] cells = gameField.getCells();
//        for (int i = 0; i < cells.length; i++) {
//            for (int j = 0; j < cells[i].length; j++) {
//                Cell cell = cells[i][j];
//                Map<Type, Set<Organism>> residents = cell.getResidents();
//
//                for (Type type : residents.keySet()) {
//                    Set<Organism> organisms = residents.get(type);
//                    organisms.removeIf(organism -> !organism.isAlive);
//                }
//            }
//        }
//    }
}