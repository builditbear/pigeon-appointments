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

public class AppointmentsController implements Initializable {

    @FXML
    private Button viewCustomers;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void onViewCustomersClicked(MouseEvent e) {
        try{
            uiManager.loadScene("customers",(Stage) viewCustomers.getScene().getWindow(),"1200x800");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onViewAppointmentsClicked.");
        }
    }
}