package dev.builditbear.controller;

import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.*;
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

public class UpdateAppointmentController implements Initializable {
    @FXML
    private TextField idField;
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
    private Button updateButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize custom cell factories for each ComboBox so objects are displayed in a readable fashion to
        // the user (as opposed to the default toString method of Object, which is what displays by default for non-primitive
        // types).
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

        // Populate the selected appointment's info.
        Appointment selectedAppointment = AppointmentsController.getSelectedAppointment();
        idField.setText(Integer.toString(selectedAppointment.getId()));
        titleField.setText(selectedAppointment.getTitle());
        descriptionField.setText(selectedAppointment.getDescription());
        locationField.setText(selectedAppointment.getLocation());
        contactComboBox.setValue(DbManager.getContact(selectedAppointment.getContactId()));
        typeField.setText(selectedAppointment.getType());
        datePicker.setValue(selectedAppointment.getStart().toLocalDate());
        startComboBox.setValue(selectedAppointment.getStart());
        endComboBox.setValue(selectedAppointment.getEnd());
        customerIdComboBox.setValue(DbManager.getCustomer(selectedAppointment.getCustomerId()));
        userIdComboBox.setValue(DbManager.getUser(selectedAppointment.getUserId()));

        ObservableList<Contact> contacts = FXCollections.observableArrayList(DbManager.getAllContacts());
        contactComboBox.setItems(contacts);
        contactComboBox.setButtonCell(contactNameAndIdDisplayingCellFactory.call(null));
        contactComboBox.setCellFactory(contactNameAndIdDisplayingCellFactory);
        contactComboBox.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(Contact contact) {
                        if (contact == null) {
                            return "";
                        } else {
                            return contact.getContactName() + " (ID: " + contact.getId() + ")";
                        }
                    }
                    @Override
                    public Contact fromString(String s) {
                        return null;
                    }
        });

        ObservableList<LocalDateTime> availableAppointmentStartTimes =
                FXCollections.observableArrayList(DbManager.getAvailableStartTimes(datePicker.getValue(), userIdComboBox.getValue()));
        startComboBox.setItems(availableAppointmentStartTimes);
        startComboBox.setButtonCell(timeDisplayingCellFactory.call(null));
        startComboBox.setCellFactory(timeDisplayingCellFactory);
        startComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDateTime dateAndTime) {
                if (dateAndTime == null) {
                    return "";
                } else {
                    return dateAndTime.format(TimeConversion.standardDateAndTime);
                }
            }
            @Override
            public LocalDateTime fromString(String s) {
                return null;
            }
        });

        ObservableList<LocalDateTime> availableAppointmentEndTimes =
                FXCollections.observableArrayList(DbManager.getAvailableEndTimes(datePicker.getValue(),
                        startComboBox.getValue(), userIdComboBox.getValue()));
        endComboBox.setItems(availableAppointmentEndTimes);
        endComboBox.setButtonCell(timeDisplayingCellFactory.call(null));
        endComboBox.setCellFactory(timeDisplayingCellFactory);
        endComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDateTime dateAndTime) {
                if (dateAndTime == null) {
                    return "";
                } else {
                    return dateAndTime.format(TimeConversion.standardDateAndTime);
                }
            }
            @Override
            public LocalDateTime fromString(String s) {
                return null;
            }
        });

        ObservableList<Customer> customers = FXCollections.observableArrayList(DbManager.getAllCustomers());
        customerIdComboBox.setItems(customers);
        customerIdComboBox.setButtonCell(customerNameAndIdDisplayingCellFactory.call(null));
        customerIdComboBox.setCellFactory(customerNameAndIdDisplayingCellFactory);
        customerIdComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer customer) {
                if (customer == null) {
                    return "";
                } else {
                    return customer.getName() + " (ID: " + customer.getId() + ")";
                }
            }
            @Override
            public Customer fromString(String s) {
                return null;
            }
        });

        ObservableList<User> users = FXCollections.observableArrayList(DbManager.getAllUsers());
        userIdComboBox.setItems(users);
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
    }

    @FXML
    private void onDateChanged(ActionEvent e) {
        LocalDate newlySelectedDate = datePicker.getValue();
        endComboBox.disableProperty().set(true);
        endComboBox.setItems(null);
        ObservableList<LocalDateTime> availableAppointmentTimes =
                FXCollections.observableArrayList(DbManager.getAvailableStartTimes(newlySelectedDate, userIdComboBox.getValue()));
        startComboBox.setItems(availableAppointmentTimes);
    }

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

    @FXML
    private void onUpdateClicked(MouseEvent e) {
        int id = Integer.parseInt(idField.getText());
        String title = titleField.getText();
        String description = descriptionField.getText();
        String location = locationField.getText();
        String type = typeField.getText();
        LocalDateTime appointmentStart = startComboBox.getValue();
        LocalDateTime appointmentEnd = endComboBox.getValue();
        int customerId = customerIdComboBox.getValue().getId();
        int userId = userIdComboBox.getValue().getId();
        int contactId = contactComboBox.getValue().getId();

        DbManager.updateAppointment(id, title, description, location, type, appointmentStart, appointmentEnd, customerId, userId, contactId);
        try {
            uiManager.loadScene("appointments", (Stage) updateButton.getScene().getWindow(), "1200x800");
        } catch(IOException ex) {
            System.out.println("IOException occurred in method onAddButtonClicked in UpdateAppointmentController:");
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void onUserChanged(MouseEvent e) {
        User newlySelectedUser = userIdComboBox.getValue();
        ObservableList<LocalDateTime> availableAppointmentStartTimes =
                FXCollections.observableArrayList(DbManager.getAvailableStartTimes(datePicker.getValue(), newlySelectedUser));
        startComboBox.setItems(availableAppointmentStartTimes);
    }

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
