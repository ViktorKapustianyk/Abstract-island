package org.example.entity.organism.animal.herbivore;

import org.example.entity.organism.animal.Animal;
import org.example.entity.organism.plant.Plant;

public abstract class Herbivore extends Animal {
    public void eat(Herbivore herbivore){}
    public void eat(Plant plant){}
}
