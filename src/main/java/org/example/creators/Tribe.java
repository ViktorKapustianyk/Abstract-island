package org.example.creators;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tribe {
    private GameField gameField;
    private Cell[][] cells;
    private Organism organism;

    public Tribe(GameField gameField) {
        this.gameField = gameField;
        this.cells = gameField.getCells();
    }

    public void createTribe() throws Exception {
        List<OrganismInfo> organismInfoList = readOrganismConfigFile("/Users/alexandra/IdeaProjects/Abstract-island/src/main/resources/organism_config.yaml");

        for (OrganismInfo organismInfo : organismInfoList) {
            Type type = Type.valueOf(organismInfo.getType().toUpperCase());

            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell cell = cells[i][j];
                    Map<Type, Set<Organism>> residents = cell.getResidents();
                    Set<Organism> tribe = residents.computeIfAbsent(type, k -> new HashSet<>());

                    for (int k = 0; k < organismInfo.getMaxNumPerCell() / 3; k++) {
                        Organism organism = type.getOrganismClass().newInstance();
                        tribe.add(organism);
                    }
                }
            }
        }
    }

    public List<OrganismInfo> readOrganismConfigFile(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File(filePath), new TypeReference<List<OrganismInfo>>() {
        });
    }
}