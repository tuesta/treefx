package org.treefx.model;

import javafx.geometry.Point2D;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.LinkedList;

/**
 * Clase que representa la información de un nodo en un árbol.
 * Contiene datos como el nombre del nodo, una URL opcional de imagen,
 * posición en el espacio 2D y una lista de nodos hijos.
 */
public class NodeInfo {
    private String name;
    private Maybe<String> imgURL;
    private Point2D pos;
    private LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children;


    /**
     * Constructor que inicializa un nuevo nodo con todos sus atributos.
     *
     * @param name     El nombre del nodo
     * @param imgURL   URL opcional de la imagen asociada al nodo
     * @param pos      Posición del nodo en el espacio 2D
     * @param children Lista de nodos hijos con sus respectivas posiciones
     */
    public NodeInfo(String name, Maybe<String> imgURL, Point2D pos, LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children) {
        this.name = name;
        this.imgURL = imgURL;
        this.pos = pos;
        this.children = children;
    }

    /**
     * Obtiene el nombre del nodo.
     *
     * @return El nombre del nodo
     */
    public String getName() { return name; }

    /**
     * Establece el nombre del nodo.
     *
     * @param name Nuevo nombre para el nodo
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene la URL de la imagen asociada al nodo.
     *
     * @return Maybe conteniendo la URL de la imagen, o Nothing si no hay imagen
     */
    public Maybe<String> getImgURL() { return imgURL; }

    /**
     * Establece la URL de la imagen del nodo.
     *
     * @param imgURL Maybe conteniendo la nueva URL de la imagen
     */
    public void setImgURL(Maybe<String> imgURL) { this.imgURL = imgURL; }

    /**
     * Obtiene la posición del nodo en el espacio 2D.
     *
     * @return La posición del nodo como Point2D
     */
    public Point2D getPos() { return pos; }

    /**
     * Establece la posición del nodo en el espacio 2D.
     *
     * @param pos Nueva posición para el nodo
     */
    public void setPos(Point2D pos) { this.pos = pos; }

    /**
     * Obtiene la lista de nodos hijos.
     *
     * @return Lista enlazada de tuplas conteniendo posición y contexto de los nodos hijos
     */
    public LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> getChildren() { return children; }

    /**
     * Establece la lista de nodos hijos.
     *
     * @param children Nueva lista de nodos hijos
     */
    public void setChildren(LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children) { this.children = children; }
}