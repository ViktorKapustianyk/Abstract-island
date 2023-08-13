package org.example.entity.organism.animal.herbivore;
import lombok.*;
import org.example.creators.EatProbability;
import org.example.creators.OrganismInfo;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import org.example.entity.organism.animal.Animal;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Rabbit extends Herbivore {
}
