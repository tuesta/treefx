package org.treefx.model;

import javafx.geometry.Point2D;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.utils.adt.T;

import java.util.LinkedList;

public class NodeInfo {
    private int id;
    private String name;
    private String imgURL;
    private Point2D pos;
    private LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children;

    public NodeInfo(int id, String name, String imgURL, Point2D pos, LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children) {
        this.id = id;
        this.name = name;
        this.imgURL = imgURL;
        this.pos = pos;
        this.children = children;
    }

    public NodeInfo(String name, String imgURL, Point2D pos, LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children) {
        this.name = name;
        this.imgURL = imgURL;
        this.pos = pos;
        this.children = children;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getImgURL() { return imgURL; }

    public void setImgURL(String imgURL) { this.imgURL = imgURL; }

    public Point2D getPos() { return pos; }

    public void setPos(Point2D pos) { this.pos = pos; }

    public LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> getChildren() { return children; }

    public void setChildren(LinkedList<T<Point2D, TreeCtxStrict<NodeInfo>>> children) { this.children = children; }
}