package org.treefx.model;

import javafx.geometry.Point2D;
import org.treefx.utils.adt.Movement;

import java.util.LinkedList;

public class MovementInSpace {
    /**
     * Posición actual en el espacio bidimensional.
     */
    private Point2D pos;
    /**
     * Lista de movimientos registrados en el espacio.
     */
    private LinkedList<Movement> movements;

    /**
     * Constructor para crear una instancia de Movimiento en el Espacio.
     *
     * @param pos       posición inicial en el espacio.
     * @param movements lista de movimientos iniciales.
     */
    public MovementInSpace(Point2D pos, LinkedList<Movement> movements) {
        this.pos = pos;
        this.movements = movements;
    }

    /**
     * Obtiene la posición actual en el espacio.
     *
     * @return la posición actual representada como un {@link Point2D}.
     */
    public Point2D getPos() {
        return pos;
    }

    /**
     * Establece la posición actual en el espacio.
     *
     * @param pos nueva posición representada como un {@link Point2D}.
     */
    public void setPos(Point2D pos) {
        this.pos = pos;
    }

    /**
     * Obtiene la lista de movimientos registrados.
     *
     * @return una lista de objetos {@link Movement}.
     */
    public LinkedList<Movement> getMovements() {
        return movements;
    }

    /**
     * Establece una nueva lista de movimientos.
     *
     * @param movements lista de movimientos que será registrada.
     */
    public void setMovements(LinkedList<Movement> movements) {
        this.movements = movements;
    }
}
