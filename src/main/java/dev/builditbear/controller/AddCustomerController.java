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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Handles user interaction with the add customer screen.
 */
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
    private ComboBox<FirstLevelDivision> fldComboBox;
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;

    private final FilteredList<FirstLevelDivision> displayedFirstLevelDivisions =
            new FilteredList<>(FXCollections.observableArrayList(DbManager.getAllFirstLevelDivisions()));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

    }

    /**
     * Adjusts the shown first level divisions based on the currently selected country, such that only first level divisions
     * in that country are available for selection.
     * @param e The event generated when the user changes the selected country.
     */
    @FXML
    private void onCountryChange(ActionEvent e) {
        String newCountry = countryComboBox.getValue();
        displayedFirstLevelDivisions.setPredicate(fld -> DbManager.isAssociatedWithCountry(fld, newCountry));
        if(!DbManager.isAssociatedWithCountry(fldComboBox.getValue(), newCountry)) {
            fldComboBox.setValue(null);
        }
    }

    /**
     * Gathers the new customer's info from the form's fields, adds the new customer to the database, and then returns
     * the user to the customers screen.
     * @param e The event generated when the user clicks on the Add buttong.
     */
    @FXML
    private void onAddButtonClicked(MouseEvent e) {
        DbManager.addCustomer(nameField.getText(), addressField.getText(), postalCode.getText(),
                              phoneNumber.getText(), fldComboBox.getValue().getId());
        try {
            uiManager.loadScene("customers", (Stage) addButton.getScene().getWindow(), "1200x800");
        } catch(IOException ex) {
            System.out.println("An IOException occurred in method onAddButtonClicked. " +
                    "Please make sure the view you are attempting to load exits.");
        }

    }

    /**
     * Returns the user to the customers screen.
     * @param e The event created by the user clicking on the Cancel buttong.
     */
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
