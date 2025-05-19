package org.treefx.utils.adt;


/**
 * Interfaz sellada que representa movimientos en una estructura jerárquica.
 * Define operaciones para moverse hacia arriba o hacia abajo en la estructura.
 */
sealed public interface Movement {

    /**
     * Representa un movimiento hacia arriba en la estructura.
     */
    public record Up() implements Movement {}


    /**
     * Representa un movimiento hacia abajo en la estructura hacia un hijo específico.
     *
     * @param child El índice del hijo al que moverse
     */
    public record Down(int child) implements Movement {}

    /**
     * Constante que representa un movimiento hacia arriba.
     */
    public static Movement UP = new Up();

    /**
     * Crea un nuevo movimiento hacia abajo hacia el hijo especificado.
     *
     * @param child El índice del hijo al que moverse
     * @return Un nuevo movimiento Down con el índice especificado
     */
    public static Movement DOWN(int child) { return new Down(child); }

    /**
     * Convierte un movimiento a su representación en cadena.
     * Los movimientos Up se representan como "Up" y los Down como "Down(n)" donde n es el índice del hijo.
     *
     * @param m El movimiento a convertir
     * @return La representación en cadena del movimiento
     */
    public static String show(Movement m) {
        return switch (m) {
            case Down(int child) -> "Down("+ child +")";
            case Up() -> "Up";
        };
    };

    /**
     * Parsea una cadena para crear un objeto Movement.
     * Acepta "Up" para movimientos hacia arriba y "Down(n)" para movimientos hacia abajo,
     * donde n es un número entero que representa el índice del hijo.
     *
     * @param s La cadena a parsear
     * @return El Movement correspondiente
     * @throws RuntimeException si la cadena no tiene el formato correcto
     */
    public static Movement read(String s) {
        if (s == "Up") return new Up();
        else if (s.startsWith("Down(") && s.endsWith(")")) {
            return new Down(Integer.parseInt(s.substring(5, s.length()-1)));
        } else {
            throw new RuntimeException("Parser error: Up | Down(<int>)");
        }
    }
}