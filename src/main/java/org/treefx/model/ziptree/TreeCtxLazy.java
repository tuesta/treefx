package org.treefx.model.ziptree;

import org.treefx.model.ziplist.ZipListInc;
import org.treefx.utils.adt.Maybe;

public class TreeCtxLazy<b> {
    private Maybe<TreeCtxLazy<b>> father;
    private ZipListInc<b, TreeCtxLazy<b>> brothers;
    private ZipListInc<b, TreeCtxLazy<b>> children;

    public TreeCtxLazy(Maybe<TreeCtxLazy<b>> father, ZipListInc<b, TreeCtxLazy<b>> brothers, ZipListInc<b, TreeCtxLazy<b>> children) {
        this.father = father;
        this.brothers = brothers;
        this.children = children;
    }

    public Maybe<TreeCtxLazy<b>> getFather() { return father; }

    public void setFather(Maybe<TreeCtxLazy<b>> father)   { this.father = father; }

    public ZipListInc<b, TreeCtxLazy<b>> getBrothers() { return brothers; }

    public void setBrothers(ZipListInc<b, TreeCtxLazy<b>> brothers) { this.brothers = brothers; }

    public ZipListInc<b, TreeCtxLazy<b>> getChildren() { return children; }

    public void setChildren(ZipListInc<b, TreeCtxLazy<b>> children) { this.children = children; }
}