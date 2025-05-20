package org.treefx.app;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import org.treefx.component.TreeEditor;
import org.treefx.component.TreeNavigation;
import org.treefx.model.ConnectionDB;
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.T;

import java.util.LinkedList;
import java.util.function.Consumer;

public class Main extends Application {
    public static ListView<T<Integer, String>> createList(Consumer<Integer> toEditor, LinkedList<T<Integer, String>> xs) {
        var listView = new ListView<T<Integer, String>>();
        listView.setFixedCellSize(40);
        listView.setFocusTraversable(false);
        listView.setStyle("""
                -fx-background-color: white;
                -fx-font-size: 16px;
                -fx-cursor: hand;
                """);
        listView.getItems().addAll(xs);
        listView.setMaxWidth(400);
        listView.setPrefHeight(xs.size() * 40 + 2);

        listView.setCellFactory((ListView<T<Integer, String>> lv) -> new ListCell<>() {
            @Override
            protected void updateItem(T<Integer, String> item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item.equals(listView.getSelectionModel().getSelectedItem())) {
                    setStyle("-fx-background-color: black; -fx-font-size: 16px; -fx-text-fill: white; -fx-border-width: 1px; -fx-border-color: black;");
                } else {
                    setStyle("-fx-background-color: white; -fx-font-size: 16px; -fx-text-fill: black; -fx-border-width: 1px; -fx-border-color: black;");
                }
                setText(empty ? null : item.snd());
            }
        });

        listView.setOnMouseClicked(event -> {
            var selectedItem = listView.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                toEditor.accept(selectedItem.fst());
            }
        });
        
        return listView;
    }

    public static HBox createNewTree(Consumer<String> toEditor) {
        var inputField = new javafx.scene.control.TextField();
        HBox.setHgrow(inputField, javafx.scene.layout.Priority.ALWAYS);
        inputField.setMaxWidth(Double.MAX_VALUE);

        var addButton = new javafx.scene.control.Button("   +   ");

        inputField.setPromptText("Introduce un nombre...");
        inputField.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-font-size: 16px;");
        addButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");

        addButton.setOnAction(event -> {
            String inputText = inputField.getText().trim();
            if (inputText.isEmpty()) {
                var alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Error de entrada");
                alert.setHeaderText("Falta el nombre del árbol");
                alert.setContentText("Por favor, proporcione un nombre para el árbol.");
                alert.showAndWait();
            } else toEditor.accept(inputText);
        });

        var container = new HBox(0, inputField, addButton);
        container.setMaxWidth(400);
        container.setAlignment(javafx.geometry.Pos.CENTER);
        return container;
    }

    public static void home(ConnectionDB connection, BorderPane root) {
        LinkedList<T<Integer, String>> xs = connection.getAllRoots();

        var listView = createList(id -> {
            var zipTree = connection.getZipTree(id);
            startTree(zipTree, connection, root);
        }, xs);
        var createNewTree = createNewTree(name -> {
            int rootID = connection.insertRoot(name);
            var newNode = new NodeInfo(rootID, "", "", new Point2D(100, 100), new LinkedList<>());

            startTree(new ZipTreeStrict<>(newNode), connection, root);
        });

        var container = new VBox(12, listView, createNewTree);
        container.setAlignment(javafx.geometry.Pos.CENTER);

        root.setCenter(container);
    }

    public static void startTree(ZipTreeStrict<NodeInfo> zipTree, ConnectionDB connection, BorderPane root) {
        Consumer<Boolean> toHomeOrNav = new Consumer<>() {
            @Override
            public void accept(Boolean homeOrNav) {
                if (homeOrNav) {
                    zipTree.toNodeCtx(zipTree.getCtx());
                    root.setCenter(new TreeNavigation(() ->
                        root.setCenter(new TreeEditor(this, connection, zipTree)), zipTree)
                    );
                } else home(connection, root);
            }
        };

        root.setCenter(new TreeEditor(toHomeOrNav, connection, zipTree));
    }

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
        ConnectionDB connectionDB = new ConnectionDB("sql7.freesqldatabase.com", "3306", "sql7776456", "KnGGaAQpPR", "sql7776456");
        home(connectionDB, root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}