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
import org.treefx.model.NodeInfo;
import org.treefx.model.ziptree.ZipTreeStrict;
import org.treefx.utils.adt.Maybe;

import java.io.IOException;

/**
 * Componente visual para la navegación en un árbol tipo "zipper", permitiendo
 * al usuario moverse entre nodos y visualizar información del nodo actual.
 */
public class TreeNavigation extends VBox {
    @FXML private VBox container;
    @FXML private AnchorPane buttons_space;
    @FXML private ImageView node_img;
    @FXML private Label node_name;
    private Button upButton;
    private Button downButton;
    private Button leftButton;
    private Button rightButton;

    private final ZipTreeStrict<NodeInfo> zipTree;

    /**
     * Constructor: inicializa el componente y carga la vista desde FXML.
     * @param zipTree árbol zipper a visualizar y navegar.
     */
    public TreeNavigation(ZipTreeStrict<NodeInfo> zipTree) {
        this.zipTree = zipTree;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TreeNavigation.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try { fxmlLoader.load(); } catch (IOException e) { throw new RuntimeException(e); }
    }


    /**
     * Metodo invocado automáticamente por JavaFX al inicializar la vista.
     * Renderiza el nodo actual y crea los botones de movimiento.
     */
    @FXML
    public void initialize() {
        renderCurrentNode();
        buildMovementButtons();
        renderMovementButtons();
    }

    /**
     * Crea los botones de movimiento (arriba, abajo, izq, der), los configura y los agrega al layout.
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

    /**
     * (Des)habilita los botones de navegación según la validez del movimiento,
     * dependiendo del contexto actual del árbol.
     */
    public void renderMovementButtons() {
        this.upButton.setDisable(this.zipTree.getCtx().getFather().isNothing());

        this.downButton.setDisable(this.zipTree.getCtx().getChildren().size() == 0);

        this.leftButton.setDisable(!this.zipTree.getCtx().getBrothers().hasPrev());

        this.rightButton.setDisable(!this.zipTree.getCtx().getBrothers().hasNext());
    }

    /**
     * Muestra la información visual del nodo actual: imagen y nombre.
     * Toma la URL de la imagen del nodo, o una imagen por defecto.
     */
    public void renderCurrentNode() {
        Image imageLoad = new Image(getClass().getResource("image-edit.png").toExternalForm());
        switch (zipTree.getCtx().getValue().getImgURL()) {
            case Maybe.Nothing() -> {}
            case Maybe.Just(String url) -> {
                var mImage = new Image(url);
                if (!mImage.isError()) imageLoad = mImage;
            }
        }

        node_img.setImage(imageLoad);

        resizeImage(container.getWidth(), container.getHeight() - 40);
        container.layoutBoundsProperty().addListener((observable, oldValue, newValue) ->
            resizeImage(newValue.getWidth(), newValue.getHeight() - 40)
        );

        node_name.setText(zipTree.getCtx().getValue().getName());
    }

    /**
     * Redimensiona la imagen del nodo respetando la relación de aspecto y centro.
     * @param "containerWidth" Ancho disponible
     * @param "containerHeight" Alto disponible
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
     * Navega al nodo padre, si existe. Actualiza la interfaz tras el movimiento.
     */
    private void moveUp() {
        this.zipTree.toFather();
        renderCurrentNode();
        renderMovementButtons();
    }

    /**
     * Navega al primer hijo del nodo, si existe. Actualiza la interfaz tras el movimiento.
     */
    private void moveDown() {
        this.zipTree.down();
        renderCurrentNode();
        renderMovementButtons();
    }

    /**
     * Navega al hermano anterior, si existe. Actualiza la interfaz tras el movimiento.
     */
    private void moveLeft() {
        this.zipTree.prev();
        renderCurrentNode();
        renderMovementButtons();
    }

    /**
     * Navega al próximo hermano, si existe. Actualiza la interfaz tras el movimiento.
     */
    private void moveRight() {
        this.zipTree.next();
        renderCurrentNode();
        renderMovementButtons();
    }
}
