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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    private ComboBox<FirstLevelDivision> fldComboBox;
    @FXML
    private Button updateButton;
    @FXML
    private Button cancelButton;

    private final FilteredList<FirstLevelDivision> displayedFirstLevelDivisions =
            new FilteredList<>(FXCollections.observableArrayList(DbManager.getAllFirstLevelDivisions()));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Customer selectedCustomer = CustomersController.getSelectedCustomer();
        idField.setText(Integer.toString(selectedCustomer.getId()));
        nameField.setText(selectedCustomer.getName());
        addressField.setText(selectedCustomer.getAddress());
        postalCode.setText(selectedCustomer.getPostalCode());
        phoneNumber.setText(selectedCustomer.getPhone());

        countryComboBox.setItems(FXCollections.observableArrayList(Country.getCountryNames()));
        // Create a custom cell factory for the fldComboBox so that the displayed value is the fld's name.
        Callback<ListView<FirstLevelDivision>, ListCell<FirstLevelDivision>> cellFactory =
                new Callback<>() {
                    @Override
                    public ListCell<FirstLevelDivision> call(ListView<FirstLevelDivision> list) {
                        return new ListCell<>() {
                            @Override
                            protected void updateItem(FirstLevelDivision firstLevelDivision, boolean empty) {
                                super.updateItem(firstLevelDivision, empty);
                                if (firstLevelDivision == null || empty) {
                                    setGraphic(null);
                                } else {
                                    setText(firstLevelDivision.getName());
                                }
                            }
                        };
                    }
                };
        fldComboBox.setButtonCell(cellFactory.call(null));
        fldComboBox.setCellFactory(cellFactory);
        fldComboBox.setItems(displayedFirstLevelDivisions);

        String fldName;
        String countryName;

        FirstLevelDivision fld = DbManager.getFirstLevelDivision(selectedCustomer.getDivisionId());
        if(fld != null) {
            Country country = DbManager.getCountry(fld.getCountryId());
            if(country != null) {
                countryComboBox.setValue(country.getName());
                // Lambda expression usage 1.
                displayedFirstLevelDivisions.setPredicate(fldEntry -> DbManager.isAssociatedWithCountry(fldEntry, countryComboBox.getValue()));
            } else {
                System.out.println("Unable to populate First Level Division due to invalid name.");
            }
            fldComboBox.setValue(fld);
        } else {
            System.out.println("Unable to populate country due to invalid name.");
        }
    }

    @FXML
    private void onCountryChange(ActionEvent e) {
        String newCountry = countryComboBox.getValue();
        // Lambda expression usage 2.
        displayedFirstLevelDivisions.setPredicate(fld -> DbManager.isAssociatedWithCountry(fld, countryComboBox.getValue()));
        if(!DbManager.isAssociatedWithCountry(fldComboBox.getValue(), newCountry)) {
            fldComboBox.setValue(null);
        }
    }

    @FXML
    private void onUpdateButtonClicked(MouseEvent e) {
        DbManager.updateCustomer(Integer.parseInt(idField.getText()), nameField.getText(), addressField.getText(),
                                 postalCode.getText(), phoneNumber.getText(), fldComboBox.getValue().getId());
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
