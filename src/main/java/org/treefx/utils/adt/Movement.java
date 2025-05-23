package org.treefx.utils.adt;

sealed public interface Movement {
    record Up() implements Movement {}
    record Down(int child) implements Movement {}

    Movement UP = new Up();
    static Movement DOWN(int child) { return new Down(child); }

    static String show(Movement m) {
        return switch (m) {
            case Down(int child) -> "Down("+ child +")";
            case Up() -> "Up";
        };
    }

    static Movement read(String s) {
        if (s.equals("Up")) return new Up();
        else if (s.startsWith("Down(") && s.endsWith(")")) {
            return new Down(Integer.parseInt(s.substring(5, s.length()-1)));
        } else {
            throw new RuntimeException("Parser error: Up | Down(<int>)");
        }
    }
}