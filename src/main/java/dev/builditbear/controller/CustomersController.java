package dev.builditbear.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {
    @FXML
    private TableView customerTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Populate the customerTable.
    }
}
