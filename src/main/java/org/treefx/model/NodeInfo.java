package org.treefx.model;

import javafx.geometry.Point2D;

import java.util.LinkedList;

/**
 * Clase que representa información sobre un nodo en un árbol.
 * Contiene datos relacionados como su identificador, nombre, URL de imagen,
 * posición y movimientos asociados.
 */
public class NodeInfo {
    private final int id;
    private String name;
    private String imgURL;
    private Point2D pos;
    private LinkedList<MovementInSpace> movements;

    /**
     * Constructor para inicializar un objeto NodeInfo con todos sus atributos.
     *
     * @param id        Identificador único del nodo.
     * @param name      Nombre descriptivo del nodo.
     * @param imgURL    URL de una imagen asociada al nodo.
     * @param pos       Posición (coordenadas) del nodo.
     * @param movements Lista de movimientos asociados al nodo en el espacio.
     */
    public NodeInfo(int id, String name, String imgURL, Point2D pos, LinkedList<MovementInSpace> movements) {
        this.id = id;
        this.name = name;
        this.imgURL = imgURL;
        this.pos = pos;
        this.movements = movements;
    }

    /**
     * Obtiene el identificador único del nodo.
     *
     * @return El identificador del nodo.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el nombre descriptivo del nodo.
     *
     * @return El nombre del nodo.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece un nuevo nombre para el nodo.
     *
     * @param name El nuevo nombre del nodo.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la URL de la imagen asociada al nodo.
     *
     * @return La URL de la imagen del nodo.
     */
    public String getImgURL() {
        return imgURL;
    }

    /**
     * Establece una nueva URL de imagen para el nodo.
     *
     * @param imgURL La nueva URL de imagen del nodo.
     */
    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    /**
     * Obtiene la posición actual (coordenadas) del nodo.
     *
     * @return La posición del nodo como un objeto Point2D.
     */
    public Point2D getPos() {
        return pos;
    }

    /**
     * Establece una nueva posición (coordenadas) para el nodo.
     *
     * @param pos La nueva posición del nodo representada por un Point2D.
     */
    public void setPos(Point2D pos) {
        this.pos = pos;
    }

    /**
     * Agrega un nuevo movimiento a la lista de movimientos del nodo.
     *
     * @param movementInSpace El movimiento que se añadirá.
     */
    public void addPos(MovementInSpace movementInSpace) {
        this.movements.add(movementInSpace);
    }

    /**
     * Obtiene la lista de movimientos asociados al nodo.
     *
     * @return Una lista enlazada con los movimientos del nodo.
     */
    public LinkedList<MovementInSpace> getChildren() {
        return movements;
    }

    /**
     * Establece una nueva lista de movimientos para el nodo.
     *
     * @param children La nueva lista de movimientos del nodo.
     */
    public void setChildren(LinkedList<MovementInSpace> children) {
        this.movements = children;
    }
}