package dev.builditbear.utility;

import dev.builditbear.App;
import dev.builditbear.model.Customer;
import javafx.scene.control.Alert;

public final class Alerts {

    private Alerts(){
        throw new RuntimeException("Instantiation of Alerts is not allowed.");
    }

    public static void invalidUserAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(App.getBundle().getString("invalid_user_title"));
        alert.setContentText(App.getBundle().getString("invalid_user_txt"));
        alert.showAndWait();
    }

    public static void invalidPasswordAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(App.getBundle().getString("invalid_pw_title"));
        alert.setContentText(App.getBundle().getString("invalid_pw_txt"));
        alert.showAndWait();
    }

    public static void customerDeletedAlert(Customer customer) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(App.getBundle().getString("customer_deleted_title"));
        alert.setContentText(App.getBundle().getString("customer_deleted_text") + "\n" + customer.getName() + "," +
                " Customer ID: " + customer.getId());
        alert.showAndWait();
    }
}