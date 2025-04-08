package ca.ucalgary.groupprojectgui.p3;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        Font sampleFont = Fonts.rajdhaniRegular(12);


        SceneManager.setStage(primaryStage);
        // Show the loading screen then load the HomePage.
        SceneManager.switchTo(

                "/ca/ucalgary/groupprojectgui/p3/login.fxml",
                "login Page",
                "login.css"

        );
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
