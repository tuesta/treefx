package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;


/**
 * Nodo para una lista doblemente enlazada utilizada en estructuras zipper.
 * Cada nodo referencia el elemento anterior y posterior (si existen) y almacena el valor actual.
 *
 * @param <a> Tipo de los elementos almacenados en cada nodo.
 */
public class NodeLinkList<a> {
    private Maybe<NodeLinkList<a>> before;
    private a current;
    private Maybe<NodeLinkList<a>> after;

    /**
     * Crea un nodo con referencias explícitas al anterior y posterior,
     * y con el valor actual dado.
     *
     * @param before Nodo anterior, o {@code Nothing} si no hay anterior.
     * @param current Valor a almacenar en este nodo.
     * @param after Nodo posterior, o {@code Nothing} si no hay posterior.
     */
    public NodeLinkList(Maybe<NodeLinkList<a>> before, a current, Maybe<NodeLinkList<a>> after) {
        this.before = before;
        this.current = current;
        this.after = after;
    }

    /**
     * Crea un nodo que es al mismo tiempo primer y último elemento de la lista.
     * El anterior y el posterior son {@code Nothing}.
     *
     * @param current Valor a almacenar en este nodo.
     */
    public NodeLinkList(a current) {
        this(new Maybe.Nothing<>(), current, new Maybe.Nothing<>());
    }

    /**
     * Obtiene la referencia al nodo anterior, si existe.
     *
     * @return {@code Maybe<NodeLinkList<a>>} con el nodo anterior o {@code Nothing}.
     */
    public Maybe<NodeLinkList<a>> getBefore() { return before; }

    /**
     * Asigna una nueva referencia al nodo anterior.
     *
     * @param before Nueva referencia al nodo anterior.
     */
    public void setBefore(Maybe<NodeLinkList<a>> before) { this.before = before; }

    /**
     * Obtiene el valor almacenado en este nodo.
     *
     * @return Valor actual.
     */
    public a getCurrent() { return current; }

    /**
     * Asigna un nuevo valor a este nodo.
     *
     * @param current Nuevo valor a almacenar.
     */
    public void setCurrent(a current) { this.current = current; }

    /**
     * Obtiene la referencia al nodo posterior, si existe.
     *
     * @return {@code Maybe<NodeLinkList<a>>} con el nodo posterior o {@code Nothing}.
     */
    public Maybe<NodeLinkList<a>> getAfter() { return after; }

    /**
     * Asigna una nueva referencia al nodo posterior.
     *
     * @param after Nueva referencia al nodo posterior.
     */
    public void setAfter(Maybe<NodeLinkList<a>> after) { this.after = after; }

    /**
     * Imprime en consola la representación de la lista desde el anterior hasta el posterior,
     * indicando este nodo como el actual.
     */
    public void show() {
        switch (this.before) {
            case Maybe.Nothing() -> System.out.print("||");
            case Maybe.Just(NodeLinkList<a> beforeNode) -> System.out.print(beforeNode.showLeft());
        }
        System.out.print("@" + ">" + this + "<" + "@");
        switch (this.after) {
            case Maybe.Nothing() -> System.out.print("||");
            case Maybe.Just(NodeLinkList<a> afterNode) -> System.out.println(afterNode.showRight());
        }
    }

    /**
     * Devuelve una cadena que representa la parte izquierda (antes de este nodo) de la lista.
     *
     * @return Cadena con la representación de la lista antes del nodo actual.
     */
    public String showLeft() {
        return switch (this.before) {
            case Maybe.Nothing() -> "||";
            case Maybe.Just(NodeLinkList<a> beforeNode) -> beforeNode.showLeft();
        } + "@" + this;
    }

    /**
     * Devuelve una cadena que representa la parte derecha (después de este nodo) de la lista.
     *
     * @return Cadena con la representación de la lista después del nodo actual.
     */
    public String showRight() {
        return this + "@" + switch (this.after) {
            case Maybe.Nothing() -> "||";
            case Maybe.Just(NodeLinkList<a> afterNode) -> afterNode.showRight();
        };
    }
}