package org.treefx.utils.adt;

/**
 * Interfaz sellada que representa un movimiento en una estructura de datos jerárquica.
 * Puede ser ascendente (Up) o descendente (Down) a un hijo específico.
 */
sealed public interface Movement {

    /**
     * Representa un movimiento hacia arriba en la jerarquía.
     */
    record Up() implements Movement {
    }

    /**
     * Representa un movimiento hacia abajo en la jerarquía hacia un hijo específico.
     *
     * @param child el índice del hijo hacia el que se realiza el movimiento
     */
    record Down(int child) implements Movement {}

    Movement UP = new Up();
    static Movement DOWN(int child) { return new Down(child); }


    /**
     * Devuelve una representación en cadena del movimiento especificado.
     *
     * @param m el movimiento que desea representarse en cadena
     * @return la representación en cadena del movimiento, en formato "Up" o "Down(<índice>)"
     */
    static String show(Movement m) {
        return switch (m) {
            case Down(int child) -> "Down(" + child + ")";
            case Up() -> "Up";
        };
    }

    /**
     * Lee y analiza un movimiento desde su representación en cadena.
     *
     * @param s la cadena que representa el movimiento, en formato "Up" o "Down(<índice>)"
     * @return una instancia de Movement correspondiente a la cadena suministrada
     * @throws RuntimeException si la cadena no tiene un formato válido
     */
    static Movement read(String s) {
        if (s.equals("Up")) return new Up();
        else if (s.startsWith("Down(") && s.endsWith(")")) {
            return new Down(Integer.parseInt(s.substring(5, s.length() - 1)));
        } else {
            throw new RuntimeException("Parser error: Up | Down(<int>)");
        }
    }
}