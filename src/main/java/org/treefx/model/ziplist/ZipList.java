package org.treefx.model.ziplist;

import org.treefx.utils.adt.Maybe;

public interface ZipList<a> {
    public <x> ZipList<x> empty();
    public boolean next();
    public boolean prev();
    public Maybe<a> extract();
    public void setCurrent(a newCurrent);
    public void show();
}