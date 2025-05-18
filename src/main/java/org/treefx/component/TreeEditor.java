package org.treefx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.Maybe;

import java.io.IOException;
import java.util.LinkedList;

public class TreeEditor extends AnchorPane {
    @FXML private SplitPane container;
    @FXML private AnchorPane tree;

    private final ZipTreeStrict<NodeInfo> zipTree;
    private NodeCtx nodeCtx;
    private Node currentNode;

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeEditor.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try { fxmlLoader.load(); } catch (IOException e) { throw new RuntimeException(e); }
    }

    public TreeEditor() {
        this.zipTree = new ZipTreeStrict<>(new NodeInfo("", new Maybe.Nothing<>(), new Point2D(0, 0), new LinkedList<>()));
        loadFxml();
    }

    public TreeEditor(ZipTreeStrict<NodeInfo> zipTree) {
        this.zipTree = zipTree;
        loadFxml();
    }

    @FXML
    public void initialize() {
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

        this.nodeCtx = new NodeCtx(this.currentNode);
        this.container.getItems().add(this.nodeCtx);
    }

    public void insertNode(Point2D pos) {
        this.zipTree.insertChild(new NodeInfo("", new Maybe.Nothing<>(), pos, new LinkedList<>()));
        this.currentNode.removeFocus();

        this.currentNode = new Node(new Maybe.Just<>(this.currentNode), new Maybe.Nothing<>(), this.zipTree.getCtx(), this);
        this.currentNode.renderNode(pos);
        this.currentNode.setFocus();
        this.tree.getChildren().add(this.currentNode);

        this.currentNode.handleLine();
        this.nodeCtx.setNode(this.currentNode);
    }

    public void changeFocus(Node node) {
        this.currentNode.removeFocus();
        node.setFocus();
        this.currentNode = node;
        this.nodeCtx.setNode(this.currentNode);
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
            this.nodeCtx.setNode(this.currentNode);
            e.setDropCompleted(true);
            e.consume();
        });
    }
}