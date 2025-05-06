package org.treefx.model.ziptree;

public interface ZipTree<a> {
    public a extract();
    public boolean prev();
    public boolean next();
    public boolean top();
    public boolean down();
}