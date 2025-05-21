package org.treefx.model.ziptree;

import org.treefx.model.ziplist.NodeLinkList;
import org.treefx.model.ziplist.ZipListStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Clase que representa un contexto estricto de árbol.
 * El contexto incluye información sobre el nodo actual, los nodos hermanos,
 * los hijos y el nodo padre del árbol.
 *
 * @param <a> Tipo de los valores almacenados en los nodos del árbol
 */
public class TreeCtxStrict<a> {
    private Maybe<TreeCtxStrict<a>> father;
    private NodeLinkList<T<a, TreeCtxStrict<a>>> current;
    private ZipListStrict<T<a, TreeCtxStrict<a>>> brothers;
    private ZipListStrict<T<a, TreeCtxStrict<a>>> children;

    /**
     * Constructor que inicializa el contexto del árbol con padre, nodo actual, hermanos e hijos.
     *
     * @param father   Contexto del nodo padre
     * @param current  Nodo actual con su valor y referencia
     * @param brothers Lista zipper con los nodos hermanos
     * @param children Lista zipper con los nodos hijos
     */
    public TreeCtxStrict(Maybe<TreeCtxStrict<a>> father,
                         NodeLinkList<T<a, TreeCtxStrict<a>>> current,
                         ZipListStrict<T<a, TreeCtxStrict<a>>> brothers,
                         ZipListStrict<T<a, TreeCtxStrict<a>>> children)
    {
        this.father = father;
        this.current = current;
        this.brothers = brothers;
        this.children = children;
    }

    /**
     * Obtiene el nodo actual.
     *
     * @return Nodo actual como NodeLinkList
     */
    public NodeLinkList<T<a, TreeCtxStrict<a>>> getCurrent() { return current; }

    /**
     * Establece el nodo actual.
     *
     * @param current Nodo actual que se debe establecer
     */
    public void setCurrent(NodeLinkList<T<a, TreeCtxStrict<a>>> current) { this.current = current; }

    /**
     * Obtiene el valor almacenado en el nodo actual.
     *
     * @return Valor del nodo actual
     */
    public a getValue() { return current.getCurrent().fst(); }

    /**
     * Obtiene el nodo padre.
     *
     * @return Nodo padre como Maybe
     */
    public Maybe<TreeCtxStrict<a>> getFather() { return father; }

    /**
     * Establece el nodo padre.
     *
     * @param father Nodo padre que se debe establecer
     */

    public void setFather(Maybe<TreeCtxStrict<a>> father) { this.father = father; }

    /**
     * Obtiene la lista zipper de hermanos del nodo actual.
     *
     * @return Lista zipper de hermanos
     */
    public ZipListStrict<T<a, TreeCtxStrict<a>>> getBrothers() { return brothers; }


    /**
     * Establece la lista zipper de hermanos.
     *
     * @param brothers Lista zipper de hermanos que se debe establecer
     */
    public void setBrothers(ZipListStrict<T<a, TreeCtxStrict<a>>> brothers) { this.brothers = brothers; }

    /**
     * Obtiene la lista zipper de hijos del nodo actual.
     *
     * @return Lista zipper de hijos
     */
    public ZipListStrict<T<a, TreeCtxStrict<a>>> getChildren() { return children; }

    /**
     * Establece la lista zipper de hijos.
     *
     * @param children Lista zipper de hijos que se debe establecer
     */
    public void setChildren(ZipListStrict<T<a, TreeCtxStrict<a>>> children) { this.children = children; }

    /**
     * Aplica una función a cada nodo descendiente, visitando recursivamente todos los hijos.
     *
     * @param k Función que se aplicará a cada nodo descendiente
     */
    public void downMap(Function<a, Void> k) {
        var a = this.current.getCurrent().fst();
        k.apply(a);
        this.children.mapM(x -> {
            x.snd().downMap(k);
            return null;
        });
    }

    /**
     * Aplica una función a cada nodo descendiente incluyendo resultados calculados
     * a partir del padre, visitando recursivamente todos los hijos.
     *
     * @param <b>            Tipo del valor calculado para el nodo padre
     * @param mfatherResult  Resultado del padre (puede no existir)
     * @param k              Función que se aplicará a cada nodo descendiente
     */
    public <b> void downMapWithFatherGO(Maybe<b> mfatherResult, BiFunction<Maybe<b>, TreeCtxStrict<a>, b> k) {
        var a = this.current.getCurrent().snd();
        var b = k.apply(mfatherResult, a);
        this.children.mapM(x -> {
            x.snd().downMapWithFatherGO(new Maybe.Just<>(b), k);
            return null;
        });
    }

    /**
     * Aplica una función a cada nodo descendiente, calculando los resultados
     * de forma recursiva y comenzando con un valor inicial para el nodo raíz.
     *
     * @param <b> Tipo del valor calculado para el nodo
     * @param k   Función que se aplicará a cada nodo descendiente
     */
    public <b> void downMapWithFather(BiFunction<Maybe<b>, TreeCtxStrict<a>, b> k) {
        this.downMapWithFatherGO(new Maybe.Nothing<>(), k);
    }
}