package org.treefx.utils.adt;
/**
 * Clase que representa una tupla de dos elementos.
 * Útil para retornar dos valores de diferentes tipos desde una función.
 *
 * @param <a> Tipo del primer elemento
 * @param <b> Tipo del segundo elemento
 */
sealed public interface T<a, b> {

    /**
     * Obtiene el primer elemento de la tupla.
     *
     * @return El primer elemento de tipo a
     */
    public a fst();

    /**
     * Obtiene el segundo elemento de la tupla.
     *
     * @return El segundo elemento de tipo b
     */
    public b snd();

    /**
     * Record que implementa la tupla con dos elementos.
     * Proporciona una implementación concreta de la tupla T.
     *
     * @param <a> Tipo del primer elemento
     * @param <b> Tipo del segundo elemento
     * @param first Primer elemento de la tupla
     * @param second Segundo elemento de la tupla
     */
    public record MkT<a, b>(a first, b second) implements T<a, b> {
        @Override
        public a fst() { return first; }

        @Override
        public b snd() { return second; }
    };
}