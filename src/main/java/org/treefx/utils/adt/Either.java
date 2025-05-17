package org.treefx.utils.adt;

sealed public interface Either<a, b> {
    public record Left<a,b>(a l) implements Either<a, b> {
    };
    public record Right<a,b>(b r) implements Either<a, b> {
    };
}