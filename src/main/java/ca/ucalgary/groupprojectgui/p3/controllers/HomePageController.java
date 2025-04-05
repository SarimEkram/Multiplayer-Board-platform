package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.User;
import Authentication.UserDatabase;
import Authentication.FriendDatabase;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;
import ca.ucalgary.groupprojectgui.p3.SceneManager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HomePageController {

    // Fields for game/home page
    @FXML private TextField gameSearchField;
    @FXML private HBox gameTilePane;
    @FXML private ImageView img;
    @FXML private StackPane popupContainer;
    @FXML private VBox rightPanel;

    @FXML private ImageView heartIcon;
    @FXML private ImageView bellIcon;
    @FXML private ImageView friendsIcon;
    @FXML private ImageView profileIcon;

    @FXML private Label welcomeLabel;
    @FXML private ListView<String> recentScores;
    @FXML private Button quickMatchButton;
    @FXML private Button logoutButton;
    @FXML private ImageView connect4Image;
    @FXML private ImageView tttImage;
    @FXML private ImageView checkersImage;

    // Fields for preview feature
    @FXML private ImageView previewImage;
    @FXML private Button connect4Btn;
    @FXML private Button tttBtn;
    @FXML private Button checkersBtn;

    // Field for home pane
    @FXML private BorderPane homePane;

    // Fields for Friend Requests functionality
    @FXML private TextField searchField;
    @FXML private VBox playersContainer;
    private List<Player> allPlayers;

    public static int friendOpponentID;

    @FXML
    public void initialize() {
        friendOpponentID = -1; // no friend is selected as opponent

        // Initialize Home Page components
        int playerId = LoginController.loginId;
        // Optionally, set welcome text if you have a username available
        // String playerName = UserDatabase.getUserById(playerId).getUsername();
        // welcomeLabel.setText("Welcome, " + playerName + "!");

        // Setup game image previews
        connect4Image.setImage(new Image(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/connect4_preview.jpg").toExternalForm()));
        tttImage.setImage(new Image(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/tictactoe_preview.jpg").toExternalForm()));
        checkersImage.setImage(new Image(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/chess_preview.jpg").toExternalForm()));
        setupGameSearch();

        var logoUrl = getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/img.png");
        if (logoUrl != null) {
            img.setImage(new Image(logoUrl.toExternalForm()));
        } else {
            System.err.println("⚠️ Logo image not found.");
        }

        loadIcon(heartIcon, "/ca/ucalgary/groupprojectgui/p3/images/heart.png");
        loadIcon(bellIcon, "/ca/ucalgary/groupprojectgui/p3/images/bell.png");
        loadIcon(profileIcon, "/ca/ucalgary/groupprojectgui/p3/images/profile.png");

        // Set up event handler for the profile icon to open Manage Profile page on click
        profileIcon.setOnMouseClicked((MouseEvent event) -> {
            openManageProfile(event);
        });

        // Initialize friend requests functionality if the FXML components exist on the Home page
        if (searchField != null && playersContainer != null) {
            initializeFriendRequests();
        }
    }

    // ---------------- Friend Requests Methods ----------------

    private void initializeFriendRequests() {
        allPlayers = PlayerDatabase.getAllPlayers();
        loadPlayerList(allPlayers);

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            List<Player> filtered = allPlayers.stream()
                    .filter(player -> player.getUsername().toLowerCase().contains(newText.toLowerCase()))
                    .collect(Collectors.toList());
            loadPlayerList(filtered);
        });
    }

    private void loadPlayerList(List<Player> players) {
        playersContainer.getChildren().clear();
        int count = 0;
        for (Player player : players) {
            playersContainer.getChildren().add(createPlayerEntry(player.getUsername()));
            if (++count >= 3) {
                return;
            }
        }
    }

    private HBox createPlayerEntry(String username) {
        HBox entry = new HBox(10);
        entry.getStyleClass().add("player-box");

        // Force the HBox to be a certain width
        entry.setPrefWidth(250); // adjust as needed
        entry.setMinWidth(250);
        entry.setMaxWidth(250);

        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("avatar");

        Label nameLabel = new Label(username);
        nameLabel.setPrefWidth(300); // Set preferred width to 300 pixels
        nameLabel.getStyleClass().add("username-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("👤+");
        addBtn.setPrefWidth(50);
        addBtn.setMinWidth(50);
        addBtn.setMaxWidth(50);
        addBtn.getStyleClass().add("add-icon");
        addBtn.setOnAction(e -> sendFriendRequest(username));

        entry.getChildren().addAll(avatar, nameLabel, spacer, addBtn);
        return entry;
    }

    private void sendFriendRequest(String username) {
        int currentUserId = LoginController.loginId;
        Player friendUser = PlayerDatabase.getPlayerByUsername(username);

        if (friendUser == null) {
            showAlert("Error", "User not found.");
            return;
        }

        int friendId = friendUser.getUserID();

        if (FriendDatabase.areFriends(currentUserId, friendId)) {
            showAlert("Info", "You are already friends with this user.");
            return;
        }

        boolean success = FriendDatabase.addFriend(currentUserId, friendId);

        if (success) {
            showAlert("Success", "Friend added successfully!");
        } else {
            showAlert("Error", "Failed to add friend.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void closePanel() {
        // Find and remove the top-level panel for friend requests
        Node popup = playersContainer.getParent().getParent();
        ((Pane) popup.getParent()).getChildren().remove(popup);
    }

    // ---------------- End of Friend Requests Methods ----------------

    // ---------------- Home Page Methods ----------------

    @FXML
    private void onConnect4Click() {
        launchGame("Connect 4");
    }

    @FXML
    private void onCheckersClick() {
        launchGame("Checkers");
    }

    @FXML
    private void onTicTacToeClick() {
        launchGame("Tic Tac Toe");
    }

    @FXML
    private void handleQuickMatch() {
        System.out.println("Searching for quick match...");
    }

    @FXML
    private void handleLogout() {
       SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/LogoutConfirmationPanel.fxml","Confirm Logout",null);
    }

    @FXML
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
            case "Checkers":
                fxmlFile = "/ca/ucalgary/groupprojectgui/p3/checkers.fxml";
                title = "Checkers Game";
                cssFile = "checkers.css";
                break;
            case "Tic Tac Toe":
                fxmlFile = "/ca/ucalgary/groupprojectgui/p3/TicTacToe.fxml";
                title = "Tic Tac Toe";
                cssFile = "TicTacToe.css";
                break;
            default:
                System.out.println("Game not recognized: " + gameName);
                return;
        }

        SceneManager.showLoadingScreenAndLoadMain(
                "/ca/ucalgary/groupprojectgui/p3/LoadingScreen.fxml",
                fxmlFile,
                title,
                cssFile
        );
    }

    private void setupGameSearch() {
        gameSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal.toLowerCase();
            for (Node node : gameTilePane.getChildren()) {
                if (node instanceof Button btn) {
                    String text = btn.getText().toLowerCase();
                    btn.setVisible(text.contains(query));
                    btn.setManaged(text.contains(query)); // avoids empty layout space
                }
            }
        });
    }




    @FXML
    private void openManageProfile(MouseEvent event) {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/ManageProfile.fxml", "Manage Profile", "ManageProfile.css");

    }

    private void showPopupAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onLeaderboardClick() {
        System.out.println("Leaderboard button clicked!");
        // Example navigation
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/Leaderboard.fxml", "Leaderboard", "leaderboard");
    }

    private void loadIcon(ImageView view, String path) {
        var url = getClass().getResource(path);
        if (url != null) {
            view.setImage(new Image(url.toExternalForm()));
        } else {
            System.err.println("❌ Icon not found: " + path);
        }
    }

    // ---------------- Nested Logout Confirmation Controller ----------------

    public class LogoutConfirmationController {

        @FXML
        private void confirmLogout() {
            System.out.println("User confirmed logout.");
            // Do logout logic here (e.g., navigate to login screen)
        }

        @FXML
        private void cancelLogout() {
            System.out.println("User canceled logout.");
            // Close/hide this popup
        }
    }
}
