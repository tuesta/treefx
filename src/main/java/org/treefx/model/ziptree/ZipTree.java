package org.treefx.model.ziptree;

/**
 * Represents a ZipTree structure, a navigable tree using zipper principles.
 *
 * @param <a> The type of values stored within the tree nodes.
 */
public interface ZipTree<a> {
    public a extract();
    public boolean prev();
    public boolean next();
    public boolean top();
    public boolean down();
}