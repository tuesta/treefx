package org.treefx.model.ziplist;

import org.treefx.utils.adt.Either;
import org.treefx.utils.adt.Maybe;

/**
 * Implementación incremental/mixta de una estructura zipper que compone dos listas:
 * una izquierda (de tipo {@code ZipList<a>}) y una derecha estricta (de tipo {@code ZipListStrict<b>}).
 * Al navegar, primero recorre la izquierda y al agotarse, va generando entradas en la derecha.
 *
 * @param <a> Tipo de los elementos de la lista izquierda.
 * @param <b> Tipo de los elementos de la lista derecha.
 */
public class ZipListInc<a, b> {

    /**
     * Lista izquierda del zipper, de cualquier implementación de {@code ZipList}.
     */
    public ZipList<a> left;

    /**
     * Lista derecha, implementación estricta, usada para almacenar valores tipo {@code b}.
     */
    public ZipListStrict<b> right;


    /**
     * Crea un nuevo zipper incremental a partir de una lista izquierda dada.
     * La lista derecha comienza vacía.
     *
     * @param left Lista izquierda inicial.
     */
    public ZipListInc(ZipList<a> left) {
        this.left = left;
        this.right = new ZipListStrict<>();
    }

    /**
     * Extrae el elemento actual.
     * Si la lista izquierda tiene un elemento actual no nulo, lo envuelve en {@code Either.Left}.
     * Si el elemento de la izquierda es nulo, extrae de la derecha y lo envuelve en {@code Either.Right}.
     * Si ninguna lista tiene elemento actual, devuelve {@code Nothing}.
     *
     * @return {@code Maybe<Either<a, b>>} con el valor actual de la izquierda o derecha, o {@code Nothing}.
     */
    public Maybe<Either<a, b>> extract() {
        switch (this.left.extract()) {
            case Maybe.Nothing() -> { return new Maybe.Nothing<>(); }
            case Maybe.Just(a l) -> {
                if (l == null) return new Maybe.Just<>(new Either.Right<>(this.right.extract().fromJust()));
                return new Maybe.Just<>(new Either.Left<>(l));
            }
        }
    }

    /**
     * Avanza el cursor una posición.
     * Intenta avanzar la izquierda. Si lo consigue, garantiza consistencia en la derecha
     * agregando una nueva posición si es necesario. Si no puede, retorna {@code false}.
     *
     * @return {@code true} si el cursor avanzó; {@code false} en caso contrario.
     */
    public boolean next() {
        if (this.left.next()) {
            if (!this.right.next()) this.right.insertR(null);
            return true;
        } else return false;
    }

    /**
     * Retrocede el cursor una posición, tanto en izquierda como en derecha.
     * Solo retorna {@code true} si ambas pueden retroceder.
     *
     * @return {@code true} si ambas listas pueden retroceder; {@code false} en caso contrario.
     */
    public boolean prev() { return this.left.prev() && this.right.prev(); }

    /**
     * Actualiza el contenido de la posición actual en la lista derecha.
     * Si la izquierda tiene valor actual, lo pone en nulo, y en la posición correspondiente
     * de la derecha almacena el nuevo valor dado (insertando una nueva posición si es necesario).
     *
     * @param b Nuevo valor a guardar en la posición derecha correspondiente.
     */
    public void setCurrent(b b) {
        switch (this.left.extract()) {
            case Maybe.Nothing() -> { }
            case Maybe.Just(a ignored) -> {
                this.left.setCurrent(null);
                switch (this.right.extract()) {
                    case Maybe.Nothing() -> this.right.insertR(b);
                    case Maybe.Just(b v1) -> v1 = b;
                }
            }
        }
    }

    /**
     * Muestra ambas listas por consola: primero la izquierda, luego la derecha.
     * Útil para depuración y visualización sencilla del contenido interno.
     */
    public void show() {
        System.out.println("left");
        this.left.show();
        System.out.println("right");
        this.right.show();
    }
}
