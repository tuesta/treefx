package org.treefx.model.ziplist;

import org.treefx.utils.adt.Either;
import org.treefx.utils.adt.Maybe;

public class ZipListInc<a, b> {
    public ZipList<a> left;
    public ZipListStrict<b> right;

    public ZipListInc(ZipList<a> left) {
        this.left = left;
        this.right = new ZipListStrict<>();
    }

    public Maybe<Either<a, b>> extract() {
        switch (this.left.extract()) {
            case Maybe.Nothing() -> { return new Maybe.Nothing<>(); }
            case Maybe.Just(a l) -> {
                if (l == null) return new Maybe.Just<>(new Either.Right<>(this.right.extract().fromJust()));
                return new Maybe.Just<>(new Either.Left<>(l));
            }
        }
    }

    public boolean next() {
        if (this.left.next()) {
            if (!this.right.next()) this.right.insertR(null);
            return true;
        } else return false;
    }

    public boolean prev() { return this.left.prev() && this.right.prev(); }

    public void setCurrent(b b) {
        switch (this.left.extract()) {
            case Maybe.Nothing() -> { }
            case Maybe.Just(a ignored) -> {
                this.left.setCurrent(null);
                switch (this.right.extract()) {
                    case Maybe.Nothing() -> this.right.insertR(b);
                    case Maybe.Just(b v1) -> v1 = b;
                }
            }
        }
    }

    public void show() {
        System.out.println("left");
        this.left.show();
        System.out.println("right");
        this.right.show();
    }
}
