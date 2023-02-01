module gr.ihu.ict.sportvideoanalysis {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires de.jensd.fx.glyphs.fontawesome;
    // add icon pack modules
    requires org.kordamp.ikonli.fontawesome5;
    requires com.jfoenix;

    opens gr.ihu.ict.sportvideoanalysis to javafx.fxml;
    exports gr.ihu.ict.sportvideoanalysis;
}