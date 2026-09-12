package ca.ucalgary.groupprojectgui.p3;

import MatchmakingLeaderboard.persistence.DataSourceProvider;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Ensures the connection pool is built and schema migrations are applied at startup,
        // rather than lazily on first use, so connectivity problems surface immediately.
        DataSourceProvider.getDataSource();

        Font sampleFont = Fonts.rajdhaniRegular(12);


        SceneManager.setStage(primaryStage);
        // Show the loading screen then load the HomePage.
        SceneManager.switchTo(

                "/ca/ucalgary/groupprojectgui/p3/views/login.fxml"


        );
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
