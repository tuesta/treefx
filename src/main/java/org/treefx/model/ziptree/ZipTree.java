package org.treefx.model.ziptree;

/**
 * Interfaz que define un árbol zipper.
 *
 * Un árbol zipper permite navegar y manipular árboles de forma eficiente,
 * manteniendo un contexto en el nodo actual dentro de la estructura.
 *
 * @param <a> Tipo de los valores almacenados en los nodos del árbol
 */
public interface ZipTree<a> {

    /**
     * Extrae el valor del nodo actual.
     *
     * @return El valor almacenado en el nodo actual
     */
    public a extract();

    /**
     * Navega al nodo hermano anterior en el mismo nivel.
     *
     * @return true si el movimiento fue exitoso, false si no hay hermano anterior
     */
    public boolean prev();

    /**
     * Navega al siguiente nodo hermano en el mismo nivel.
     *
     * @return true si el movimiento fue exitoso, false si no hay siguiente hermano
     */
    public boolean next();

    /**
     * Navega al nodo padre del nodo actual.
     *
     * @return true si el movimiento fue exitoso, false si no hay nodo padre
     */
    public boolean top();

    /**
     * Navega al primer nodo hijo del nodo actual.
     *
     * @return true si el movimiento fue exitoso, false si no hay nodos hijos
     */
    public boolean down();

}