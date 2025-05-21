package org.treefx.model.ziptree;

import org.treefx.model.ziplist.ZipList;
import org.treefx.model.ziplist.ZipListInc;
import org.treefx.utils.adt.Either;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.function.Function;

/**
 * Clase que representa un árbol perezoso (lazy zip tree).
 *
 * Este árbol se construye dinámicamente a medida que es navegado,
 * utilizando un generador para calcular los nodos hijos en el momento
 * necesario.
 *
 * @param <b> Tipo del estado utilizado para generar nuevos nodos
 * @param <a> Tipo de los valores almacenados en el árbol
 */
public class ZipTreeLazy<b, a> implements ZipTree<a> {
    private final Tree<a> memo;
    private TreeCtxLazy<b> ctx;
    private final Function<b, T<a, ZipList<b>>> generate;

    /**
     * Constructor que inicializa un árbol perezoso.
     *
     * @param ctx     Lista zipper que representa el contexto inicial
     * @param state   Estado inicial utilizado para generar el nodo raíz
     * @param generate Función que genera los datos de un nodo a partir de un estado
     */
    public ZipTreeLazy(ZipList<b> ctx, b state, Function<b, T<a, ZipList<b>>> generate) {
        T<a, ZipList<b>> node = generate.apply(state);
        this.memo = new Tree<>(new NodeLinkTree<>(node.fst(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>()));
        this.ctx = new TreeCtxLazy<>(new Maybe.Nothing<>(), new ZipListInc<>(ctx.empty()), new ZipListInc<>(node.snd()));
        this.generate = generate;
    }

    /**
     * Obtiene el valor del nodo actual del árbol.
     *
     * @return Valor del nodo actual
     */
    @Override
    public a extract() { return this.memo.extract(); }

    /**
     * Navega al nodo hermano anterior en el mismo nivel.
     *
     * @return true si el movimiento fue exitoso, false si no hay hermano anterior
     */
    @Override
    public boolean prev() {
        if (this.memo.prev()) {
            var brothersRef = this.ctx.getBrothers();
            brothersRef.prev();
            return switch (brothersRef.extract().fromJust()) {
                case Either.Left(b ignored) -> throw new Error("ZipListInc mismatch with memo, cannot be state");
                case Either.Right(TreeCtxLazy<b> ignored) -> true;
            };
        } else return false;
    }
    /**
     * Se mueve al siguiente nodo en la estructura de árbol diferido.
     * Esta operación ajusta el contexto actual y actualiza la memoria asociada
     * según el estado de los nodos hermanos y sus valores extraídos.
     *
     * @return true si el movimiento al siguiente nodo se realiza correctamente; false en caso contrario
     * (p. ej., no hay más nodos en el contexto actual).
     *
     * @throws Error si hay una discrepancia con la estructura memorizada debido a un estado obsoleto.
     */
    @Override
    public boolean next() {
        if (this.memo.next()) {
            this.ctx.getBrothers().next();
            switch (this.ctx.getBrothers().extract().fromJust()) {
                case Either.Left(b ignored) -> throw new Error("ZipTreeLazy mismatch with memo, cannot be state");
                case Either.Right(TreeCtxLazy<b> brothers) -> { this.ctx = brothers; return true; }
            }
        } else {
            var brothersRef = this.ctx.getBrothers();
            if (!brothersRef.next()) return false;
            switch (brothersRef.extract().fromJust()) {
                case Either.Left(b brotherState) -> {
                    T<a, ZipList<b>> newNode = this.generate.apply(brotherState);
                    TreeCtxLazy<b> newCtx = new TreeCtxLazy<>(this.ctx.getFather(), brothersRef, new ZipListInc<>(newNode.snd()));
                    brothersRef.setCurrent(newCtx);
                    this.ctx = newCtx;
                    this.memo.insertR(newNode.fst());
                    return true;
                }
                case Either.Right(TreeCtxLazy<b> ignored) -> throw new Error("ZipTreeLazy mismatch with memo, memo outdated");
            }
        }
    }

    /**
     * Navega al nodo padre del nodo actual.
     *
     * @return true si el movimiento fue exitoso, false si no hay nodo padre
     */
    @Override
    public boolean top() {
        if (this.memo.top()) {
            this.ctx = this.ctx.getFather().fromJust();
            return true;
        } else return false;
    }


    /**
     * Se desplaza al primer nodo hijo del contexto del árbol actual, actualizando el estado y el contexto memorizados.
     * El meodo ajusta el contexto interno del árbol (`ctx`) y lo sincroniza con el estado memorizado (`memo`).
     * Si el estado memorizado ya ha descendido, el contexto hijo se extrae y se actualiza.
     * Si el estado memorizado no ha descendido, el metodo intenta generar un nuevo contexto hijo basado en el estado extraído y actualiza el memo en consecuencia.
     *
     * @return true si la navegación al nodo hijo fue exitosa,
     * false si no hay nodos hijos disponibles en el contexto actual.
     * @throws Error si hay una discrepancia entre el estado memorizado y el contexto del árbol actual.
     */
    @Override
    public boolean down() {
        if (this.memo.down()) {
            switch (this.ctx.getChildren().extract().fromJust()) {
                case Either.Left(b ignored) -> throw new Error("ZipTreeLazy mismatch with memo, cannot be state");
                case Either.Right(TreeCtxLazy<b> children) -> { this.ctx = children; return true; }
            }
        } else {
            var childrenRef = this.ctx.getChildren();
            switch (childrenRef.extract()) {
                case Maybe.Nothing() -> { return false; }
                case Maybe.Just(Either<b, TreeCtxLazy<b>> childrenEither) -> {
                    switch (childrenEither) {
                        case Either.Left(b childrenState) -> {
                            T<a, ZipList<b>> newNode = this.generate.apply(childrenState);
                            TreeCtxLazy<b> newCtx = new TreeCtxLazy<>(new Maybe.Just<>(this.ctx), childrenRef, new ZipListInc<>(newNode.snd()));
                            childrenRef.setCurrent(newCtx);
                            this.ctx = newCtx;
                            this.memo.insertD(newNode.fst());
                            return true;
                        }
                        case Either.Right(TreeCtxLazy<b> ignored) -> throw new Error("ZipTreeLazy mismatch with memo, memo outdated");
                    }
                }
            }
        }
    }

    /**
     * Imprime una representación del árbol almacenado en memoria.
     */
    public void drawMemo() { System.out.println(this.memo.toString()); }
}