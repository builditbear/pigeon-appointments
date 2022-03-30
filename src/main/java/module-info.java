module dev.builditbear {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;

    opens dev.builditbear;
    opens dev.builditbear.controller to javafx.fxml;
    opens dev.builditbear.model to javafx.base;
    exports dev.builditbear;
    exports dev.builditbear.controller;
    exports dev.builditbear.model;
}
