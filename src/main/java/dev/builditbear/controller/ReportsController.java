package dev.builditbear.controller;

import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.Appointment;
import dev.builditbear.model.Contact;
import dev.builditbear.model.Customer;
import dev.builditbear.utility.TimeConversion;
import dev.builditbear.utility.uiManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Handles user interaction with the reports screen, which displays three predefined reports simultaneously:
 * 1) An appointment count for a user specified appointment type in a given month.
 * 2) A list of appointments for a user specified contact person.
 * 3) A list of appointments for a user specified customer.
 */
public class ReportsController implements Initializable {
    @FXML
    private TextField typeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label appointmentCountLabel;
    @FXML
    private ComboBox<Contact> contactComboBox;
    @FXML
    private TableView<Appointment> contactTableView;
    @FXML
    private TableColumn<Appointment, Integer> contactAppointmentId;
    @FXML
    private TableColumn<Appointment, String> contactTitle;
    @FXML
    private TableColumn<Appointment, String> contactDescription;
    @FXML
    private TableColumn<Appointment, String> contactLocation;
    @FXML
    private TableColumn<Appointment, String> contactContact;
    @FXML
    private TableColumn<Appointment, String> contactType;
    @FXML
    private TableColumn<Appointment, String> contactStart;
    @FXML
    private TableColumn<Appointment, String> contactEnd;
    @FXML
    private TableColumn<Appointment, Integer> contactCustomerId;
    @FXML
    private TableColumn<Appointment, Integer> contactUserId;
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private TableView<Appointment> customerTableView;
    @FXML
    private TableColumn<Appointment, Integer> customerAppointmentId;
    @FXML
    private TableColumn<Appointment, String> customerTitle;
    @FXML
    private TableColumn<Appointment, String> customerDescription;
    @FXML
    private TableColumn<Appointment, String> customerLocation;
    @FXML
    private TableColumn<Appointment, String> customerContact;
    @FXML
    private TableColumn<Appointment, String> customerType;
    @FXML
    private TableColumn<Appointment, String> customerStart;
    @FXML
    private TableColumn<Appointment, String> customerEnd;
    @FXML
    private TableColumn<Appointment, Integer> customerCustomerId;
    @FXML
    private TableColumn<Appointment, Integer> customerUserId;
    @FXML
    private Button backToAppointmentsButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Callback<ListView<Contact>, ListCell<Contact>> contactNameAndIdDisplayingCellFactory =
                new Callback<>() {
                    @Override
                    public ListCell<Contact> call(ListView<Contact> list) {
                        return new ListCell<>() {
                            @Override
                            protected void updateItem(Contact contact, boolean empty) {
                                super.updateItem(contact, empty);
                                if (contact == null || empty) {
                                    setGraphic(null);
                                } else {
                                    setText(contact.getContactName() + " (ID: " + contact.getId() + ")");
                                }
                            }
                        };
                    }
                };
        ArrayList<Contact> contacts = DbManager.getAllContacts();
        contactComboBox.setItems(FXCollections.observableArrayList(contacts));
        contactComboBox.setButtonCell(contactNameAndIdDisplayingCellFactory.call(null));
        contactComboBox.setCellFactory(contactNameAndIdDisplayingCellFactory);
        configureAppointmentsTable(contactAppointmentId, contactTitle, contactDescription, contactLocation, contactContact,
                contactType, contactStart, contactEnd, contactCustomerId, contactUserId);
        Callback<ListView<Customer>, ListCell<Customer>> customerNameAndIdDisplayingCellFactory =
                new Callback<>() {
                    @Override
                    public ListCell<Customer> call(ListView<Customer> list) {
                        return new ListCell<>() {
                            @Override
                            protected void updateItem(Customer customer, boolean empty) {
                                super.updateItem(customer, empty);
                                if (customer == null || empty) {
                                    setGraphic(null);
                                } else {
                                    setText(customer.getName() + " (ID: " + customer.getId() + ")");
                                }
                            }
                        };
                    }
                };
        ArrayList<Customer> customers = DbManager.getAllCustomers();
        customerComboBox.setItems(FXCollections.observableArrayList(customers));
        customerComboBox.setButtonCell(customerNameAndIdDisplayingCellFactory.call(null));
        customerComboBox.setCellFactory(customerNameAndIdDisplayingCellFactory);
        configureAppointmentsTable(customerAppointmentId, customerTitle, customerDescription, customerLocation, customerContact,
                customerType, customerStart, customerEnd, customerCustomerId, customerUserId);
    }

    /**
     * Assigns each column of a TableView to an appropriate field in the Appointment object which the TableView holds.
     * @param appointmentId The appointment's ID.
     * @param title The appointment's title.
     * @param description A brief description of the appointment.
     * @param location Where the appointment is being held.
     * @param contact The contact person for this appointment.
     * @param type The kind of appointment.
     * @param start The start time and date for this appointment in the user's local time zone.
     * @param end The end time and date for this appointment in the user's local time zone.
     * @param customerId The customer ID associated with this appointment.
     * @param userId The user ID associated with this appointment.
     */
    private void configureAppointmentsTable(TableColumn<Appointment, Integer> appointmentId, TableColumn<Appointment, String> title,
                                            TableColumn<Appointment, String> description, TableColumn<Appointment, String> location,
                                            TableColumn<Appointment, String> contact, TableColumn<Appointment, String> type,
                                            TableColumn<Appointment, String> start, TableColumn<Appointment, String> end,
                                            TableColumn<Appointment, Integer> customerId, TableColumn<Appointment, Integer> userId) {
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
            return new ReadOnlyObjectWrapper<>(ldt.format(TimeConversion.standardDateAndTime));
        });
        end.setCellValueFactory(appointment -> {
            LocalDateTime ldt = appointment.getValue().getEnd();
            return new ReadOnlyObjectWrapper<>(ldt.format(TimeConversion.standardDateAndTime));
        });
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    /**
     * Recalculates the appointment count whenever the type being counted for is changed by the user typing and deleting
     * characters in the type field.
     * @param e The event generated whenever the user presses a key while the appointment type field is active.
     */
    @FXML
    private void onTypeChanged(KeyEvent e) {
        if(datePicker.getValue() != null) {
            updateAppointmentCount();
        }
    }

    /**
     * Recalculates the appointment count in the event that the user changes the month we're counting for.
     * @param e The event generated by the user interacting with the DatePicker.
     */
    @FXML
    private void onDateChanged(ActionEvent e) {
        updateAppointmentCount();
    }

    /**
     * Repopulates the TableView showing a contact's appointments to reflect the user's changing the selected contact.
     * @param e The event generated by the user interacting with the ComboBox used to select a contact.
     */
    @FXML
    private void onContactChanged(ActionEvent e) {
        ArrayList<Appointment> appointments = DbManager.getAllAppointments();
        Contact selectedContact = contactComboBox.getValue();
        appointments.removeIf(appointment -> appointment.getContactId() != selectedContact.getId());
        contactTableView.setItems(FXCollections.observableArrayList(appointments));
    }

    /**
     * Repopulates the TableView showing a customer's appointments to reflect the user's changing the selected customer.
     * @param e The event generated by the user interacting with the ComboBox used to select a customer.
     */
    @FXML
    private void onCustomerChanged(ActionEvent e) {
        ArrayList<Appointment> appointments = DbManager.getAllAppointments();
        Customer selectedCustomer = customerComboBox.getValue();
        appointments.removeIf(appointment -> appointment.getCustomerId() != selectedCustomer.getId());
        customerTableView.setItems(FXCollections.observableArrayList(appointments));
    }

    /**
     * Returns the User to the appointments screen.
     * @param e The event generated by the user clicking on the 'Back to Appointments' button.
     */
    @FXML
    private void onBackToAppointmentsClicked(MouseEvent e) {
        try{
            uiManager.loadScene("appointments",(Stage) backToAppointmentsButton.getScene().getWindow(),"1200x800");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in onBackToAppointmentsClicked in ReportsController:");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Updates the appointments by reevaluating the selected appointment type and month.
     */
    private void updateAppointmentCount() {
        ArrayList<Appointment> appointments = getAppointmentsWithMonthAndType(typeField.getText(), datePicker.getValue().getMonth());
        appointmentCountLabel.setText(" => " + appointments.size());
    }

    /**
     * Retrieves a list of Appointments that are in the given month and of the given type.
     * @param type The type of appointment to retrieve.
     * @param month The month that the results must take place within.
     * @return
     */
    private ArrayList<Appointment> getAppointmentsWithMonthAndType(String type, Month month) {
        ArrayList<Appointment> appointments = DbManager.getAllAppointments();
        appointments.removeIf(appointment -> !appointment.getType().equals(type) || !appointment.getStart().getMonth().equals(month));
        return appointments;
    }

}
