package org.example.entity.map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
@Getter
@Setter
public class GameField {
    private final int width;
    private final int height;
    private Cell[][] cells;

    @JsonCreator
    public GameField(@JsonProperty("width") int width, @JsonProperty("height") int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        initializeCells();
    }

    private void initializeCells() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(new HashMap<>());
            }
        }
    }
    public static GameField readGameFieldConfigFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(new File(filePath), GameField.class);
    }
}
