package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.User;
import Authentication.UserDatabase;
import Authentication.FriendDatabase;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HomePageController {
    public HBox gameFriend;
    public HBox mainContainer;
    @FXML
    private VBox playersContainer;
    // Fields for game/home page
    @FXML
    private TextField gameSearchField;
    @FXML
    private HBox gameTilePane;
    @FXML
    private ImageView img;
    @FXML
    private StackPane popupContainer;
    @FXML
    private StackPane friendSelectionOverlay;
    @FXML
    private ListView<String> friendListView;

    @FXML
    private ImageView profileIcon;
    @FXML
    private VBox friendPopupPlaceholder;
    @FXML
    private Button logoutButton;
    @FXML
    private ImageView connect4Image;
    @FXML
    private ImageView tttImage;
    @FXML
    private ImageView checkersImage;

    // Fields for preview feature
    @FXML
    private Button connect4Btn;
    @FXML
    private Button tttBtn;
    @FXML
    private Button checkersBtn;

    // Field for home pane
    @FXML
    private BorderPane homePane;

    @FXML
    private Pane logoutPane; // the popup pane for logout

    // Fields for Friend Requests functionality
    @FXML
    private TextField searchField;

    private List<Player> allPlayers;

    public static int friendOpponentID;

    private String currentGameName;

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

        // Load the logo image
        var logoUrl = getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/img.png");
        if (logoUrl != null) {
            img.setImage(new Image(logoUrl.toExternalForm()));
        } else {
            System.err.println("⚠️ Logo image not found.");
        }

// Load the profile icon image
        loadIcon(profileIcon, "/ca/ucalgary/groupprojectgui/p3/images/profile.png");

// Create a drop shadow effect for hovering
        DropShadow neonShadow = new DropShadow();
        neonShadow.setColor(Color.web("#ff00ff"));
        neonShadow.setRadius(20);
        neonShadow.setSpread(0.5);

// Set up hover effect on the profile icon
        profileIcon.setOnMouseEntered(event -> {
            // Scale up the icon slightly
            profileIcon.setScaleX(1.1);
            profileIcon.setScaleY(1.1);
            profileIcon.setCursor(Cursor.HAND);

            // Set the drop shadow effect
            profileIcon.setEffect(neonShadow);
        });
        profileIcon.setOnMouseExited(event -> {
            // Return the icon back to original scale
            profileIcon.setScaleX(1.0);
            profileIcon.setScaleY(1.0);
            // Remove the drop shadow effect
            profileIcon.setEffect(null);
        });

// Set up click event to open the Manage Profile page
        profileIcon.setOnMouseClicked((MouseEvent event) -> {
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/ManageProfile.fxml", "Manage Profile", "ManageProfile.css");
        });


        // Initialize friend requests functionality if the FXML components exist on the Home page
        if (searchField != null && playersContainer != null) {
            initializeFriendRequests();
        }
        if (playersContainer != null) {
            loadFriendList();
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
            showOverlayAlert("Error", "User not found.");
            return;
        }

        int friendId = friendUser.getUserID();

        if (FriendDatabase.areFriends(currentUserId, friendId)) {
            showOverlayAlert("Info", "You are already friends with this user.");
            return;
        }

        boolean success = FriendDatabase.addFriend(currentUserId, friendId);

        if (success) {
            showOverlayAlert("Success", "Friend added successfully!");
        } else {
            showOverlayAlert("Error", "Failed to add friend.");
        }
    }

    /**
     * Displays an overlay popup on top of the current window.
     */
    private void showOverlayAlert(String title, String message) {
        VBox overlay = new VBox(10);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 20; -fx-background-radius: 10;");
        overlay.setMaxWidth(300);
        overlay.setAlignment(Pos.CENTER);
        overlay.setMaxHeight(100);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(280);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            popupContainer.getChildren().remove(overlay);
            popupContainer.setVisible(false);  // hide container when closed
        });

        overlay.getChildren().addAll(titleLabel, messageLabel, closeButton);

        popupContainer.getChildren().clear();
        popupContainer.getChildren().add(overlay);
        StackPane.setAlignment(overlay, Pos.CENTER);

        popupContainer.setVisible(true);  // make container visible
    }


    private void closePanel() {
        // Find and remove the top-level panel for friend requests
        Node popup = playersContainer.getParent().getParent();
        ((Pane) popup.getParent()).getChildren().remove(popup);
    }

    // ---------------- End of Friend Requests Methods ----------------

    // ---------------- Home Page Methods ----------------

    @FXML
    private void onConnect4Click() {
        chooseOpponent("Connect 4");
    }

    @FXML
    private void onCheckersClick() {
        chooseOpponent("Checkers");
    }

    @FXML
    private void onTicTacToeClick() {
        chooseOpponent("Tic Tac Toe");
    }

    @FXML
    private void handleLogout() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/LogoutConfirmationPanel.fxml", "Confirm Logout", null);
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
                if (node instanceof VBox card) {
                    for (Node inner : card.getChildren()) {
                        if (inner instanceof Label label && label.getStyleClass().contains("game-title")) {
                            String gameName = label.getText().toLowerCase();
                            boolean match = gameName.contains(query);
                            card.setVisible(match);
                            card.setManaged(match);
                            break;
                        }
                    }
                }
            }
        });
    }

    @FXML
    public void chooseOpponent(String forGame) {
        currentGameName = forGame;
        // Apply blur effect on the main container
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);

        // Get the root pane from the scene
        StackPane rootPane = (StackPane) mainContainer.getScene().getRoot();

        // Create an overlay for opponent choice
        StackPane overlay = new StackPane();
        overlay.getStyleClass().add("friend-selection-overlay");
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Build modal dialog (Random or Friend)
        VBox modal = new VBox(20);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(20));
        modal.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-background-radius: 10;");
        modal.setMinWidth(300);
        modal.setMinHeight(150);

        Label prompt = new Label("WHOM  DO  YOU  WANNA  PLAY  WITH??");
        prompt.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        Button randomButton = new Button("Random");
        Button friendButton = new Button("Friend");
        randomButton.setStyle("-fx-background-color: #5f27cd; -fx-text-fill: white; -fx-background-radius: 10;");
        friendButton.setStyle("-fx-background-color: #341f97; -fx-text-fill: white; -fx-background-radius: 10;");

        HBox buttonBox = new HBox(10, randomButton, friendButton);
        buttonBox.setAlignment(Pos.CENTER);

        modal.getChildren().addAll(prompt, buttonBox);
        overlay.getChildren().add(modal);

        // Add overlay and bring it to the front
        rootPane.getChildren().add(overlay);
        overlay.toFront();

        // Random button: remove overlay, remove blur, then launch game
        randomButton.setOnAction(e -> {
            rootPane.getChildren().remove(overlay);
            mainContainer.setEffect(null);
            launchGame(currentGameName);
        });

        // Friend button: remove this overlay/blur then show friend selection
        friendButton.setOnAction(e -> {
            rootPane.getChildren().remove(overlay);
            mainContainer.setEffect(null);
            showFriendSelection();
        });
    }

    @FXML
    public void showFriendSelection() {
        // Clear previous items (if any)
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);
        friendListView.getItems().clear();

        int currentUserId = LoginController.loginId;
        Set<Integer> friendIds = FriendDatabase.getFriends(currentUserId);

        for (Integer friendId : friendIds) {
            Player friend = PlayerDatabase.getPlayerByUserID(friendId);
            if (friend != null) {
                friendListView.getItems().add(friend.getUsername());
                friendListView.setFixedCellSize(32);
                friendListView.setPrefHeight(friendListView.getItems().size() * 32 + 2);
            }
        }

        // Make the overlay visible
        friendSelectionOverlay.setVisible(true);
    }

    @FXML
    private void onConfirmFriendSelection(ActionEvent event) {
        String selectedFriend = friendListView.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            Player friendDet = PlayerDatabase.getPlayerByUsername(selectedFriend);
            friendOpponentID = friendDet.getUserID();
            // Hide the overlay and clear any blur effects, then launch the game
            friendSelectionOverlay.setVisible(false);
            mainContainer.setEffect(null);
            launchGame(currentGameName);  // Pass the appropriate game name
        } else {
            System.out.println("Please select a friend.");
        }
    }

    @FXML
    private void onCancelFriendSelection(ActionEvent event) {
        // Hide the overlay and clear blur effects
        friendSelectionOverlay.setVisible(false);
        mainContainer.setEffect(null);
    }


    @FXML
    private void openManageProfile(MouseEvent event) {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/ManageProfile.fxml", "Manage Profile", "ManageProfile.css");
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


    private void loadFriendList() {
        playersContainer.getChildren().clear();

        int currentUserId = LoginController.loginId;
        Set<Integer> friendIds = FriendDatabase.getFriends(currentUserId);

        for (Integer friendId : friendIds) {
            Player friend = PlayerDatabase.getPlayerByUserID(friendId);
            if (friend != null) {
                playersContainer.getChildren().add(createFriendItem(friend.getUsername()));
            }
        }
    }

    private HBox createFriendItem(String username) {
        HBox friendItem = new HBox(15);
        friendItem.setAlignment(Pos.CENTER_LEFT);
        friendItem.getStyleClass().add("friend-item");

        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("friend-initial");

        Label nameLabel = new Label(username);
        nameLabel.getStyleClass().add("friend-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button removeButton = new Button("✖");
        removeButton.getStyleClass().add("remove-button");
        removeButton.setOnAction(this::handleRemoveFriend);

        friendItem.getChildren().addAll(avatar, nameLabel, spacer, removeButton);
        return friendItem;
    }

    @FXML
    public void handleRemoveFriend(ActionEvent event) {
        // Get the remove button and the associated friend item.
        Button removeButton = (Button) event.getSource();
        HBox friendItem = (HBox) removeButton.getParent();
        Label friendLabel = (Label) friendItem.getChildren().get(0);
        String friendName = friendLabel.getText();

        // Create a Popup instance.
        Popup popup = new Popup();

        // Create a styled container for the pop-up content.
        VBox popupContent = new VBox(15);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setStyle(
                "-fx-background-color: radial-gradient(radius 100%, #111, #333);" +
                        "-fx-padding: 15;" +
                        "-fx-border-color: #00ffff;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,255,0.75), 10, 0.5, 0, 0);"
        );


        // Create the confirmation message.
        Label message = new Label("Remove friend: " + friendName + "?");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Create a container for the buttons.
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // Yes button styling
        Button yesButton = new Button("Yes");
        yesButton.setStyle(
                "-fx-background-color: #00ffff;" +      // Neon cyan background
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,255,0.8), 10, 0.5, 0, 0);"
        );

        Button noButton = new Button("No");
        noButton.setStyle(
                "-fx-background-color: #ff00ff;" +      // Neon magenta background
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255,0,255,0.8), 10, 0.5, 0, 0);"
        );


        buttonBox.getChildren().addAll(yesButton, noButton);
        popupContent.getChildren().addAll(message, buttonBox);
        popup.getContent().add(popupContent);

        // Position the popup near the friend item.
        Bounds bounds = friendItem.localToScreen(friendItem.getBoundsInLocal());
        popup.show(removeButton.getScene().getWindow(), bounds.getMinX() + 50, bounds.getMinY() + 20);

        // Action handlers for the buttons.
        yesButton.setOnAction(e -> {
            playersContainer.getChildren().remove(friendItem);
            popup.hide();
            System.out.println("Removed friend: " + friendName);
        });

        noButton.setOnAction(e -> {
            popup.hide();
            System.out.println("Removal canceled for friend: " + friendName);
        });
    }

    @FXML
    private void openAddFriendPopup() {
        System.out.println("✅ openAddFriendPopup() triggered!");

        VBox popupContent = new VBox(10);
        popupContent.setAlignment(Pos.TOP_CENTER);
        popupContent.setPrefSize(250, 320);
        popupContent.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.9);" +
                        "-fx-padding: 15;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #00ffff;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,255,255,0.6), 10, 0.5, 0, 0);"
        );

        Label title = new Label("Add Friend");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        TextField friendUsernameField = new TextField();
        friendUsernameField.setPromptText("Username...");
        friendUsernameField.setPrefWidth(200);

        // Recommended friends section
        VBox recommendations = new VBox(8);
        recommendations.setAlignment(Pos.TOP_LEFT);
        int currentUserId = LoginController.loginId;
        User currentUser = UserDatabase.getUserById(currentUserId);

        List<Player> suggestedUsers = PlayerDatabase.getAllPlayers().stream()
                .filter(p -> !p.getUsername().equalsIgnoreCase(currentUser.getUsername()))
                .filter(p -> !FriendDatabase.areFriends(currentUserId, p.getUserID()))
                .limit(5)
                .collect(Collectors.toList());


        for (Player player : suggestedUsers) {
            String name = player.getUsername(); // 💡 Extract username

            HBox row = new HBox(8);
            Label nameLabel = new Label(name);
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            Button addBtn = new Button("+");
            addBtn.setStyle(
                    "-fx-background-color: #00ffff;" +
                            "-fx-text-fill: black;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 5;"
            );
            addBtn.setOnAction(e -> {
                friendUsernameField.setText(name); // auto-fill the search field
            });
            row.getChildren().addAll(nameLabel, addBtn);
            recommendations.getChildren().add(row);
        }

        // Action buttons
        Button sendRequestButton = new Button("Send");
        sendRequestButton.setStyle(
                "-fx-background-color: #00ffff;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;"
        );

        Button closeBtn = new Button("Cancel");
        closeBtn.setStyle(
                "-fx-background-color: #ff0066;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;"
        );

        sendRequestButton.setOnAction(e -> {
            String enteredName = friendUsernameField.getText().trim();
            if (!enteredName.isEmpty()) {
                sendFriendRequest(enteredName);
            }
            friendPopupPlaceholder.getChildren().clear();
            friendPopupPlaceholder.setVisible(false);
            friendPopupPlaceholder.setManaged(false);
        });

        closeBtn.setOnAction(e -> {
            friendPopupPlaceholder.getChildren().clear();
            friendPopupPlaceholder.setVisible(false);
            friendPopupPlaceholder.setManaged(false);
        });

        HBox buttonRow = new HBox(10, sendRequestButton, closeBtn);
        buttonRow.setAlignment(Pos.CENTER);

        popupContent.getChildren().addAll(title, friendUsernameField, new Separator(), recommendations, buttonRow);

        friendPopupPlaceholder.getChildren().clear();
        friendPopupPlaceholder.getChildren().add(popupContent);
        friendPopupPlaceholder.setVisible(true);
        friendPopupPlaceholder.setManaged(true);
    }




    @FXML
    private void cancelLogout() {
        if (logoutPane.getParent() instanceof Pane) {
            Pane parent = (Pane) logoutPane.getParent();
            parent.getChildren().remove(logoutPane);
        } else {
            System.err.println("Logout popup is not attached to a parent; nothing to remove.");
        }
    }

}


