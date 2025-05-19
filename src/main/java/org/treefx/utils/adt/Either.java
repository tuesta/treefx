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
     * @param l El valor contenido en el caso izquierdo
     */
    public record Left<a,b>(a l) implements Either<a, b> {
    };

    /**
     * Record que representa el caso derecho de la disyunción.
     * Se utiliza típicamente para representar éxitos o el segundo caso de una alternativa.
     *
     * @param <a> Tipo del valor izquierdo (no utilizado en esta variante)
     * @param <b> Tipo del valor derecho
     * @param r El valor contenido en el caso derecho
     */
    public record Right<a,b>(b r) implements Either<a, b> {
    };
}