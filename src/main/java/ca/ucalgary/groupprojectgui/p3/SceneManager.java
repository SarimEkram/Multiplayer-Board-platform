package ca.ucalgary.groupprojectgui.p3;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getStage() {
        return primaryStage;
    }

    /**
     * Switch to a scene, providing FXML path, window title, and optional CSS file name (inside /styles).
     *
     * @param fxmlPath   Path to the .fxml file
     * @param title      Title of the window
     * @param cssFile    Name of the CSS file (e.g., "connect4.css"), or null to skip loading
     */
    public static void switchTo(String fxmlPath, String title, String cssFile) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            if (cssFile != null && !cssFile.isEmpty()) {
                URL cssURL = SceneManager.class.getResource("/ca/ucalgary/groupprojectgui/p3/styles/" + cssFile);
                if (cssURL != null) {
                    scene.getStylesheets().add(cssURL.toExternalForm());
                } else {
                    System.err.println("⚠️  CSS not found for: " + cssFile);
                }
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
