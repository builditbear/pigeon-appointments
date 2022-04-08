package dev.builditbear.controller;

import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.Customer;
import dev.builditbear.model.FirstLevelDivision;
import dev.builditbear.utility.Alerts;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Handles user interaction with the customers screen.
 */
public class CustomersController implements Initializable {
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> customerId;
    @FXML
    private TableColumn<Customer, String> name;
    @FXML
    private TableColumn<Customer, String> address;
    @FXML
    private TableColumn<Customer, String> postalCode;
    @FXML
    private TableColumn<Customer, String> phone;
    @FXML
    private TableColumn<Customer, LocalDateTime> createDate;
    @FXML
    private TableColumn<Customer, String> createdBy;
    @FXML
    private TableColumn<Customer, Timestamp> lastUpdate;
    @FXML
    private TableColumn<Customer, String> lastUpdatedBy;
    @FXML
    private TableColumn<Customer, String> firstLevelDivision;
    @FXML
    private Button deleteButton;
    @FXML
    private Button viewAppointments;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;

    private static Customer selectedCustomer = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the customerTable.
        ObservableList<Customer> customers = FXCollections.observableArrayList(DbManager.getAllCustomers());
        configureCustomerTable(customers, customerTable, customerId, name, address, postalCode, phone, createDate,
                createdBy, lastUpdate, lastUpdatedBy, firstLevelDivision);
    }

    /**
     * Resets the currently selected customer.
     */
    public static void clearSelectedCustomer() {
        selectedCustomer = null;
    }

    /**
     * Returns the currently selected Customer.
     * @return The currently selected Customer.
     */
    public static Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    /**
     * Removes whichever customer is currently selected in the TableView from both the database and the TableView.
     * @param e The event generated when the user clicks on the delete button.
     */
    @FXML
    private void onDeleteClicked(MouseEvent e) {
        Customer customer = customerTable.getSelectionModel().getSelectedItem();
        DbManager.removeCustomer(customer.getId());
        customerTable.setItems(FXCollections.observableArrayList(DbManager.getAllCustomers()));
        Alerts.customerDeletedAlert(customer);
    }

    /**
     * Takes the user to the update customer screen for whichever customer is currently selected, or does nothing
     * if no customer is selected.
     * @param e The event generated when the user clicks on the Update button.
     */
    @FXML
    private void onUpdateClicked(MouseEvent e) {
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        try {
            uiManager.loadScene("updateCustomer", (Stage) updateButton.getScene().getWindow(), "480x480");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onUpdateClicked. " +
                    "Make sure that the view you're attempting to load exits.");
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Brings the user to add customer screen.
     * @param e The event generated when the user clicks on the Add button.
     */
    @FXML
    private void onAddClicked(MouseEvent e) {
        try{
            uiManager.loadScene("addCustomer",(Stage) addButton.getScene().getWindow(),"480x480");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onAddClicked. " +
                    "Make sure that the view you're attempting to load exists.");
        }
    }

    /**
     * Assigns each column of the customer TableView to a field in the Customer objects the table holds.
     * @param customers A List of all Customers to be shown in the table.
     * @param customerTable The table that will hold the Customers for viewing.
     * @param customerId The ID of a customer.
     * @param name The full name of a customer
     * @param address The address (excluding zipcode and first level division) of a customer.
     * @param postalCode The postal code of a customer.
     * @param phone The phone number of a customer.
     * @param createDate The date a customer's record was created on.
     * @param createdBy The user who created a customer's record.
     * @param lastUpdate The date on which a customer's record was last modified.
     * @param lastUpdatedBy The last user to modify a customer's record.
     * @param firstLevelDivision The first level division a customer resides in.
     */
    private void configureCustomerTable(ObservableList<Customer> customers, TableView<Customer> customerTable,
                                        TableColumn<Customer, Integer> customerId, TableColumn<Customer, String> name,
                                        TableColumn<Customer, String> address, TableColumn<Customer, String> postalCode,
                                        TableColumn<Customer, String> phone, TableColumn<Customer, LocalDateTime> createDate,
                                        TableColumn<Customer, String> createdBy, TableColumn<Customer, Timestamp> lastUpdate,
                                        TableColumn<Customer, String> lastUpdatedBy, TableColumn<Customer, String> firstLevelDivision) {
        customerTable.setItems(customers);
        customerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        createDate.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        createdBy.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        lastUpdate.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        lastUpdatedBy.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));
        // Lambda usage 4 -- Used to render the associated fld's name instead of its ID as contained in the Customer object.
        firstLevelDivision.setCellValueFactory(customer -> {
            FirstLevelDivision fld = DbManager.getFirstLevelDivision(customer.getValue().getDivisionId());
            return new ReadOnlyObjectWrapper<>(fld.getName());
        });
    }

    /**
     * Brings the user to the appointments screen.
     * @param e The event generated when the user clicks on the View Appointments button.
     */
    @FXML
    private void onViewAppointmentsClicked(MouseEvent e) {
        try{
            uiManager.loadScene("appointments",(Stage) viewAppointments.getScene().getWindow(),"1200x800");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onViewAppointmentsClicked. " +
                    "Make sure that the view you're attempting to load exists.");
            System.out.println(ex.getMessage());
        }
    }
}
