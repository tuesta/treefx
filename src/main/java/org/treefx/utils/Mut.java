package org.treefx.utils;

/**
 * Clase de contenedor mutable para almacenar un valor de tipo genérico.
 *
 * @param <a> El tipo del valor que será almacenado.
 */
public class Mut<a> {
    /**
     * Valor almacenado en este contenedor mutable.
     */
    public a val;

    /**
     * Constructor que inicializa el contenedor mutable con un valor inicial.
     *
     * @param val El valor inicial que será almacenado.
     */
    public Mut(a val) {
        this.val = val;
    }
}
