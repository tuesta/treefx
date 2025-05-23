package org.treefx.model;

import javafx.geometry.Point2D;
import org.treefx.utils.adt.Movement;

import java.util.LinkedList;

public class MovementInSpace {
    private Point2D pos;
    private LinkedList<Movement> movements;

    public MovementInSpace(Point2D pos, LinkedList<Movement> movements) {
        this.pos = pos;
        this.movements = movements;
    }

    public Point2D getPos() { return pos; }

    public void setPos(Point2D pos) { this.pos = pos; }

    public LinkedList<Movement> getMovements() { return movements; }

    public void setMovements(LinkedList<Movement> movements) { this.movements = movements; }
}
