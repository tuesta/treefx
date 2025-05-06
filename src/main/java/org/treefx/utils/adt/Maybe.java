package org.treefx.utils.adt;

sealed public interface Maybe<a> {
    a fromJust();
    boolean isNothing();
    boolean isJust();

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