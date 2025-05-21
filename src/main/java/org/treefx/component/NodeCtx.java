package org.treefx.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.treefx.utils.adt.Maybe;

import java.io.IOException;


/**
 * Componente de interfaz gráfica que permite la edición y visualización
 * del contexto de un nodo, incluyendo su nombre y la URL de su imagen relacionada.
 * Este panel puede ser incrustado en otras vistas para editar la información
 * detallada de un nodo concreto.
 */
public class NodeCtx extends VBox {
    /**
     * Imagen asociada al nodo.
     */
    @FXML private ImageView node_img;
    /**
     * Campo de texto para editar o mostrar la URL de la imagen del nodo.
     */
    @FXML private TextField node_imgURL;
    /**
     * Campo de texto para editar o mostrar el nombre del nodo.
     */
    @FXML private TextField node_name;
    /**
     * Botón para confirmar y actualizar los cambios sobre el nodo.
     */
    @FXML private Button node_update;

    /**
     * Referencia al nodo gestionado por este panel de contexto.
     */
    private Node node;

    /**
     * Establece el nodo al que se le aplica este panel de contexto,
     * cargando su información en los campos correspondientes.
     *
     * @param node Nodo del cual mostrar y editar sus datos.
     */
    public void setNode(Node node) {
        this.node = node;
        this.loadNodeInfo();
    }

    /**
     * Maneja el evento de actualización del nodo,
     * cargando los datos de los campos de texto y guardando los cambios en el nodo asociado.
     * Se invoca por el controlador al pulsar el botón de actualización.
     */
    private void onNodeUpdate() {
        String name = this.node_name.getText();
        String imgURL = this.node_imgURL.getText();

        node.getNodeCtx().getValue().setName(name);
        node.getNodeCtx().getValue().setImgURL(imgURL.isEmpty() ? new Maybe.Nothing<>() : new Maybe.Just<>(imgURL));
        node.loadNodeInfo();
        this.loadNodeInfo();
    }

    /**
     * Constructor.
     * Inicializa el panel de contexto para el nodo especificado,
     * y carga la interfaz a partir del archivo FXML correspondiente.
     *
     * @param node Nodo sobre el cual se visualizará y editará la información.
     */
    public NodeCtx(Node node) {
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
     * Inicializador de la interfaz gráfica tras cargar el archivo FXML.
     * Configura la carga de datos inicial y el comportamiento del botón de actualización.
     */
    @FXML
    public void initialize() {
        loadNodeInfo();
        node_update.setOnAction(event -> onNodeUpdate());
    }

    /**
     * Carga en la interfaz gráfica la información actual del nodo,
     * mostrando el nombre, la imagen (por URL o imagen por defecto si no hay)
     * y la URL de la imagen en los campos correspondientes.
     */
    public void loadNodeInfo() {
        Image imageLoad = new Image(getClass().getResource("image-edit.png").toExternalForm());

        switch (node.getNodeCtx().getValue().getImgURL()) {
            case Maybe.Nothing() -> node_imgURL.setText("");
            case Maybe.Just(String url) -> {
                var mImage = new Image(url);
                if (!mImage.isError()) imageLoad = mImage;
                node_imgURL.setText(url);
            }
        }

        node_img.setImage(imageLoad);
        node_name.setText(this.node.getNodeCtx().getValue().getName());
    }
}