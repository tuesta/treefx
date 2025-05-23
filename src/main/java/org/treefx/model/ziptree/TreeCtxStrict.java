package org.treefx.model.ziptree;

import org.treefx.model.ziplist.NodeLinkList;
import org.treefx.model.ziplist.ZipListStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Clase que representa un contexto de árbol estricto (TreeCtxStrict) para la manipulación
 * estructurada de nodos en un árbol. Un TreeCtxStrict mantiene información sobre el padre,
 * el nodo actual, los hermanos y los hijos, lo que permite realizar operaciones sobre el árbol
 * de forma navegable y eficiente.
 *
 * @param <a> El tipo de datos contenido en los nodos del árbol.
 */
public class TreeCtxStrict<a> {
    /**
     * Referencia al nodo padre del nodo actual. Es opcional (puede no existir).
     */
    private Maybe<TreeCtxStrict<a>> father;
    /**
     * Nodo actual en el contexto del árbol. Contiene el valor del nodo y la referencia a su contexto.
     */
    private NodeLinkList<T<a, TreeCtxStrict<a>>> current;
    /**
     * Estructura que representa los hermanos del nodo actual. 
     * Permite navegar entre los nodos hermanos en el mismo nivel.
     */
    private ZipListStrict<T<a, TreeCtxStrict<a>>> brothers;
    /**
     * Estructura que contiene los nodos hijos del nodo actual.
     * Permite realizar operaciones sobre los hijos del nodo.
     */
    private ZipListStrict<T<a, TreeCtxStrict<a>>> children;

    /**
     * Constructor para crear un contexto de un árbol con las referencias iniciales proporcionadas.
     *
     * @param father   Referencia opcional al nodo padre.
     * @param current  Nodo actual en el contexto del árbol.
     * @param brothers Lista de hermanos del nodo actual.
     * @param children Lista de hijos del nodo actual.
     */
    public TreeCtxStrict(Maybe<TreeCtxStrict<a>> father,
                         NodeLinkList<T<a, TreeCtxStrict<a>>> current,
                         ZipListStrict<T<a, TreeCtxStrict<a>>> brothers,
                         ZipListStrict<T<a, TreeCtxStrict<a>>> children) {
        this.father = father;
        this.current = current;
        this.brothers = brothers;
        this.children = children;
    }

    /**
     * Obtiene el nodo actual en el contexto del árbol.
     *
     * @return El nodo actual en el contexto.
     */
    public NodeLinkList<T<a, TreeCtxStrict<a>>> getCurrent() {
        return current;
    }

    /**
     * Establece el nodo actual en el contexto del árbol.
     *
     * @param current El nodo actual a establecer.
     */
    public void setCurrent(NodeLinkList<T<a, TreeCtxStrict<a>>> current) {
        this.current = current;
    }

    /**
     * Obtiene el valor almacenado en el nodo actual.
     *
     * @return El valor del nodo actual.
     */
    public a getValue() {
        return current.getCurrent().fst();
    }

    /**
     * Obtiene la referencia al nodo padre del nodo actual.
     *
     * @return Una instancia de {@link Maybe} que puede contener el nodo padre.
     */
    public Maybe<TreeCtxStrict<a>> getFather() {
        return father;
    }

    /**
     * Establece al nodo padre para el nodo actual.
     *
     * @param father El nodo padre a establecer.
     */
    public void setFather(Maybe<TreeCtxStrict<a>> father) {
        this.father = father;
    }

    /**
     * Obtiene la lista de hermanos del nodo actual.
     *
     * @return Una instancia de {@link ZipListStrict} con los nodos hermanos.
     */
    public ZipListStrict<T<a, TreeCtxStrict<a>>> getBrothers() {
        return brothers;
    }

    /**
     * Establece la lista de hermanos del nodo actual.
     *
     * @param brothers La lista de nodos hermanos.
     */
    public void setBrothers(ZipListStrict<T<a, TreeCtxStrict<a>>> brothers) {
        this.brothers = brothers;
    }

    /**
     * Obtiene la lista de hijos del nodo actual.
     *
     * @return Una instancia de {@link ZipListStrict} con los nodos hijos.
     */
    public ZipListStrict<T<a, TreeCtxStrict<a>>> getChildren() {
        return children;
    }

    /**
     * Establece la lista de hijos del nodo actual.
     *
     * @param children La lista de nodos hijos.
     */
    public void setChildren(ZipListStrict<T<a, TreeCtxStrict<a>>> children) {
        this.children = children;
    }

    /**
     * Aplica una función de forma descendente a todos los nodos desde el nodo actual.
     *
     * @param k La función a aplicar a los valores de los nodos.
     */
    public void downMap(Function<a, Void> k) {
        var a = this.current.getCurrent().fst();
        k.apply(a);
        this.children.mapM(x -> x.snd().downMap(k));
    }

    /**
     * Aplica una función de forma descendente a todos los nodos del árbol desde el nodo actual,
     * proporcionando información sobre el resultado del padre al aplicar la función.
     *
     * @param mfatherResult Valor opcional del resultado del nodo padre.
     * @param k             Una función que recibe el resultado del padre y el nodo actual, y produce un nuevo resultado.
     * @param <b>           El tipo del resultado producido por la función.
     */
    public <b> void downMapWithFatherGO(Maybe<b> mfatherResult, BiFunction<Maybe<b>, TreeCtxStrict<a>, b> k) {
        var a = this.current.getCurrent().snd();
        var b = k.apply(mfatherResult, a);
        this.children.mapM(x -> x.snd().downMapWithFatherGO(new Maybe.Just<>(b), k));
    }

    /**
     * Aplica una función de forma descendente a todos los nodos del árbol desde el nodo actual,
     * sin proporcionar inicialmente información del resultado del padre.
     *
     * @param k   Una función que recibe el resultado del padre (si existe) y el nodo actual, y produce un nuevo resultado.
     * @param <b> El tipo del resultado producido por la función.
     */
    public <b> void downMapWithFather(BiFunction<Maybe<b>, TreeCtxStrict<a>, b> k) {
        this.downMapWithFatherGO(new Maybe.Nothing<>(), k);
    }
}