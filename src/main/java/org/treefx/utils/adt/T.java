package org.treefx.utils.adt;

sealed public interface T<a, b> {
    public a fst();
    public b snd();

    public record MkT<a, b>(a a, b b) implements T<a, b> {
        @Override
        public a fst() { return a; }

        @Override
        public b snd() { return b; }
    };
}