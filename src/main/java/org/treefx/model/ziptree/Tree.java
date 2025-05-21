package org.treefx.model.ziptree;

import org.treefx.utils.adt.Maybe;

/**
 * Clase que representa un árbol basado en una estructura de nodos enlazados.
 * Implementa la interfaz ZipTree para permitir la navegación y manipulación
 * de los nodos del árbol.
 *
 * @param <a> El tipo de datos almacenados en los nodos del árbol
 */
public class Tree<a> implements ZipTree<a> {

    /** Nodo actual en el árbol */
    private NodeLinkTree<a> node;

    /**
     * Constructor que inicializa el árbol con un nodo principal.
     *
     * @param node Nodo principal del árbol
     */
    public Tree(NodeLinkTree<a> node) { this.node = node; }

    /**
     * Constructor que crea un árbol a partir de un valor inicial.
     * Crea un nodo raíz con el valor proporcionado.
     *
     * @param value Valor para el nodo raíz del árbol
     */
    public Tree(a value) {
        this.node = new NodeLinkTree<>(value, new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>());
    }

    /**
     * Obtiene el nodo actual del árbol.
     *
     * @return Nodo actual como NodeLinkTree
     */
    public NodeLinkTree<a> getNode() { return this.node; }

    /**
     * Extrae el valor actual del nodo donde se encuentra el iterador.
     *
     * @return El valor del nodo actual
     */
    @Override
    public a extract() { return node.getCurrent(); }

    /**
     * Navega al nodo hermano anterior, si existe.
     *
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    @Override
    public boolean prev() {
        switch (this.node.getBefore()) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(NodeLinkTree<a> beforeNode) -> { this.node = beforeNode; return true; }
        }
    }

    /**
     * Navega al nodo hermano siguiente, si existe.
     *
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    @Override
    public boolean next() {
        switch (this.node.getAfter()) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(NodeLinkTree<a> afterNode) -> { this.node = afterNode; return true; }
        }
    }

    /**
     * Navega al nodo padre del nodo actual, si existe.
     *
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    @Override
    public boolean top() {
        switch (node.getUp()) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(NodeLinkTree<a> upNode) -> { this.node = upNode; return true; }
        }
    }

    /**
     * Navega al primer nodo hijo del nodo actual, si existe.
     *
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    @Override
    public boolean down() {
        switch (node.getDown()) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(NodeLinkTree<a> downNode) -> { this.node = downNode; return true; }
        }
    }

    /**
     * Inserta un nuevo nodo hermano a la derecha del nodo actual.
     * El nuevo nodo pasa a ser el nodo actual.
     *
     * @param val Valor a insertar en el nuevo nodo
     */
    public void insertR(a val) {
        var cNode = this.node;
        var newNode = new NodeLinkTree<>(val, new Maybe.Just<>(cNode), cNode.getAfter(),cNode.getUp(), new Maybe.Nothing<>());
        cNode.setAfter(new Maybe.Just<>(newNode));
        this.node = newNode;
    }

    /**
     * Inserta un nuevo nodo hijo del nodo actual.
     * El nuevo nodo pasa a ser el nodo actual.
     *
     * @param val Valor a insertar en el nuevo nodo
     */
    public void insertD(a val) {
        NodeLinkTree<a> newNode;
        switch (this.node.getDown()) {
            case Maybe.Nothing() ->
                newNode = new NodeLinkTree<>(val, new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Just<>(this.node), new Maybe.Nothing<>());
            case Maybe.Just(NodeLinkTree<a> downNode) -> {
                newNode = new NodeLinkTree<>(val, new Maybe.Just<>(downNode), downNode.getAfter(), new Maybe.Just<>(this.node), new Maybe.Nothing<>());
                downNode.setAfter(new Maybe.Just<>(newNode));
            }
        }

        this.node.setDown(new Maybe.Just<>(newNode));
        this.node = newNode;
    }

    /**
     * Muestra la estructura del árbol comenzando desde el nodo actual.
     */
    public void show() { this.node.show(); }
}
