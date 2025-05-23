package org.treefx.model.ziplist;

import org.treefx.utils.adt.Either;
import org.treefx.utils.adt.Maybe;

/**
 * Clase que representa una lista con "zippers" incrementales (ZipListInc).
 * <p>
 * Esta clase gestiona dos estructuras: una lista zipper a la izquierda (`left`) y una lista zipper estricta
 * a la derecha (`right`). Su objetivo es permitir modificaciones navegables en estas listas como
 * extraer elementos, desplazarse entre posiciones, establecer valores, y mostrar su contenido.
 * </p>
 *
 * @param <a> El tipo de datos que almacena la lista izquierda (left).
 * @param <b> El tipo de datos que almacena la lista derecha (right).
 */
public class ZipListInc<a, b> {
    public ZipList<a> left;
    public ZipListStrict<b> right;

    public ZipListInc(ZipList<a> left) {
        this.left = left;
        this.right = new ZipListStrict<>();
    }

    /**
     * Extrae el elemento actual de la lista izquierda o derecha.
     * <p>
     * Si el elemento actual en la izquierda es nulo, extrae el valor del lado derecho. Si hay
     * un elemento válido en la izquierda, lo devuelve directamente.
     * </p>
     *
     * @return Un objeto {@link Maybe} que contiene un {@link Either} con el elemento extraído del lado izquierdo
     * o derecho, o un {@link Maybe.Nothing} si no hay valores disponibles.
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
     * Avanza al siguiente elemento de ambas listas.
     * <p>
     * Si la lista izquierda puede avanzar, también inserta null en la derecha si esta no puede avanzar.
     * </p>
     *
     * @return true si el avance en la lista izquierda fue exitoso, false en caso contrario.
     */
    public boolean next() {
        if (this.left.next()) {
            if (!this.right.next()) this.right.insertR(null);
            return true;
        } else return false;
    }

    /**
     * Retrocede al elemento anterior en ambas listas.
     *
     * @return true si ambas listas pudieron retroceder con éxito, false en caso contrario.
     */
    public boolean prev() {
        return this.left.prev() && this.right.prev();
    }

    /**
     * Establece un nuevo valor en la posición actual de la lista derecha.
     * <p>
     * Si hay un elemento en la lista izquierda, lo sustituye por null y establece el nuevo
     * valor en la lista derecha.
     * </p>
     *
     * @param b Valor a establecer en la posición actual de la lista derecha.
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
     * Muestra el contenido de ambas listas (izquierda y derecha) en la salida estándar.
     */
    public void show() {
        System.out.println("left");
        this.left.show();
        System.out.println("right");
        this.right.show();
    }
}
