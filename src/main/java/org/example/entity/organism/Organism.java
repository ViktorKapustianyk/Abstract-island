package org.example.entity.organism;
import lombok.*;
import org.example.creators.EatProbability;
import org.example.creators.OrganismInfo;
import org.example.entity.organism.animal.Animal;
import org.example.interfaces.Reproducible;
import java.util.List;

@Getter
@Setter
public abstract class Organism implements Reproducible {
    private double weight;
    private int maxNumPerCell;
    private int speed;
    private double foodNeed;
    private boolean isAlive;
    public void killOrganism() {
        isAlive = false;
    }
    public abstract Organism reproduce();

}
