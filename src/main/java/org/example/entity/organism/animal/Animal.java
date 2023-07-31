package org.example.entity.organism.animal;

import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class Animal extends Organism {
    private int speed;
    private double foodNeed;
    public void move(Cell currentCell, Cell[][] neighbors){
    }

    public boolean isAlive() {
        // Реалізуйте логіку, яка повертає true, якщо тварина жива, і false - якщо ні
        // Наприклад, перевірка, чи не перевищено максимальний вік тварини
        return true;
    }
    public void eat(Cell currentCell){
        // Реалізація логіки харчування травоїдних
        // Наприклад, перевірка наявності рослин у поточній клітинці
        // Якщо такої логіки ще немає, можна тимчасово просто вивести повідомлення
        System.out.println(" is eating.");
    }
}
