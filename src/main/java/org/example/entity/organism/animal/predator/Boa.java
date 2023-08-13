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
}
