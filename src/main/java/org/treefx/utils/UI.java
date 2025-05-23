package org.treefx.utils;

import javafx.scene.control.Button;
import javafx.scene.image.Image;

public abstract class UI {
    /**
     * Crea un botón circular y lo posiciona en la ubicación especificada.
     *
     * @param r el radio del botón circular
     * @param x la coordenada X de la posición donde se centra el botón
     * @param y la coordenada Y de la posición donde se centra el botón
     * @return un objeto Button estilizado con forma circular ubicado en la posición especificada
     */
    public static Button createCircularButtonAt(int r, double x, double y) {
        Button button = new Button();

        button.setStyle(String.format("""
           -fx-background-color: rgba(255, 0, 0, 0.3);
           -fx-text-fill: white;
           -fx-background-radius: %dpx;
           -fx-min-width: %dpx;
           -fx-min-height: %dpx;
           -fx-max-width: %dpx;
           -fx-max-height: %dpx;
           -fx-cursor: hand;
           """, r, r * 2, r * 2, r * 2, r * 2));

        button.setLayoutX(x - r);
        button.setLayoutY(y - r);

        return button;
    }


    /**
     * Carga una imagen desde una URL o utiliza una predeterminada en caso de error.
     *
     * @param imageLoad la URL de la imagen predeterminada
     * @param murl      la URL alternativa de la imagen
     * @return un objeto Image cargado desde la URL alternativa o desde la predeterminada si ocurre un error
     */
    public static Image loadImageOrDefault(String imageLoad, String murl) {
        String url = murl.isEmpty() ? imageLoad : murl;
        try {
            var mImage = new Image(url);
            if (mImage.isError()) mImage = new Image(imageLoad);
            return mImage;
        } catch (Exception e) {
            return new Image(imageLoad);
        }
    }
}
