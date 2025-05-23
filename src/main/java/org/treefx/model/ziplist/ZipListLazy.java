package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.function.Function;

/**
 * Clase `ZipListLazy` implementa una lista perezosa (lazy) que permite manejar
 * operaciones sobre una secuencia de elementos generados bajo demanda.
 * <p>
 * `ZipListLazy` combina una función generadora con un estado inicial para
 * construir elementos secuenciales solo cuando es necesario, conservando
 * los elementos generados en una lista estricta (memo).
 * </p>
 *
 * @param <b> El tipo del estado utilizado para generar nuevos elementos.
 * @param <a> El tipo de los elementos almacenados en la lista.
 */
public class ZipListLazy<b, a> implements ZipList<a> {
    private Maybe<b> state;
    private final ZipListStrict<a> memo;
    private final Function<b, Maybe<T<a, b>>> generate;

    /**
     * Constructor que inicializa un `ZipListLazy` con un estado inicial y una
     * función de generación.
     *
     * @param state    El estado inicial utilizado para generar la lista.
     * @param generate Una función que, dado un estado, produce un nuevo
     *                 elemento opcional y un estado actualizado.
     */
    public ZipListLazy(b state, Function<b, Maybe<T<a, b>>> generate) {
        this.state = new Maybe.Just<>(state);
        this.generate = generate;
        this.memo = new ZipListStrict<>();
    }

    /**
     * Constructor predeterminado para un `ZipListLazy` vacío.
     * La lista generada será inicialmente vacía.
     */
    public ZipListLazy() {
        this.state = new Maybe.Nothing<>();
        this.generate = null;
        this.memo = new ZipListStrict<>();

    }

    /**
     * Crea una nueva lista vacía del tipo `ZipListLazy`.
     *
     * @param <x> El tipo de los elementos en la nueva lista vacía.
     * @return Una nueva instancia de `ZipListLazy` vacía.
     */
    @Override
    public <x> ZipList<x> empty() {
        return new ZipListLazy<>();
    }

    /**
     * Obtiene el elemento actual de la lista. Si aún no se ha generado,
     * intenta generarlo en base al estado y la función generadora.
     *
     * @return Un objeto `Maybe` que contiene el elemento actual, o `Nothing`
     * si la lista está vacía.
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
     * Establece un nuevo elemento como el actual en la lista.
     *
     * @param newCurrent El nuevo elemento a establecer como actual.
     */
    @Override
    public void setCurrent(a newCurrent) {
        this.memo.setCurrent(newCurrent);
    }

    /**
     * Avanza al siguiente elemento disponible en la lista. Si el próximo elemento
     * no ha sido generado todavía, intenta generarlo en base al estado actual
     * y la función generadora.
     *
     * @return `true` si se pudo avanzar al siguiente elemento; `false` si ya no
     * hay más elementos.
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
     * Retrocede al elemento anterior en la lista estricta (`memo`).
     *
     * @return `true` si se pudo retroceder al elemento previo; `false` si ya
     * no hay elementos previos.
     */
    @Override
    public boolean prev() {
        return this.memo.prev();
    }

    /**
     * Imprime la representación actual de la lista estricta (`memo`)
     * en la consola.
     */
    @Override
    public void show() {
        this.memo.show();
    }
}