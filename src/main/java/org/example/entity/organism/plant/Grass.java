package org.example.entity.organism.plant;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.entity.organism.Organism;
import org.example.creators.OrganismInfo;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Grass extends Plant{
}
