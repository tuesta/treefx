package org.treefx.utils;

import javafx.scene.control.Button;
import javafx.scene.image.Image;

public abstract class UI {
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
           """, r, r * 2, r * 2, r * 2, r * 2));

        button.setLayoutX(x - r);
        button.setLayoutY(y - r);

        return button;
    }

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
