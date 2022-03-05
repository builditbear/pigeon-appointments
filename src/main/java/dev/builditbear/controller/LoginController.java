package dev.builditbear.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LoginController implements Initializable{
    @FXML
    private ImageView logo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //String.valueOf(LoginController.class.getResource("logo.png")
        Image logoImage = new Image("/dev/builditbear/logo-with-label.png");
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
}
