package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;

/**
 * Una interfaz que representa una estructura de datos ZipList, una lista que permite navegar
 * y operar sobre elementos de una forma específica.
 *
 * @param <a> El tipo de elementos contenidos en la lista.
 */
public interface ZipList<a> {

    /**
     * Crea una nueva lista vacía de un tipo dado.
     *
     * @param <x> El tipo de los elementos en la nueva lista vacía.
     * @return Una lista vacía.
     */
    public <x> ZipList<x> empty();

    /**
     * Avanza al siguiente elemento de la lista.
     *
     * @return {@code true} si se avanzó con éxito, {@code false} si no es posible avanzar.
     */
    public boolean next();

    /**
     * Retrocede al elemento anterior de la lista.
     *
     * @return {@code true} si se retrocedió con éxito, {@code false} si no es posible retroceder.
     */
    public boolean prev();

    /**
     * Extrae el elemento actual de la lista, si existe.
     *
     * @return Un {@link Maybe} que contiene el elemento actual, o {@link Maybe.Nothing} si no hay un elemento actual.
     */
    public Maybe<a> extract();

    /**
     * Establece el elemento actual en un nuevo valor.
     *
     * @param newCurrent El nuevo valor para el elemento actual.
     */
    public void setCurrent(a newCurrent);

    /**
     * Muestra el contenido de la lista de una manera amigable para el usuario, generalmente con salida en la consola.
     */
    public void show();
}