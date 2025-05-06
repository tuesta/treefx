package org.treefx.model.ziptree;

import org.treefx.model.ziplist.ZipList;
import org.treefx.model.ziplist.ZipListInc;
import org.treefx.utils.adt.Either;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.function.Function;

public class ZipTreeLazy<b, a> implements ZipTree<a> {
    private final ZipTreeStrict<a> memo;
    private TreeF<b> context;
    private final Function<b, T<a, ZipList<b>>> generate;

    public ZipTreeLazy(ZipList<b> ctx, b state, Function<b, T<a, ZipList<b>>> generate) {
        T<a, ZipList<b>> node = generate.apply(state);
        this.memo = new ZipTreeStrict<>(new NodeLinkTree<>(node.fst(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>()));
        this.context = new TreeF<>(new Maybe.Nothing<>(), new ZipListInc<>(ctx.empty()), new ZipListInc<>(node.snd()));
        this.generate = generate;
    }

    @Override
    public a extract() { return this.memo.extract(); }

    @Override
    public boolean prev() {
        if (this.memo.prev()) {
            var brothersRef = this.context.getBrothers();
            brothersRef.prev();
            return switch (brothersRef.extract().fromJust()) {
                case Either.Left(b ignored) -> throw new Error("ZipListInc mismatch with memo, cannot be state");
                case Either.Right(TreeF<b> ignored) -> true;
            };
        } else return false;
    }

    @Override
    public boolean next() {
        if (this.memo.next()) {
            this.context.getBrothers().next();
            switch (this.context.getBrothers().extract().fromJust()) {
                case Either.Left(b ignored) -> throw new Error("ZipTreeLazy mismatch with memo, cannot be state");
                case Either.Right(TreeF<b> brothers) -> { this.context = brothers; return true; }
            }
        } else {
            var brothersRef = this.context.getBrothers();
            if (!brothersRef.next()) return false;
            switch (brothersRef.extract().fromJust()) {
                case Either.Left(b brotherState) -> {
                    T<a, ZipList<b>> newNode = this.generate.apply(brotherState);
                    TreeF<b> newCtx = new TreeF<>(this.context.getFather(), brothersRef, new ZipListInc<>(newNode.snd()));
                    brothersRef.setCurrent(newCtx);
                    this.context = newCtx;
                    this.memo.insertR(newNode.fst());
                    return true;
                }
                case Either.Right(TreeF<b> ignored) -> throw new Error("ZipTreeLazy mismatch with memo, memo outdated");
            }
        }
    }

    @Override
    public boolean top() {
        if (this.memo.top()) {
            this.context = this.context.getFather().fromJust();
            return true;
        } else return false;
    }

    @Override
    public boolean down() {
        if (this.memo.down()) {
            switch (this.context.getChildren().extract().fromJust()) {
                case Either.Left(b ignored) -> throw new Error("ZipTreeLazy mismatch with memo, cannot be state");
                case Either.Right(TreeF<b> children) -> { this.context = children; return true; }
            }
        } else {
            var childrenRef = this.context.getChildren();
            switch (childrenRef.extract()) {
                case Maybe.Nothing() -> { return false; }
                case Maybe.Just(Either<b, TreeF<b>> childrenEither) -> {
                    switch (childrenEither) {
                        case Either.Left(b childrenState) -> {
                            T<a, ZipList<b>> newNode = this.generate.apply(childrenState);
                            TreeF<b> newCtx = new TreeF<>(new Maybe.Just<>(this.context), childrenRef, new ZipListInc<>(newNode.snd()));
                            childrenRef.setCurrent(newCtx);
                            this.context = newCtx;
                            this.memo.insertD(newNode.fst());
                            return true;
                        }
                        case Either.Right(TreeF<b> ignored) -> throw new Error("ZipTreeLazy mismatch with memo, memo outdated");
                    }
                }
            }
        }
    }

    public void drawMemo() { System.out.println(this.memo.toString()); }
}