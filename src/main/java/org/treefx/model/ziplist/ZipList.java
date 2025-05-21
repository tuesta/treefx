package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;

/**
 * Interfaz que define el contrato para una estructura zipper genérica:
 * una lista que mantiene un cursor móvil que permite recorrer y modificar el elemento actual,
 * navegando eficientemente en ambas direcciones.
 *
 * @param <a> Tipo de los elementos almacenados en la lista.
 */
public interface ZipList<a> {

    /**
     * Genera y retorna una lista zipper vacía del tipo especificado.
     * Es útil para construir listas de cualquier tipo o para retornar una representación de lista vacía.
     *
     * @param <x> Tipo de los elementos de la nueva lista vacía.
     * @return Nueva instancia vacía de {@code ZipList<x>}.
     */
    public <x> ZipList<x> empty();


    /**
     * Avanza el cursor de la lista hacia la siguiente posición, si existe.
     *
     * @return {@code true} si el cursor avanzó correctamente; {@code false} si no hay siguiente.
     */
    public boolean next();

    /**
     * Retrocede el cursor de la lista hacia la posición anterior, si existe.
     *
     * @return {@code true} si el cursor retrocedió correctamente; {@code false} si no hay anterior.
     */
    public boolean prev();

    /**
     * Extrae el elemento en la posición actual indicada por el cursor.
     *
     * @return {@code Maybe<a>} que contiene el elemento actual si existe, o {@code Nothing} si la lista está vacía.
     */
    public Maybe<a> extract();

    /**
     * Modifica el valor del elemento actual de la lista, en la posición del cursor.
     *
     * @param newCurrent Nuevo valor para almacenar en la posición actual.
     */
    public void setCurrent(a newCurrent);

    /**
     * Muestra el contenido de la lista por consola,
     * normalmente indicando el punto actual (cursor) para depuración o visualización.
     */
    public void show();
}