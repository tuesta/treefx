module org.treefx {
    exports org.treefx.model.ziptree;
    exports org.treefx.model.ziplist;
    exports org.treefx.model;
    exports org.treefx.utils;
    exports org.treefx.utils.adt;

    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires java.desktop;

    opens org.treefx.view to javafx.fxml;
    exports org.treefx.view;
    exports org.treefx.controller;
    opens org.treefx.controller to javafx.fxml;

    opens org.treefx.app to javafx.fxml;
    exports org.treefx.app;
    exports org.treefx.component;
    opens org.treefx.component to javafx.fxml;
}