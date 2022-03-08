module dev.builditbear {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;

    opens dev.builditbear;
    opens dev.builditbear.controller to javafx.fxml;
    exports dev.builditbear;
    exports dev.builditbear.controller;
}
