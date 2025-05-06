package org.treefx.model.ziplist;


import org.treefx.utils.adt.Maybe;

public class ZipListStrict<a> implements ZipList<a>{
    private Maybe<NodeLinkList<a>> mNode;

    @Override
    public void setCurrent(a a) {
        switch (this.mNode) {
            case Maybe.Nothing() -> {}
            case Maybe.Just(NodeLinkList<a> node) -> node.setCurrent(a);
        }
    }

    public ZipListStrict() { this.mNode = new Maybe.Nothing<>(); }

    @Override
    public Maybe<a> extract() {
        return switch (this.mNode) {
            case Maybe.Nothing() -> new Maybe.Nothing<>();
            case Maybe.Just(NodeLinkList<a> node) -> new Maybe.Just<>(node.getCurrent());
        };
    }

    @Override
    public <x> ZipList<x> empty() {
        return new ZipListStrict<>();
    }

    @Override
    public boolean next() {
        switch (this.mNode) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(NodeLinkList<a> node) -> {
                switch (node.getAfter()) {
                    case Maybe.Nothing() -> { return false; }
                    case Maybe.Just(NodeLinkList<a> ignored) -> {
                        this.mNode = node.getAfter();
                        return true;
                    }
                }
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
                        return true;
                    }
                }
            }
        }
    }

    private Maybe<NodeLinkList<a>> singletonNode(a val) {
        return new Maybe.Just<>(new NodeLinkList<>(new Maybe.Nothing<>(), val, new Maybe.Nothing<>()));
    }

    public void insertR(a val) {
        switch (this.mNode) {
            case Maybe.Nothing() -> this.mNode = singletonNode(val);
            case Maybe.Just(NodeLinkList<a> node) -> {
                var newNode = new Maybe.Just<>(new NodeLinkList<>(this.mNode, val, node.getAfter()));
                node.setAfter(newNode);
                this.mNode = newNode;
            }
        }
    }

    @Override
    public void show() {
        switch (this.mNode) {
            case Maybe.Nothing() -> System.out.println("--");
            case Maybe.Just(NodeLinkList<a> node) -> node.show();
        }
    }
}