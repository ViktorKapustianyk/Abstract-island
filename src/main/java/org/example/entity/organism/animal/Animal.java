package org.example.entity.organism.animal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;

import java.util.*;

@Getter
@Setter
public abstract class Animal extends Organism {
    private Map<Type, Double> eatProbabilities = new HashMap<>();

    public void setEatProbability(Type type, double probability) {
        eatProbabilities.put(type, probability);
    }

    public double getEatProbability(Type type) {
        return eatProbabilities.getOrDefault(type, 0.0);
    }

    public void move(Cell currentCell) {

//        Random random = new Random();
//        int currentRow = currentCell.getRow();
//        int currentCol = currentCell.getCol();
//
//        // Генеруємо випадковий напрямок руху (вверх, вниз, вліво або вправо)
//        int direction = random.nextInt(4);
//
//        // Залежно від швидкості тварини, генеруємо кількість кроків, які вона може зробити
//        int steps = random.nextInt(speed) + 1; // Генерує випадкове число від 1 до speed
//
//        // Виконуємо випадковий рух тварини з урахуванням швидкості
//        for (int i = 0; i < steps; i++) {
//            switch (direction) {
//                case 0: // Вверх
//                    currentRow--;
//                    break;
//                case 1: // Вниз
//                    currentRow++;
//                    break;
//                case 2: // Вліво
//                    currentCol--;
//                    break;
//                case 3: // Вправо
//                    currentCol++;
//                    break;
//            }
//
//            // Перевірка, чи нові координати знаходяться в межах острова
//            if (currentRow >= 0 && currentRow < neighbors.length && currentCol >= 0 && currentCol < neighbors[0].length) {
//                // Переміщаємо тварину до нової клітинки, якщо вона вільна
//                Cell newCell = neighbors[currentRow][currentCol];
//                if (newCell.isEmpty()) {
//                    currentCell.removeOrganism(this);
//                    newCell.addOrganism(this);
//                }
//            }
//        }
    }

    public boolean isAlive() {
        // Реалізуйте логіку, яка повертає true, якщо тварина жива, і false - якщо ні
        // Наприклад, перевірка, чи не перевищено максимальний вік тварини
        return true;
    }

    //    public void eat(Cell currentCell){
//        // Отримуємо всі об'єкти, які є у поточній клітині
//        Map<Type, Set<Organism>> residents = currentCell.getResidents();
//        //Отримуємо вірогідність з якою тварина з'їдає "їжу"
//        Random random = new Random();
//        //double chance = random.nextDouble();
//
//        //Задаємо параметр насиченості
//        int foodEnough = 0;
//
//        // Отримуємо можливі об'єкти для харчування згідно ймовірностей
//        List<Organism> potentialPreys = new ArrayList<>();
//        for (Type type : eatProbabilities.keySet()) {
//            Set<Organism> preySet = residents.getOrDefault(type, new HashSet<>());
//            potentialPreys.addAll(preySet);
//        }
//        for (Organism prey : potentialPreys) {
//            double chance = random.nextDouble();
//            Type preyType = Type.getTypeFromClass(prey.getClass());
//            double preyProbability = eatProbabilities.get(preyType);
//
//            if (chance >= preyProbability && prey.isAlive()) {
//                prey.setAlive(false); // Тварина їсть добичу і вона помирає
//                foodEnough++; // Збільшуємо насиченість
//            }
//        }
//    }
    public void eat(Cell currentCell) {
        System.out.println("Eating process:");

        // Отримуємо всі об'єкти, які є у поточній клітині
        Map<Type, Set<Organism>> residents = currentCell.getResidents();
        //Отримуємо вірогідність з якою тварина з'їдає "їжу"
        Random random = new Random();
        int foodEnough = 0;

        // Отримуємо можливі об'єкти для харчування згідно ймовірностей
        List<Organism> potentialPreys = new ArrayList<>();
        for (Map.Entry<Type, Set<Organism>> entry : residents.entrySet()) {
            Type type = entry.getKey();
            Set<Organism> preySet = entry.getValue();
            potentialPreys.addAll(preySet);
        }
        for (Organism prey : potentialPreys) {
            double chance = random.nextInt(101);
            Type preyType = Type.getTypeFromClass(prey.getClass());
            String preyTypeKey = preyType.toString().toUpperCase(); // перетворення на великі літери
            System.out.println("Prey type: " + preyType); // Додайте цей рядок
            System.out.println("Prey type key: " + preyTypeKey);
            System.out.println("Eat probabilities: " + eatProbabilities);
            Double preyProbability = eatProbabilities.get(Type.getTypeFromClass(prey.getClass()));

            System.out.println("Chosen prey: " + prey.getClass().getSimpleName());
            System.out.println("Random chance: " + chance);
            System.out.println("Prey probability: " + preyProbability);
            //System.out.println("Show foodNeed: " + getFoodNeed());

            if (preyProbability != null && chance <= preyProbability && prey.isAlive() && foodEnough <= getFoodNeed()) {
                System.out.println("Eating successful!");
                prey.setAlive(false); // Тварина їсть добичу і вона помирає
                prey.isAlive = false; // Додайте цей рядок
                foodEnough += prey.getWeight(); // Збільшуємо насиченість
                if (foodEnough >= getFoodNeed()) {
                    System.out.println("Animal is full. Eating process finished.");

                    for (Type type : residents.keySet()) {
                        Set<Organism> organisms = residents.get(type);
                        organisms.removeIf(organism -> !organism.isAlive);
                    }

                    return; // Завершуємо процес їжі, оскільки тварина наїлась
                }
            } else {
                System.out.println("Eating failed or prey is not alive.");
            }
        }
    }
}
