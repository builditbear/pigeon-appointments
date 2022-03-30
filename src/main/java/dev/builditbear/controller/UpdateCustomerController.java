package dev.builditbear.controller;

import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.Country;
import dev.builditbear.model.Customer;
import dev.builditbear.model.FirstLevelDivision;
import dev.builditbear.utility.uiManager;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UpdateCustomerController implements Initializable {
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField postalCode;
    @FXML
    private TextField phoneNumber;
    @FXML
    private ComboBox<String> countryComboBox;
    @FXML
    private ComboBox<String> fldComboBox;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;

    private final FilteredList<String> displayedFirstLevelDivisions =
            new FilteredList<>(FXCollections.observableArrayList(FirstLevelDivision.getFldNames()));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Customer selectedCustomer = CustomersController.getSelectedCustomer();
        idField.setText(Integer.toString(selectedCustomer.getId()));
        nameField.setText(selectedCustomer.getName());
        addressField.setText(selectedCustomer.getAddress());
        postalCode.setText(selectedCustomer.getPostalCode());
        phoneNumber.setText(selectedCustomer.getPhone());

        countryComboBox.setItems(FXCollections.observableArrayList(Country.getCountryNames()));
        fldComboBox.setItems(displayedFirstLevelDivisions);

        String fldName;
        String countryName;

        FirstLevelDivision fld = DbManager.getFirstLevelDivision(selectedCustomer.getDivisionId());
        if(fld != null) {
            Country country = DbManager.getCountry(fld.getCountryId());
            if(country != null) {
                countryComboBox.setValue(country.getCountry());
                updateCountryFilter();
            } else {
                System.out.println("Unable to populate First Level Division due to invalid name.");
            }
            fldComboBox.setValue(fld.getDivision());
        } else {
            System.out.println("Unable to populate country due to invalid name.");
        }
    }

    private void updateCountryFilter() {
        // Lambda expression usage 1.
        displayedFirstLevelDivisions.setPredicate(fld -> DbManager.isAssociatedWithCountry(fld, countryComboBox.getValue()));
    }

    @FXML
    private void onCountryChange(ActionEvent e) {
        updateCountryFilter();
    }

    @FXML
    private void onUpdateButtonClicked(MouseEvent e) {
        DbManager.updateCustomer(Integer.parseInt(idField.getText()), nameField.getText(), addressField.getText(),
                                 postalCode.getText(), phoneNumber.getText(), DbManager.getFldId(fldComboBox.getValue()));
        try {
            uiManager.loadScene("customers", (Stage) updateButton.getScene().getWindow(), "1200x800");
        } catch(IOException ex) {
            System.out.println("An IOException occurred in method onCancelButtonClicked. " +
                    "Please make sure the view you are attempting to load exits.");
        }

    }

    @FXML
    private void onCancelButtonClicked(MouseEvent e) {
        try{
            uiManager.loadScene("customers", (Stage) cancelButton.getScene().getWindow(), "1200x800");
        } catch(IOException ex) {
            System.out.println("An IOException occurred in method onCancelButtonClicked. " +
                    "Please make sure the view you are attempting to load exists.");
        }
    }
}
