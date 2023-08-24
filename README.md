# Island Ecosystem Simulation

This project provides a comprehensive program for simulating the interactions between plants and animals. The program is developed using object-oriented programming principles and employs threading and thread pooling for optimal performance.

## Installation and Execution

To run the simulation on your local machine, follow these steps:

- Download Files: Begin by downloading the JAR file and the two YAML files from the repository.
- Arrange Files: Place these downloaded files in the same directory.
- Start the Program: Open your terminal or command prompt, navigate to the directory containing the downloaded files, and execute the following command to start the program: java -Xms512m -Xmx1024m -jar Abstract-island.jar start  

## Functionality Overview

### Island and Locations

The island is represented by an array of locations, each of which can contain plants and animals. Locations are objects of the `Cell` class that have properties for storing the plants and animals present in that location.

### Plants

Plants are represented as objects of the `Plant` class. They grow over time and serve as a food source for animals with herbivorous diets.

### Animals

All animal types inherit from the abstract class `Organism`. The `Animal` class, which is a subclass of `Organism`, contains the core properties and methods applicable to all animal types. There are two subclasses of `Animal`: `Predator` and `Herbivore`, representing carnivores and herbivores respectively. Animals can consume plants or other animals, move between locations, reproduce, and be subject to predation.

### Interaction and Probability

Interactions between plants and animals are determined by probabilities specified in the YAML file. For instance, the interaction "wolf eats rabbit" has a probability of 60%. This means that a wolf has a 60% chance of consuming a rabbit if they share the same location.

### Threads and Thread Pooling

Threads and thread pools are employed to optimize computations. This enables parallel calculations of tasks such as plant growth, animal life cycles, and other operations, ultimately enhancing program efficiency.

### Statistics Output

The program offers the capability to display statistics concerning the distribution of plants and animals on the island, the number of plants and animals consumed, and other insightful data about the island's ecosystem.

## Example YAML Object Description

Below is an example of the "Wolf" object description from the YAML file. This YAML example outlines the parameters of a wolf (weight, maximum quantity per location, speed, food requirements, etc.) and the probabilities of its feeding behavior (probability of consuming other animal types or encountered plants).
This example defines the parameters of the animal and its behavior probabilities:

```yaml
- type: Wolf
  weight: 50.0
  maxNumPerCell: 30
  speed: 3
  foodNeed: 8
  eatProbabilities:
    - type: WOLF
      probability: 0.0
    - type: BOA
      probability: 0.0
    - type: FOX
      probability: 0.0
    ... (other animal types and probabilities)
    - type: GRASS
      probability: 0.0
