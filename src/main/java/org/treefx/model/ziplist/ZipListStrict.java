package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;

import java.util.function.Consumer;

public class ZipListStrict<a> implements ZipList<a>{
    private Maybe<NodeLinkList<a>> mNode;
    private int size;
    private int index;
    private NodeLinkList<a> head = null;
    private NodeLinkList<a> last = null;

    public NodeLinkList<a> getHead() { return head; }

    public NodeLinkList<a> getLast() { return last; }

    public int size() { return this.size; }
    public int getIx() { return this.index; }


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
                var newNode = new NodeLinkList<>(val);
                this.head = newNode;
                this.last = newNode;
                this.mNode = new Maybe.Just<>(newNode);
                this.size = 1;
                this.index = 1;
            }
            case Maybe.Just(NodeLinkList<a> node) -> {
                var newNode = new NodeLinkList<>(this.mNode, val, node.getAfter());
                var mnewNode = new Maybe.Just<>(newNode);
                node.setAfter(mnewNode);
                this.mNode = mnewNode;
                this.size++;
                this.index++;
                if (this.size == this.index) this.last = newNode;
            }
        }
    }

    public void insert(a val) {
        int ix = this.index;
        if (ix != 0) this.toEnd();
        insertR(val);
        this.to(ix);
    }

    public Maybe<a> deleteCurrent() {
        return switch (this.mNode) {
            case Maybe.Nothing() -> new Maybe.Nothing<>();
            case Maybe.Just(NodeLinkList<a> node) -> {
                switch (node.getBefore()) {
                    case Maybe.Nothing() -> {
                        switch (node.getAfter()) {
                            case Maybe.Nothing() -> {
                                this.head = null;
                                this.last = null;
                                this.size = 0;
                                this.index = 0;
                                this.mNode = new Maybe.Nothing<>();
                            }
                            case Maybe.Just(NodeLinkList<a> nodeAfter) -> {
                                this.head = nodeAfter;
                                this.size--;
                                nodeAfter.setBefore(new Maybe.Nothing<>());
                                this.mNode = node.getAfter();
                            }
                        }
                    }
                    case Maybe.Just(NodeLinkList<a> nodeBefore) -> {
                        switch (node.getAfter()) {
                            case Maybe.Nothing() -> {
                                this.last = nodeBefore;
                                this.size--;
                                this.index--;
                                nodeBefore.setAfter(new Maybe.Nothing<>());
                                this.mNode = node.getBefore();
                            }
                            case Maybe.Just(NodeLinkList<a> nodeAfter) -> {
                                nodeBefore.setAfter(node.getAfter());
                                this.size--;
                                this.index--;
                                this.mNode = node.getBefore();
                            }
                        }
                    }
                }
                yield new Maybe.Just<>(node.getCurrent());
            }
        };
    }

    public Maybe<a> delete(int i) {
        int ix = this.index;
        this.to(i);
        Maybe<a> deleted = new Maybe.Nothing<>();
        if (this.index == i) deleted = this.deleteCurrent();
        this.to(ix);
        return deleted;
    }

    public int to(int i) {
        if (this.size == 0) return 0;
        if (i < 1) i = 1;
        if (i > this.size) i = this.size;

        int closenessToHead = i;
        int closenessToLast = this.size - i;
        int closenessToCurrent = Math.abs(this.index - i);

        if (closenessToHead < closenessToLast && closenessToHead < closenessToCurrent) {
            this.toStart();
        } else if (closenessToLast < closenessToCurrent) {
            this.toEnd();
        }

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

    private void toEnd() {
        this.index = this.size;
        this.mNode = new Maybe.Just<>(this.last);
    }

    public void mapM(Consumer<a> k) {
        if (size == 0) return;

        toStart();
        do {
            k.accept(this.mNode.fromJust().getCurrent());
        }
        while (this.next());
        this.toStart();
    }

    @Override
    public void show() {
        switch (this.mNode) {
            case Maybe.Nothing() -> System.out.println("--");
            case Maybe.Just(NodeLinkList<a> node) -> node.show();
        }
    }
}