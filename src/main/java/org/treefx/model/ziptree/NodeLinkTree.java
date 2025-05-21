package org.treefx.model.ziptree;

import org.treefx.utils.Mut;
import org.treefx.utils.adt.Maybe;

import java.util.LinkedList;

/**
 * Clase que representa un nodo enlazado en una estructura de árbol.
 * Cada nodo contiene un valor y referencias a nodos vecinos, incluyendo
 * nodos anteriores, posteriores, superiores (padre) e inferiores (hijos).
 *
 * @param <a> Tipo del dato almacenado en el nodo
 */
public class NodeLinkTree<a> {

    /** Valor almacenado en el nodo actual */
    private a current;

    /** Referencia al nodo anterior */
    private Maybe<NodeLinkTree<a>> before;

    /** Referencia al nodo posterior */
    private Maybe<NodeLinkTree<a>> after;

    /** Referencia al nodo superior (padre) */
    private Maybe<NodeLinkTree<a>> up;

    /** Referencia al nodo inferior (hijos) */
    private Maybe<NodeLinkTree<a>> down;

    /**
     * Constructor que inicializa un nodo con un valor y sus referencias
     * a nodos vecinos.
     *
     * @param current Valor almacenado en el nodo actual
     * @param before Nodo anterior
     * @param after Nodo posterior
     * @param up Nodo superior (padre)
     * @param down Nodo inferior (hijos)
     */
    public NodeLinkTree(a current, Maybe<NodeLinkTree<a>> before, Maybe<NodeLinkTree<a>> after, Maybe<NodeLinkTree<a>> up, Maybe<NodeLinkTree<a>> down) {
        this.current = current;
        this.before = before;
        this.after = after;
        this.up = up;
        this.down = down;
    }

    /**
     * Constructor que inicializa un nodo con un valor,
     * sin referencias hacia nodos vecinos.
     *
     * @param current Valor almacenado en el nodo actual
     */
    public NodeLinkTree(a current) {
        this(current, new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>());
    }

    /**
     * Obtiene el valor almacenado en el nodo actual.
     *
     * @return Valor del nodo actual
     */
    public a getCurrent() { return current; }

    /**
     * Establece un nuevo valor en el nodo actual.
     *
     * @param current Nuevo valor a establecer
     */
    public void setCurrent(a current) { this.current = current; }

    /**
     * Obtiene la referencia al nodo anterior.
     *
     * @return Nodo anterior como Maybe
     */
    public Maybe<NodeLinkTree<a>> getBefore() { return before; }

    /**
     * Establece la referencia al nodo anterior.
     *
     * @param before Nodo anterior a establecer
     */
    public void setBefore(Maybe<NodeLinkTree<a>> before) { this.before = before; }

    /**
     * Obtiene la referencia al nodo posterior.
     *
     * @return Nodo posterior como Maybe
     */
    public Maybe<NodeLinkTree<a>> getAfter() { return after; }

    /**
     * Establece la referencia al nodo posterior.
     *
     * @param after Nodo posterior a establecer
     */
    public void setAfter(Maybe<NodeLinkTree<a>> after) { this.after = after; }

    /**
     * Obtiene la referencia al nodo superior (padre).
     *
     * @return Nodo padre como Maybe
     */
    public Maybe<NodeLinkTree<a>> getUp() { return up; }

    /**
     * Establece la referencia al nodo superior (padre).
     *
     * @param up Nodo superior a establecer
     */
    public void setUp(Maybe<NodeLinkTree<a>> up) { this.up = up; }

    /**
     * Obtiene la referencia al nodo inferior (hijos).
     *
     * @return Nodo inferior como Maybe
     */
    public Maybe<NodeLinkTree<a>> getDown() { return down; }

    /**
     * Establece la referencia al nodo inferior (hijos).
     *
     * @param down Nodo inferior a establecer
     */
    public void setDown(Maybe<NodeLinkTree<a>> down) { this.down = down; }

    /**
     * Une las líneas de texto almacenadas en una lista en una cadena
     * única, separándolas mediante saltos de línea.
     *
     * @param xs Lista de líneas como Mut<String>
     * @return Cadena unificada con saltos de línea
     */
    public String unlines(LinkedList<Mut<String>> xs) {
        return xs.stream()
                .map(mut -> mut.val)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    public void show() { System.out.println(unlines(draw(this))); }

    /**
     * Muestra la representación jerárquica del árbol comenzando
     * desde el nodo actual.
     */
    public LinkedList<Mut<String>> draw(NodeLinkTree<a> node) {
        return drawUp(drawBoth(new Maybe.Just<>(node), true), node.up);
    }


    /**
     * Construye una representación textual jerárquica de un árbol recorriéndolo hacia arriba
     * desde un nodo inicial. El metodo procesa recursivamente los nodos padre para generar
     * una salida estructurada que incluye los hijos dados y los vincula con los nodos vecinos.
     *
     * @param children Una lista enlazada de cadenas mutables que representan las líneas de texto
     * del nodo actual y su contenido descendente.
     * @param mnode Un nodo con un encapsulado Maybe que representa el nodo actual en el recorrido ascendente.
     * Si es Nothing, el recorrido se detiene; si es Just, se procesa el contenido del nodo.
     * @return Una lista enlazada de cadenas mutables que contiene la representación jerárquica completa
     * del árbol, incluyendo el nodo actual procesado y su(s) nivel(es) padre.
     */
    public LinkedList<Mut<String>> drawUp(LinkedList<Mut<String>> children, Maybe<NodeLinkTree<a>> mnode) {
        switch (mnode) {
            case Maybe.Nothing() -> { return children; }
            case Maybe.Just(NodeLinkTree<a> upNode) -> {
                LinkedList<Mut<String>> level = drawLeft(upNode.before);

                LinkedList<Mut<String>> current = new LinkedList<>();
                current.add(new Mut<>(upNode.toString()));
                current.addAll(children);
                if (upNode.up.isJust()) {
                    if (upNode.after.isNothing()) shift(current, "`- ", "   ");
                    else shift(current, "+- ", "|  ");
                    current.addFirst(new Mut<>("|"));
                }

                level.addAll(current);
                level.addAll(drawRight(upNode.after, true, false));

                return drawUp(level, upNode.up);
            }
        }
    }

    /**
     * Genera una lista enlazada de cadenas mutables basada en el nodo y el estado proporcionados.
     * El metodo combina los elementos izquierdo, derecho y posiblemente central para construir una representación textual jerárquica del árbol.
     *
     * @param mnode Un Maybe<NodeLinkTree<a>> que representa el nodo actual del árbol. Puede ser Maybe.Nothing(), que representa la ausencia de un nodo, o Maybe.Just(), que representa un nodo válido en el árbol.
     * @param isCurrent Un booleano que indica si el nodo actual es el foco de la operación.
     * Puede afectar la forma en que se dibujan o combinan los elementos.
     * @return Un LinkedList<Mut<String>> que contiene la representación textual de la jerarquía construida a partir del nodo proporcionado y su estado.
     */
    public LinkedList<Mut<String>> drawBoth(Maybe<NodeLinkTree<a>> mnode, boolean isCurrent) {
        LinkedList<Mut<String>> both;
        switch (mnode) {
            case Maybe.Nothing() -> both = new LinkedList<>();
            case Maybe.Just(NodeLinkTree<a> node) -> {
                both = drawLeft(node.before);
                if (node.up.isJust()) both.add(new Mut<>("|"));
                both.addAll(drawRight(mnode, true, isCurrent));
            }
        }
        return both;
    }

    /**
     * Genera una representación textual jerárquica del lado izquierdo de un árbol,
     * a partir del nodo proporcionado, y la procesa recursivamente.
     *
     * @param mnode Una instancia de Maybe<NodeLinkTree<a>> que representa el nodo inicial.
     * Puede ser Maybe.Nothing() si no hay ningún nodo presente, o Maybe.Just()
     * si hay un nodo válido disponible.
     * @return Una LinkedList<Mut<String>> que contiene la estructura jerárquica textual
     * del lado izquierdo del árbol, construida recursivamente a partir del nodo dado.
     */
    public LinkedList<Mut<String>> drawLeft(Maybe<NodeLinkTree<a>> mnode) {
        LinkedList<Mut<String>> left;
        switch (mnode) {
            case Maybe.Nothing() -> left = new LinkedList<>();
            case Maybe.Just(NodeLinkTree<a> nodeLeft) -> {
                left = drawLeft(nodeLeft.before);

                LinkedList<Mut<String>> children = new LinkedList<>();
                children.add(new Mut<>(nodeLeft.toString()));
                children.addAll(drawBoth(nodeLeft.down, false));
                if (nodeLeft.up.isJust()) {
                    shift(children, "`+ ", "|  ");
                    children.addFirst(new Mut<>("|"));
                }

                left.addAll(children);
            }
        }
        return left;
    }

    /**
     * Genera una representación textual jerárquica del lado derecho de un árbol,
     * comenzando desde el nodo dado y procesándolo recursivamente.
     *
     * @param mnode Una instancia de Maybe<NodeLinkTree<a>> que representa el nodo inicial.
     * Puede ser Maybe.Nothing() si no hay ningún nodo presente, o Maybe.Just()
     * si hay un nodo válido disponible.
     * @param isFromBoth Un booleano que indica si el metodo se ha invocado
     * como parte de un recorrido por ambos lados del árbol. Esto modifica la representación
     * resultante según el contexto del recorrido.
     * @param isCurrent Un booleano que especifica si el nodo actual es el foco durante
     * la operación. Si es verdadero, el formato del nodo en la salida se
     * resaltará como corresponde. * @return Una LinkedList<Mut<String>> que contiene la estructura textual jerárquica
     * del lado derecho del árbol, construida recursivamente a partir del nodo dado.
     */
    public LinkedList<Mut<String>> drawRight(Maybe<NodeLinkTree<a>> mnode, boolean isFromBoth, boolean isCurrent) {
        LinkedList<Mut<String>> right;
        switch (mnode) {
            case Maybe.Nothing() -> right = new LinkedList<>();
            case Maybe.Just(NodeLinkTree<a> nodeRight) -> {
                LinkedList<Mut<String>> children = new LinkedList<>();
                String currentStr = nodeRight.toString();
                if (isCurrent) {
                    currentStr = ">>" + currentStr + "<<";
                }
                children.add(new Mut<>(currentStr));
                children.addAll(drawBoth(nodeRight.down, false));
                if (nodeRight.up.isJust()) {
                    if (nodeRight.after.isNothing()) shift(children, "`- ", "   ");
                    else shift(children, "+- ", "|  ");
                }

                if (!isFromBoth) children.addFirst(new Mut<>("|"));

                right = children;
                right.addAll(drawRight(nodeRight.after, false, false));
            }
        }
        return right;
    }

    /**
     * Modifica una lista de elementos mutables agregando un prefijo a cada uno de los valores
     * contenidos en los elementos. El primer valor recibe un prefijo específico, mientras
     * que los demás reciben un prefijo genérico.
     *
     * @param xs Una lista enlazada de objetos Mut<String> que contienen los valores a modificar.
     * @param first El prefijo que se agrega al primer elemento de la lista.
     * @param other El prefijo que se agrega a todos los demás elementos de la lista.
     */
    public void shift(LinkedList<Mut<String>> xs, String first, String other) {
        boolean isFirst = true;
        for (Mut<String> str : xs) {
            String prefix = isFirst ? first : other;
            isFirst = false;
            str.val = prefix + str.val;
        }
    }
}