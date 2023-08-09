package org.example.creators;
import lombok.Getter;
import lombok.Setter;
import org.example.entity.organism.Type;

import java.util.List;

@Getter
@Setter
public class EatProbability {
    private Type type;
    private double probability;
}
