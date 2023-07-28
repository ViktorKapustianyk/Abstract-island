package org.example.entity.map;

public class GameField {
    private int width;
    private int height;
    private Cell[][] cells = new Cell[width][height];

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
