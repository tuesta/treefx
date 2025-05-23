package org.treefx.model.ziptree;

import org.treefx.utils.adt.Maybe;

/**
 * Esta clase representa un árbol que implementa la interfaz ZipTree.
 * Permite la manipulación y navegación de un árbol mediante un nodo actual
 * que puede moverse entre padres, hijos o hermanos, así como operaciones
 * de inserción y extracción.
 *
 * @param <a> El tipo de datos que contendrán los nodos del árbol.
 */
public class Tree<a> implements ZipTree<a> {
    private NodeLinkTree<a> node;

    /**
     * Crea una instancia del árbol con un nodo inicial dado.
     *
     * @param node El nodo inicial que será la raíz del árbol.
     */
    public Tree(NodeLinkTree<a> node) {
        this.node = node;
    }

    /**
     * Crea una instancia del árbol con un valor inicial. Se genera un nodo raíz
     * que contiene dicho valor.
     *
     * @param value El valor inicial que será almacenado en el nodo raíz.
     */
    public Tree(a value) {
        this.node = new NodeLinkTree<>(value, new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>());
    }

    /**
     * Devuelve el nodo actual del árbol.
     *
     * @return El nodo actual como un objeto de tipo NodeLinkTree.
     */
    public NodeLinkTree<a> getNode() {
        return this.node;
    }

    /**
     * Extrae el valor contenido en el nodo actual.
     *
     * @return El valor almacenado en el nodo actual.
     */
    @Override
    public a extract() {
        return node.getCurrent();
    }

    /**
     * Mueve la referencia del nodo actual hacia el nodo previo, si existe.
     *
     * @return true si el movimiento fue exitoso, false de lo contrario.
     */
    @Override
    public boolean prev() {
        switch (this.node.getBefore()) {
            case Maybe.Nothing() -> {
                return false;
            }
            case Maybe.Just(NodeLinkTree<a> beforeNode) -> {
                this.node = beforeNode;
                return true;
            }
        }
    }

    /**
     * Mueve la referencia del nodo actual hacia el siguiente nodo, si existe.
     *
     * @return true si el movimiento fue exitoso, false de lo contrario.
     */
    @Override
    public boolean next() {
        switch (this.node.getAfter()) {
            case Maybe.Nothing() -> {
                return false;
            }
            case Maybe.Just(NodeLinkTree<a> afterNode) -> {
                this.node = afterNode;
                return true;
            }
        }
    }

    /**
     * Mueve la referencia del nodo actual hacia su nodo padre, si existe.
     *
     * @return true si el movimiento fue exitoso, false de lo contrario.
     */
    @Override
    public boolean top() {
        switch (node.getUp()) {
            case Maybe.Nothing() -> {
                return false;
            }
            case Maybe.Just(NodeLinkTree<a> upNode) -> {
                this.node = upNode;
                return true;
            }
        }
    }

    /**
     * Mueve la referencia del nodo actual hacia su nodo hijo principal, si existe.
     *
     * @return true si el movimiento fue exitoso, false de lo contrario.
     */
    @Override
    public boolean down() {
        switch (node.getDown()) {
            case Maybe.Nothing() -> {
                return false;
            }
            case Maybe.Just(NodeLinkTree<a> downNode) -> {
                this.node = downNode;
                return true;
            }
        }
    }

    /**
     * Inserta un nuevo nodo como hermano derecho del nodo actual,
     * con el valor proporcionado.
     *
     * @param val El valor que se almacenará en el nuevo nodo.
     */
    public void insertR(a val) {
        var cNode = this.node;
        var newNode = new NodeLinkTree<>(val, new Maybe.Just<>(cNode), cNode.getAfter(), cNode.getUp(), new Maybe.Nothing<>());
        cNode.setAfter(new Maybe.Just<>(newNode));
        this.node = newNode;
    }

    /**
     * Inserta un nuevo nodo como hijo principal del nodo actual,
     * con el valor proporcionado.
     *
     * @param val El valor que se almacenará en el nuevo nodo.
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
}
