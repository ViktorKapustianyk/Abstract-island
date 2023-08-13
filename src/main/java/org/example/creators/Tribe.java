package org.example.creators;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.example.entity.map.Cell;
import org.example.entity.map.GameField;

import java.io.File;
import java.util.List;

public class Tribe {
    private GameField gameField;
    private Cell[][] cells;

    public Tribe(GameField gameField) {
        this.gameField = gameField;
        this.cells = gameField.getCells();
    }

    public void createTribe() throws Exception {
        List<OrganismInfo> organismInfoList = readOrganismConfigFile("src/main/resources/organism_config.yaml");

        OrganismPopulationManager populationManager = new OrganismPopulationManager();
        populationManager.populate(cells, organismInfoList);
    }

    public List<OrganismInfo> readOrganismConfigFile(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File(filePath), new TypeReference<List<OrganismInfo>>() {
        });
    }
}