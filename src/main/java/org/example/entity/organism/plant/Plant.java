package org.example.entity.organism.plant;

import lombok.Getter;
import org.example.creators.OrganismFactory;
import org.example.creators.OrganismInfo;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;

import java.util.*;

@Getter
public abstract class Plant extends Organism {

    private OrganismFactory organismFactory = new OrganismFactory();

    public void reproducePlant() {
        Cell currentCell = this.getCell();
        if (currentCell == null) {
            return; // Организм не знает, в какой клетке он находится, поэтому не может есть
        }
        Map<Type, Set<Organism>> residents = currentCell.getResidents();

        Map<Type, Integer> typeIntegerMap = typeCounts(currentCell);

        Type typeOfOrganism = targetType(this);

        List<Organism> newOrganisms = new ArrayList<>(); // Список для хранения новых объектов
        int count = residents.get(typeOfOrganism).size();

        Set<Organism> organisms = residents.get(typeOfOrganism);
        for (Organism organism : organisms) {
            if (organism instanceof Plant plant) {
                Organism newOrganism = plant.reproduce();
                int maxNumPerCell = organism.getMaxNumPerCell(); // Получаем максимальное количество объектов для данного типа

                if (count < maxNumPerCell) { // Проверяем, что можно добавить еще объектов данного типа
                    newOrganisms.add(newOrganism);
                    count++;
                }
            }
        }
        for (Organism organism : newOrganisms) {
            residents.get(targetType(organism)).add(organism);
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

    public void execute(GameField gameField){
        reproducePlant();

    }
}
