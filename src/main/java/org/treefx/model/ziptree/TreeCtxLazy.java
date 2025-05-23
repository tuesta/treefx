package org.treefx.model.ziptree;

import org.treefx.model.ziplist.ZipListInc;
import org.treefx.utils.adt.Maybe;

/**
 * Clase que representa un nodo en la estructura de árbol con contexto perezoso.
 * <p>
 * Esta clase modela un nodo en un árbol que tiene una referencia opcional a su
 * nodo padre, un conjunto de nodos hermanos y un conjunto de nodos hijos.
 * El uso de un enfoque perezoso permite optimizar las operaciones sobre la estructura.
 * </p>
 *
 * @param <b> El tipo de datos almacenados en los nodos del árbol.
 */
public class TreeCtxLazy<b> {
    private Maybe<TreeCtxLazy<b>> father;
    private ZipListInc<b, TreeCtxLazy<b>> brothers;
    private ZipListInc<b, TreeCtxLazy<b>> children;

    /**
     * Constructor de la clase TreeCtxLazy.
     *
     * @param father   El nodo padre de este nodo, si existe.
     * @param brothers La lista de nodos hermanos de este nodo.
     * @param children La lista de nodos hijos de este nodo.
     */
    public TreeCtxLazy(Maybe<TreeCtxLazy<b>> father, ZipListInc<b, TreeCtxLazy<b>> brothers, ZipListInc<b, TreeCtxLazy<b>> children) {
        this.father = father;
        this.brothers = brothers;
        this.children = children;
    }

    /**
     * Obtiene el nodo padre de este nodo, si existe.
     *
     * @return Un objeto {@link Maybe} que representa el nodo padre, o un valor vacío si no hay ninguno.
     */
    public Maybe<TreeCtxLazy<b>> getFather() {
        return father;
    }

    /**
     * Establece el nodo padre de este nodo.
     *
     * @param father El nodo padre a establecer. Puede ser vacío si no hay un padre.
     */
    public void setFather(Maybe<TreeCtxLazy<b>> father) {
        this.father = father;
    }

    /**
     * Obtiene la lista de nodos hermanos de este nodo.
     *
     * @return Un {@link ZipListInc} que contiene los nodos hermanos.
     */
    public ZipListInc<b, TreeCtxLazy<b>> getBrothers() {
        return brothers;
    }

    /**
     * Establece la lista de nodos hermanos de este nodo.
     *
     * @param brothers La lista de nodos hermanos a establecer.
     */
    public void setBrothers(ZipListInc<b, TreeCtxLazy<b>> brothers) {
        this.brothers = brothers;
    }

    /**
     * Obtiene la lista de nodos hijos de este nodo.
     *
     * @return Un {@link ZipListInc} que contiene los nodos hijos.
     */
    public ZipListInc<b, TreeCtxLazy<b>> getChildren() {
        return children;
    }

    /**
     * Establece la lista de nodos hijos de este nodo.
     *
     * @param children La lista de nodos hijos a establecer.
     */
    public void setChildren(ZipListInc<b, TreeCtxLazy<b>> children) {
        this.children = children;
    }
}