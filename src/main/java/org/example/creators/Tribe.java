package org.example.creators;

import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tribe {
    private GameField gameField;
    private Cell[][] cells;
    private Organism organism;

    public Tribe(GameField gameField) {
        this.gameField = gameField;
        this.cells = gameField.getCells();
    }
    public void createTribe() throws InstantiationException, IllegalAccessException {
        Type[] types = Type.values();

        for (Type type : types) {

            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell cell = cells[i][j];
                    Map<Type, Set<Organism>> residents = cell.getResidents();
                    Set<Organism> tribe = residents.computeIfAbsent(type, k -> new HashSet<>());

                    for (int k = 0; k < type.getOrganismClass().newInstance().getMaxNumPerCell() / 3; k++) {
                        Organism organism = type.getOrganismClass().newInstance();
                        tribe.add(organism);
                    }
                }
            }
        }
    }
}