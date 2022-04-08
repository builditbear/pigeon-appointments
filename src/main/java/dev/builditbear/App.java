package dev.builditbear;

import dev.builditbear.db_interface.ConnectionManager;
import dev.builditbear.utility.uiManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The entry point for this program.
 */
public class App extends Application {
    private static final Locale locale = Locale.getDefault();
    private static final ResourceBundle bundle = ResourceBundle.getBundle(("dev/builditbear/pigeonApp"), locale);

    @Override
    public void start(Stage stage) throws IOException {
        ConnectionManager.openConnection();
        uiManager.loadScene("login", stage, "480x480");
    }

    /**
     * Retrieve the assigned locale.
     * @return The assigned locale.
     */
    public static Locale getLocale() {
        return locale;
    }

    /**
     * Retrieve the assigned resource bundle. It currently supports English and French for the login screen and all alerts.
     * @return The assigned resource bundle.
     */
    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static void main(String[] args) {
        launch();
    }

}