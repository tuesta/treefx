package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;

/**
 * Representa una lista enlazada doblemente con nodos opcionales para los elementos previos
 * y posteriores, y un elemento actual. Permite la navegación y manipulación de nodos.
 *
 * @param <a> El tipo genérico de los elementos almacenados en los nodos de la lista.
 */
public class NodeLinkList<a> {
    /**
     * Nodo anterior de la lista, que puede estar vacío.
     */
    private Maybe<NodeLinkList<a>> before;

    /**
     * Elemento actual almacenado en este nodo.
     */
    private a current;

    /**
     * Nodo siguiente de la lista, que puede estar vacío.
     */
    private Maybe<NodeLinkList<a>> after;

    /**
     * Constructor que inicializa un nodo con nodos antes y después opcionales.
     *
     * @param before  El nodo anterior, que puede estar vacío.
     * @param current El elemento actual a almacenar en este nodo.
     * @param after   El nodo siguiente, que puede estar vacío.
     */
    public NodeLinkList(Maybe<NodeLinkList<a>> before, a current, Maybe<NodeLinkList<a>> after) {
        this.before = before;
        this.current = current;
        this.after = after;
    }

    /**
     * Constructor que inicializa un nodo con un elemento actual, y sin nodos previos ni siguientes.
     *
     * @param current El elemento actual a almacenar.
     */
    public NodeLinkList(a current) {
        this(new Maybe.Nothing<>(), current, new Maybe.Nothing<>());
    }

    /**
     * Obtiene el nodo anterior.
     *
     * @return El nodo previo como un objeto Maybe (puede estar vacío).
     */
    public Maybe<NodeLinkList<a>> getBefore() {
        return before;
    }

    /**
     * Establece el nodo anterior.
     *
     * @param before El nodo previo a asignar, como un objeto Maybe.
     */
    public void setBefore(Maybe<NodeLinkList<a>> before) {
        this.before = before;
    }

    /**
     * Obtiene el elemento actual.
     *
     * @return El elemento actual almacenado en este nodo.
     */
    public a getCurrent() {
        return current;
    }

    /**
     * Establece el elemento actual.
     *
     * @param current El nuevo valor del elemento actual en este nodo.
     */
    public void setCurrent(a current) {
        this.current = current;
    }

    /**
     * Obtiene el nodo siguiente.
     *
     * @return El nodo siguiente como un objeto Maybe (puede estar vacío).
     */
    public Maybe<NodeLinkList<a>> getAfter() {
        return after;
    }

    /**
     * Establece el nodo siguiente.
     *
     * @param after El nodo siguiente a asignar, como un objeto Maybe.
     */
    public void setAfter(Maybe<NodeLinkList<a>> after) {
        this.after = after;
    }

    /**
     * Muestra visualmente el nodo actual y los nodos antes y después de este.
     */
    public void show() {
        switch (this.before) {
            case Maybe.Nothing() -> System.out.print("||");
            case Maybe.Just(NodeLinkList<a> beforeNode) -> System.out.print(beforeNode.showLeft());
        }
        System.out.print("@" + ">" + this.getCurrent() + "<" + "@");
        switch (this.after) {
            case Maybe.Nothing() -> System.out.print("||");
            case Maybe.Just(NodeLinkList<a> afterNode) -> System.out.println(afterNode.showRight());
        }
    }

    /**
     * Muestra de forma recursiva los nodos previos y el nodo actual.
     *
     * @return Una representación en forma de cadena de los nodos previos y el nodo actual.
     */
    public String showLeft() {
        return switch (this.before) {
            case Maybe.Nothing() -> "||";
            case Maybe.Just(NodeLinkList<a> beforeNode) -> beforeNode.showLeft();
        } + "@" + this.getCurrent();
    }

    /**
     * Muestra de forma recursiva el nodo actual y los nodos siguientes.
     *
     * @return Una representación en forma de cadena del nodo actual y los nodos siguientes.
     */
    public String showRight() {
        return this.getCurrent() + "@" + switch (this.after) {
            case Maybe.Nothing() -> "||";
            case Maybe.Just(NodeLinkList<a> afterNode) -> afterNode.showRight();
        };
    }
}