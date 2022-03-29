package dev.builditbear.controller;

import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.Country;
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

public class AddCustomerController implements Initializable {
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
    private Button cancelButton;
    @FXML
    private Button addButton;

    private final FilteredList<String> displayedFirstLevelDivisions =
            new FilteredList<>(FXCollections.observableArrayList(FirstLevelDivision.getFldNames()));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        countryComboBox.setItems(FXCollections.observableArrayList(Country.getCountryNames()));
        fldComboBox.setItems(displayedFirstLevelDivisions);
    }

    @FXML
    private void onCountryChange(ActionEvent e) {
        String newCountry = countryComboBox.getValue();
        displayedFirstLevelDivisions.setPredicate(fld -> DbManager.isAssociatedWithCountry(fld, newCountry));
    }

    @FXML
    private void onAddButtonClicked(MouseEvent e) {
        DbManager.addCustomer(nameField.getText(), addressField.getText(), postalCode.getText(),
                              phoneNumber.getText(), DbManager.getFldId(fldComboBox.getValue()));
        try {
            uiManager.loadScene("customers", (Stage) addButton.getScene().getWindow(), "1200x800");
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
