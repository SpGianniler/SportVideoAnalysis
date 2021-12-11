module gr.ihu.ict.sportvideoanalysis {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    // add icon pack modules
    requires org.kordamp.ikonli.fontawesome5;

    opens gr.ihu.ict.sportvideoanalysis to javafx.fxml;
    exports gr.ihu.ict.sportvideoanalysis;
}