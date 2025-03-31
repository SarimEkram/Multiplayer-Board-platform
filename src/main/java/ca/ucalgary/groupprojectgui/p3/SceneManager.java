package ca.ucalgary.groupprojectgui.p3;

import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class SceneManager {
    private static Stage primaryStage;
    private static Scene scene; // reused scene

    public static void setStage(Stage stage) {
        primaryStage = stage;
        // Create an empty scene and set its fill to black.
        scene = new Scene(new Group());
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
    }

    /**
     * Shows a loading screen then transitions with a cross-fade to the main content.
     */
    public static void showLoadingScreenAndLoadMain(String loadingFXMLPath, String mainFXMLPath, String title, String cssFile) {
        try {
            // Load loading screen FXML.
            FXMLLoader loadingLoader = new FXMLLoader(SceneManager.class.getResource(loadingFXMLPath));
            Parent loadingRoot = loadingLoader.load();
            // Set the loading screen as the scene's root.
            scene.setRoot(loadingRoot);
            primaryStage.setTitle("Loading...");

            // Fade in the loading screen.
            loadingRoot.setOpacity(0);
            FadeTransition fadeInLoading = new FadeTransition(Duration.millis(300), loadingRoot);
            fadeInLoading.setFromValue(0);
            fadeInLoading.setToValue(1);
            fadeInLoading.play();

            // Background task to load the main content.
            Task<Parent> loadTask = new Task<Parent>() {
                @Override
                protected Parent call() throws Exception {
                    // Simulate delay (replace with real initialization if needed)
                    Thread.sleep(2000);
                    FXMLLoader mainLoader = new FXMLLoader(SceneManager.class.getResource(mainFXMLPath));
                    return mainLoader.load();
                }
            };

            loadTask.setOnSucceeded(e -> {
                Parent mainRoot = loadTask.getValue();

                // If a CSS file is provided, update the scene's stylesheets.
                if (cssFile != null && !cssFile.isEmpty()) {
                    scene.getStylesheets().clear();
                    URL cssURL = SceneManager.class.getResource("/ca/ucalgary/groupprojectgui/p3/styles/" + cssFile);
                    if (cssURL != null) {
                        scene.getStylesheets().add(cssURL.toExternalForm());
                    } else {
                        System.err.println("CSS file not found: " + cssFile);
                    }
                }

                // Start fade-out transition for the loading screen.
                FadeTransition fadeOutLoading = new FadeTransition(Duration.millis(300), loadingRoot);
                fadeOutLoading.setFromValue(1);
                fadeOutLoading.setToValue(0);
                fadeOutLoading.setOnFinished(event -> {
                    // Once loading screen is fully faded out, update the root.
                    scene.setRoot(mainRoot);
                    primaryStage.setTitle(title);

                    // Fade in the main content.
                    mainRoot.setOpacity(0);
                    FadeTransition fadeInMain = new FadeTransition(Duration.millis(300), mainRoot);
                    fadeInMain.setFromValue(0);
                    fadeInMain.setToValue(1);
                    fadeInMain.play();
                });
                fadeOutLoading.play();
            });

            new Thread(loadTask).start();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * A simple switch method (if needed) that reuses the same Scene.
     */
    public static void switchTo(String fxmlPath, String title, String cssFile) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            scene.setRoot(root);
            if (cssFile != null && !cssFile.isEmpty()) {
                scene.getStylesheets().clear();
                URL cssURL = SceneManager.class.getResource("/ca/ucalgary/groupprojectgui/p3/styles/" + cssFile);
                if (cssURL != null) {
                    scene.getStylesheets().add(cssURL.toExternalForm());
                }
            }
            primaryStage.setTitle(title);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
