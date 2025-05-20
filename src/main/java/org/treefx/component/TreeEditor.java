package org.treefx.component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.treefx.model.ConnectionDB;
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.Maybe;

import java.io.IOException;
import java.util.LinkedList;
import java.util.function.Consumer;

public class TreeEditor extends AnchorPane {
    private final Consumer<Boolean> toHomeOrNav;
    private final ConnectionDB connection;

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

    public TreeEditor(Consumer<Boolean> toHomeOrNav, ConnectionDB connection, ZipTreeStrict<NodeInfo> zipTree) {
        this.toHomeOrNav = toHomeOrNav;
        this.connection = connection;
        zipTree.toRoot();
        this.zipTree = zipTree;
        loadFxml();
    }

    @FXML
    public void initialize() {
        this.tree.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                Point2D p = tree.sceneToLocal(e.getSceneX(), e.getSceneY());
                insertNode(p);
                e.consume();
            }
        });

        this.zipTree.mapWithFatherM((Maybe<Node> mfather, TreeCtxStrict<NodeInfo> nodeCtx) -> {
            var newNode = new Node(mfather, nodeCtx, this);
            newNode.renderNode(nodeCtx.getValue().getPos());
            this.tree.getChildren().add(newNode);

            if (mfather.isNothing()) {this.currentNode = newNode;}

            Platform.runLater(newNode::handleLine);

            return newNode;
        });

        Platform.runLater(this::layout);

        this.currentNode.setFocus();
        this.nodeCtx = new NodeCtx(this.toHomeOrNav, this.connection, this.currentNode);
        this.container.getItems().add(this.nodeCtx);
    }

    public void insertNode(Point2D pos) {
        int newId = this.connection.insertChild(pos, this.zipTree.getCtx().getValue().getId());
        this.zipTree.insertChild(new NodeInfo(newId, "", "", pos, new LinkedList<>()));

        var children = this.zipTree.getCtx().getChildren();
        this.zipTree.setCtx(children.getLast().getCurrent().snd());
        this.currentNode.removeFocus();

        this.currentNode = new Node(new Maybe.Just<>(this.currentNode), this.zipTree.getCtx(), this);
        this.currentNode.renderNode(pos);
        this.currentNode.setFocus();
        this.tree.getChildren().add(this.currentNode);

        this.currentNode.handleLine();
        this.nodeCtx.setNode(this.currentNode);
    }

    public void changeFocus(Node node) {
        this.zipTree.setCtx(node.getNodeCtx());

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
            this.zipTree.setCtx(node.getNodeCtx());
            this.currentNode = node;
            Point2D p = tree.sceneToLocal(e.getSceneX(), e.getSceneY());

            var nodeInfo = node.getNodeCtx().getValue();
            connection.updateNodeInfo(nodeInfo.getId(), p);
            nodeInfo.setPos(p);

            node.renderNode(p);
            this.nodeCtx.setNode(this.currentNode);
            e.setDropCompleted(true);
            e.consume();
        });
    }
}