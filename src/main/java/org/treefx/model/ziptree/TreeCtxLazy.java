package org.treefx.model.ziptree;

import org.treefx.model.ziplist.ZipListInc;
import org.treefx.utils.adt.Maybe;

/**
 * Clase que representa un contexto "Lazy" de árbol.
 * El contexto incluye información sobre el nodo padre, los nodos hermanos
 * y los nodos hijos del árbol. Se utiliza una evaluación diferida
 * (lazy evaluation) para calcular los nodos según sea necesario.
 *
 * @param <b> Tipo del dato estado utilizado en los nodos del árbol
 */
public class TreeCtxLazy<b> {

    /** Nodo padre del contexto actual */
    private Maybe<TreeCtxLazy<b>> father;

    /** Lista zipper de los nodos hermanos en el mismo nivel */
    private ZipListInc<b, TreeCtxLazy<b>> brothers;

    /** Lista zipper de los nodos hijos del nodo actual */
    private ZipListInc<b, TreeCtxLazy<b>> children;

    /**
     * Constructor que inicializa el contexto del nodo lazy con padre,
     * hermanos e hijos.
     *
     * @param father   Maybe que puede guardar el nodo padre (o estar vacío)
     * @param brothers Lista zipper de los nodos hermanos
     * @param children Lista zipper de los nodos hijos
     */
    public TreeCtxLazy(Maybe<TreeCtxLazy<b>> father, ZipListInc<b, TreeCtxLazy<b>> brothers, ZipListInc<b, TreeCtxLazy<b>> children) {
        this.father = father;
        this.brothers = brothers;
        this.children = children;
    }

    /**
     * Obtiene el nodo padre del contexto actual.
     *
     * @return Maybe conteniendo el nodo padre (o vacío, si no hay un padre)
     */
    public Maybe<TreeCtxLazy<b>> getFather() { return father; }

    /**
     * Establece el nodo padre del contexto actual.
     *
     * @param father Nodo padre que se debe establecer
     */
    public void setFather(Maybe<TreeCtxLazy<b>> father)   { this.father = father; }

    /**
     * Obtiene la lista zipper de hermanos en el contexto actual.
     *
     * @return Lista zipper de hermanos
     */
    public ZipListInc<b, TreeCtxLazy<b>> getBrothers() { return brothers; }

    /**
     * Establece una nueva lista zipper de hermanos en el contexto actual.
     *
     * @param brothers Nueva lista zipper de hermanos
     */
    public void setBrothers(ZipListInc<b, TreeCtxLazy<b>> brothers) { this.brothers = brothers; }

    /**
     * Obtiene la lista zipper de hijos en el contexto actual.
     *
     * @return Lista zipper de hijos
     */
    public ZipListInc<b, TreeCtxLazy<b>> getChildren() { return children; }

    /**
     * Establece una nueva lista zipper de hijos en el contexto actual.
     *
     * @param children Nueva lista zipper de hijos
     */
    public void setChildren(ZipListInc<b, TreeCtxLazy<b>> children) { this.children = children; }
}