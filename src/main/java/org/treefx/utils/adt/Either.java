package org.treefx.utils.adt;
/**
 * Tipo de dato que representa una disyunción entre dos tipos.
 * Puede contener un valor de tipo 'a' (Left) o un valor de tipo 'b' (Right).
 *
 * @param <a> Tipo del valor izquierdo
 * @param <b> Tipo del valor derecho
 */
sealed public interface Either<a, b> {

    /**
     * Subclase que representa el valor izquierdo.
     *
     * @param <a> Tipo del valor izquierdo
     * @param <b> Tipo del valor derecho (no utilizado en esta variante)
     */
    public record Left<a,b>(a l) implements Either<a, b> {
    };

    /**
     * Subclase que representa el valor derecho.
     *
     * @param <a> Tipo del valor izquierdo (no utilizado en esta variante)
     * @param <b> Tipo del valor derecho
     */
    public record Right<a,b>(b r) implements Either<a, b> {
    };
}