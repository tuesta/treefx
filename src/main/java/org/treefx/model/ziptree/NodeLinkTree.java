package org.treefx.model.ziptree;

import org.treefx.utils.adt.Maybe;

public class NodeLinkTree<a> {
    private a current;
    private Maybe<NodeLinkTree<a>> before;
    private Maybe<NodeLinkTree<a>> after;
    private Maybe<NodeLinkTree<a>> up;
    private Maybe<NodeLinkTree<a>> down;

    public NodeLinkTree(a current, Maybe<NodeLinkTree<a>> before, Maybe<NodeLinkTree<a>> after, Maybe<NodeLinkTree<a>> up, Maybe<NodeLinkTree<a>> down) {
        this.current = current;
        this.before = before;
        this.after = after;
        this.up = up;
        this.down = down;
    }

    public NodeLinkTree(a current) {
        this(current, new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>(), new Maybe.Nothing<>());
    }

    public a getCurrent() { return current; }

    public void setCurrent(a current) { this.current = current; }

    public Maybe<NodeLinkTree<a>> getBefore() { return before; }

    public void setBefore(Maybe<NodeLinkTree<a>> before) { this.before = before; }

    public Maybe<NodeLinkTree<a>> getAfter() { return after; }

    public void setAfter(Maybe<NodeLinkTree<a>> after) { this.after = after; }

    public Maybe<NodeLinkTree<a>> getUp() { return up; }

    public void setUp(Maybe<NodeLinkTree<a>> up) { this.up = up; }

    public Maybe<NodeLinkTree<a>> getDown() { return down; }

    public void setDown(Maybe<NodeLinkTree<a>> down) { this.down = down; }
}