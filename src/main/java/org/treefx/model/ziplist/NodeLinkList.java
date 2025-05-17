package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;

public class NodeLinkList<a> {
    private Maybe<NodeLinkList<a>> before;
    private a current;
    private Maybe<NodeLinkList<a>> after;

    public NodeLinkList(Maybe<NodeLinkList<a>> before, a current, Maybe<NodeLinkList<a>> after) {
        this.before = before;
        this.current = current;
        this.after = after;
    }

    public NodeLinkList(a current) {
        this(new Maybe.Nothing<>(), current, new Maybe.Nothing<>());
    }

    public Maybe<NodeLinkList<a>> getBefore() { return before; }

    public void setBefore(Maybe<NodeLinkList<a>> before) { this.before = before; }

    public a getCurrent() { return current; }

    public void setCurrent(a current) { this.current = current; }

    public Maybe<NodeLinkList<a>> getAfter() { return after; }

    public void setAfter(Maybe<NodeLinkList<a>> after) { this.after = after; }

    public void show() {
        switch (this.before) {
            case Maybe.Nothing() -> System.out.print("||");
            case Maybe.Just(NodeLinkList<a> beforeNode) -> System.out.print(beforeNode.showLeft());
        }
        System.out.print("@" + ">" + this + "<" + "@");
        switch (this.after) {
            case Maybe.Nothing() -> System.out.print("||");
            case Maybe.Just(NodeLinkList<a> afterNode) -> System.out.println(afterNode.showRight());
        }
    }

    public String showLeft() {
        return switch (this.before) {
            case Maybe.Nothing() -> "||";
            case Maybe.Just(NodeLinkList<a> beforeNode) -> beforeNode.showLeft();
        } + "@" + this;
    }

    public String showRight() {
        return this + "@" + switch (this.after) {
            case Maybe.Nothing() -> "||";
            case Maybe.Just(NodeLinkList<a> afterNode) -> afterNode.showRight();
        };
    }
}