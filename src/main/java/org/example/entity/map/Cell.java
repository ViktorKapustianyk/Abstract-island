package org.example.entity.map;

import org.example.entity.organism.Organism;

import org.example.entity.organism.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Cell {
    private final Map<Type, Set<Organism>> residents;

    public Cell(Map<Type, Set<Organism>> residents) {
        this.residents = new HashMap<>();
    }

    public Map<Type, Set<Organism>> getResidents() {
        return residents;
    }
}
