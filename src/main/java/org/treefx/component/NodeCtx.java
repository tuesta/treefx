package org.treefx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.treefx.model.ConnectionDB;
import javafx.geometry.Point2D;
import org.treefx.model.MovementInSpace;
import org.treefx.utils.UI;

import java.io.IOException;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * Clase que gestiona el comportamiento y visualización de un nodo en el editor de árboles.
 * <p>
 * NodeCtx es una clase que representa la interfaz gráfica de un nodo, permitiendo establecer su información,
 * cargar su representación visual, y registrar movimientos para incorporar botones interactivos.
 * También gestiona las acciones relacionadas con la navegación y retorno a la vista principal.
 * </p>
 */
public class NodeCtx extends VBox {
    private final Consumer<Boolean> toHomeOrNav;
    private final TreeEditor editor;
    private final ConnectionDB connection;

    @FXML private ImageView node_img;
    @FXML private AnchorPane container_img;
    @FXML private TextField node_imgURL;
    @FXML private TextField node_name;
    @FXML private Button node_update;
    @FXML private Button node_navigation;
    @FXML private Button node_home;
    private LinkedList<Button> btns;
    private Node node;

    /**
     * Establece el nodo que será gestionado por esta instancia.
     * <p>
     * Llama al método {@link #loadNodeInfo()} para cargar y renderizar la información del nodo proporcionado.
     * </p>
     *
     * @param node El nodo a ser gestionado y visualizado.
     */
    public void setNode(Node node) {
        this.node = node;
        this.loadNodeInfo();
    }

    /**
     * Actualiza la información del nodo asociado, como el nombre e imagen, tanto en la base de datos como en la interfaz gráfica.
     */
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

    /**
     * Constructor para crear una instancia de la clase NodeCtx.
     *
     * @param toHomeOrNav Un consumidor que gestiona la navegación entre la vista principal y la vista de navegación.
     * @param editor      El editor de árboles asociado a esta instancia.
     * @param connection  La conexión a la base de datos para actualizar información de nodos.
     * @param node        El nodo que será gestionado por esta instancia de la clase.
     */
    public NodeCtx(Consumer<Boolean> toHomeOrNav, TreeEditor editor, ConnectionDB connection, Node node) {
        this.toHomeOrNav = toHomeOrNav;
        this.editor = editor;
        this.connection = connection;
        this.btns = new LinkedList<>();
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

    /**
     * Inicializa los componentes y registra los manejadores de eventos para la interfaz gráfica del nodo.
     * Este método se ejecuta automáticamente después de cargar el archivo FXML.
     */
    @FXML
    public void initialize() {
        loadNodeInfo();
        node_update.setOnAction(event -> onNodeUpdate());
        node_navigation.setOnAction(event -> this.toHomeOrNav.accept(true));
        node_home.setOnAction(event -> this.toHomeOrNav.accept(false));
        node_img.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();

            double imageSmallWidth = node_img.getBoundsInParent().getWidth();
            double imageSmallHeight = node_img.getBoundsInParent().getHeight();

            double xRelative = x / imageSmallWidth;
            double yRelative = y / imageSmallHeight;

            if (this.editor.handleInsertButton(new Point2D(xRelative, yRelative))) {
                Button smallButton = UI.createCircularButtonAt(15, x, y);
                this.btns.add(smallButton);
                this.container_img.getChildren().add(smallButton);
            }
        });
    }

    /**
     * Renderiza un botón en la posición calculada a partir de un movimiento en el espacio.
     *
     * @param dim             Las dimensiones (ancho y alto) del contenedor del nodo.
     * @param movementInSpace La información de posición del movimiento que determinará dónde se colocará el botón.
     * @return El botón creado y añadido al contenedor de la imagen del nodo.
     */
    private Button renderButtonMovements(Point2D dim, MovementInSpace movementInSpace) {
        double x = movementInSpace.getPos().getX() * dim.getX();
        double y = movementInSpace.getPos().getY() * dim.getY();

        Button smallButton = UI.createCircularButtonAt(15, x, y);
        this.container_img.getChildren().add(smallButton);
        return smallButton;
    }

    /**
     * Carga la información del nodo en la interfaz gráfica.
     * <p>
     * Este método actualiza el nombre del nodo, su imagen, y coloca botones representativos para los movimientos
     * asociados al nodo en el espacio proporcionado.
     * </p>
     */
    public void loadNodeInfo() {
        for (Button btnInSpace : this.btns) {
            System.out.println();
            this.container_img.getChildren().remove(btnInSpace);
        }

        String imageLoad = getClass().getResource("image-edit.png").toExternalForm();
        String nodeURL = this.node.getNodeCtx().getValue().getImgURL();
        Image img = UI.loadImageOrDefault(imageLoad, nodeURL);
        node_imgURL.setText(nodeURL);
        node_img.setImage(img);

        LinkedList<MovementInSpace> movementsInSpace = this.node.getNodeCtx().getValue().getChildren();
        var btnInSpace = new LinkedList<Button>();
        for (var movementInSpace : movementsInSpace) {
            double imageSmallWidth = node_img.getBoundsInParent().getWidth();
            double imageSmallHeight = node_img.getBoundsInParent().getHeight();

            var button = this.renderButtonMovements(new Point2D(imageSmallWidth, imageSmallHeight), movementInSpace);
            btnInSpace.add(button);
        }
        this.btns = btnInSpace;

        node_name.setText(this.node.getNodeCtx().getValue().getName());
    }
}