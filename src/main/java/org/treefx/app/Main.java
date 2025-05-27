package org.treefx.app;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseButton;
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
import org.treefx.utils.ReadCredentials;
import org.treefx.utils.adt.T;

import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Clase principal de la aplicación TreeFX.
 * Encargada de inicializar y mostrar la interfaz gráfica principal utilizando JavaFX.
 * Construye un árbol de nodos de ejemplo que es visualizado por el componente TreeNavigation.
 */
public class Main extends Application {
    /**
     * Representa la conexión con la base de datos utilizada por la aplicación.
     * Proporciona métodos para interactuar con los nodos del árbol almacenados en la base de datos.
     */
    private ConnectionDB connection;

    /**
     * Crea una lista interactiva que muestra elementos con un identificador y un nombre.
     * Cada elemento puede ser seleccionado o eliminado, manejando los eventos
     * asociados mediante funciones proporcionadas.
     *
     * @param toEditor Una función que toma el identificador de un elemento al hacer clic sobre él.
     * @param xs       Una lista de elementos que la lista visualiza, donde cada elemento es un par de identificador y nombre.
     * @return Un componente de tipo {@code ListView} que contiene los elementos proporcionados.
     */
    public ListView<T<Integer, String>> createList(Consumer<Integer> toEditor, LinkedList<T<Integer, String>> xs) {
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
                if (event.getButton() == MouseButton.SECONDARY) {
                    connection.removeRoot(selectedItem.fst());
                    listView.getItems().remove(selectedItem);
                    listView.setPrefHeight((xs.size() - 1) * 40 + 2);
                } else {
                    toEditor.accept(selectedItem.fst());
                }
            }
        });
        
        return listView;
    }

    
    /**
     * Crea un componente de interfaz gráfica que permite al usuario añadir un nuevo árbol.
     * El componente incluye un campo de texto donde el usuario puede introducir
     * el nombre del árbol y un botón para confirmar la creación. Si el campo de texto
     * está vacío, se muestra un mensaje de advertencia solicitando un nombre válido.
     *
     * @param toEditor Una función que procesa el nombre ingresado para crear y mostrar el nuevo árbol.
     * @return Un contenedor de tipo {@code HBox} que incluye el campo de entrada y el botón para agregar el nuevo árbol.
     */
    public HBox createNewTree(Consumer<String> toEditor) {
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

    /**
     * Muestra la vista inicial de la aplicación, donde los usuarios pueden visualizar
     * una lista de árboles existentes y realizar acciones como crear o seleccionar un árbol.
     *
     * @param root El elemento {@code BorderPane} principal donde se carga la vista inicial.
     *             Este contenedor incluye una lista de árboles existentes y un formulario
     *             para la creación de nuevos árboles.
     */
    public void home(BorderPane root) {
        LinkedList<T<Integer, String>> xs = this.connection.getAllRoots();

        var listView = createList(id -> {
            var zipTree = this.connection.getZipTree(id);
            startTree(zipTree, root);
        }, xs);
        var createNewTree = createNewTree(name -> {
            int rootID = this.connection.insertRoot(name);
            var newNode = new NodeInfo(rootID, "", "", new Point2D(100, 100), new LinkedList<>());

            startTree(new ZipTreeStrict<>(newNode), root);
        });

        var container = new VBox(12, listView, createNewTree);
        container.setAlignment(javafx.geometry.Pos.CENTER);

        root.setCenter(container);
    }

    /**
     * Configura y muestra la vista del árbol para un {@code ZipTreeStrict<NodeInfo>} específico,
     * Los usuarios pueden navegar por el árbol o regresar a la vista inicial.
     *
     * @param zipTree Una instancia de {@code ZipTreeStrict<NodeInfo>} que representa la estructura jerárquica del árbol.
     * @param root    El elemento {@code BorderPane} principal sobre el cual se cargarán las vistas del árbol o de home.
     */
    public void startTree(ZipTreeStrict<NodeInfo> zipTree, BorderPane root) {
        Consumer<Boolean> toHomeOrNav = new Consumer<>() {
            @Override
            public void accept(Boolean homeOrNav) {
                if (homeOrNav) {
                    zipTree.toNodeCtx(zipTree.getCtx());
                    root.setCenter(new TreeNavigation(() ->
                        root.setCenter(new TreeEditor(this, connection, zipTree)), zipTree)
                    );
                } else home(root);
            }
        };

        root.setCenter(new TreeEditor(toHomeOrNav, connection, zipTree));
    }


    /**
     * Handles the credentials required to connect to the database.
     * Reads a file named "DATABASE_CREDENTIALS" which must contain the credentials
     * in the following format:
     *
     * <pre>
     * USER=your_username
     * PASSWORD=your_password
     * </pre>
     * <p>
     * If the file has a valid format, the username and password are extracted
     * and passed to the provided consumer. In case of an error, such as an invalid
     * file format or an exception during reading, an error dialog is displayed
     * to inform the user about the issue.
     *
     * @param startWithCredentials A consumer that will be invoked if the credentials
     *                             are valid, receiving the username and password as parameters.
     */
    public void handleCredentials(BiConsumer<String, String> startWithCredentials) {
        try {
            var userPass = ReadCredentials.read();
            startWithCredentials.accept(userPass.fst(), userPass.snd());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error en el archivo DATABASE_CREDENTIALS, debe tener el siguiente formato");
            alert.setContentText("USER=tu_nombre_de_usuario\nPASSWORD=tu_contraseña\n");
            alert.showAndWait();
        }
    }

    /**
     * Punto principal de entrada para la aplicación JavaFX.
     * Este método se encarga de inicializar la ventana principal y configurar la interfaz gráfica de usuario.
     * Se conecta a la base de datos utilizando las credenciales proporcionadas, muestra un árbol jerárquico
     * almacenado en la base de datos, y gestiona posibles errores de conexión con un mensaje de advertencia.
     *
     * @param primaryStage El escenario principal que se utiliza para mostrar la ventana principal de la aplicación.
     */
    @Override
    public void start(Stage primaryStage) {
        handleCredentials((user, password) -> {
            this.connection = new ConnectionDB("localhost", "3306", user, password, "treefx");
            BorderPane root = new BorderPane();
            try {
                Scene scene = new Scene(root,1280,720);
                primaryStage.setScene(scene);
                primaryStage.setTitle("TreeFX");
                primaryStage.show();
            } catch(Exception e) {
                e.printStackTrace();
            }

            if (!this.connection.success()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error de Conexión");
                alert.setHeaderText("⚠️ No se pudo establecer conexión con la base de datos");
                alert.setContentText("Posibles causas:\n• Servidor no disponible\n• Credenciales incorrectas");
                alert.showAndWait();
            }

            home(root);
        });
    }

    /**
     * Método llamado automáticamente al cerrar la aplicación JavaFX.
     * Se utiliza para liberar recursos, como cerrar conexiones a la base de datos.
     */
    @Override
    public void stop() { if (this.connection != null) this.connection.close(); }

    public static void main(String[] args) {
        launch(args);
    }
}