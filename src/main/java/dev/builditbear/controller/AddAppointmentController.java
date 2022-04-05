package dev.builditbear.controller;

import dev.builditbear.db_interface.DbManager;
import dev.builditbear.utility.TimeConversion;
import dev.builditbear.utility.uiManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<LocalDateTime> startComboBox;
    @FXML
    private ComboBox<LocalDateTime> endComboBox;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setValue(LocalDate.now());
        ObservableList<LocalDateTime> availableAppointmentTimes =
                FXCollections.observableArrayList(DbManager.getAvailableStartTimes(datePicker.getValue()));
        startComboBox.setItems(availableAppointmentTimes);
        Callback<ListView<LocalDateTime>, ListCell<LocalDateTime>> timeDisplayingCellFactory =
                new Callback<>() {
                    @Override
                    public ListCell<LocalDateTime> call(ListView<LocalDateTime> list) {
                        return new ListCell<>() {
                            @Override
                            protected void updateItem(LocalDateTime dateAndTime, boolean empty) {
                                super.updateItem(dateAndTime, empty);
                                if (dateAndTime == null || empty) {
                                    setGraphic(null);
                                } else {
                                    setText(dateAndTime.format(TimeConversion.standard));
                                }
                            }
                        };
                    }
                };
        startComboBox.setButtonCell(timeDisplayingCellFactory.call(null));
        startComboBox.setCellFactory(timeDisplayingCellFactory);
        endComboBox.setButtonCell(timeDisplayingCellFactory.call(null));
        endComboBox.setCellFactory(timeDisplayingCellFactory);
    }

    @FXML
    private void onDateChanged(ActionEvent e) {
        LocalDate newlySelectedDate = datePicker.getValue();
        endComboBox.disableProperty().set(true);
        endComboBox.setItems(null);
        ObservableList<LocalDateTime> availableAppointmentTimes =
                FXCollections.observableArrayList(DbManager.getAvailableStartTimes(newlySelectedDate));
        startComboBox.setItems(availableAppointmentTimes);
    }

    @FXML
    private void onStartChanged(ActionEvent e) {
        ObservableList<LocalDateTime> availableAppointmentEndTimes =
                FXCollections.observableArrayList(DbManager.getAvailableEndTimes(datePicker.getValue(), startComboBox.getValue()));
        endComboBox.disableProperty().set(false);
        endComboBox.setItems(availableAppointmentEndTimes);
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
