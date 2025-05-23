package org.treefx.model.ziptree;

import org.treefx.model.ziplist.ZipListStrict;
import org.treefx.utils.adt.Movement;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.LinkedList;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ZipTreeStrict<a> {
    private TreeCtxStrict<a> ctx;
    private final TreeCtxStrict<a> root;

    public ZipTreeStrict(a val) {
        ZipListStrict<T<a, TreeCtxStrict<a>>> brothers = new ZipListStrict<>();
        this.ctx = new TreeCtxStrict<>(new Maybe.Nothing<>(), null, brothers, new ZipListStrict<>());

        T<a, TreeCtxStrict<a>> current = new T.MkT<>(val, this.ctx);
        brothers.insertR(current);
        this.ctx.setCurrent(brothers.getMNode().fromJust());

        this.root = ctx;
    }

    public void setCtx(TreeCtxStrict<a> ctx) { this.ctx = ctx; }

    public a extract() { return this.ctx.getValue(); }
    public TreeCtxStrict<a> getCtx() { return this.ctx; }

    public boolean toFather() {
        switch (this.ctx.getFather()) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(TreeCtxStrict<a> fatherCtx) -> {
                toNodeCtx(fatherCtx);
                return true;
            }
        }
    }

    public void toRoot() { this.ctx = this.root; }

    /**
     * Actualiza el contexto del árbol al contexto del nodo especificado.
     *
     * @param nodeCtx el nuevo contexto del nodo que se establecerá como el contexto actual
     */
    public void toNodeCtx(TreeCtxStrict<a> nodeCtx) {
        this.ctx = nodeCtx;
        var brothers = this.ctx.getBrothers();
        int ix = brothers.getIndex(nodeCtx.getCurrent());
        brothers.to(ix);
    }

    public int toChild(int ix) {
        var children = this.ctx.getChildren();

        int newIx = children.to(ix);
        if (newIx == 0) return 0;

        this.ctx = children.extract().fromJust().snd();
        return newIx;
    }

    public boolean next() {
        if (this.ctx.getBrothers().next()) {
            this.ctx = this.ctx.getBrothers().getMNode().fromJust().getCurrent().snd();
            return true;
        }
        return false;
    }

    public boolean prev() {
        if (this.ctx.getBrothers().prev()) {
            this.ctx = this.ctx.getBrothers().getMNode().fromJust().getCurrent().snd();
            return true;
        }
        return false;
    }

    public boolean down() {
        var children = this.ctx.getChildren();
        if (children.getMNode().isNothing()) return false;
        this.ctx = children.getMNode().fromJust().getCurrent().snd();
        return true;
    }

    public void insertChild(a val) {
        var children = this.ctx.getChildren();
        var newCtx = new TreeCtxStrict<>(new Maybe.Just<>(this.ctx), null,children, new ZipListStrict<>());
        var current = new T.MkT<>(val, newCtx);
        children.insert(current);
        newCtx.setCurrent(children.getLast());
    }

    public LinkedList<Movement> getRelativePosition(TreeCtxStrict<a> targetCtx) {
        Stack<TreeCtxStrict<a>> ancestorsCurrent = new Stack<>();
        ancestorsCurrent.push(this.ctx);
        while (ancestorsCurrent.peek().getFather().isJust()) {
            ancestorsCurrent.push(ancestorsCurrent.peek().getFather().fromJust());
        }

        Stack<TreeCtxStrict<a>> ancestorsTarget = new Stack<>();
        ancestorsTarget.push(targetCtx);
        while (ancestorsTarget.peek().getFather().isJust()) {
            ancestorsTarget.push(ancestorsTarget.peek().getFather().fromJust());
        }


        TreeCtxStrict<a> commonFather = root;
        while (!ancestorsCurrent.isEmpty() && !ancestorsTarget.isEmpty() &&
               ancestorsCurrent.peek() == ancestorsTarget.peek()) {
            commonFather = ancestorsCurrent.peek();
            ancestorsCurrent.pop();
            ancestorsTarget.pop();
        }
        System.out.println(ancestorsCurrent);
        System.out.println(ancestorsTarget);
        ancestorsCurrent.push(commonFather);
        ancestorsTarget.push(commonFather);
        System.out.println(ancestorsCurrent);

        LinkedList<Movement> movements = new LinkedList<>();

        if (ancestorsCurrent.isEmpty() && ancestorsTarget.isEmpty()) return movements;

        // Empieza en uno, para solo contar los padres
        for (int i = 1; i < ancestorsCurrent.size(); i++) movements.add(Movement.UP);

        if (ancestorsTarget.isEmpty()) return movements;
        // Remove common father
        ancestorsTarget.pop();

        while (!ancestorsTarget.isEmpty()) {
            var ctx = ancestorsTarget.pop();
            int child = ctx.getBrothers().getIndex(ctx.getCurrent());
            movements.add(Movement.DOWN(child));
        }

        return movements;
    }

    public boolean moveTo(LinkedList<Movement> movements) {
        for (Movement m : movements) {
            switch (m) {
                case Movement.Up() -> {
                   if (!this.toFather()) return false;
                }
                case Movement.Down(int child) -> {
                    if (this.toChild(child) != child) return false;
                }
            }
        }
        return true;
    }

    public void mapM(Function<a, Void> k) { this.root.downMap(k); }

    public <b> void mapWithFatherM(BiFunction<Maybe<b>, TreeCtxStrict<a>, b> k) { this.root.downMapWithFather(k); }
}
