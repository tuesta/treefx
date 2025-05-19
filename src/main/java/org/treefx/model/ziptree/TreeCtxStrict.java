package org.treefx.model.ziptree;

import org.treefx.model.ziplist.NodeLinkList;
import org.treefx.model.ziplist.ZipListStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TreeCtxStrict<a> {
    private Maybe<TreeCtxStrict<a>> father;
    private NodeLinkList<T<a, TreeCtxStrict<a>>> current;
    private ZipListStrict<T<a, TreeCtxStrict<a>>> brothers;
    private ZipListStrict<T<a, TreeCtxStrict<a>>> children;

    public TreeCtxStrict(Maybe<TreeCtxStrict<a>> father,
                         NodeLinkList<T<a, TreeCtxStrict<a>>> current,
                         ZipListStrict<T<a, TreeCtxStrict<a>>> brothers,
                         ZipListStrict<T<a, TreeCtxStrict<a>>> children)
    {
        this.father = father;
        this.current = current;
        this.brothers = brothers;
        this.children = children;
    }

    public NodeLinkList<T<a, TreeCtxStrict<a>>> getCurrent() { return current; }
    public void setCurrent(NodeLinkList<T<a, TreeCtxStrict<a>>> current) { this.current = current; }

    public a getValue() { return current.getCurrent().fst(); }

    public Maybe<TreeCtxStrict<a>> getFather() { return father; }

    public void setFather(Maybe<TreeCtxStrict<a>> father) { this.father = father; }

    public ZipListStrict<T<a, TreeCtxStrict<a>>> getBrothers() { return brothers; }

    public void setBrothers(ZipListStrict<T<a, TreeCtxStrict<a>>> brothers) { this.brothers = brothers; }

    public ZipListStrict<T<a, TreeCtxStrict<a>>> getChildren() { return children; }

    public void setChildren(ZipListStrict<T<a, TreeCtxStrict<a>>> children) { this.children = children; }

    public void downMap(Function<a, Void> k) {
        var a = this.current.getCurrent().fst();
        k.apply(a);
        this.children.mapM(x -> {
            x.snd().downMap(k);
            return null;
        });
    }

    public <b> void downMapWithFatherGO(Maybe<b> mfatherResult, BiFunction<Maybe<b>, TreeCtxStrict<a>, b> k) {
        var a = this.current.getCurrent().snd();
        var b = k.apply(mfatherResult, a);
        this.children.mapM(x -> {
            x.snd().downMapWithFatherGO(new Maybe.Just<>(b), k);
            return null;
        });
    }

    public <b> void downMapWithFather(BiFunction<Maybe<b>, TreeCtxStrict<a>, b> k) {
        this.downMapWithFatherGO(new Maybe.Nothing<>(), k);
    }
}