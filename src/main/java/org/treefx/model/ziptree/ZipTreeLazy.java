package org.treefx.model.ziptree;

import org.treefx.model.ziplist.ZipList;
import org.treefx.model.ziplist.ZipListInc;
import org.treefx.utils.adt.Either;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.function.Function;

/**
 * Representa un árbol zipper (ZipTree) perezoso.
 * <p>
 * El "ZipTreeLazy" utiliza un árbol de memo (copia del árbol en memoria),
 * junto con un contexto que puede regenerar nodos en el momento,
 * utilizando una función generadora basada en un estado.
 *
 * @param <b> Tipo del estado usado para generar nodos del árbol.
 * @param <a> Tipo de los valores almacenados en los nodos del árbol.
 */
public class ZipTreeLazy<b, a> implements ZipTree<a> {
    private final Tree<a> memo;
    private TreeCtxLazy<b> ctx;
    private final Function<b, T<a, ZipList<b>>> generate;

    /**
     * Construye un árbol zipper perezoso basado en el estado inicial,
     * una lista zipper de contexto y una función generadora.
     *
     * @param ctx      Una lista zipper que actúa como base para la generación del árbol.
     * @param state    El estado inicial usado para generar el primer nodo del árbol.
     * @param generate Función generadora de nodos del árbol con base en el estado.
     */
    public ZipTreeLazy(ZipList<b> ctx, b state, Function<b, T<a, ZipList<b>>> generate) {
        T<a, ZipList<b>> node = generate.apply(state);
        this.memo = new Tree<>(new NodeLinkTree<>(node.fst(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>()));
        this.ctx = new TreeCtxLazy<>(new Maybe.Nothing<>(), new ZipListInc<>(ctx.empty()), new ZipListInc<>(node.snd()));
        this.generate = generate;
    }

    /**
     * Extrae el valor del nodo actual en el memo.
     *
     * @return El valor del nodo actual.
     */
    @Override
    public a extract() {
        return this.memo.extract();
    }

    /**
     * Se mueve al nodo anterior dentro del nivel actual del árbol.
     *
     * @return {@code true} si se movió exitosamente, de lo contrario {@code false}.
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
     * Se mueve al siguiente nodo hermano en el nivel actual.
     * Si el nodo aún no existe, lo genera usando la función generadora asociada.
     *
     * @return {@code true} si se movió exitosamente, de lo contrario {@code false}.
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
     * Se mueve al nodo padre del nodo actual si existe.
     *
     * @return {@code true} si se movió exitosamente, de lo contrario {@code false}.
     */
    @Override
    public boolean top() {
        if (this.memo.top()) {
            this.ctx = this.ctx.getFather().fromJust();
            return true;
        } else return false;
    }

    /**
     * Se mueve al nodo hijo del nodo actual.
     * Si el nodo hijo aún no existe, lo genera usando la función generadora asociada.
     *
     * @return {@code true} si se movió exitosamente, de lo contrario {@code false}.
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
}