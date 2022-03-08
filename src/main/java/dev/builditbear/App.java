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
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Locale locale;

    @Override
    public void start(Stage stage) throws IOException {
        locale = Locale.getDefault();
        ConnectionManager.openConnection();
        uiManager.loadScene("login",stage,locale);
    }

    /**
     * Retrieve the assigned locale.
     * @return The assigned locale.
     */
    public static Locale getLocale() {
        return locale;
    }

    public static void main(String[] args) {
        launch();
    }

}