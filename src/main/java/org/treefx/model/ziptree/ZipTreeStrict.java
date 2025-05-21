package org.treefx.model.ziptree;

import org.treefx.model.ziplist.ZipListStrict;
import org.treefx.utils.adt.Movement;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.LinkedList;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Clase que implementa un árbol zipper estricto.
 * Permite navegar, manipular y realizar transformaciones sobre los nodos de un árbol
 * utilizando una estructura de contexto de árbol.
 *
 * @param <a> Tipo de valores almacenados en los nodos del árbol
 */
public class ZipTreeStrict<a> {
    private TreeCtxStrict<a> ctx;
    private final TreeCtxStrict<a> root;

    /**
     * Constructor que inicializa un árbol zipper a partir de un valor en la raíz.
     *
     * @param val Valor inicial en la raíz del árbol
     */
    public ZipTreeStrict(a val) {
        ZipListStrict<T<a, TreeCtxStrict<a>>> brothers = new ZipListStrict<>();
        this.ctx = new TreeCtxStrict<>(new Maybe.Nothing<>(), null, brothers, new ZipListStrict<>());

        T<a, TreeCtxStrict<a>> current = new T.MkT<>(val, this.ctx);
        brothers.insertR(current);
        this.ctx.setCurrent(brothers.getMNode().fromJust());

        this.root = ctx;
    }

    /**
     * Extrae el valor del nodo actual en el contexto del árbol.
     *
     * @return Valor almacenado en el nodo actual
     */
    public a extract() { return this.ctx.getBrothers().extract().fromJust().fst(); }

    /**
     * Obtiene el contexto actual del árbol.
     *
     * @return Contexto actual del árbol
     */
    public TreeCtxStrict<a> getCtx() { return this.ctx; }

    /**
     * Mueve el contexto al nodo padre del actual.
     *
     * @return true si el movimiento fue exitoso, false si no existe un nodo padre
     */
    public boolean toFather() {
        switch (this.ctx.getFather()) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(TreeCtxStrict<a> fatherCtx) -> {
                this.ctx = fatherCtx;
                return true;
            }
        }
    }

    /**
     * Mueve el contexto al nodo raíz del árbol.
     */
    public void toRoot() { this.ctx = this.root; }

    /**
     * Mueve el contexto al hijo en la posición especificada.
     *
     * @param ix Índice del hijo al que se desea mover
     * @return Nuevo índice al que se movió el contexto. Devuelve 0 si el movimiento falla.
     */
    public int toChild(int ix) {
        var children = this.ctx.getChildren();

        int newIx = children.to(ix);
        if (newIx == 0) return 0;

        this.ctx = children.extract().fromJust().snd();
        return newIx;
    }

    /**
     * Mueve el contexto al siguiente hermano del nodo actual.
     *
     * @return true si el movimiento fue exitoso, false si no hay siguiente hermano
     */
    public boolean next() {
        if (this.ctx.getBrothers().next()) {
            this.ctx = this.ctx.getBrothers().getMNode().fromJust().getCurrent().snd();
            return true;
        }
        return false;
    }

    /**
     * Mueve el contexto al hermano anterior del nodo actual.
     *
     * @return true si el movimiento fue exitoso, false si no hay hermano anterior
     */
    public boolean prev() {
        if (this.ctx.getBrothers().prev()) {
            this.ctx = this.ctx.getBrothers().getMNode().fromJust().getCurrent().snd();
            return true;
        }
        return false;
    }

    /**
     * Mueve el contexto al primer hijo del nodo actual.
     *
     * @return true si el movimiento fue exitoso, false si no hay hijos
     */
    public boolean down() {
        var children = this.ctx.getChildren();
        if (children.getMNode().isNothing()) return false;
        this.ctx = children.getMNode().fromJust().getCurrent().snd();
        return true;
    }

    /**
     * Inserta un nuevo hijo en el nodo actual y mueve el contexto al hijo recién insertado.
     *
     * @param val Valor del nuevo nodo hijo
     */
    public void insertChild(a val) {
        var children = this.ctx.getChildren();
        var newCtx = new TreeCtxStrict<>(new Maybe.Just<>(this.ctx), null,children, new ZipListStrict<>());
        var current = new T.MkT<>(val, newCtx);
        children.insertR(current);
        newCtx.setCurrent(children.getMNode().fromJust());

        this.ctx = newCtx;
    }

    /**
     * Obtiene la lista de movimientos necesarios para navegar desde el nodo actual
     * hasta un nodo objetivo en el árbol.
     *
     * @param targetCtx Contexto del nodo objetivo
     * @return Lista de movimientos necesarios
     */
    public LinkedList<Movement> getRelativePosition(TreeCtxStrict<a> targetCtx) {
        Stack<TreeCtxStrict<a>> ancestorsCurrent = new Stack<>();
        ancestorsCurrent.push(this.ctx);
        while (ancestorsCurrent.peek().getFather().isJust()) {
            ancestorsCurrent.push(ancestorsCurrent.peek().getFather().fromJust());
        }

        Stack<TreeCtxStrict<a>> ancestorsTarget = new Stack<>();
        ancestorsTarget.push(targetCtx);
        while (ancestorsTarget.peek().getFather().isJust()) {
            ancestorsTarget.push(ancestorsTarget.peek().getFather().fromJust());
        }

        TreeCtxStrict<a> commonFather = root;
        while (ancestorsCurrent.peek() == ancestorsTarget.peek()) {
            commonFather = ancestorsCurrent.peek();
            ancestorsCurrent.pop();
            ancestorsTarget.pop();
        }
        ancestorsCurrent.push(commonFather);
        ancestorsTarget.push(commonFather);

        LinkedList<Movement> movements = new LinkedList<>();

        if (ancestorsCurrent.isEmpty() && ancestorsTarget.isEmpty()) return movements;

        // Empieza en uno, para solo contar los padres
        for (int i = 1; i < ancestorsCurrent.size(); i++) movements.add(Movement.UP);

        if (ancestorsTarget.isEmpty()) return movements;
        // Remove common father
        ancestorsTarget.pop();

        while (!ancestorsTarget.isEmpty()) {
            var ctx = ancestorsTarget.pop();
            int child = ctx.getBrothers().getIndex(ctx.getCurrent());
            movements.add(Movement.DOWN(child));
        }

        return movements;
    }

    /**
     * Realiza una secuencia de movimientos para alcanzar un nodo específico.
     *
     * @param movements Lista de movimientos para realizar
     * @return true si el movimiento fue exitoso, false si alguno falló
     */
    public boolean moveTo(LinkedList<Movement> movements) {
        for (Movement m : movements) {
            switch (m) {
                case Movement.Up() -> {
                   if (!this.toFather()) return false;
                }
                case Movement.Down(int child) -> {
                    if (this.toChild(child) != child) return false;
                }
            }
        }
        return true;
    }

    /**
     * Aplica una función a todos los nodos del árbol, descendiendo desde la raíz.
     *
     * @param k Función a aplicar
     */
    public void mapM(Function<a, Void> k) { this.root.downMap(k); }

    /**
     * Aplica una función a todos los nodos del árbol junto con resultados calculados para sus padres.
     *
     * @param k Función que utiliza el resultado del padre para calcular el valor del nodo actual
     * @param <b> Tipo del resultado producido
     */
    public <b> void mapWithFatherM(BiFunction<Maybe<b>, TreeCtxStrict<a>, b> k) { this.root.downMapWithFather(k); }
}
