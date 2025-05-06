package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.function.Function;

public class ZipListLazy<b, a> implements ZipList<a> {
    private Maybe<b> state;
    private final ZipListStrict<a> memo;
    private final Function<b, Maybe<T<a, b>>> generate;

    public ZipListLazy(b state, Function<b, Maybe<T<a, b>>> generate) {
        this.state = new Maybe.Just<>(state);
        this.generate = generate;
        this.memo = new ZipListStrict<>();
    }

    public ZipListLazy() {
        this.state = new Maybe.Nothing<>();
        this.generate = null;
        this.memo = new ZipListStrict<>();

    }

    @Override
    public <x> ZipList<x> empty() { return new ZipListLazy<>(); }

    public Maybe<a> extract() {
        switch (this.memo.extract()) {
            case Maybe.Just<a> v -> { return v; }
            case Maybe.Nothing<a>() -> {
                if (this.state instanceof Maybe.Nothing) return new Maybe.Nothing<>();
                this.next();
                return this.extract();
            }
        }
    }

    @Override
    public void setCurrent(a newCurrent) { this.memo.setCurrent(newCurrent); }

    @Override
    public boolean next() {
        if (this.memo.next()) return true;
        switch (this.state) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(b b) -> {
                switch (this.generate.apply(b)) {
                    case Maybe.Nothing() -> {
                        this.state = new Maybe.Nothing<>();
                        return false;
                    }
                    case Maybe.Just(T.MkT(a a, b newState)) -> {
                        this.state = new Maybe.Just<>(newState);
                        this.memo.insertR(a);
                        return true;
                    }
                }
            }
        }
    }

    @Override
    public boolean prev() { return this.memo.prev(); }

    @Override
    public void show() { this.memo.show(); }
}