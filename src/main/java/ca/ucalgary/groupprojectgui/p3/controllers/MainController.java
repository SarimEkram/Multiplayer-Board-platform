package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() throws IOException {
        welcomeText.setText("Welcome to OMG");

        URL fxml = getClass().getResource("/ca/ucalgary/groupprojectgui/p3/connect4UI.fxml");
        System.out.println("Loaded path: " + fxml);  // debug

        if (fxml == null) {
            throw new IOException("FXML file not found!");
        }

        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();

        Stage stage = (Stage) welcomeText.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Connect 4 Game");
        stage.show();
    }
}
