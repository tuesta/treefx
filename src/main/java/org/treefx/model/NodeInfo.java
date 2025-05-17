package org.treefx.model;

import javafx.geometry.Point2D;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.util.LinkedList;

public class NodeInfo {
    private String name;
    private Maybe<String> imgURL;
    private Point2D pos;
    private LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children;

    public NodeInfo(String name, Maybe<String> imgURL, Point2D pos, LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children) {
        this.name = name;
        this.imgURL = imgURL;
        this.pos = pos;
        this.children = children;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Maybe<String> getImgURL() { return imgURL; }

    public void setImgURL(Maybe<String> imgURL) { this.imgURL = imgURL; }

    public Point2D getPos() { return pos; }

    public void setPos(Point2D pos) { this.pos = pos; }

    public LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> getChildren() { return children; }

    public void setChildren(LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children) { this.children = children; }
}