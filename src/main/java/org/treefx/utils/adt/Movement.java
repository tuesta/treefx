package org.treefx.utils.adt;

sealed public interface Movement {
    public record Up() implements Movement {}
    public record Down(int child) implements Movement {}

    public static Movement UP = new Up();
    public static Movement DOWN(int child) { return new Down(child); }

    public static String show(Movement m) {
        return switch (m) {
            case Down(int child) -> "Down("+ child +")";
            case Up() -> "Up";
        };
    };

    public static Movement read(String s) {
        if (s == "Up") return new Up();
        else if (s.startsWith("Down(") && s.endsWith(")")) {
            return new Down(Integer.parseInt(s.substring(5, s.length()-1)));
        } else {
            throw new RuntimeException("Parser error: Up | Down(<int>)");
        }
    }
}