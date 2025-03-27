package ca.ucalgary.groupprojectgui.p3;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        SceneManager.setStage(primaryStage);

        //switch to login
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/connect4UI.fxml", "Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
