package dev.builditbear.controller;

import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.Appointment;
import dev.builditbear.model.Contact;
import dev.builditbear.model.Customer;
import dev.builditbear.utility.Alerts;
import dev.builditbear.utility.TimeConversion;
import dev.builditbear.utility.uiManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class AppointmentsController implements Initializable {
    // Business hours in Eastern Standard Time.
    private static final LocalTime businessOpen = LocalTime.of(8, 0);
    private static final LocalTime businessClose = LocalTime.of(22, 0);
    private static final ZoneId businessTimezone = ZoneId.of("EST");

    public static LocalTime getBusinessOpen() {
        return businessOpen;
    }
    public static LocalTime getBusinessClose() {
        return businessClose;
    }

    public static ZoneId getBusinessTimezone() {
        return businessTimezone;
    }

    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentId;
    @FXML
    private TableColumn<Appointment, String> title;
    @FXML
    private TableColumn<Appointment, String> description;
    @FXML
    private TableColumn<Appointment, String> location;
    @FXML
    private TableColumn<Appointment, String> contact;
    @FXML
    private TableColumn<Appointment, String> type;
    @FXML
    private TableColumn<Appointment, String> start;
    @FXML
    private TableColumn<Appointment, String> end;
    @FXML
    private TableColumn<Appointment, Integer> customerId;
    @FXML
    private TableColumn<Appointment, Integer> userId;
    @FXML
    private Button viewCustomers;
    @FXML
    private Button addButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList(DbManager.getAllAppointments());
        populateAppointmentsTable(appointments, appointmentId, title, description, location, contact, type, start, end, customerId, userId);
    }

    private void populateAppointmentsTable(ObservableList<Appointment> appointments,
                                           TableColumn<Appointment, Integer> appointmentId, TableColumn<Appointment, String> title,
                                           TableColumn<Appointment, String> description, TableColumn<Appointment, String> location,
                                           TableColumn<Appointment, String> contact, TableColumn<Appointment, String> type,
                                           TableColumn<Appointment, String> start, TableColumn<Appointment, String> end,
                                           TableColumn<Appointment, Integer> customerId, TableColumn<Appointment, Integer> userId) {
        appointmentsTable.setItems(appointments);
        appointmentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        // Lambda use 5 -- Adjusting the displayed value for this column by supplying a custom defined cell value factory.
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        contact.setCellValueFactory(appointment -> {
            Contact c = DbManager.getContact(appointment.getValue().getContactId());
            return new ReadOnlyObjectWrapper<>(c.getContactName());
        });
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        start.setCellValueFactory(appointment -> {
            LocalDateTime ldt = appointment.getValue().getStart();
            return new ReadOnlyObjectWrapper<>(ldt.format(TimeConversion.standard));
        });
        end.setCellValueFactory(appointment -> {
            LocalDateTime ldt = appointment.getValue().getStart();
            return new ReadOnlyObjectWrapper<>(ldt.format(TimeConversion.standard));
        });
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    @FXML
    private void onViewCustomersClicked(MouseEvent e) {
        try{
            uiManager.loadScene("customers",(Stage) viewCustomers.getScene().getWindow(),"1200x800");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onViewAppointmentsClicked.");
        }
    }

    @FXML
    private void onAddClicked(MouseEvent e) {
        try{
            uiManager.loadScene("addAppointment",(Stage) addButton.getScene().getWindow(),"480x480");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onAddClicked. " +
                    "Make sure that the view you're attempting to load exists.");
        }
    }

    @FXML
    private void onDeleteClicked(MouseEvent e) {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();
        int result = DbManager.removeAppointment(appointment.getId());
        if(result == 1) {
            Alerts.appointmentDeletedAlert(appointment);
        } else {
            Alerts.appointmentDeleteFailedAlert(appointment);
        }
        appointmentsTable.setItems(FXCollections.observableArrayList(DbManager.getAllAppointments()));
    }
}