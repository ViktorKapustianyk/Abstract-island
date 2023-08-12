package org.example.creators;

import org.example.entity.organism.Type;

import java.util.HashSet;
import java.util.Set;

public class OrganismCreator<T> {
    private Set<T> tribe;
    private int maxTribe;
    Class<T> clazz;
    public OrganismCreator(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T createOrganism(Class<T> clazz) {
        T organism = null;
        try {
            organism = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return organism;
    }

//    public Set<T> createTribeOrganism(int maxTribe){
//        Set<T> tribe = new HashSet<>();
//        for (int i = 0; i < maxTribe; i++) {
//            T organism = null;
//            try {
//                organism = clazz.newInstance();
//            } catch (InstantiationException | IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//            tribe.add(organism);
//        }
//        return tribe;
//    }
}

