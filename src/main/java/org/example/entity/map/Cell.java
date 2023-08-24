package org.example.entity.map;

import org.example.entity.organism.Organism;

import org.example.entity.organism.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class Cell {
    private final Map<Type, Set<Organism>> residents;
    private Lock residentsLock = new ReentrantLock();
    private final Lock lock = new ReentrantLock();
    private final Map<Type, Lock> typeLocks = new HashMap<>();

    public Cell(Map<Type, Set<Organism>> residents) {
        this.residents = new HashMap<>();
    }

    public Map<Type, Set<Organism>> getResidents() {
        return residents;
    }
    public Lock getLock() {
        return lock;
    }
    public Lock getTypeLock(Type type) {
        typeLocks.putIfAbsent(type, new ReentrantLock());
        return typeLocks.get(type);
    }
    public Lock getResidentsLock() {
        return residentsLock;
    }


}
