package org.example.interfaces;

import org.example.entity.map.Cell;
import org.example.entity.map.GameField;

public interface Movable {
    void move(Cell currentCell, Integer currentX, Integer currentY, GameField gameField);
}