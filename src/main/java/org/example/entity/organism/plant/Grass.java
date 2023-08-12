package org.example.entity.organism.plant;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.entity.organism.Organism;
import org.example.creators.OrganismInfo;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Grass extends Plant{
    @Override
    public Organism reproduce(){
        OrganismInfo organismInfo = new OrganismInfo();
        organismInfo.setType(this.getClass().getSimpleName());
        organismInfo.setWeight(this.getWeight());
        organismInfo.setMaxNumPerCell(this.getMaxNumPerCell());
        organismInfo.setSpeed(this.getSpeed());
        organismInfo.setFoodNeed(this.getFoodNeed());
        organismInfo.setAlive(true);

        try {
            return createOrganism(organismInfo);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
