package org.example.entity.map;
import java.util.HashMap;

public class GameField {
    private int width = 3;
    private int height = 3;
    private Cell[][] cells = new Cell[width][height];

    public GameField() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(new HashMap<>());
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
