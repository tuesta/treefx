package org.treefx.component;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.utils.adt.Maybe;

import java.io.IOException;

public class Node extends VBox {
    @FXML private VBox node_container;
    @FXML private ImageView node_img;
    @FXML private Label node_name;
    private final Maybe<Node> mNodeFather;
    private Maybe<Line> mline;
    private final TreeCtxStrict<NodeInfo> nodeCtx;
    private final TreeEditor editor;

    public TreeCtxStrict<NodeInfo> getNodeCtx() { return nodeCtx; }

    public Node(Maybe<Node> mNodeFather, TreeCtxStrict<NodeInfo> nodeCtx, TreeEditor editor) {
        this.mNodeFather = mNodeFather;
        this.mline = new Maybe.Nothing<>();
        this.nodeCtx = nodeCtx;
        this.editor = editor;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Node.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleLine() {
        switch (this.mNodeFather) {
            case Maybe.Nothing() -> {}
            case Maybe.Just(Node nodeFather) ->
                this.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
                    Point2D fatherPoint = nodeFather.localToScene(nodeFather.getWidth() / 2, nodeFather.getHeight());
                    Point2D childPoint = this.localToScene( newValue.getWidth() / 2, 0);
                    this.updateLine(fatherPoint, childPoint);
                    nodeFather.boundsInParentProperty().addListener((observable1, oldValue1, newValue1) -> {
                        Point2D newFatherPoint = nodeFather.localToScene( newValue1.getWidth() / 2, newValue1.getHeight());
                        this.updateLine(newFatherPoint, childPoint);
                    });
                });
        }
    }

    public void updateLine(Point2D father, Point2D child) {
        switch (mline) {
            case Maybe.Nothing() -> {
                Line line = new Line();
                line.setStartX(father.getX());
                line.setStartY(father.getY());
                line.setEndX(child.getX());
                line.setEndY(child.getY());
                line.setStyle("-fx-stroke: black; -fx-stroke-width: 2;");
                this.editor.getChildren().add(line);
                this.mline = new Maybe.Just<>(line);
            }
            case Maybe.Just(Line line) -> {
                line.setStartX(father.getX());
                line.setStartY(father.getY());
                line.setEndX(child.getX());
                line.setEndY(child.getY());
            }
        }
    }

    @FXML
    public void initialize() {
        loadNodeInfo();

        removeFocus();

        node_container.setOnDragDetected(e -> {
            Dragboard db = node_container.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString("");
            db.setContent(content);
            editor.dragDetection(this);
            e.consume();
        });

        node_container.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                editor.changeFocus(this);
            }
        });
    }

    public void setFocus() {
        String cssLayout = """
                -fx-border-color: red;
                -fx-border-insets: 5;
                -fx-border-width: 3;
                """;

        this.setStyle(cssLayout);
    }

    public void removeFocus() {
        String cssLayout = """
                -fx-border-color: white;
                -fx-border-insets: 5;
                -fx-border-width: 3;
                """;
        this.setStyle(cssLayout);
    }

    public void renderNode(Point2D localCoords) {
        this.nodeCtx.getValue().setPos(localCoords);

        this.relocate(
                (int) localCoords.getX() - (this.getBoundsInParent().getWidth() / 2),
                (int) localCoords.getY() - (this.getBoundsInParent().getHeight() / 2)
        );
    }

    public void loadNodeInfo() {
        Image imageLoad = new Image(getClass().getResource("image-edit.png").toExternalForm());

        switch (nodeCtx.getValue().getImgURL()) {
            case Maybe.Nothing() -> {}
            case Maybe.Just(String url) -> {
                var mImage = new Image(url);
                if (!mImage.isError()) imageLoad = mImage;
            }
        }

        node_img.setImage(imageLoad);

        String nameLoad = nodeCtx.getValue().getName();
        node_name.setText(nameLoad.isEmpty() ? "[name]" : nameLoad);
    }
}