package org.treefx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.treefx.model.ConnectionDB;
import org.treefx.model.MovementInSpace;
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.Movement;

import java.io.IOException;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Editor visual de árboles que permite la creación, navegación y manipulación de nodos.
 * <p>
 * La clase {@code TreeEditor} proporciona una interfaz gráfica para editar estructuras
 * tipo árbol utilizando JavaFX. Se integra con una base de datos para almacenar
 * información de nodos y movimientos, y permite operaciones como inserción, cambio
 * de foco, gestión de eventos de ratón y detección de arrastres.
 * </p>
 *
 * <h3>Características principales:</h3>
 * <ul>
 *     <li>Gestión de nodos mediante un modelo de árbol zipper ({@link ZipTreeStrict}).</li>
 *     <li>Actualización dinámica del entorno gráfico basado en eventos JavaFX.</li>
 *     <li>Soporte para la conexión a una base de datos a través de {@link ConnectionDB}.</li>
 * </ul>
 *
 * <p>
 * Para inicializar esta clase, es necesario proporcionar un consumidor que
 * permita volver a la pantalla principal o navegación, una conexión a la base
 * de datos, y un árbol zipper preconfigurado.
 * </p>
 *
 * <h3>Constructores:</h3>
 * <ul>
 *     <li>{@link #TreeEditor(Consumer, ConnectionDB, ZipTreeStrict)}</li>
 * </ul>
 *
 * <h3>Métodos relevantes:</h3>
 * <ul>
 *     <li>{@link #insertNode(Point2D)}: Inserta un nuevo nodo en una posición específica.</li>
 *     <li>{@link #handleInsertButton(Point2D)}: Maneja la inserción de movimientos desde un nodo coactual.</li>
 *     <li>{@link #changeFocus(Node)}: Cambia el foco al nodo proporcionado.</li>
 *     <li>{@link #changeCoFocus(Node)}: Cambia el foco coactual a otro nodo.</li>
 *     <li>{@link #dragDetection(Node)}: Configura la lógica de detección de arrastre para un nodo.</li>
 * </ul>
 *
 * @see ZipTreeStrict
 * @see ConnectionDB
 * @see Node
 */
public class TreeEditor extends AnchorPane {
    private final Consumer<Boolean> toHomeOrNav;
    private final ConnectionDB connection;

    @FXML private SplitPane container;
    @FXML private AnchorPane tree;

    private final ZipTreeStrict<NodeInfo> zipTree;
    private NodeCtx nodeCtx;
    private Node currentNode;
    private Maybe<Node> mCoCurrentNode;

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeEditor.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try { fxmlLoader.load(); } catch (IOException e) { throw new RuntimeException(e); }
    }

    public TreeEditor(Consumer<Boolean> toHomeOrNav, ConnectionDB connection, ZipTreeStrict<NodeInfo> zipTree) {
        this.toHomeOrNav = toHomeOrNav;
        this.connection = connection;
        this.mCoCurrentNode = new Maybe.Nothing<>();
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

            // sin Platform.runLater
            newNode.handleLine();

            return newNode;
        });

        // Reload-layout
        this.layout();

        this.currentNode.setFocus();
        this.nodeCtx = new NodeCtx(this.toHomeOrNav, this, this.connection, this.currentNode);
        this.container.getItems().add(this.nodeCtx);
    }

    /**
     * Inserta un nuevo nodo en el árbol en la posición especificada.
     *
     * @param pos La posición en coordenadas (x, y) donde se insertará el nodo.
     */
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

    /**
     * Maneja la inserción de un movimiento desde el nodo coactual hacia una posición especificada.
     *
     * @param pos La posición en coordenadas (x, y) del movimiento.
     * @return {@code true} si se completa la operación de inserción, o {@code false} si no hay nodo coactual.
     */
    public boolean handleInsertButton(Point2D pos) {
        switch (mCoCurrentNode) {
            case Maybe.Nothing() -> { return false; }
            case Maybe.Just(Node coCurrentNode) -> {
                LinkedList<Movement> moves = this.zipTree.getRelativePosition(coCurrentNode.getNodeCtx());
                MovementInSpace movementInSpace = new MovementInSpace(pos, moves);
                connection.insertMovementInSpace(currentNode.getNodeCtx().getCurrent().getCurrent().fst().getId(), movementInSpace);
                this.zipTree.extract().addPos(movementInSpace);
                return true;
            }
        }
    }

    /**
     * Cambia el foco al nodo proporcionado.
     *
     * @param node El nuevo nodo al cual se cambiará el foco.
     */
    public void changeFocus(Node node) {
        this.zipTree.setCtx(node.getNodeCtx());

        switch (this.mCoCurrentNode) {
            case Maybe.Just(Node coCurrentNode) -> {
                if (coCurrentNode == node) this.mCoCurrentNode = new Maybe.Nothing<>();
            }
            case Maybe.Nothing() -> {}
        }

        this.currentNode.removeFocus();
        node.setFocus();
        this.currentNode = node;
        this.nodeCtx.setNode(this.currentNode);
    }

    /**
     * Cambia el foco coactual al nodo especificado.
     * Ignora la operación si el nodo es igual al nodo con foco actual.
     *
     * @param node El nuevo nodo al cual se cambiará el foco coactual.
     */
    public void changeCoFocus(Node node) {
        if (node == this.currentNode) return;
        switch (this.mCoCurrentNode) {
            case Maybe.Nothing() -> {}
            case Maybe.Just(Node coCurrentNode) -> coCurrentNode.removeFocus();
        }

        this.mCoCurrentNode = new Maybe.Just<>(node);
        node.setCoFocus();
    }

    /**
     * Configura la detección de arrastre para un nodo específico.
     * Permite mover el nodo mediante un evento de arrastre del ratón en el entorno gráfico.
     *
     * @param node El nodo al que se asociará la lógica de detección de arrastre.
     */
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