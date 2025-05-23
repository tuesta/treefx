package org.treefx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.treefx.model.ConnectionDB;
import javafx.geometry.Point2D;
import org.treefx.model.MovementInSpace;
import org.treefx.utils.adt.T;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.function.Consumer;

public class NodeCtx extends VBox {
    private final Consumer<Boolean> toHomeOrNav;
    private final TreeEditor editor;
    private final ConnectionDB connection;

    @FXML private ImageView node_img;
    @FXML private AnchorPane container_img;
    @FXML private TextField node_imgURL;
    @FXML private TextField node_name;
    @FXML private Button node_update;
    @FXML private Button node_navigation;
    @FXML private Button node_home;
    private LinkedList<Button> btns;
    private Node node;

    public void setNode(Node node) {
        this.node = node;
        this.loadNodeInfo();
    }

    private void onNodeUpdate() {
        String name = this.node_name.getText();
        String imgURL = this.node_imgURL.getText();

        var nodeInfo = this.node.getNodeCtx().getValue();
        this.connection.updateNodeInfo(nodeInfo.getId(), name, imgURL);
        nodeInfo.setName(name);
        nodeInfo.setImgURL(imgURL);
        node.loadNodeInfo();
        this.loadNodeInfo();
    }

    public NodeCtx(Consumer toHomeOrNav, TreeEditor editor, ConnectionDB connection, Node node) {
        this.toHomeOrNav = toHomeOrNav;
        this.editor = editor;
        this.connection = connection;
        this.btns = new LinkedList<>();
        this.node = node;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NodeCtx.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        loadNodeInfo();
        node_update.setOnAction(event -> onNodeUpdate());
        node_navigation.setOnAction(event -> this.toHomeOrNav.accept(true));
        node_home.setOnAction(event -> this.toHomeOrNav.accept(false));
        node_img.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();

            double imageSmallWidth = node_img.getBoundsInParent().getWidth();
            double imageSmallHeight = node_img.getBoundsInParent().getHeight();

            double xRelative = x / imageSmallWidth;
            double yRelative = y / imageSmallHeight;

            if (this.editor.handleInsertButton(new Point2D(xRelative, yRelative))) {
                Button smallButton = createCircularButtonAt(x, y);
                this.container_img.getChildren().add(smallButton);
            };
        });
    }

    private Button renderButtonMovements(Point2D dim, MovementInSpace movementInSpace) {
        double x = movementInSpace.getPos().getX() * dim.getX();
        double y = movementInSpace.getPos().getY() * dim.getY();

        Button smallButton = createCircularButtonAt(x, y);
        this.container_img.getChildren().add(smallButton);
        return smallButton;
    }

    private Button createCircularButtonAt(double x, double y) {
        Button button = new Button();

        button.setStyle("""
           -fx-background-color: rgba(0, 0, 0, 0.3);
           -fx-text-fill: white;
           -fx-background-radius: 15px;
           -fx-min-width: 30px;
           -fx-max-width: 30px;
           -fx-max-height: 30px;
           """);

        double radius = 15;
        button.setLayoutX(x - radius);
        button.setLayoutY(y - radius);

        return button;
    }

    public void loadNodeInfo() {
        for (Button btnInSpace : this.btns) this.container_img.getChildren().remove(btnInSpace);

        String imageLoad = getClass().getResource("image-edit.png").toExternalForm();
        String nodeURL = this.node.getNodeCtx().getValue().getImgURL();
        String url = nodeURL.isEmpty() ? imageLoad : nodeURL;
        var mImage = new Image(url);
        if (mImage.isError()) mImage = new Image(imageLoad);
        node_imgURL.setText(nodeURL);
        node_img.setImage(mImage);

        LinkedList<MovementInSpace> movementsInSpace = this.node.getNodeCtx().getValue().getChildren();
        var btnInSpace = new LinkedList<Button>();
        for (var movementInSpace : movementsInSpace) {
            double imageSmallWidth = node_img.getBoundsInParent().getWidth();
            double imageSmallHeight = node_img.getBoundsInParent().getHeight();

            var button = this.renderButtonMovements(new Point2D(imageSmallWidth, imageSmallHeight), movementInSpace);
            btnInSpace.add(button);
        }
        this.btns = btnInSpace;

        node_name.setText(this.node.getNodeCtx().getValue().getName());
    }
}