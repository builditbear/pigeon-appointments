package dev.builditbear.utility;

import dev.builditbear.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public final class uiManager {
    private uiManager() {
        throw new RuntimeException("Instantiation of uiManager is not allowed.");
    }
    /**
     * Loads the specified FXML resource with the appropriate ResourceBundle and returns the result as a Parent node.
     * @param fxml The name of the FXML resource to be loaded.
     * @param bundle The ResourceBundle appropriate for the program user's locale.
     * @return A Parent node containing a scene to be rendered on a Stage.
     * @throws IOException Thrown in the event that the FXML resource specified is not available.
     */
    public static Parent loadFXML(String fxml, ResourceBundle bundle) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"), bundle);
        return fxmlLoader.load();
    }

    /**
     * Loads a new scene with the view resource passed in, sets it on the stage, and then shows it. This method
     * will load the appropriate resource bundle as well. Renders the scene at the given resolution.
     * @param view The name of the view resource we intend to load. Must match the name of the associated ResourceBundle,
     *             minus the language and country code.
     * @param stage The stage a new scene is to be rendered upon.
     * @param resolution The desired dimensions of the rendered scene in pixels, in the format IntegerxInteger.
     * @throws IOException Thrown in the event that the specified resource is not available for the FXML Loader.
     */
    public static void loadScene(String view, Stage stage, String resolution) throws IOException {
        String[] dimensions = resolution.split("x");
        Scene scene = new Scene(loadFXML(view, App.getBundle()), Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
        // Remove focus from the UserID textfield, which is focused by default, allowing the prompt text to be seen.
        scene.getRoot().requestFocus();
        stage.setScene(scene);
        stage.show();
    }
}
