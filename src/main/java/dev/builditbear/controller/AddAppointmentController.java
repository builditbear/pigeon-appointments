package dev.builditbear.controller;

import dev.builditbear.db_interface.ConnectionManager;
import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.Contact;
import dev.builditbear.model.Customer;
import dev.builditbear.model.User;
import dev.builditbear.utility.Alerts;
import dev.builditbear.utility.TimeConversion;
import dev.builditbear.utility.uiManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Handles user interaction with the Add Appointment screen.
 */
public class AddAppointmentController implements Initializable {
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox<Contact> contactComboBox;
    @FXML
    private TextField typeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<LocalDateTime> startComboBox;
    @FXML
    private ComboBox<LocalDateTime> endComboBox;
    @FXML
    private ComboBox<Customer> customerIdComboBox;
    @FXML
    private ComboBox<User> userIdComboBox;
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize custom cell factories for each ComboBox so objects are displayed in a readable fashion to
        // the user (as opposed to the default toString method of Object, which is what displays by default for non-primitive
        // types.
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
        Callback<ListView<LocalDateTime>, ListCell<LocalDateTime>> timeDisplayingCellFactory =
                new Callback<>() {
                    @Override
                    public ListCell<LocalDateTime> call(ListView<LocalDateTime> list) {
                        return new ListCell<>() {
                            @Override
                            protected void updateItem(LocalDateTime dateAndTime, boolean empty) {
                                super.updateItem(dateAndTime, empty);
                                if (dateAndTime == null || empty) {
                                    setGraphic(null);
                                } else {
                                    setText(dateAndTime.format(TimeConversion.standardDateAndTime));
                                }
                            }
                        };
                    }
                };
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
        Callback<ListView<User>, ListCell<User>> userNameAndIdDisplayingCellFactory =
                new Callback<>() {
                    @Override
                    public ListCell<User> call(ListView<User> list) {
                        return new ListCell<>() {
                            @Override
                            protected void updateItem(User user, boolean empty) {
                                super.updateItem(user, empty);
                                if (user == null || empty) {
                                    setGraphic(null);
                                } else {
                                    setText(user.getName() + " (ID: " + user.getId() + ")");
                                }
                            }
                        };
                    }
                };
        ObservableList<User> users = FXCollections.observableArrayList(DbManager.getAllUsers());
        userIdComboBox.setItems(users);
        userIdComboBox.setValue(ConnectionManager.getCurrentUser());
        userIdComboBox.setButtonCell(userNameAndIdDisplayingCellFactory.call(null));
        userIdComboBox.setCellFactory(userNameAndIdDisplayingCellFactory);
        userIdComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                if (user == null) {
                    return "";
                } else {
                    return user.getName() + " (ID: " + user.getId() + ")";
                }
            }
            @Override
            public User fromString(String s) {
                return null;
            }
        });

        ObservableList<Contact> contacts = FXCollections.observableArrayList(DbManager.getAllContacts());
        contactComboBox.setItems(contacts);
        contactComboBox.setButtonCell(contactNameAndIdDisplayingCellFactory.call(null));
        contactComboBox.setCellFactory(contactNameAndIdDisplayingCellFactory);

        datePicker.setValue(LocalDate.now());
        ObservableList<LocalDateTime> availableAppointmentTimes =
                FXCollections.observableArrayList(DbManager.getAvailableStartTimes(datePicker.getValue(), userIdComboBox.getValue()));
        startComboBox.setItems(availableAppointmentTimes);
        startComboBox.setButtonCell(timeDisplayingCellFactory.call(null));
        startComboBox.setCellFactory(timeDisplayingCellFactory);
        endComboBox.setButtonCell(timeDisplayingCellFactory.call(null));
        endComboBox.setCellFactory(timeDisplayingCellFactory);

        ObservableList<Customer> customers = FXCollections.observableArrayList(DbManager.getAllCustomers());
        customerIdComboBox.setItems(customers);
        customerIdComboBox.setButtonCell(customerNameAndIdDisplayingCellFactory.call(null));
        customerIdComboBox.setCellFactory(customerNameAndIdDisplayingCellFactory);
    }

    /**
     * Adjusts the appointment start times and disabled the appointment end times to match a newly selected date.
     * @param e The event generated when the user picks a new date for the appointment.
     */
    @FXML
    private void onDateChanged(ActionEvent e) {
        LocalDate newlySelectedDate = datePicker.getValue();
        endComboBox.disableProperty().set(true);
        endComboBox.setItems(null);
        ObservableList<LocalDateTime> availableAppointmentTimes =
                FXCollections.observableArrayList(DbManager.getAvailableStartTimes(newlySelectedDate, userIdComboBox.getValue()));
        startComboBox.setItems(availableAppointmentTimes);
    }

    /**
     * Alters the appointment end times to match the selected appointment start time.
     * @param e The event created when the user selects an appointment start time.
     */
    @FXML
    private void onStartChanged(ActionEvent e) {
        if(startComboBox.getValue() != null) {
            ObservableList<LocalDateTime> availableAppointmentEndTimes =
                    FXCollections.observableArrayList(DbManager.getAvailableEndTimes(datePicker.getValue(),
                            startComboBox.getValue(), userIdComboBox.getValue()));
            endComboBox.disableProperty().set(false);
            endComboBox.setItems(availableAppointmentEndTimes);
        }
    }

    /**
     * Filters the appointment start times to reflect a change in the selected user.
     * @param e The event generated when the user changes the user associated with this appointment.
     */
    @FXML
    private void onUserChanged(ActionEvent e) {
        User newlySelectedUser = userIdComboBox.getValue();
        ObservableList<LocalDateTime> availableAppointmentStartTimes =
                FXCollections.observableArrayList(DbManager.getAvailableStartTimes(datePicker.getValue(), newlySelectedUser));
        startComboBox.setItems(availableAppointmentStartTimes);
    }

    @FXML
    private void onAddButtonClicked(MouseEvent e) {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String location = locationField.getText();
        String type = typeField.getText();
        LocalDateTime appointmentStart = startComboBox.getValue();
        LocalDateTime appointmentEnd = endComboBox.getValue();
        int customerId = customerIdComboBox.getValue().getId();
        int userId = userIdComboBox.getValue().getId();
        int contactId = contactComboBox.getValue().getId();

        /* The user choices for scheduling an appointment will automatically be filtered such that the selected User
        cannot be double booked and, additionally, should ensure that an appointment cannot be schedule outside of
        business hours. However, per the project requirements, these checks will ensure that the appointment
        is scheduled for a time within business hours, and that the customer selected is not scheduled in two places
        at the same time. */
        LocalDateTime businessOpen = DbManager.generateBusinessOpen(appointmentStart.toLocalDate());
        LocalDateTime businessClose = DbManager.generateBusinessClose(appointmentStart.toLocalDate());
        if(!DbManager.appointmentWithinBusinessHours(appointmentStart, appointmentEnd, businessOpen, businessClose)){
            Alerts.appointmentTimeOutsideBusinessHours();
        } else if(DbManager.customerBeingDoubleBooked(appointmentStart, appointmentEnd, DbManager.getCustomer(customerId))) {
            Alerts.appointmentOverlapsWithExistingAppointment();
        } else {
            DbManager.addAppointment(title, description, location, type, appointmentStart, appointmentEnd, customerId, userId, contactId);
            try {
                uiManager.loadScene("appointments", (Stage) addButton.getScene().getWindow(), "1200x800");
            } catch(IOException ex) {
                System.out.println("IOException occurred in method onAddButtonClicked in AddAppointmentController:");
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Returns the user to the appointment screen.
     * @param e The event generated when the user clicks on the Cancel button.
     */
    @FXML
    private void onCancelButtonClicked(MouseEvent e) {
        try{
            uiManager.loadScene("appointments", (Stage) cancelButton.getScene().getWindow(), "1200x800");
        } catch(IOException ex) {
            System.out.println("An IOException occurred in method onCancelButtonClicked. " +
                    "Please make sure the view you are attempting to load exists.");
        }
    }
}
