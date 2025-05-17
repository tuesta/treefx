package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;

import java.util.function.Function;

public class ZipListStrict<a> implements ZipList<a>{
    private Maybe<NodeLinkList<a>> mNode;
    private int size;
    private int index;
    private NodeLinkList<a> head = null;

    public int size() { return this.size; }

    @Override
    public void setCurrent(a a) {
        switch (this.mNode) {
            case Maybe.Nothing() -> {}
            case Maybe.Just(NodeLinkList<a> node) -> node.setCurrent(a);
        }
    }

    public ZipListStrict() {
        this.mNode = new Maybe.Nothing<>();
        this.size = 0;
        this.index = 0;
    }

    public int getIndex(NodeLinkList<a> node) {
        if (this.head == null) return 0;
        this.toStart();

        do {
            if (this.mNode.fromJust() == node) return this.index;
        } while (this.next());

        return 0;
    }

    public Maybe<NodeLinkList<a>> getMNode() { return this.mNode; }

    @Override
    public Maybe<a> extract() {
        return switch (this.mNode) {
            case Maybe.Nothing() -> new Maybe.Nothing<>();
            case Maybe.Just(NodeLinkList<a> node) -> new Maybe.Just<>(node.getCurrent());
        };
    }

    @Override
    public <x> ZipList<x> empty() { return new ZipListStrict<>(); }

    @Override
    public boolean next() {
        switch (this.mNode) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(NodeLinkList<a> node) -> {
                switch (node.getAfter()) {
                    case Maybe.Nothing() -> { return false; }
                    case Maybe.Just(NodeLinkList<a> ignored) -> {
                        this.mNode = node.getAfter();
                        this.index++;
                        return true;
                    }
                }
            }
        }
    }

    public boolean hasNext() {
        switch (this.mNode) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(NodeLinkList<a> node) -> {
                return node.getAfter().isJust();
            }
        }
    }

    @Override
    public boolean prev() {
        switch (this.mNode) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(NodeLinkList<a> node) -> {
                switch (node.getBefore()) {
                    case Maybe.Nothing() -> { return false; }
                    case Maybe.Just(NodeLinkList<a> ignored) -> {
                        this.mNode = node.getBefore();
                        this.index--;
                        return true;
                    }
                }
            }
        }
    }

    public boolean hasPrev() {
        switch (this.mNode) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(NodeLinkList<a> node) -> {
                return node.getBefore().isJust();
            }
        }
    }

    public void insertR(a val) {
        switch (this.mNode) {
            case Maybe.Nothing() -> {
                this.head = new NodeLinkList<>(val);
                this.mNode = new Maybe.Just<>(this.head);
                this.size = 1;
                this.index = 1;
            }
            case Maybe.Just(NodeLinkList<a> node) -> {
                var newNode = new Maybe.Just<>(new NodeLinkList<>(this.mNode, val, node.getAfter()));
                node.setAfter(newNode);
                this.mNode = newNode;
                this.size++;
                this.index++;
            }
        }
    }

    public int to(int i) {
        if (this.size == 0) return 0;
        if (i < 1) i = 1;
        if (i > this.size) i = this.size;

        while (i != this.index) {
            if (this.index < i) this.next();
            else this.prev();
        }

        return this.index;
    }

    private void toStart() {
        this.index = 1;
        this.mNode = new Maybe.Just<>(this.head);
    }

    public void mapM(Function<a, Void> k) {
        toStart();
        do {
            k.apply(this.mNode.fromJust().getCurrent());
        }
        while (this.next());
    }

    @Override
    public void show() {
        switch (this.mNode) {
            case Maybe.Nothing() -> System.out.println("--");
            case Maybe.Just(NodeLinkList<a> node) -> node.show();
        }
    }
}