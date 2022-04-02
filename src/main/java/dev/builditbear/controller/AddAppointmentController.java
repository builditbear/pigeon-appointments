package dev.builditbear.controller;

import dev.builditbear.utility.uiManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {
    @FXML
    private Button cancelButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void onAddButtonClicked(MouseEvent e) {
        System.out.println("DUMMY FUNCTION, DUMMY FUNCTION, AHAHAHAHAHA.");
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
