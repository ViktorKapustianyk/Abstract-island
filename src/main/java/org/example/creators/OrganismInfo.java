package org.example.creators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganismInfo {
    private String type;
    private double weight;
    private int maxNumPerCell;
    private int speed;
    private double foodNeed;
    private boolean isAlive;
    private List<EatProbability> eatProbabilities;
}
