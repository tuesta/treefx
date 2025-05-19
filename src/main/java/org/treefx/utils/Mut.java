package org.treefx.utils;

/**
 * Clase contenedora mutable genérica que envuelve un valor de cualquier tipo.
 * Útil para compartir un valor mutable entre diferentes partes del código o
 * para pasar referencias mutables a métodos.
 *
 * @param <a> Tipo del valor contenido
 */
public class Mut<a> {

    /**
     * El valor contenido en este contenedor mutable.
     * Este campo es público para permitir acceso directo al valor.
     */
    public a val;

    /**
     * Constructor que crea un nuevo contenedor mutable con el valor especificado.
     *
     * @param val Valor inicial a almacenar en el contenedor
     */
    public Mut(a val) {
        this.val = val;
    }
}
