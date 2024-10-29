module com.footballfixture {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires javafx.graphics;
    requires java.base;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.opencsv;
    requires java.desktop;
    requires org.apache.commons.collections4;
    requires org.apache.commons.compress;
    requires org.apache.commons.io;

    opens com.footballfixture to javafx.fxml;
    exports com.footballfixture;
    exports com.footballfixture.model;
    opens com.footballfixture.model to javafx.fxml;
}
