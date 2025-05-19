package org.treefx.utils.adt;

/**
 * Interfaz sellada que representa un tipo de dato que puede contener un valor o no.
 * Implementa el patrón Maybe/Option común en programación funcional.
 *
 * @param <a> Tipo del valor que puede contener
 */
sealed public interface Maybe<a> {

     /**
     * Extrae el valor contenido en el Maybe.
     *
     * @return El valor contenido si es Just, null si es Nothing
     */
    a fromJust();

    /**
     * Verifica si la instancia es Nothing (sin valor).
     *
     * @return true si es Nothing, false si es Just
     */
    boolean isNothing();

    /**
     * Verifica si la instancia es Just (contiene un valor).
     *
     * @return true si es Just, false si es Nothing
     */
    boolean isJust();

    /**
     * Record que representa la ausencia de un valor.
     * Implementación de Maybe que indica que no hay valor presente.
     *
     * @param <a> Tipo del valor (no presente)
     */
    record Nothing<a>() implements Maybe<a> {
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
     * Record que representa la presencia de un valor.
     * Implementación de Maybe que contiene un valor real.
     *
     * @param <a> Tipo del valor contenido
     * @param value El valor almacenado
     */
    record Just<a>(a value) implements Maybe<a> {
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