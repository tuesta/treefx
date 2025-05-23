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
import org.treefx.utils.UI;
import org.treefx.utils.adt.Maybe;

import java.io.IOException;

/**
 * Representa un nodo en una interfaz gráfica interactiva.
 * <p>
 * Esta clase extiende de {@link VBox} y utiliza componentes de JavaFX para mostrar
 * información de un nodo como su imagen, nombre, y relaciones con otros nodos. También maneja
 * eventos de interacción del usuario, como arrastrar, hacer clic y actualizar las posiciones de los nodos en la interfaz.
 * </p>
 */
public class Node extends VBox {
    @FXML private VBox node_container;
    @FXML private ImageView node_img;
    @FXML private Label node_name;
    /**
     * Referencia opcional al nodo padre de este nodo.
     */
    private final Maybe<Node> mNodeFather;
    /**
     * Línea que representa la conexión visual entre este nodo y su nodo padre, si existe.
     */
    private Maybe<Line> mline;
    /**
     * Contexto del árbol que contiene información asociada a este nodo.
     */
    private final TreeCtxStrict<NodeInfo> nodeCtx;
    /**
     * Referencia al editor asociado que administra las interacciones con este nodo.
     */
    private final TreeEditor editor;

    /**
     * Obtiene el contexto del árbol asociado con este nodo.
     *
     * @return El contexto del árbol que contiene la información de este nodo.
     */
    public TreeCtxStrict<NodeInfo> getNodeCtx() {
        return nodeCtx;
    }

    /**
     * Constructor de la clase Node.
     *
     * @param mNodeFather Una referencia opcional al nodo padre, si existe.
     * @param nodeCtx     El contexto del árbol que contiene los datos asociados al nodo.
     * @param editor      El editor que administra la interacción con este nodo.
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
     * Configura la lógica para manejar la línea que conecta este nodo con su padre.
     */
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

    /**
     * Actualiza la posición de la línea de conexión entre este nodo y su nodo padre.
     *
     * @param father Las coordenadas del nodo padre.
     * @param child  Las coordenadas del nodo hijo.
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
            if (e.getButton() == MouseButton.MIDDLE) {
                editor.changeCoFocus(this);
            }
        });
    }

    /**
     * Establece el enfoque en este nodo, cambiando su estilo visual para indicar que está seleccionado.
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
     * Establece el co-enfoque en este nodo, cambiando su estilo visual para indicar que está co-seleccionado.
     */
    public void setCoFocus() {
        String cssLayout = """
                -fx-border-color: green;
                -fx-border-insets: 5;
                -fx-border-width: 3;
                """;

        this.setStyle(cssLayout);
    }

    /**
     * Elimina el enfoque de este nodo, restaurando su estilo visual al estado predeterminado.
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
     * Renderiza el nodo en la interfaz gráfica en las coordenadas locales especificadas.
     *
     * @param localCoords Las coordenadas locales donde se debe posicionar el nodo.
     */
    public void renderNode(Point2D localCoords) {
        this.nodeCtx.getValue().setPos(localCoords);

        this.relocate(
                (int) localCoords.getX() - (this.getBoundsInParent().getWidth() / 2),
                (int) localCoords.getY() - (this.getBoundsInParent().getHeight() / 2)
        );
    }

    /**
     * Carga y renderiza la información visual del nodo, incluyendo su imagen y nombre.
     */
    public void loadNodeInfo() {
        String imageLoad = getClass().getResource("image-edit.png").toExternalForm();
        Image img = UI.loadImageOrDefault(imageLoad, nodeCtx.getValue().getImgURL());
        node_img.setImage(img);

        String nameLoad = nodeCtx.getValue().getName();
        node_name.setText(nameLoad.isEmpty() ? "[name]" : nameLoad);
    }
}