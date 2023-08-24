package org.example.entity.organism;

import org.example.entity.organism.animal.herbivore.*;
import org.example.entity.organism.animal.predator.*;

import org.example.entity.organism.plant.Grass;

import java.util.HashMap;
import java.util.Map;

public enum Type {
    WOLF(Wolf.class),
    BOA(Boa.class),
    FOX(Fox.class),
    BEAR(Bear.class),
    EAGLE(Eagle.class),
    HORSE(Horse.class),
    DEER(Deer.class),
    RABBIT(Rabbit.class),
    MOUSE(Mouse.class),
    GOAT(Goat.class),
    SHEEP(Sheep.class),
    BOAR(Boar.class),
    BUFFALO(Buffalo.class),
    DUCK(Duck.class),
    GOOSE(Goose.class),
    GRASS(Grass.class);

    private final Class<? extends Organism> organismClass;
    private static final Map<Class<? extends Organism>, Type> classToTypeMap = new HashMap<>();

    static {
        for (Type type : Type.values()) {
            classToTypeMap.put(type.getOrganismClass(), type);
        }
    }

    Type(Class<? extends Organism> organismClass) {
        this.organismClass = organismClass;
    }

    public Class<? extends Organism> getOrganismClass() {
        return organismClass;
    }

    public static <T extends Organism> Type getTypeFromClass(Class<T> clazz) {
        return classToTypeMap.get(clazz);
    }
}
