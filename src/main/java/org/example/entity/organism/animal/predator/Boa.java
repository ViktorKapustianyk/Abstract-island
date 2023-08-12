package org.example.entity.organism.animal.predator;
import lombok.*;
import org.example.creators.EatProbability;
import org.example.entity.organism.Organism;
import org.example.entity.organism.animal.Animal;
import org.example.creators.OrganismInfo;
import org.example.entity.organism.Type;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Boa extends Predator {
    @Override
    public Organism reproduce(){
        OrganismInfo organismInfo = new OrganismInfo();
        organismInfo.setType(this.getClass().getSimpleName());
        organismInfo.setWeight(this.getWeight());
        organismInfo.setMaxNumPerCell(this.getMaxNumPerCell());
        organismInfo.setSpeed(this.getSpeed());
        organismInfo.setFoodNeed(this.getFoodNeed());
        organismInfo.setAlive(true);

        Animal animal = this;
        List<EatProbability> eatProbabilities = new ArrayList<>();
        for (Type eatType : animal.getEatProbabilities().keySet()) {
            EatProbability eatProbability = new EatProbability();
            eatProbability.setType(eatType);
            eatProbability.setProbability(animal.getEatProbabilities().get(eatType));
            eatProbabilities.add(eatProbability);
        }
        organismInfo.setEatProbabilities(eatProbabilities);

        try {
            return createOrganism(organismInfo);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
