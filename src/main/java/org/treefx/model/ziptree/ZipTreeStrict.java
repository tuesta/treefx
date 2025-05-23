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
 * Clase que implementa una estructura de árbol denominada "ZipTreeStrict",
 * diseñada para la manipulación estructurada y navegable de árboles.
 * <p>
 * Un ZipTreeStrict es una estructura de datos que, además de contener los valores
 * normales de un árbol, también mantiene referencias navegables a padres,
 * hermanos y nodos hijos. Esto facilita operaciones como el viaje hacia arriba,
 * abajo, adelante, atrás, o la inserción de nuevos nodos.
 * </p>
 * <p>
 * La estructura mantiene un "contexto" actual, que hace referencia al nodo
 * actual en el árbol. Este contexto puede ser modificado con operaciones
 * específicas para desplazarse o manipular elementos.
 * </p>
 *
 * @param <a> El tipo de datos contenido en los nodos del árbol.
 */
public class ZipTreeStrict<a> {
    private TreeCtxStrict<a> ctx;
    private final TreeCtxStrict<a> root;

    /**
     * Construye un nuevo árbol ZipTreeStrict con el valor inicial especificado.
     *
     * @param val El valor inicial a almacenar en el nodo raíz del árbol.
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
     * Establece el contexto actual del árbol.
     *
     * @param ctx El contexto del árbol que se establecerá como el actual.
     */
    public void setCtx(TreeCtxStrict<a> ctx) {
        this.ctx = ctx;
    }

    /**
     * Obtiene el valor almacenado en el nodo actual del contexto.
     *
     * @return El valor contenido en el nodo actual.
     */
    public a extract() {
        return this.ctx.getValue();
    }

    /**
     * Devuelve el contexto actual del árbol.
     *
     * @return El contexto actual del árbol.
     */
    public TreeCtxStrict<a> getCtx() {
        return this.ctx;
    }

    /**
     * Cambia el contexto actual al padre del nodo actual.
     *
     * @return true si se cambió al padre, false si no hay padre.
     */
    public boolean toFather() {
        switch (this.ctx.getFather()) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(TreeCtxStrict<a> fatherCtx) -> {
                toNodeCtx(fatherCtx);
                return true;
            }
        }
    }

    /**
     * Cambia el contexto actual al nodo raíz del árbol.
     */
    public void toRoot() {
        this.ctx = this.root;
    }

    /**
     * Actualiza el contexto del árbol al contexto del nodo especificado.
     *
     * @param nodeCtx el nuevo contexto del nodo que se establecerá como el contexto actual
     */
    public void toNodeCtx(TreeCtxStrict<a> nodeCtx) {
        this.ctx = nodeCtx;
        var brothers = this.ctx.getBrothers();
        int ix = brothers.getIndex(nodeCtx.getCurrent());
        brothers.to(ix);
    }


    /**
     * Cambia el contexto actual al hijo en el índice especificado.
     *
     * @param ix El índice del hijo al que se desea cambiar el contexto.
     * @return El índice del nuevo hijo seleccionado, o 0 si falla.
     */
    public int toChild(int ix) {
        var children = this.ctx.getChildren();

        int newIx = children.to(ix);
        if (newIx == 0) return 0;

        this.ctx = children.extract().fromJust().snd();
        return newIx;
    }

    /**
     * Cambia el contexto actual al siguiente hermano del nodo actual.
     *
     * @return true si el cambio fue exitoso, false si no hay un siguiente hermano.
     */
    public boolean next() {
        if (this.ctx.getBrothers().next()) {
            this.ctx = this.ctx.getBrothers().getMNode().fromJust().getCurrent().snd();
            return true;
        }
        return false;
    }

    /**
     * Cambia el contexto actual al hermano previo del nodo actual.
     *
     * @return true si el cambio fue exitoso, false si no hay un hermano previo.
     */
    public boolean prev() {
        if (this.ctx.getBrothers().prev()) {
            this.ctx = this.ctx.getBrothers().getMNode().fromJust().getCurrent().snd();
            return true;
        }
        return false;
    }

    /**
     * Cambia el contexto al primer hijo del nodo actual.
     *
     * @return true si el cambio fue exitoso, false si no hay hijos.
     */
    public boolean down() {
        var children = this.ctx.getChildren();
        if (children.getMNode().isNothing()) return false;
        this.ctx = children.getMNode().fromJust().getCurrent().snd();
        return true;
    }

    /**
     * Inserta un nuevo hijo con el valor especificado al nodo actual.
     *
     * @param val El valor que tendrá el nuevo nodo hijo.
     */
    public void insertChild(a val) {
        var children = this.ctx.getChildren();
        var newCtx = new TreeCtxStrict<>(new Maybe.Just<>(this.ctx), null,children, new ZipListStrict<>());
        var current = new T.MkT<>(val, newCtx);
        children.insert(current);
        newCtx.setCurrent(children.getLast());
    }

    /**
     * Calcula una lista de movimientos relativos desde el nodo actual hasta el nodo objetivo.
     *
     * @param targetCtx El nodo objetivo al que se desea calcular la ruta.
     * @return Una lista de movimientos para llegar al nodo objetivo.
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
        while (!ancestorsCurrent.isEmpty() && !ancestorsTarget.isEmpty() &&
               ancestorsCurrent.peek() == ancestorsTarget.peek()) {
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
     * Mueve el contexto actual siguiendo una lista de movimientos especificada.
     *
     * @param movements Una lista de movimientos que se ejecutarán.
     * @return true si todos los movimientos fueron exitosos, false de lo contrario.
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
     * Aplica una función de forma descendente a todos los nodos del árbol desde la raíz.
     *
     * @param k La función a aplicar a cada nodo.
     */
    public void mapM(Function<a, Void> k) {
        this.root.downMap(k);
    }

    /**
     * Aplica una función de forma descendente a todos los nodos del árbol desde la raíz,
     * proporcionando información del padre de cada nodo.
     *
     * @param k   La función a aplicar, recibiendo información del nodo y su padre.
     * @param <b> El tipo del valor resultante de la función.
     */
    public <b> void mapWithFatherM(BiFunction<Maybe<b>, TreeCtxStrict<a>, b> k) {
        this.root.downMapWithFather(k);
    }
}
