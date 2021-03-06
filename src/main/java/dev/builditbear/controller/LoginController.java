package dev.builditbear.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.ResourceBundle;

import dev.builditbear.db_interface.ConnectionManager;
import dev.builditbear.db_interface.DbManager;
import dev.builditbear.model.Appointment;
import dev.builditbear.model.User;
import dev.builditbear.utility.Alerts;
import dev.builditbear.utility.TimeConversion;
import dev.builditbear.utility.uiManager;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * Handles user interaction with the app's login screen.
 */
public class LoginController implements Initializable{
    @FXML
    private ImageView logo;
    @FXML
    private Label timeLabel;
    @FXML
    private TextField userField;
    @FXML
    private TextField pwField;
    @FXML
    private Button loginButton;
    private LocalDateTime currentLdt = LocalDateTime.now();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image logoImage = new Image("/dev/builditbear/logo-with-label.png");
        clipLogo(logoImage);

        /* Start a timer routine that will update LocalDateTime every second, such that the user is always viewing
           the current time to the second. */
        new AnimationTimer() {
            private long timeOfLastTick = 0;
            @Override
            public void handle(long now) {
                if(now - timeOfLastTick >= 1_000_000L) {
                    timeOfLastTick = now;
                    currentLdt = LocalDateTime.now();
                    timeLabel.setText(resourceBundle.getString("localTime") + ": " + TimeConversion.printZonedLocalTime(currentLdt));
                }
            }
        }.start();
    }

    /**
     * Clips the given logo with a transparent rectangular mask in order to give it rounded corners.
     * @param logoImage The logo image to be clipped.
     */
    private void clipLogo(Image logoImage) {
        logo.setImage(logoImage);
        // Create a rounded rectangle we can use as a clipping mask for the otherwise square logo.
        Rectangle imgClipper = new Rectangle(logo.getFitWidth(), logo.getFitHeight());
        imgClipper.setArcHeight(20);
        imgClipper.setArcWidth(20);
        logo.setClip(imgClipper);

        // Snapshot the clipped image before reloading.
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = logo.snapshot(parameters, null);

        logo.setClip(null);

        logo.setImage(image);
    }

    /**
     * Event handler for the login form's login button. It retrieves the entered username and password from their
     * respective fields and attempts to authenticate them by querying the database's user table. If the authentication
     * process is unsuccessful due to an invalid username or password being entered, an alert explaining this will be
     * depicted via the GUI.
     * @param e An event generated by the user's click on the login button.
     */
    @FXML
    private void onLoginClicked(MouseEvent e) {
        String username = userField.getText();
        String password = pwField.getText();
        boolean authenticationSuccessful = ConnectionManager.authenticateLogin(username, password);

        if(authenticationSuccessful) {
            logAuthenticationAttempt(true);
            Appointment upcomingAppointment = appointmentWithinFifteenMinutes(ConnectionManager.getCurrentUser());
            if(upcomingAppointment != null) {
                Alerts.appointmentImminent(upcomingAppointment);
            } else {
                Alerts.noAppointmentsImminent();
            }
            try{
                uiManager.loadScene("appointments",(Stage) loginButton.getScene().getWindow(),"1200x800");
            } catch(IOException ex) {
                System.out.println("An IO exception occurred in method onLoginClicked. " +
                                   "Make sure that the view you're attempting to load exists.");
                System.out.println(ex.getMessage());
            }
        } else {
            logAuthenticationAttempt(false);
        }

    }

    /**
     * Determines whether or not the given user has any upcoming appointments taking place in the next 15 minutes.
     * @param user The user who is logging in.
     * @return The earliest appointment taking place in the next 15 minutes, and null if no appointments take place in
     * the next 15 minutes.
     */
    private Appointment appointmentWithinFifteenMinutes(User user) {
        ArrayList<Appointment> appointments = DbManager.getUsersBookedAppointments(user);
        for(Appointment appointment : appointments) {
            LocalDateTime start = appointment.getStart();
            if(start.isAfter(LocalDateTime.now()) && start.isBefore(LocalDateTime.now().plusMinutes(15))) {
                return appointment;
            }
        }
        return null;
    }

    /**
     * Logs all login attempts to a txt file found in the root directory of this program by default.
     * @param loginSuccessful Whether or not the login attempt in question succeeded.
     */
    private void logAuthenticationAttempt(boolean loginSuccessful) {
        try {
            File logFile = new File("login_activity.txt");
            if(logFile.createNewFile()) {
                System.out.println("No log file detected. New log file created.");
            }
            LocalDateTime loginTime = LocalDateTime.now();
            FileWriter logger = new FileWriter("login_activity.txt", true);
            logger.write("\nUser '" + userField.getText() + "'" + (loginSuccessful ? " successfully" : " unsuccessfully") +
                    " attempted to log in on " + loginTime.format(TimeConversion.standardDate) + " @ " +
                    Timestamp.valueOf(loginTime));
            logger.close();
        } catch(IOException ex) {
            System.out.println("An IOException occurred in method logAuthenticationAttempt:");
            System.out.println(ex.getMessage());
        }
    }
}
