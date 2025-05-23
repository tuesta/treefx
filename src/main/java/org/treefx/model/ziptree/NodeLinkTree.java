package org.treefx.model.ziptree;

import org.treefx.utils.adt.Maybe;

/**
 * Clase que representa un árbol enlazado que permite la navegación
 * a través de nodos relacionados, como el anterior, siguiente,
 * padre e hijo.
 *
 * @param <a> El tipo de dato que contiene cada nodo del árbol.
 */
public class NodeLinkTree<a> {
    private a current;
    private Maybe<NodeLinkTree<a>> before;
    private Maybe<NodeLinkTree<a>> after;
    private Maybe<NodeLinkTree<a>> up;
    private Maybe<NodeLinkTree<a>> down;

    /**
     * Constructor que inicializa un nodo del árbol con valores específicos para las
     * relaciones de navegación.
     *
     * @param current El valor almacenado en el nodo actual.
     * @param before  El nodo anterior (si existe).
     * @param after   El nodo siguiente (si existe).
     * @param up      El nodo padre (si existe).
     * @param down    El nodo hijo (si existe).
     */
    public NodeLinkTree(a current, Maybe<NodeLinkTree<a>> before, Maybe<NodeLinkTree<a>> after, Maybe<NodeLinkTree<a>> up, Maybe<NodeLinkTree<a>> down) {
        this.current = current;
        this.before = before;
        this.after = after;
        this.up = up;
        this.down = down;
    }

    /**
     * Constructor que inicializa un nodo del árbol con un valor y sin conexiones
     * (antes, después, arriba o abajo).
     *
     * @param current El valor almacenado en el nodo.
     */
    public NodeLinkTree(a current) {
        this(current, new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>());
    }

    /**
     * Devuelve el valor almacenado en el nodo actual.
     *
     * @return El valor actual del nodo.
     */
    public a getCurrent() {
        return current;
    }

    /**
     * Actualiza el valor del nodo actual.
     *
     * @param current El nuevo valor del nodo.
     */
    public void setCurrent(a current) {
        this.current = current;
    }

    /**
     * Devuelve el nodo anterior al nodo actual.
     *
     * @return El nodo anterior, si existe, o un {@code Maybe.Nothing}.
     */
    public Maybe<NodeLinkTree<a>> getBefore() {
        return before;
    }

    /**
     * Establece el nodo anterior al nodo actual.
     *
     * @param before El nodo a establecer como anterior.
     */
    public void setBefore(Maybe<NodeLinkTree<a>> before) {
        this.before = before;
    }

    /**
     * Devuelve el nodo siguiente al nodo actual.
     *
     * @return El nodo siguiente, si existe, o un {@code Maybe.Nothing}.
     */
    public Maybe<NodeLinkTree<a>> getAfter() {
        return after;
    }

    /**
     * Establece el nodo siguiente al nodo actual.
     *
     * @param after El nodo a establecer como siguiente.
     */
    public void setAfter(Maybe<NodeLinkTree<a>> after) {
        this.after = after;
    }

    /**
     * Devuelve el nodo padre del nodo actual.
     *
     * @return El nodo padre, si existe, o un {@code Maybe.Nothing}.
     */
    public Maybe<NodeLinkTree<a>> getUp() {
        return up;
    }

    /**
     * Establece el nodo padre del nodo actual.
     *
     * @param up El nodo a establecer como padre.
     */
    public void setUp(Maybe<NodeLinkTree<a>> up) {
        this.up = up;
    }

    /**
     * Devuelve el nodo hijo del nodo actual.
     *
     * @return El nodo hijo, si existe, o un {@code Maybe.Nothing}.
     */
    public Maybe<NodeLinkTree<a>> getDown() {
        return down;
    }

    /**
     * Establece el nodo hijo del nodo actual.
     *
     * @param down El nodo a establecer como hijo.
     */
    public void setDown(Maybe<NodeLinkTree<a>> down) {
        this.down = down;
    }
}