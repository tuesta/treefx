package org.treefx.utils.adt;

/**
 * Interfaz genérica que representa una estructura de datos que puede o no contener un valor.
 *
 * @param <a> El tipo del valor contenido.
 */
sealed public interface Maybe<a> {
    /**
     * Obtiene el valor contenido en la instancia. Si la instancia es {@link Nothing}, retorna {@code null}.
     *
     * @return el valor contenido o {@code null} si no hay valor.
     */
    a fromJust();

    /**
     * Verifica si la instancia es de tipo {@link Nothing}, indicando la ausencia de un valor.
     *
     * @return {@code true} si no contiene un valor, de lo contrario {@code false}.
     */
    boolean isNothing();

    /**
     * Verifica si la instancia es de tipo {@link Just}, indicando que contiene un valor.
     *
     * @return {@code true} si contiene un valor, de lo contrario {@code false}.
     */
    boolean isJust();

    /**
     * Representa la ausencia de un valor.
     *
     * @param <a> El tipo genérico para mantener la consistencia con la interfaz {@link Maybe}.
     */
    record Nothing<a>() implements Maybe<a> {
        /**
         * Retorna {@code null} ya que no existe ningún valor para esta instancia.
         *
         * @return siempre {@code null}.
         */
        @Override
        public a fromJust() {
            return null;
        }
        @Override
        public boolean isNothing() { return true; }
        @Override
        public boolean isJust() { return false; }
    }

    /**
     * Representa una instancia que contiene un valor.
     *
     * @param <a> El tipo del valor contenido.
     */
    record Just<a>(a value) implements Maybe<a> {
        /**
         * Obtiene el valor contenido en esta instancia.
         *
         * @return el valor contenido.
         */
        @Override
        public a fromJust() {
            return value;
        }
        @Override
        public boolean isNothing() { return false; }
        @Override
        public boolean isJust() { return true; }
    }
}