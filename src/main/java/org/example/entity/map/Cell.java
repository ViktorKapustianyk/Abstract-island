package org.example.entity.map;

import org.example.entity.organism.Organism;

import org.example.entity.organism.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Cell {
    private final Map<Type, Set<Organism>> residents;
    private final Lock lock;
    private final Map<Type, Lock> typeLocks = new HashMap<>(); // Добавляем мапу для хранения блокировок по типам

    public Cell(Map<Type, Set<Organism>> residents) {
        this.residents = new HashMap<>();
        this.lock = new ReentrantLock();
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
}
