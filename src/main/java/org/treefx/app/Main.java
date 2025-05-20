package org.treefx.app;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.treefx.component.TreeEditor;
import org.treefx.component.TreeNavigation;
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.Maybe;

import java.util.LinkedList;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        try {
            Scene scene = new Scene(root,640,480);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

        var zipTree = new ZipTreeStrict(new NodeInfo("Hola Mundo", new Maybe.Nothing<>(), new Point2D(300, 100), new LinkedList<>()));
        zipTree.insertChild(new NodeInfo("Hola Mundo 2", new Maybe.Just<>("https://wallpapers.com/images/featured/fondos-verticales-0ky9f0wy7qxg8h0x.jpg"), new Point2D(100, 300), new LinkedList<>()));
        zipTree.insertChild(new NodeInfo("Hola Mundo 3", new Maybe.Just<>("https://images.wallpaperscraft.com/image/single/street_night_wet_155637_1920x1080.jpg"), new Point2D(300, 300), new LinkedList<>()));
        zipTree.insertChild(new NodeInfo("Hola Mundo 4", new Maybe.Just<>("https://i.pinimg.com/736x/26/da/78/26da780891a603e8f9793810350ac13a.jpg"), new Point2D(500, 300), new LinkedList<>()));
        zipTree.toChild(2);
        zipTree.insertChild(new NodeInfo("Hola Mundo 3-1", new Maybe.Just<>("https://images.wallpapersden.com/image/download/escape-hd-minimal-cool_bWZnbGqUmZqaraWkpJRmbmdlrWZlbWU.jpg"), new Point2D(300, 600), new LinkedList<>()));

        Runnable toNavigation = new Runnable() {
            @Override
            public void run() {
                zipTree.toNodeCtx(zipTree.getCtx());
                root.setCenter(new TreeNavigation(() -> {
                    root.setCenter(new TreeEditor(this, zipTree));
                }, zipTree));
            }
        };

        root.setCenter(new TreeEditor(toNavigation, zipTree));
    }

    public static void main(String[] args) {
        launch(args);
    }
}