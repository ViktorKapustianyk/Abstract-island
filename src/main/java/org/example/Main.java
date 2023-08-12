package org.example;

import org.example.creators.Tribe;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;

import java.util.*;

import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;
import org.example.entity.organism.plant.Grass;

public class Main {
    public static void main(String[] args) throws Exception {

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

                    List<Organism> newGrasses = new ArrayList<>(); // Список для зберігання нових об'єктів Grass

                    for (Type type : residents.keySet()) {
                        Set<Organism> organisms = residents.get(type);

                        Iterator<Organism> iterator = organisms.iterator();
                        while (iterator.hasNext()) {
                            Organism organism = iterator.next();
                            if (organism instanceof Animal) {
                                Animal animal = (Animal) organism;
                                if (animal.isAlive()) {
                                    System.out.println("****Chosen HUNT: ******" + animal.getClass().getSimpleName() + animal.hashCode());
                                    animal.eat(currentCell);
                                }
                            } else if (organism instanceof Grass) {
                                Grass grass = (Grass) organism;
                                if (grass.isAlive()) {
                                    System.out.println("****Chosen REPRODUCE: ******" + grass.getClass().getSimpleName() + grass.hashCode());
                                    Organism newGrass = grass.reproduce();
                                    newGrasses.add(newGrass); // Додавання нового трав'янистого об'єкта до списку
                                }
                            }
                        }
                    }

                    // Додавання нових об'єктів Grass після закінчення ітерації
                    for (Organism newGrass : newGrasses) {
                        residents.get(Type.GRASS).add(newGrass);
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
                                    animal.move(currentCell, i, j, gameField);
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