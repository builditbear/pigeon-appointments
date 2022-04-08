package dev.builditbear.utility;

import dev.builditbear.App;
import dev.builditbear.model.Appointment;
import dev.builditbear.model.Customer;
import javafx.scene.control.Alert;

import static dev.builditbear.utility.TimeConversion.standardDateAndTime;

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

    public static void appointmentDeletedAlert(Appointment appointment) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(App.getBundle().getString("appointment_deleted_title"));
        alert.setContentText(App.getBundle().getString("appointment_deleted_text") + " " +
                appointment.getId() + ", " + appointment.getType() );
        alert.showAndWait();
    }

    public static void appointmentDeleteFailedAlert(Appointment appointment) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(App.getBundle().getString("appointment_delete_failed_title"));
        alert.setContentText(App.getBundle().getString("appointment_delete_failed_text") + " " +
                appointment.getId() + ", " + appointment.getType());
        alert.showAndWait();
    }

    public static void appointmentTimeOutsideBusinessHours() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(App.getBundle().getString("appointment_outside_business_title"));
        alert.setContentText(App.getBundle().getString("appointment_outside_business_text"));
        alert.showAndWait();
    }

    public static void appointmentOverlapsWithExistingAppointment() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(App.getBundle().getString("appointment_overlap_title"));
        alert.setContentText(App.getBundle().getString("appointment_overlap_text"));
        alert.showAndWait();
    }

    public static void appointmentImminent(Appointment appointment) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(App.getBundle().getString("appointment_imminent_title"));
        alert.setContentText(App.getBundle().getString("appointment_imminent_text") + "\nID " +
                appointment.getId() + ", " + appointment.getStart().format(standardDateAndTime));
        alert.showAndWait();
    }

    public static void noAppointmentsImminent() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(App.getBundle().getString("no_imminent_appointments_title"));
        alert.setContentText(App.getBundle().getString("no_imminent_appointments_text"));
        alert.showAndWait();
    }
}
