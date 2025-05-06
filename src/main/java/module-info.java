module org.treefx.treefx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens org.treefx.view to javafx.fxml;
    exports org.treefx.view;
    exports org.treefx.controller;
    opens org.treefx.controller to javafx.fxml;
}