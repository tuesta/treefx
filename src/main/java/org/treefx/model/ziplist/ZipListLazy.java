package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.function.Function;

/**
 * Implementación de una lista zipper (ZipList) con evaluación perezosa.
 * Permite recorrer y construir la lista bajo demanda conforme se navega con el cursor.
 * El siguiente elemento se genera a partir de un estado y una función de generación.
 *
 * @param <b> Tipo del estado usado para la generación perezosa de elementos.
 * @param <a> Tipo de los elementos almacenados en la lista.
 */
public class ZipListLazy<b, a> implements ZipList<a> {

    /**
     * Estado interno usado para generar el siguiente elemento.
     * Si es Nothing, indica que no hay más elementos por generar.
     */
    private Maybe<b> state;

    /**
     * Lista zipper que representa el contexto actual.
     */
    private final ZipListStrict<a> memo;

    /**
     * Función de generación. Recibe el estado actual y, si es posible,
     * retorna un nuevo par (elemento generado, siguiente estado).
     */
    private final Function<b, Maybe<T<a, b>>> generate;

    /**
     * Constructor principal de la clase.
     *
     * @param state Estado inicial para la generación de la secuencia.
     * @param generate Función generadora que, dado un estado, produce el siguiente elemento (y nuevo estado).
     */
    public ZipListLazy(b state, Function<b, Maybe<T<a, b>>> generate) {
        this.state = new Maybe.Just<>(state);
        this.generate = generate;
        this.memo = new ZipListStrict<>();
    }

    /**
     * Constructor para crear una lista vacía, sin estado ni función de generación.
     * Útil para operaciones genéricas y para representar el valor vacío/empty.
     */
    public ZipListLazy() {
        this.state = new Maybe.Nothing<>();
        this.generate = null;
        this.memo = new ZipListStrict<>();

    }

    /**
     * Crea una nueva lista vacía del tipo especificado.
     *
     * @param <x> Tipo de los elementos de la lista vacía.
     * @return Nueva instancia vacía de {@code ZipListLazy}.
     */
    @Override
    public <x> ZipList<x> empty() { return new ZipListLazy<>(); }

    /**
     * Extrae el elemento actual de la lista.
     * Si el elemento aún no ha sido generado, lo intenta generar usando la función y el estado internos.
     *
     * @return Un {@code Maybe} con el elemento actual si existe, o {@code Nothing} si no hay elemento actual.
     */
    public Maybe<a> extract() {
        switch (this.memo.extract()) {
            case Maybe.Just<a> v -> { return v; }
            case Maybe.Nothing<a>() -> {
                if (this.state instanceof Maybe.Nothing) return new Maybe.Nothing<>();
                this.next();
                return this.extract();
            }
        }
    }

    /**
     * Modifica el valor almacenado en la posición actual del cursor.
     *
     * @param newCurrent Nuevo valor para el elemento actual.
     */
    @Override
    public void setCurrent(a newCurrent) { this.memo.setCurrent(newCurrent); }

    /**
     * Avanza el cursor hacia el siguiente elemento de la lista.
     * Si el siguiente elemento aún no ha sido generado, lo crea usando la función de generación y lo almacena (memoización).
     *
     * @return {@code true} si se avanzó correctamente; {@code false} si no hay siguiente elemento.
     */
    @Override
    public boolean next() {
        if (this.memo.next()) return true;
        switch (this.state) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(b b) -> {
                switch (this.generate.apply(b)) {
                    case Maybe.Nothing() -> {
                        this.state = new Maybe.Nothing<>();
                        return false;
                    }
                    case Maybe.Just(T.MkT(a a, b newState)) -> {
                        this.state = new Maybe.Just<>(newState);
                        this.memo.insertR(a);
                        return true;
                    }
                }
            }
        }
    }

    /**
     * Retrocede el cursor al elemento anterior ya memoizado.
     * No permite regresar hasta estados no generados/lazy, solamente a elementos ya existentes.
     *
     * @return {@code true} si se retrocedió correctamente; {@code false} si no hay anterior.
     */
    @Override
    public boolean prev() { return this.memo.prev(); }

    /**
     * Muestra la lista por consola usando la implementación estricta.
     * Imprime todos los elementos memoizados/ya generados.
     */
    @Override
    public void show() { this.memo.show(); }
}