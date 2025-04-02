package ca.ucalgary.groupprojectgui.p3;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        SceneManager.setStage(primaryStage);
        // Show the loading screen then load the HomePage.
        SceneManager.switchTo(

                "/ca/ucalgary/groupprojectgui/p3/HomePage.fxml",
                "Home Page",
                "home.css"

        );
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
