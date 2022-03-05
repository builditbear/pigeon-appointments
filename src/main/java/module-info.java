module dev.builditbear {
    requires javafx.controls;
    requires javafx.fxml;

    opens dev.builditbear;
    opens dev.builditbear.controller to javafx.fxml;
    exports dev.builditbear;
    exports dev.builditbear.controller;
}
