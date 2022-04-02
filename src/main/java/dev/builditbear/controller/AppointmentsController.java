package dev.builditbear.controller;

import dev.builditbear.model.Appointment;
import dev.builditbear.model.Customer;
import dev.builditbear.utility.uiManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppointmentsController implements Initializable {

    @FXML
    private TableView appointmentsTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentId;
    @FXML
    private TableColumn<Appointment, String> title;
    @FXML
    private TableColumn<Appointment, String> description;
    @FXML
    private TableColumn<Appointment, String> location;
    @FXML
    private TableColumn<Appointment, Integer> contact;
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

    @FXML
    private void onAddClicked(MouseEvent mouseEvent) {
        System.out.println("You monster - you CLICKED me!");
    }
}