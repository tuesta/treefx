package org.treefx.utils.adt;

/**
 * Representa un contenedor que puede contener un valor de dos tipos posibles (izquierdo o derecho).
 *
 * @param <a> El tipo del valor izquierdo.
 * @param <b> El tipo del valor derecho.
 */
sealed public interface Either<a, b> {
    /**
     * Representa el caso en el que un valor está presente en la parte izquierda del contenedor.
     *
     * @param <a> El tipo del valor izquierdo.
     * @param <b> El tipo del valor derecho.
     * @param l   El valor almacenado en la parte izquierda.
     */
    public record Left<a, b>(a l) implements Either<a, b> {}

    /**
     * Representa el caso en el que un valor está presente en la parte derecha del contenedor.
     *
     * @param <a> El tipo del valor izquierdo.
     * @param <b> El tipo del valor derecho.
     * @param r   El valor almacenado en la parte derecha.
     */
    public record Right<a, b>(b r) implements Either<a, b> {}
}