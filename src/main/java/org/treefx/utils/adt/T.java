package org.treefx.utils.adt;

/**
 * Una interfaz genérica que representa un par de valores del tipo {@code a} y {@code b}.
 * Proporciona métodos para acceder al primer y segundo elemento del par.
 *
 * @param <a> El tipo del primer elemento del par.
 * @param <b> El tipo del segundo elemento del par.
 */
sealed public interface T<a, b> {

    /**
     * Obtiene el primer elemento del par.
     *
     * @return El primer elemento del par de tipo {@code a}.
     */
    public a fst();

    /**
     * Obtiene el segundo elemento del par.
     *
     * @return El segundo elemento del par de tipo {@code b}.
     */
    public b snd();

    /**
     * Un registro que implementa la interfaz {@link T} para representar un par con un valor del tipo {@code a} y un valor del tipo {@code b}.
     *
     * @param <a> El tipo del primer elemento.
     * @param <b> El tipo del segundo elemento.
     */
    public record MkT<a, b>(a a, b b) implements T<a, b> {

        /**
         * Constructor del registro {@code MkT}.
         *
         * @param a El valor del primer elemento.
         * @param b El valor del segundo elemento.
         */
        public MkT { }

        /**
         * Retorna el primer elemento del par.
         *
         * @return El primer elemento del par de tipo {@code a}.
         */
        @Override
        public a fst() { return a; }

        /**
         * Retorna el segundo elemento del par.
         *
         * @return El segundo elemento del par de tipo {@code b}.
         */
        @Override
        public b snd() { return b; }
    };
}