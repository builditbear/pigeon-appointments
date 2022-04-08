package dev.builditbear.controller;

import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.Appointment;
import dev.builditbear.model.Contact;
import dev.builditbear.utility.Alerts;
import dev.builditbear.utility.TimeConversion;
import dev.builditbear.utility.uiManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class AppointmentsController implements Initializable {
    // Business hours in Eastern Standard Time.
    private static final LocalTime businessOpen = LocalTime.of(8, 0);
    private static final LocalTime businessClose = LocalTime.of(22, 0);
    private static final ZoneId businessTimezone = ZoneId.of("EST", ZoneId.SHORT_IDS);
    private static Appointment selectedAppointment = null;

    public static LocalTime getBusinessOpen() {
        return businessOpen;
    }
    public static LocalTime getBusinessClose() {
        return businessClose;
    }

    public static ZoneId getBusinessTimezone() {
        return businessTimezone;
    }

    public static Appointment getSelectedAppointment() {
        return selectedAppointment;
    }

    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentId;
    @FXML
    private TableColumn<Appointment, String> title;
    @FXML
    private TableColumn<Appointment, String> description;
    @FXML
    private TableColumn<Appointment, String> location;
    @FXML
    private TableColumn<Appointment, String> contact;
    @FXML
    private TableColumn<Appointment, String> type;
    @FXML
    private TableColumn<Appointment, String> start;
    @FXML
    private TableColumn<Appointment, String> end;
    @FXML
    private TableColumn<Appointment, Integer> customerId;
    @FXML
    private TableColumn<Appointment, Integer> userId;
    @FXML
    private Button viewCustomers;
    @FXML
    private Button reportsButton;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Label dateRangeLabel;
    @FXML
    private Label weekIndicatorLabel;
    @FXML
    private Label monthIndicatorLabel;
    @FXML
    private Button previousIntervalButton;
    @FXML
    private Button nextIntervalButton;
    @FXML
    private Button todayButton;
    @FXML
    private Label intervalLabel;

    private LocalDate weekStart = TimeConversion.getWeekStartDate();
    private LocalDate weekEnd = weekStart.plusDays(6);
    private LocalDate monthStart = TimeConversion.getMonthStartDate();
    private LocalDate monthEnd = monthStart.plusDays(getMonthLength(monthStart) - 1);
    private FilteredList<Appointment> displayedAppointments = new FilteredList<>(
            FXCollections.observableArrayList(DbManager.getAllAppointments()), this::appointmentInDisplayedWeek);
    private boolean monthViewToggled = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        configureAppointmentsTable(appointmentId, title, description, location, contact, type, start, end, customerId, userId);
        appointmentsTable.setItems(displayedAppointments);
        dateRangeLabel.setText(weekStart.format(TimeConversion.standardDate) + " - " + weekEnd.format(TimeConversion.standardDate));
    }

    private void configureAppointmentsTable(TableColumn<Appointment, Integer> appointmentId, TableColumn<Appointment, String> title,
                                            TableColumn<Appointment, String> description, TableColumn<Appointment, String> location,
                                            TableColumn<Appointment, String> contact, TableColumn<Appointment, String> type,
                                            TableColumn<Appointment, String> start, TableColumn<Appointment, String> end,
                                            TableColumn<Appointment, Integer> customerId, TableColumn<Appointment, Integer> userId) {
        appointmentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        // Lambda use 5 -- Adjusting the displayed value for this column by supplying a custom defined cell value factory.
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        contact.setCellValueFactory(appointment -> {
            Contact c = DbManager.getContact(appointment.getValue().getContactId());
            return new ReadOnlyObjectWrapper<>(c.getContactName());
        });
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        start.setCellValueFactory(appointment -> {
            LocalDateTime ldt = appointment.getValue().getStart();
            return new ReadOnlyObjectWrapper<>(ldt.format(TimeConversion.standardDateAndTime));
        });
        end.setCellValueFactory(appointment -> {
            LocalDateTime ldt = appointment.getValue().getEnd();
            return new ReadOnlyObjectWrapper<>(ldt.format(TimeConversion.standardDateAndTime));
        });
        customerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    @FXML
    private void onTodayClicked(MouseEvent e) {
        resetToToday();
        setDateRangeLabel();
        displayedAppointments.setPredicate(monthViewToggled ? this::appointmentInDisplayedMonth : this::appointmentInDisplayedWeek);
    }

    @FXML
    private void onNextIntervalClicked(MouseEvent e) {
        nextTimeInterval();
        setDateRangeLabel();
        displayedAppointments.setPredicate(monthViewToggled ? this::appointmentInDisplayedMonth : this::appointmentInDisplayedWeek);
    }

    @FXML
    private void onPreviousIntervalClicked(MouseEvent e) {
        previousTimeInterval();
        setDateRangeLabel();
        displayedAppointments.setPredicate(monthViewToggled ? this::appointmentInDisplayedMonth : this::appointmentInDisplayedWeek);
    }

    @FXML
    private void onToggleClicked(MouseEvent e) {
        if(monthViewToggled) {
            indicateWeekViewActive();
            weekStart = TimeConversion.getWeekStartDate(monthStart);
            weekEnd = weekStart.plusDays(6);
            updateDisplayedAppointments(this::appointmentInDisplayedWeek);
            monthViewToggled = false;
        } else {
            indicateMonthViewActive();
            monthStart = TimeConversion.getMonthStartDate(weekStart);
            monthEnd = monthStart.plusDays(getMonthLength(monthStart) - 1);
            updateDisplayedAppointments(this::appointmentInDisplayedMonth);
            monthViewToggled = true;
        }
        setDateRangeLabel();
        setControlLabels();
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
    private void onReportsClicked(MouseEvent e) {
        try{
            uiManager.loadScene("reports",(Stage) reportsButton.getScene().getWindow(),"1200x800");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onReportsClicked.");
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void onAddClicked(MouseEvent e) {
        try{
            uiManager.loadScene("addAppointment",(Stage) addButton.getScene().getWindow(),"480x480");
        } catch(IOException ex) {
            System.out.println("An IO exception occurred in event handler onAddClicked. " +
                    "Make sure that the view you're attempting to load exists.");
        }
    }

    @FXML
    private void onUpdateClicked(MouseEvent e) {
        selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
        if(selectedAppointment != null) {
            try {
                uiManager.loadScene("updateAppointment", (Stage) updateButton.getScene().getWindow(), "480x480");
            } catch(IOException ex) {
                System.out.println("An IOException occurred in event handler onUpdateClicked in AppointmentController:");
                System.out.println(ex.getMessage());
            }
        }
    }

    @FXML
    private void onDeleteClicked(MouseEvent e) {
        Appointment appointment = appointmentsTable.getSelectionModel().getSelectedItem();
        int result = DbManager.removeAppointment(appointment.getId());
        if(result == 1) {
            Alerts.appointmentDeletedAlert(appointment);
        } else {
            Alerts.appointmentDeleteFailedAlert(appointment);
        }
        updateDisplayedAppointments(monthViewToggled ? this::appointmentInDisplayedMonth : this::appointmentInDisplayedWeek);
    }

    private int getMonthLength(LocalDate d) {
        return d.getMonth().length(d.isLeapYear());
    }

    private boolean appointmentInDisplayedWeek(Appointment appointment) {
        LocalDate appointmentDate = appointment.getStart().toLocalDate();
        return (appointmentDate.isEqual(weekStart) || appointmentDate.isAfter(weekStart)) &&
                (appointmentDate.isBefore(weekEnd) || appointmentDate.isEqual(weekEnd));
    }

    private boolean appointmentInDisplayedMonth(Appointment appointment) {
        LocalDate appointmentDate = appointment.getStart().toLocalDate();
        return (appointmentDate.isEqual(monthStart) || appointmentDate.isAfter(monthStart)) &&
                (appointmentDate.isBefore(monthEnd) || appointmentDate.isBefore(monthEnd));
    }

    private void resetToToday() {
        weekStart = TimeConversion.getWeekStartDate();
        weekEnd = weekStart.plusDays(6);
        monthStart = TimeConversion.getMonthStartDate();
        monthEnd = monthStart.plusDays(getMonthLength(monthStart) - 1);
    }
    private void nextTimeInterval() {
        if(monthViewToggled) {
            monthStart = monthStart.plusDays(getMonthLength(monthStart));
            monthEnd = monthStart.plusDays(getMonthLength(monthStart) - 1);
        } else {
            weekStart = weekStart.plusDays(7);
            weekEnd = weekStart.plusDays(6);
        }
    }

    private void previousTimeInterval() {
        if(monthViewToggled) {
            monthStart = monthStart.minusDays(getMonthLength(monthStart));
            monthEnd = monthStart.plusDays(getMonthLength(monthStart) - 1);
        } else {
            weekStart = weekStart.minusDays(7);
            weekEnd = weekStart.plusDays(6);
        }
    }

    private void setDateRangeLabel() {
        if(monthViewToggled) {
            dateRangeLabel.setText(monthStart.format(TimeConversion.standardDate) + " - " + monthEnd.format(TimeConversion.standardDate));
        } else {
            dateRangeLabel.setText(weekStart.format(TimeConversion.standardDate) + " - " + weekEnd.format(TimeConversion.standardDate));
        }
    }

    private void setControlLabels() {
        if(monthViewToggled) {
            previousIntervalButton.setText("Previous Month");
            nextIntervalButton.setText("Next Month");
            intervalLabel.setText("Month of");
        } else {
            previousIntervalButton.setText("Previous Week");
            nextIntervalButton.setText("Next Week");
            intervalLabel.setText("Week of");
        }
    }

    private void updateDisplayedAppointments(Predicate<Appointment> filter) {
        displayedAppointments = new FilteredList<>(
                FXCollections.observableArrayList(DbManager.getAllAppointments()), filter);
        appointmentsTable.setItems(displayedAppointments);
    }

    private void indicateWeekViewActive() {
        monthIndicatorLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        monthIndicatorLabel.setTextFill(Color.BLACK);
        monthIndicatorLabel.setUnderline(false);
        weekIndicatorLabel.setFont(Font.font("Arial Black", FontWeight.NORMAL, 20));
        weekIndicatorLabel.setTextFill(Color.valueOf("#495d94"));
        weekIndicatorLabel.setUnderline(true);
    }

    private void indicateMonthViewActive() {
        weekIndicatorLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        weekIndicatorLabel.setTextFill(Color.BLACK);
        weekIndicatorLabel.setUnderline(false);
        monthIndicatorLabel.setFont(Font.font("Arial Black", FontWeight.NORMAL, 20));
        monthIndicatorLabel.setTextFill(Color.valueOf("#495d94"));
        monthIndicatorLabel.setUnderline(true);
    }
}