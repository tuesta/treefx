package org.treefx.component;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.TreeCtxStrict;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.Maybe;
import org.treefx.utils.adt.T;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Componente principal de edición visual de árboles.
 * Permite crear, visualizar y manipular la estructura jerárquica de nodos.
 * Soporta inserción, enfoque y operaciones de arrastrar-soltar de nodos.
 */
public class TreeEditor extends AnchorPane {
    @FXML private SplitPane container;
    @FXML private AnchorPane tree;

    private final ZipTreeStrict<NodeInfo> zipTree;
    private NodeCtx nodeCtx;
    private Node currentNode;

    /**
     * Carga la vista definida en el archivo FXML y enlaza los controles UI.
     */
    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeEditor.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try { fxmlLoader.load(); } catch (IOException e) { throw new RuntimeException(e); }
    }

    /**
     * Constructor por defecto. Inicializa un árbol vacío con una raíz.
     */
    public TreeEditor() {
        this.zipTree = new ZipTreeStrict<>(new NodeInfo("", new Maybe.Nothing<>(), new Point2D(0, 0), new LinkedList<>()));
        loadFxml();
    }

    /**
     * Constructor que parte de un árbol ya existente.
     * @param zipTree Árbol a editar
     */
    public TreeEditor(ZipTreeStrict<NodeInfo> zipTree) {
        zipTree.toRoot();
        this.zipTree = zipTree;
       loadFxml();
    }

    /**
     * Inicializador llamado tras cargar el FXML.
     * - Configura el click derecho para insertar nodos.
     * - Renderiza todos los nodos actuales en el árbol.
     * - Establece el nodo inicial como enfocado.
     */
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

        this.currentNode.setFocus();
        this.nodeCtx = new NodeCtx(this.currentNode);
        this.container.getItems().add(this.nodeCtx);
    }

    /**
     * Inserta un nodo hijo en la posición indicada.
     * Actualiza el foco al nuevo nodo y lo agrega visualmente.
     * @param pos Posición donde colocar el nuevo nodo
     */
    public void insertNode(Point2D pos) {
        this.zipTree.insertChild(new NodeInfo("", new Maybe.Nothing<>(), pos, new LinkedList<>()));
        this.currentNode.removeFocus();

        this.currentNode = new Node(new Maybe.Just<>(this.currentNode), this.zipTree.getCtx(), this);
        this.currentNode.renderNode(pos);
        this.currentNode.setFocus();
        this.tree.getChildren().add(this.currentNode);

        this.currentNode.handleLine();
        this.nodeCtx.setNode(this.currentNode);
    }

    /**
     * Cambia el foco al nodo especificado, actualizando la interfaz visual y contextual.
     * @param node Nodo al que cambiar el foco
     */
    public void changeFocus(Node node) {
        this.currentNode.removeFocus();
        node.setFocus();
        this.currentNode = node;
        this.nodeCtx.setNode(this.currentNode);
    }

    /**
     * Habilita la detección y manejo de eventos de arrastrar/soltar para un nodo específico.
     * Cambia el foco y la posición del nodo arrastrado.
     * @param node Nodo a arrastrar/soltar
     */
    public void dragDetection(Node node) {
        container.setOnDragOver(e -> {
            e.acceptTransferModes(TransferMode.ANY);
            e.consume();
        });

        tree.setOnDragDropped(e -> {
            this.currentNode.removeFocus();
            node.setFocus();
            this.currentNode = node;
            Point2D p = tree.sceneToLocal(e.getSceneX(), e.getSceneY());
            node.renderNode(p);
            this.nodeCtx.setNode(this.currentNode);
            e.setDropCompleted(true);
            e.consume();
        });
    }
}