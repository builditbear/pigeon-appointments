package dev.builditbear.controller;

import dev.builditbear.App;
import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.Customer;
import dev.builditbear.utility.Alerts;
import dev.builditbear.utility.uiManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

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
    private TableColumn<Customer, Integer> firstLevelDivision;
    @FXML
    private Button deleteButton;
    @FXML
    private Button viewAppointments;
    @FXML
    private Button addButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the customerTable.
        ObservableList<Customer> customers = FXCollections.observableArrayList(DbManager.getAllCustomers());
        populateCustomerTable(customers, customerTable, customerId, name, address, postalCode, phone, createDate,
                createdBy, lastUpdate, lastUpdatedBy, firstLevelDivision);
    }

    @FXML
    private void onDeleteClicked(MouseEvent e) {
        Customer customer = customerTable.getSelectionModel().getSelectedItem();
        DbManager.removeCustomer(customer.getId());
        customerTable.setItems(FXCollections.observableArrayList(DbManager.getAllCustomers()));
        Alerts.customerDeletedAlert(customer);
    }

    @FXML
    private void onAddClicked(MouseEvent e) {
        try{
            uiManager.loadScene("addCustomer",(Stage) addButton.getScene().getWindow(),"480x480");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onAddClicked. " +
                    "Make sure that the view you're attempting to load exists.");
        }
    }

    private void populateCustomerTable(ObservableList<Customer> customers, TableView<Customer> customerTable,
                                  TableColumn<Customer, Integer> customerId, TableColumn<Customer, String> name,
                                  TableColumn<Customer, String> address, TableColumn<Customer, String> postalCode,
                                  TableColumn<Customer, String> phone, TableColumn<Customer, LocalDateTime> createDate,
                                  TableColumn<Customer, String> createdBy, TableColumn<Customer, Timestamp> lastUpdate,
                                  TableColumn<Customer, String> lastUpdatedBy, TableColumn<Customer, Integer> divisionId) {
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
        divisionId.setCellValueFactory(new PropertyValueFactory<>("divisionId"));
    }

    @FXML
    private void onViewAppointmentsClicked(MouseEvent e) {
        try{
            uiManager.loadScene("appointments",(Stage) viewAppointments.getScene().getWindow(),"1200x800");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onViewAppointmentsClicked. " +
                    "Make sure that the view you're attempting to load exists.");
        }
    }
}
