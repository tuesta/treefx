package org.treefx.model.ziptree;

import org.treefx.utils.Mut;
import org.treefx.utils.adt.Maybe;

import java.util.LinkedList;

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

    public String unlines(LinkedList<Mut<String>> xs) {
        return xs.stream()
                .map(mut -> mut.val)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    public void show() { System.out.println(unlines(draw(this))); }

    public LinkedList<Mut<String>> draw(NodeLinkTree<a> node) {
        return drawUp(drawBoth(new Maybe.Just<>(node), true), node.up);
    }

    public LinkedList<Mut<String>> drawUp(LinkedList<Mut<String>> children, Maybe<NodeLinkTree<a>> mnode) {
        switch (mnode) {
            case Maybe.Nothing() -> { return children; }
            case Maybe.Just(NodeLinkTree<a> upNode) -> {
                LinkedList<Mut<String>> level = drawLeft(upNode.before);

                LinkedList<Mut<String>> current = new LinkedList<>();
                current.add(new Mut<>(upNode.toString()));
                current.addAll(children);
                if (upNode.up.isJust()) {
                    if (upNode.after.isNothing()) shift(current, "`- ", "   ");
                    else shift(current, "+- ", "|  ");
                    current.addFirst(new Mut<>("|"));
                }

                level.addAll(current);
                level.addAll(drawRight(upNode.after, true, false));

                return drawUp(level, upNode.up);
            }
        }
    }

    public LinkedList<Mut<String>> drawBoth(Maybe<NodeLinkTree<a>> mnode, boolean isCurrent) {
        LinkedList<Mut<String>> both;
        switch (mnode) {
            case Maybe.Nothing() -> both = new LinkedList<>();
            case Maybe.Just(NodeLinkTree<a> node) -> {
                both = drawLeft(node.before);
                if (node.up.isJust()) both.add(new Mut<>("|"));
                both.addAll(drawRight(mnode, true, isCurrent));
            }
        }
        return both;
    }

    public LinkedList<Mut<String>> drawLeft(Maybe<NodeLinkTree<a>> mnode) {
        LinkedList<Mut<String>> left;
        switch (mnode) {
            case Maybe.Nothing() -> left = new LinkedList<>();
            case Maybe.Just(NodeLinkTree<a> nodeLeft) -> {
                left = drawLeft(nodeLeft.before);

                LinkedList<Mut<String>> children = new LinkedList<>();
                children.add(new Mut<>(nodeLeft.toString()));
                children.addAll(drawBoth(nodeLeft.down, false));
                if (nodeLeft.up.isJust()) {
                    shift(children, "`+ ", "|  ");
                    children.addFirst(new Mut<>("|"));
                }

                left.addAll(children);
            }
        }
        return left;
    }

    public LinkedList<Mut<String>> drawRight(Maybe<NodeLinkTree<a>> mnode, boolean isFromBoth, boolean isCurrent) {
        LinkedList<Mut<String>> right;
        switch (mnode) {
            case Maybe.Nothing() -> right = new LinkedList<>();
            case Maybe.Just(NodeLinkTree<a> nodeRight) -> {
                LinkedList<Mut<String>> children = new LinkedList<>();
                String currentStr = nodeRight.toString();
                if (isCurrent) {
                    currentStr = ">>" + currentStr + "<<";
                }
                children.add(new Mut<>(currentStr));
                children.addAll(drawBoth(nodeRight.down, false));
                if (nodeRight.up.isJust()) {
                    if (nodeRight.after.isNothing()) shift(children, "`- ", "   ");
                    else shift(children, "+- ", "|  ");
                }

                if (!isFromBoth) children.addFirst(new Mut<>("|"));

                right = children;
                right.addAll(drawRight(nodeRight.after, false, false));
            }
        }
        return right;
    }

    public void shift(LinkedList<Mut<String>> xs, String first, String other) {
        boolean isFirst = true;
        for (Mut<String> str : xs) {
            String prefix = isFirst ? first : other;
            isFirst = false;
            str.val = prefix + str.val;
        }
    }
}