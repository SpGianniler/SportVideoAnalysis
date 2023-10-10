module gr.ihu.ict.sportvideoanalysis {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    // add icon pack modules
    requires de.jensd.fx.glyphs.fontawesome;
    requires org.kordamp.ikonli.fontawesome5;
    requires com.jfoenix;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.io;
    requires java.sql;

    opens gr.ihu.ict.sportvideoanalysis to javafx.fxml;
    exports gr.ihu.ict.sportvideoanalysis;
}