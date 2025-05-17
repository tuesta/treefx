package org.treefx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.Maybe;

import java.io.IOException;
import java.util.LinkedList;

public class TreeEditor extends AnchorPane {
    @FXML private SplitPane container;
    @FXML private AnchorPane tree;
    @FXML private VBox node_details;

    private ZipTreeStrict<NodeInfo> zipTree;
    private Node currentNode;

    public AnchorPane getTree() { return tree; }

    public TreeEditor() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeSpace.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try { fxmlLoader.load(); } catch (IOException e) { throw new RuntimeException(e); }
    }

    @FXML
    public void initialize() {
        this.zipTree = new ZipTreeStrict<>(new NodeInfo("[name]", new Maybe.Nothing<>(), new Point2D(0, 0), new LinkedList<>()));
        this.currentNode = new Node(new Maybe.Nothing<>(), new Maybe.Nothing<>(), this.zipTree.getCtx(), this);
        this.tree.getChildren().add(this.currentNode);
        this.currentNode.setFocus();

        this.tree.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                Point2D p = tree.sceneToLocal(e.getSceneX(), e.getSceneY());
                insertNode(p);
                e.consume();
            }
        });
    }

    public void insertNode(Point2D pos) {
        this.zipTree.insertChild(new NodeInfo("[name]", new Maybe.Nothing<>(), pos, new LinkedList<>()));
        this.currentNode.removeFocus();

        this.currentNode = new Node(new Maybe.Just<>(this.currentNode), new Maybe.Nothing<>(), this.zipTree.getCtx(), this);
        this.currentNode.renderNode(pos);
        this.currentNode.setFocus();
        this.tree.getChildren().add(this.currentNode);

        this.currentNode.handleLine();
    }

    public void dragDetection(Node node) {
        container.setOnDragOver(e -> {
            e.acceptTransferModes(TransferMode.ANY);
            e.consume();
        });

        tree.setOnDragDropped(e -> {
            this.currentNode.removeFocus();
            node.setFocus();
            this.currentNode = node;
            Point2D p = tree.sceneToLocal(e.getSceneX(), e.getSceneY());
            node.renderNode(p);
            e.setDropCompleted(true);
            e.consume();
        });
    }
}