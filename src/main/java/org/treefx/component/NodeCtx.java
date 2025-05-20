package org.treefx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.treefx.model.ConnectionDB;

import java.io.IOException;
import java.util.function.Consumer;

public class NodeCtx extends VBox {
    private final Consumer<Boolean> toHomeOrNav;
    private final ConnectionDB connection;

    @FXML private ImageView node_img;
    @FXML private TextField node_imgURL;
    @FXML private TextField node_name;
    @FXML private Button node_update;
    @FXML private Button node_navigation;
    @FXML private Button node_home;
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

    public NodeCtx(Consumer toHomeOrNav, ConnectionDB connection, Node node) {
        this.toHomeOrNav = toHomeOrNav;
        this.connection = connection;
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
    }

    public void loadNodeInfo() {
        String imageLoad = getClass().getResource("image-edit.png").toExternalForm();
        String nodeURL = node.getNodeCtx().getValue().getImgURL();
        String url = nodeURL.isEmpty() ? imageLoad : nodeURL;

        var mImage = new Image(url);
        if (mImage.isError()) mImage = new Image(imageLoad);
        node_imgURL.setText(nodeURL);
        node_img.setImage(mImage);

        node_name.setText(this.node.getNodeCtx().getValue().getName());
    }
}