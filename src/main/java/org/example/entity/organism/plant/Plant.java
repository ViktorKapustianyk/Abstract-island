package org.example.entity.organism.plant;

import lombok.Getter;
import org.example.creators.OrganismFactory;
import org.example.creators.OrganismInfo;
import org.example.entity.map.Cell;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;

import java.util.*;

@Getter
public abstract class Plant extends Organism {

    private OrganismFactory organismFactory = new OrganismFactory();

    public void reproducePlant(Cell currentCell) {
        Map<Type, Set<Organism>> residents = currentCell.getResidents();

        Map<Type, Integer> typeCounts = new HashMap<>(); // Мапа для зберігання лічильників кількості об'єктів кожного типу

        for (Type type : residents.keySet()) {
            Set<Organism> organisms = residents.get(type);
            int count = 0; // Лічильник кількості об'єктів даного типу

            for (Organism organism : organisms) {
                if (organism instanceof Plant) {
                    count++;
                }
            }

            typeCounts.put(type, count);
        }

        List<Organism> newOrganisms = new ArrayList<>(); // Список для зберігання нових об'єктів

        for (Type type : residents.keySet()) {
            int count = typeCounts.getOrDefault(type, 0);
            int canMaxAdd = getMaxNumPerCell() - count;
            if (count < getMaxNumPerCell()) { // Перевіряємо, чи об'єктів даного типу не більше MaxNumPerCell
                Set<Organism> organisms = residents.get(type);
                for (Organism organism : organisms) {
                    if (organism instanceof Plant) {
                        Plant plant = (Plant) organism;
                        Organism newOrganism = plant.reproduce();
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

    @Override
    public Organism reproduce() {
        OrganismInfo organismInfo = new OrganismInfo();
        organismInfo.setType(this.getClass().getSimpleName());
        organismInfo.setWeight(this.getWeight());
        organismInfo.setMaxNumPerCell(this.getMaxNumPerCell());
        organismInfo.setSpeed(this.getSpeed());
        organismInfo.setFoodNeed(this.getFoodNeed());
        organismInfo.setAlive(true);

        try {
            return organismFactory.createOrganism(organismInfo);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
