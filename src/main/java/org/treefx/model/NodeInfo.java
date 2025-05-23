package org.treefx.model;

import javafx.geometry.Point2D;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.Movement;
import org.treefx.utils.adt.T;

import java.util.LinkedList;

public class NodeInfo {
    private int id;
    private String name;
    private String imgURL;
    private Point2D pos;
    private LinkedList<MovementInSpace> movements;

    public NodeInfo(int id, String name, String imgURL, Point2D pos, LinkedList<MovementInSpace> movements) {
        this.id = id;
        this.name = name;
        this.imgURL = imgURL;
        this.pos = pos;
        this.movements = movements;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getImgURL() { return imgURL; }

    public void setImgURL(String imgURL) { this.imgURL = imgURL; }

    public Point2D getPos() { return pos; }

    public void setPos(Point2D pos) { this.pos = pos; }

    public void addPos(MovementInSpace movementInSpace) { this.movements.add(movementInSpace); }

    public LinkedList<MovementInSpace> getChildren() { return movements; }

    public void setChildren(LinkedList<MovementInSpace> children) { this.movements = children; }
}