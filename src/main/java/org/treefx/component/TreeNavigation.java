package org.treefx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.treefx.model.MovementInSpace;
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.UI;
import org.treefx.utils.adt.Movement;
import org.treefx.utils.adt.T;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Clase que proporciona navegación de nodos en un árbol visual interactivo.
 * <p>
 * Esta clase extiende {@link VBox} y es responsable de renderizar visualmente un árbol, permitiendo
 * al usuario moverse entre nodos, mostrar el contenido actual y administrar botones de navegación.
 * </p>
 */
public class TreeNavigation extends VBox {
    private final Runnable toEditor;

    @FXML private VBox container;
    @FXML private AnchorPane buttons_space;
    @FXML private ImageView node_img;
    @FXML private Label node_name;
    @FXML private Button node_editor;
    private LinkedList<T<Point2D, Button>> btns;

    private Button upButton;
    private Button downButton;
    private Button leftButton;
    private Button rightButton;

    private final ZipTreeStrict<NodeInfo> zipTree;

    /**
     * Constructor de la clase {@code TreeNavigation}.
     *
     * @param toEditor Una acción que se ejecuta al presionar el botón del editor.
     * @param zipTree  Estructura de datos que contiene la información del árbol.
     */
    public TreeNavigation(Runnable toEditor, ZipTreeStrict<NodeInfo> zipTree) {
        this.toEditor = toEditor;
        this.zipTree = zipTree;
        this.btns = new LinkedList<>();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeNavigation.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try { fxmlLoader.load(); } catch (IOException e) { throw new RuntimeException(e); }
    }

    /**
     * Inicializa el componente, configurando los botones y el nodo actual al iniciar.
     */
    @FXML
    public void initialize() {
        renderCurrentNode();
        buildMovementButtons();
        renderMovementButtons();
        this.node_editor.setOnAction(e -> this.toEditor.run());
    }

    /**
     * Renderiza y habilita o deshabilita los botones que permiten mover entre nodos,
     * dependiendo de las opciones de navegación disponibles en el nodo actual.
     */
    public void renderMovementButtons() {
        this.upButton.setDisable(this.zipTree.getCtx().getFather().isNothing());

        this.downButton.setDisable(this.zipTree.getCtx().getChildren().size() == 0);

        this.leftButton.setDisable(!this.zipTree.getCtx().getBrothers().hasPrev());

        this.rightButton.setDisable(!this.zipTree.getCtx().getBrothers().hasNext());
    }

    /**
     * Renderiza el nodo actual junto con los botones asociados a los nodos hijos.
     */
    public void renderCurrentNode() {
        NodeInfo currentNode = this.zipTree.extract();
        for (T<Point2D, Button> button : btns) buttons_space.getChildren().remove(button.snd());

        String imageLoad = getClass().getResource("image-edit.png").toExternalForm();
        Image img = UI.loadImageOrDefault(imageLoad, currentNode.getImgURL());
        node_img.setImage(img);
        resizeImage(container.getWidth(), container.getHeight() - 40);

        LinkedList<MovementInSpace> toChildrens = currentNode.getChildren();
        LinkedList<T<Point2D, Button>> toChildrensBtn = new LinkedList<>();
        this.btns = toChildrensBtn;

        for (var toChild : toChildrens) {
            double x = toChild.getPos().getX() * node_img.getBoundsInParent().getWidth();
            double y = toChild.getPos().getY() * node_img.getBoundsInParent().getHeight();
            Point2D coordsInParent = node_img.localToParent(new Point2D(x, y));

            var toButton = UI.createCircularButtonAt(30, 0, 0);
            toButton.setLayoutX(coordsInParent.getX() - 30);
            toButton.setLayoutY(coordsInParent.getY() - 30);

            toButton.setOnAction(event -> this.moveTo(toChild.getMovements()));
            this.buttons_space.getChildren().add(toButton);
            toChildrensBtn.add(new T.MkT<>(toChild.getPos(), toButton));
        }

        container.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            resizeImage(newValue.getWidth(), newValue.getHeight() - 40);

            for (T<Point2D, Button> button : toChildrensBtn) {
                double x = button.fst().getX() * node_img.getBoundsInParent().getWidth();
                double y = button.fst().getY() * node_img.getBoundsInParent().getHeight();

                Point2D coordsInParent = node_img.localToParent(new Point2D(x, y));
                button.snd().setLayoutX(coordsInParent.getX() - 30);
                button.snd().setLayoutY(coordsInParent.getY() - 30);
            }
        });

        node_name.setText(zipTree.extract().getName());
    }

    /**
     * Ajusta la imagen del nodo para que se redimensione en función del tamaño del contenedor,
     * manteniendo su relación de aspecto.
     *
     * @param containerWidth  El ancho del contenedor.
     * @param containerHeight La altura del contenedor.
     */
    public void resizeImage(double containerWidth, double containerHeight) {
        if (containerHeight / containerWidth >= node_img.getImage().getHeight() / node_img.getImage().getWidth()) {
            node_img.setFitWidth(containerWidth);
            node_img.setFitHeight(-1); // Reset height to maintain aspect ratio
        } else {
            node_img.setFitHeight(containerHeight);
            node_img.setFitWidth(-1); // Reset width to maintain aspect ratio
        }

        node_img.setLayoutX((containerWidth - node_img.getBoundsInParent().getWidth()) / 2);
    }

    /**
     * Cambia al nodo especificado por una lista de movimientos y actualiza la vista.
     *
     * @param nodeCtx Lista de movimientos necesarios para llegar al nodo destino.
     */
    private void moveTo(LinkedList<Movement> nodeCtx) {
        this.zipTree.moveTo(nodeCtx);
        renderCurrentNode();
        renderMovementButtons();
    }

    /**
     * Navega al nodo padre del nodo actual y actualiza la vista.
     */
    private void moveUp() {
        this.zipTree.toFather();
        renderCurrentNode();
        renderMovementButtons();
    }

    /**
     * Navega al nodo hijo del nodo actual y actualiza la vista.
     */
    private void moveDown() {
        this.zipTree.down();
        renderCurrentNode();
        renderMovementButtons();
    }

    /**
     * Navega al nodo hermano izquierdo del nodo actual (anterior) y actualiza la vista.
     */
    private void moveLeft() {
        this.zipTree.prev();
        renderCurrentNode();
        renderMovementButtons();
    }

    /**
     * Navega al nodo hermano derecho del nodo actual (siguiente) y actualiza la vista.
     */
    private void moveRight() {
        this.zipTree.next();
        renderCurrentNode();
        renderMovementButtons();
    }

    /**
     * Construye los botones de navegación (`↑`, `↓`, `←`, `→`) e inicializa
     * su funcionalidad y estilos.
     */
    public void buildMovementButtons() {
        // Create buttons for each direction
        String buttonStyle = """
                -fx-background-color: rgba(255, 255, 255, 0.3);
                -fx-border-color: white;
                -fx-border-width: 1;
                -fx-text-fill: black;
                -fx-font-size: 16;
                -fx-cursor: hand;
                """;

        this.upButton = new Button("↑");
        this.upButton.setStyle(buttonStyle);
        this.upButton.setOnAction(e -> moveUp());

        this.downButton = new Button("↓");
        this.downButton.setStyle(buttonStyle);
        this.downButton.setOnAction(e -> moveDown());

        this.leftButton = new Button("←");
        this.leftButton.setStyle(buttonStyle);
        this.leftButton.setOnAction(e -> moveLeft());

        this.rightButton = new Button("→");
        rightButton.setStyle(buttonStyle);
        this.rightButton.setOnAction(e -> moveRight());

        // Create a GridPane layout
        var gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add buttons to the GridPane with their respective positions
        gridPane.add(this.upButton, 1, 0);    // Centered in the top row
        gridPane.add(this.leftButton, 0, 1);  // Left in the middle row
        gridPane.add(this.rightButton, 2, 1); // Right in the middle row
        gridPane.add(this.downButton, 1, 2);  // Centered in the bottom row

        // Add GridPane to buttons_space and position it in the bottom-right corner
        AnchorPane.setBottomAnchor(gridPane, 10.0);
        AnchorPane.setRightAnchor(gridPane, 10.0);
        this.buttons_space.getChildren().add(gridPane);
    }
}
