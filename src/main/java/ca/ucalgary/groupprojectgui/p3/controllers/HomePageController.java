package ca.ucalgary.groupprojectgui.p3.controllers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.TilePane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import ca.ucalgary.groupprojectgui.p3.SceneManager;

public class HomePageController {
    @FXML private TextField gameSearchField;
    @FXML private TilePane gameTilePane;


    @FXML private Label welcomeLabel;
    @FXML private ListView<String> friendsList;
    @FXML private ListView<String> recentScores;
    @FXML private Button quickMatchButton;
    @FXML private Button logoutButton;

    // 👉 New for preview feature
    @FXML private ImageView previewImage;
    @FXML private Button connect4Btn;
    @FXML private Button tttBtn;
    @FXML private Button chessBtn;

    @FXML
    public void initialize() {
        // Simulate logged-in user
        String playerName = "Generic Player";
        welcomeLabel.setText("Welcome, " + playerName + "!");

        // Simulate online friends
        ObservableList<String> friends = FXCollections.observableArrayList(
                "Player1 (Online)", "Player2 (Online)", "Player3 (Online)"
        );
        friendsList.setItems(friends);

        // Simulate recent scores
        ObservableList<String> scores = FXCollections.observableArrayList(
                "Tic Tac Toe - Win vs Player1",
                "Connect 4 - Loss vs Player2"
        );
        recentScores.setItems(scores);

        // Setup image preview on hover
        setupPreview(connect4Btn, "/ca/ucalgary/groupprojectgui/p3/images/connect4_preview.jpg");
        setupPreview(tttBtn, "/ca/ucalgary/groupprojectgui/p3/images/tictactoe_preview.jpg");
        setupPreview(chessBtn, "/ca/ucalgary/groupprojectgui/p3/images/chess_preview.jpg");

        setupGameSearch();

    }

    private void setupPreview(Button button, String imagePath) {
        var url = getClass().getResource(imagePath);

        if (url == null) {
            System.err.println("❌ Image not found at path: " + imagePath);
            return;
        }

        Image image = new Image(url.toExternalForm());

        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            previewImage.setImage(image);
            previewImage.setVisible(true);
        });

        button.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            previewImage.setVisible(false);
        });
    }


    @FXML
    private void onConnect4Click() {
        launchGame("Connect 4");
    }

    @FXML
    private void handleQuickMatch() {
        System.out.println("Searching for quick match...");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/login.fxml", "Login Page", "login.css");
    }

    public void launchGame(String gameName) {
        System.out.println("Launching game: " + gameName);

        String fxmlFile;
        String title;
        String cssFile;

        switch (gameName) {
            case "Connect 4":
                fxmlFile = "/ca/ucalgary/groupprojectgui/p3/connect4UI.fxml";
                title = "Connect 4 Game";
                cssFile = "connect4.css";
                break;

            default:
                System.out.println("Game not recognized: " + gameName);
                return;
        }

        SceneManager.switchTo(fxmlFile, title, cssFile);
    }
    private void setupGameSearch() {
        gameSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal.toLowerCase();

            for (javafx.scene.Node node : gameTilePane.getChildren()) {
                if (node instanceof Button btn) {
                    String text = btn.getText().toLowerCase();
                    btn.setVisible(text.contains(query));
                    btn.setManaged(text.contains(query)); // avoids empty layout space
                }
            }
        });
    }

}
