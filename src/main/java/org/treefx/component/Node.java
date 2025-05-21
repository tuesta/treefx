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

/**
 * Componente visual que representa un nodo individual dentro del editor de árbol.
 * Proporciona la visualización, edición y las interacciones de arrastre, selección y dibujo de líneas de conexión.
 * Cada instancia gestiona su propio contexto de datos y su relación (líneas) con un posible nodo padre.
 */
public class Node extends VBox {
    /** Contenedor principal del nodo en la interfaz gráfica. */
    @FXML private VBox node_container;
    /** Imagen asociada al nodo (puede ser personalizada o por defecto). */
    @FXML private ImageView node_img;
    /** Etiqueta que muestra el nombre del nodo. */
    @FXML private Label node_name;

    /** Referencia opcional al nodo padre (None si es la raíz). */
    private final Maybe<Node> mNodeFather;
    /** Línea de conexión visual con el padre (si existe). */
    private Maybe<Line> mline;
    /** Contexto de árbol que almacena la información y relaciones de este nodo. */
    private final TreeCtxStrict<NodeInfo> nodeCtx;
    /** Referencia al editor de árbol que contiene este nodo. */
    private final TreeEditor editor;

    /**
     * Obtiene el contexto de árbol del nodo.
     * @return Contexto de árbol con la información y relaciones del nodo.
     */
    public TreeCtxStrict<NodeInfo> getNodeCtx() { return nodeCtx; }

    /**
     * Constructor principal del nodo visual.
     *
     * @param mNodeFather  Nodo padre (opcional), necesario para dibujar la conexión.
     * @param nodeCtx      Contexto de árbol y datos asociados.
     * @param editor       Editor de árbol al que pertenece este nodo.
     */
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

    /**
     * Configura y mantiene visualmente la línea de conexión con el nodo padre.
     * Debe llamarse después de que el nodo haya sido añadido a la escena.
     * Escucha cambios de tamaño y posición, ajustando la línea automáticamente.
     */
    public void handleLine() {
        switch (this.mNodeFather) {
            case Maybe.Nothing() -> {}
            case Maybe.Just(Node nodeFather) ->
                this.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
                    System.out.println(newValue.getWidth());
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

    /**
     * Crea o actualiza la línea de conexión gráfica entre este nodo y el padre,
     * posicionando los extremos de la línea en sus coordenadas actuales.
     *
     * @param father Punto de conexión en el padre.
     * @param child  Punto de conexión en este nodo.
     */
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

    /**
     * Inicializador llamado automáticamente por JavaFX tras cargar el FXML.
     * Configura interacciones del usuario: arrastre y cambio de foco.
     * Carga la información visual del nodo.
     */
    @FXML
    public void initialize() {
        loadNodeInfo();

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

    /**
     * Inicializador llamado automáticamente por JavaFX tras cargar el FXML.
     * Configura interacciones del usuario: arrastre y cambio de foco.
     * Carga la información visual del nodo.
     */
    public void setFocus() {
        String cssLayout = """
                -fx-border-color: red;
                -fx-border-insets: 5;
                -fx-border-width: 3;
                """;

        this.setStyle(cssLayout);
    }

    /**
     * Quita los estilos de selección, devolviendo el nodo a su apariencia normal.
     */
    public void removeFocus() {
        String cssLayout = """
                -fx-border-color: white;
                -fx-border-insets: 5;
                -fx-border-width: 3;
                """;
        this.setStyle(cssLayout);
    }

    /**
     * Posiciona y reubica el nodo gráficamente según las coordenadas locales especificadas.
     * Actualiza la posición también en el modelo de datos.
     *
     * @param localCoords Coordenadas donde ubicar el nodo en la interfaz.
     */
    public void renderNode(Point2D localCoords) {
        this.nodeCtx.getValue().setPos(localCoords);

        this.relocate(
                (int) localCoords.getX() - (this.getBoundsInParent().getWidth() / 2),
                (int) localCoords.getY() - (this.getBoundsInParent().getHeight() / 2)
        );
    }

    /**
     * Carga visualmente la información del nodo: imagen (URL personalizable o por defecto) y nombre.
     * Si no hay nombre, muestra "[name]" como placeholder.
     */
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