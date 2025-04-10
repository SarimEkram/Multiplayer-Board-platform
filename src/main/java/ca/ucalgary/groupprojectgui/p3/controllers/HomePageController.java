package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.*;
import MatchmakingLeaderboard.GameType;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import MatchmakingLeaderboard.RankTier;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HomePageController {
    public HBox gameFriend;
    public HBox mainContainer;
    public StackPane opponentOverlay;
    public Button switchGameButton;
    public Button randomButton;
    public Button friendButton;
    public Button cancelLogoutButton;
    public Button confirmLogoutButton;
    public StackPane logoutOverlay;
    public StackPane addFriendOverlay;
    public VBox addFriendPopupContainer;
    public Button selfStatsButton;
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

    // Fields for Friend Requests functionality
    @FXML
    private TextField searchField;

    private List<Player> allPlayers;

    public static int friendOpponentID;

    private String currentGameName;

    private int playerId;

    @FXML
    public void initialize() {
        friendOpponentID = -1; // no friend is selected as opponent

        // Initialize Home Page components
        playerId = LoginController.loginId;
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
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/manageProfile.fxml");
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
            // Refresh the friend list so the new friend appears immediately.
            loadFriendList();
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
    public void launchGame(String gameName) {

        String fxmlFile;
        String title;
        switch (gameName) {
            case "Connect 4":
                fxmlFile = "/ca/ucalgary/groupprojectgui/p3/views/connect4.fxml";

                break;
            case "Checkers":
                fxmlFile = "/ca/ucalgary/groupprojectgui/p3/views/checkers.fxml";

                break;
            case "Tic Tac Toe":
                fxmlFile = "/ca/ucalgary/groupprojectgui/p3/views/ticTacToe.fxml";

                break;
            default:
                System.out.println("Game not recognized: " + gameName);
                return;
        }

        SceneManager.showLoadingScreenAndLoadMain(
                "/ca/ucalgary/groupprojectgui/p3/views/loadingScreen.fxml",
                fxmlFile
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

        // Blur effect
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);

        // Show overlay
        opponentOverlay.setVisible(true);
        opponentOverlay.setOpacity(1.0);

        // Switch Game button behavior
        switchGameButton.setOnAction(e -> {
            opponentOverlay.setVisible(false);
            opponentOverlay.setOpacity(0.0);
            mainContainer.setEffect(null);
        });

        // Random button behavior
        randomButton.setOnAction(e -> {
            opponentOverlay.setVisible(false);
            opponentOverlay.setOpacity(0.0);
            mainContainer.setEffect(null);
            launchGame(currentGameName);
        });

        // Friend button behavior
        friendButton.setOnAction(e -> {
            opponentOverlay.setVisible(false);
            opponentOverlay.setOpacity(0.0);
            mainContainer.setEffect(null);
            showFriendSelection();
        });
    }

    @FXML
    public void showFriendSelection() {
        // Apply blur effect
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);

        // Clear previous items (if any)
        friendListView.getItems().clear();

        int currentUserId = LoginController.loginId;
        Set<Integer> friendIds = FriendDatabase.getFriends(currentUserId);

        for (Integer friendId : friendIds) {
            Player friend = PlayerDatabase.getPlayerByUserID(friendId);
            User onlineUser = UserDatabase.getUserById(friendId);
            // Check if the friend exists and is online (assuming isOnline() exists)
            if (friend != null && onlineUser != null && onlineUser.isOnline() ) {
                friendListView.getItems().add(friend.getUsername());
                // Update ListView display properties dynamically based on the number of items
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
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/manageProfile.fxml");
    }

    @FXML
    private void onLeaderboardClick() {
        // Example navigation
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/leaderboard.fxml");
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

    @FXML
    public void showLogoutOverlay() {
        logoutOverlay.setVisible(true);
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur); // mainContainer should be your root layout pane
    }

    @FXML
    public void confirmLogout() {

        // Navigate to login or home screen
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/login.fxml");
    }

    @FXML
    public void cancelLogout() {
        logoutOverlay.setVisible(false);
        mainContainer.setEffect(null);
    }

    private void loadFriendList() {
        // Clear the existing content
        playersContainer.getChildren().clear();

        // Create a ScrollPane to contain the friends list
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Create a VBox to hold the friend items with left alignment
        VBox friendsContent = new VBox(10);
        friendsContent.setPadding(new Insets(10));
        friendsContent.setAlignment(Pos.TOP_LEFT);

        int currentUserId = LoginController.loginId;
        Set<Integer> friendIds = FriendDatabase.getFriends(currentUserId);
        for (Integer friendId : friendIds) {
            Player friend = PlayerDatabase.getPlayerByUserID(friendId);
            if (friend != null) {
                friendsContent.getChildren().add(createFriendItem(friend.getUsername(), friendId));
            }
        }
        scrollPane.setContent(friendsContent);
        playersContainer.getChildren().add(scrollPane);

        // Hide the vertical scrollbar by setting its opacity and preferred width to zero
        Platform.runLater(() -> {
            Node vBar = scrollPane.lookup(".scroll-bar:vertical");
            if (vBar != null) {
                vBar.setStyle("-fx-opacity: 0; -fx-pref-width: 0;");
            }
        });

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
    }

    private HBox createFriendItem(String username, int friendId) {
        HBox friendItem = new HBox(15);
        friendItem.setAlignment(Pos.CENTER_LEFT);
        friendItem.getStyleClass().add("friend-item");
        friendItem.setMaxWidth(Double.MAX_VALUE);
        friendItem.setPrefHeight(40);

        // Avatar styled to match HTML using Orbitron font and gradient background.
        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ff00ff, #00ffff);" +
                        "-fx-text-fill: #00ffff;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 50%;" +
                        "-fx-min-width: 40px;" +
                        "-fx-min-height: 40px;" +
                        "-fx-alignment: center;"
        );

        // Username label with updated inline style using Orbitron font.
        Label nameLabel = new Label(username);
        nameLabel.getStyleClass().add("friend-name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.NEVER);

        Label friendStatus = new Label("⚪️");
        friendStatus.setStyle("-fx-font-size: 12px;");
        User onlineUser = UserDatabase.getUserById(friendId);
        if (onlineUser != null && onlineUser.isOnline()) {
            friendStatus.setText("🟢");
        } else {
            friendStatus.setText("🔴");
        }

        friendItem.setOnMouseEntered(e -> {
            friendItem.setStyle("-fx-background-color: rgba(255, 0, 255, 0.1); -fx-cursor: hand;");
        });
        friendItem.setOnMouseExited(e -> {
            friendItem.setStyle("-fx-background-color: transparent;");
        });
        friendItem.setOnMouseClicked(event -> {
            showFriendProfilePopup(username);
        });

        friendItem.getChildren().addAll(avatar, nameLabel, spacer, friendStatus);
        return friendItem;
    }

    private void showFriendProfilePopup(String username) {
        // Retrieve friend data
        Player player = PlayerDatabase.getPlayerByUsername(username);
        User friend = UserDatabase.getUserById(player.getUserID());
        if (friend == null) {
            System.err.println("Friend not found for username: " + username);
            return;
        }
        int friendId = friend.getUserID();
        String email = friend.getEmail();
        int level = player.getLevel();
        String onlineStatus = friend.isOnline() ? "Online" : "Offline";

        Scene scene = mainContainer.getScene();
        if (scene == null) {
            System.err.println("mainContainer is not attached to a scene!");
            return;
        }
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);
        StackPane rootPane = (StackPane) scene.getRoot();

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Create modal container with adjusted width (480px)
        VBox modal = new VBox(18); // Slightly increased spacing from 15 to 18
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(25)); // Increased padding from 20 to 25
        modal.setPrefWidth(480);
        modal.setMaxWidth(480);
        modal.setPrefHeight(550);
        modal.setMaxHeight(550);
        modal.setStyle(
                "-fx-background-color: rgba(10, 5, 20, 0.95);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(255, 0, 255, 0.5);" +
                        "-fx-border-width: 1;"
        );
        DropShadow neonShadow = new DropShadow();
        neonShadow.setOffsetX(0);
        neonShadow.setOffsetY(0);
        neonShadow.setRadius(30);
        neonShadow.setColor(Color.rgb(255, 0, 255, 0.5));
        modal.setEffect(neonShadow);
        modal.setTranslateY(20);

        // Header section - adjusted with slightly more space
        HBox headerBox = new HBox(12); // Increased spacing from 10 to 12
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label avatarLabel = new Label(username.substring(0, 1).toUpperCase());
        avatarLabel.setMinSize(70, 70); // Increased from 60 to 70
        avatarLabel.setAlignment(Pos.CENTER);
        avatarLabel.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ff00ff, #00ffff);" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 28px;" + // Increased from 24px to 28px
                        "-fx-background-radius: 35px;" // Increased from 30px to 35px
        );

        VBox infoBox = new VBox(4); // Slightly increased spacing from 3 to 4
        Label nameLabel = new Label(username.toUpperCase());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'Orbitron';"); // Increased from 20px to 22px
        Label statusLabel = new Label(onlineStatus);
        statusLabel.setStyle("-fx-text-fill: " + (friend.isOnline() ? "#00ff00" : "#ff0000") +
                "; -fx-font-size: 13px;"); // Increased from 12px to 13px
        infoBox.getChildren().addAll(nameLabel, statusLabel);
        headerBox.getChildren().addAll(avatarLabel, infoBox);

        // Personal Details Section - slightly more spacious
        HBox idBox = createDetailLabel("User ID:", String.valueOf(friendId)); // Restored full label
        HBox emailBox = createDetailLabel("Email:", email);
        HBox levelBox = createDetailLabel("Level:", String.valueOf(level));
        VBox personalInfoBox = new VBox(10, idBox, emailBox, levelBox); // Increased spacing from 8 to 10
        personalInfoBox.setPadding(new Insets(10)); // Increased from 8 to 10
        personalInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);" +
                "-fx-background-radius: 5;" +
                "-fx-border-color: rgba(0, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;");

        // Separator - adjusted to new width
        Rectangle headerSeparator = new Rectangle(440, 1.5); // Increased width from 400 to 440, height from 1 to 1.5
        headerSeparator.setFill(new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.3, Color.web("#ff00ff")),
                new Stop(0.7, Color.web("#00ffff")),
                new Stop(1, Color.TRANSPARENT)
        ));
        headerSeparator.setEffect(new DropShadow(4, 0, 2, Color.rgb(0, 0, 0, 0.6)));

        TabPane gameStatsTabPane = new TabPane();
        gameStatsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        gameStatsTabPane.setPrefWidth(500); // Match content width
        gameStatsTabPane.setMinWidth(500);
        gameStatsTabPane.setMaxWidth(500);


        // Create tab content container with proper width constraints
        for (GameType game : GameType.values()) {
            Tab tab = new Tab(game.toString().replace("_", " "));
            VBox tabContent = new VBox();
            tabContent.setPrefWidth(500);
            tabContent.setMinWidth(500);
            tabContent.setMaxWidth(500);
            tabContent.getChildren().add(createGameStatsContent(player, game));
            tab.setContent(tabContent);
            gameStatsTabPane.getTabs().add(tab);

        }

        Rectangle overallLevelBar = new Rectangle(300, 8); // Increased width from 280 to 300
        overallLevelBar.setFill(new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#00ffff")),
                new Stop(1, Color.web("#ff00ff"))
        ));

        // Close button - restored to original size
        Button closeButton = new Button("CLOSE");
        closeButton.setStyle("-fx-background-color: #ff00ff;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Orbitron';" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 8 20;"); // Restored original padding
        closeButton.setOnAction(e -> {
            rootPane.getChildren().remove(overlay);
            mainContainer.setEffect(null);
        });
        closeButton.setOnMouseEntered(e -> {
            closeButton.setStyle("-fx-background-color: #ff33ff;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-family: 'Orbitron';" +
                    "-fx-background-radius: 5;" +
                    "-fx-padding: 8 20;" +
                    "-fx-effect: dropshadow(gaussian, rgba(255, 0, 255, 0.8), 10, 0.5, 0, 0);");
        });
        closeButton.setOnMouseExited(e -> {
            closeButton.setStyle("-fx-background-color: #ff00ff;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-font-family: 'Orbitron';" +
                    "-fx-background-radius: 5;" +
                    "-fx-padding: 8 20;");
        });

// Within showFriendProfilePopup(), after creating the modal and overlay:
        final StackPane finalOverlay = overlay;  // Capture overlay for the lambda
// Create the extra button in the popup
        Button extraButton = new Button("REMOVE FRIEND");
        extraButton.setStyle("-fx-background-color: #00ccff;" +
                "-fx-text-fill: black;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Orbitron';" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 8 20;");
        extraButton.setOnAction(e -> {
            confirmRemoveFriend(username, () -> {
                Pane parent = (Pane) finalOverlay.getParent();
                parent.getChildren().remove(finalOverlay);
                mainContainer.setEffect(null);
            });
        });
        HBox buttonContainer = new HBox(closeButton);
        if (username.equals(PlayerDatabase.getPlayerByUserID(playerId).getUsername())) {
            extraButton.setVisible(false);

            buttonContainer.setAlignment(Pos.CENTER);
        }else {
            buttonContainer = new HBox(10, extraButton, closeButton);
            buttonContainer.setAlignment(Pos.CENTER);
        }



// Create a spacer region for separation (if needed)
        Region spacer = new Region();
        spacer.setPrefHeight(20);
        VBox.setMargin(spacer, new Insets(5, 0, 5, 0));

// Build the bottom section with the button container
        VBox bottomSection = new VBox(10, spacer, buttonContainer);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10, 0, 0, 0));

// Add the bottom section to your modal content

// Update the modal content creation to include proper spacing
        VBox modalContent = new VBox(15, headerBox, personalInfoBox, headerSeparator, gameStatsTabPane);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setMaxWidth(500);


        modalContent.getChildren().add(bottomSection);
        // Add margin to the tierBox to further separate it from the tab pane

        overlay.getChildren().add(modalContent);
        StackPane.setAlignment(modalContent, Pos.CENTER);

        TranslateTransition tt = new TranslateTransition(Duration.millis(300), modalContent);
        tt.setFromY(20);
        tt.setToY(0);
        tt.play();

        rootPane.getChildren().add(overlay);
        overlay.toFront();
    }
    private VBox createGameStatsContent(Player player, GameType game) {
        VBox contentBox = new VBox(15); // Adjusted spacing
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(15, 20, 15, 20));
        contentBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);" +
                "-fx-border-color: rgba(0, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;");
        contentBox.setPrefWidth(440); // Match tab pane width
        contentBox.setMinWidth(440);
        contentBox.setMaxWidth(440);

        // Ranking Points
        HBox pointsBox = createStatBox("RANKING POINTS",
                String.valueOf(player.getMMR(game)),
                "#00ffff");

        // Tier
        String tier = player.rankForPlayer(game);


        RankTier tierEnum;
            tierEnum = RankTier.valueOf(String.valueOf(RankTier.valueOf(tier.toUpperCase())));


        HBox tierBox = createStatBox("TIER",
                tier.toString(),
                getTierColor(tierEnum));



        // Win Ratio
        double winRatio = player.getWinRatio(game);
        HBox winRatioBox = createStatBox("WIN RATIO",
                String.format("%.1f%%", winRatio),
                "#ff00ff");


        contentBox.getChildren().addAll(pointsBox, tierBox, winRatioBox);
        return contentBox;
    }

    private HBox createStatBox(String label, String value, String color) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5, 15, 5, 15));

        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-text-fill: #cccccc;" +
                "-fx-font-size: 12px;" +
                "-fx-font-family: 'Rajdhani';" +
                "-fx-min-width: 120px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: " + color + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-family: 'Orbitron';" +
                "-fx-font-weight: bold;");

        box.getChildren().addAll(nameLabel, valueLabel);
        return box;
    }

    private HBox createDetailLabel(String title, String value) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #00ffff;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: 'Rajdhani';" +
                "-fx-font-weight: bold;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: 'Rajdhani';");

        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }

    private String getTierColor(RankTier tier) {
        switch (tier) {
            case DIAMOND:
                return "#00ffff";   // Cyan
            case GOLD:
                return "#ffd700";   // Gold
            case SILVER:
                return "#c0c0c0";   // Silver
            case BRONZE:
                return "#cd7f32";   // Bronze
            default:
                return "white";
        }
    }



    @FXML
    public void handleRemoveFriend(ActionEvent event) {
        Button removeButton = (Button) event.getSource();
        HBox friendItem = (HBox) removeButton.getParent();
        Label usernameLabel = (Label) friendItem.getChildren().get(1);
        String friendUsername = usernameLabel.getText();

        // Use the helper and remove the friend item from the UI after successful removal.
        confirmRemoveFriend(friendUsername, () -> {
            playersContainer.getChildren().remove(friendItem);
        });
    }


    @FXML
    private void openAddFriendPopup() {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);
        VBox modalContent = new VBox(15);
        modalContent.setAlignment(Pos.TOP_CENTER);
        modalContent.setPrefSize(400, 500);
        modalContent.setMaxSize(400, 500);
        modalContent.setPadding(new Insets(25));
        modalContent.setStyle(
                "-fx-background-color: rgba(10, 5, 20, 0.95);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #ff00ff;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 0, 255, 0.5), 30, 0.5, 0, 0);"
        );

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 15, 0));
        Label titleLabel = new Label("ADD FRIEND");
        titleLabel.setStyle(
                "-fx-text-fill: #00ffff;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-shadow: 0 0 5px #00ffff;"
        );
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button closeButton = new Button();
        Text closeIcon = new Text("✕");
        closeIcon.setStyle("-fx-font-size: 20px; -fx-fill: #ff00ff;");
        closeButton.setGraphic(closeIcon);
        closeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnMouseEntered(e -> closeButton.setRotate(90));
        closeButton.setOnMouseExited(e -> closeButton.setRotate(0));
        closeButton.setOnAction(e -> closeAddFriendPopup());
        headerBox.getChildren().addAll(titleLabel, spacer, closeButton);

        TextField searchField = new TextField();
        searchField.setPromptText("Search players...");
        searchField.setPrefWidth(350);
        searchField.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.5);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Rajdhani';" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 10 15;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(0, 255, 255, 0.3);" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;"
        );

        ListView<Player> resultsListView = new ListView<>();
        resultsListView.setPrefHeight(300);
        resultsListView.setStyle(
                "-fx-control-inner-background: linear-gradient(#2b0057, #4a0077);" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 0;" +
                        "-fx-background: transparent;"
        );
        resultsListView.setCellFactory(lv -> new ListCell<Player>() {
            private final HBox cellContainer = new HBox(10);
            private final Label avatarLabel = new Label();
            private final Label nameLabel = new Label();
            private final Region cellSpacer = new Region();
            private final Button addButton = new Button("+");

            {
                cellContainer.setAlignment(Pos.CENTER_LEFT);
                cellContainer.setPadding(new Insets(5, 15, 5, 15));
                cellContainer.setStyle("-fx-background-color: transparent;");
                avatarLabel.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #ff00ff, #00ffff);" +
                                "-fx-text-fill: #00ffff;" +
                                "-fx-font-family: 'Orbitron';" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 50%;" +
                                "-fx-min-width: 40px;" +
                                "-fx-min-height: 40px;" +
                                "-fx-alignment: center;"
                );
                nameLabel.setStyle(
                        "-fx-text-fill: white;" +
                                "-fx-font-family: 'Rajdhani';" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-weight: 500;"
                );
                HBox.setHgrow(cellSpacer, Priority.ALWAYS);
                addButton.setStyle(
                        "-fx-background-color: #00ffff;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-family: 'Orbitron';" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 5;" +
                                "-fx-min-width: 30px;" +
                                "-fx-min-height: 30px;"
                );
                addButton.setOnMouseEntered(e -> addButton.setStyle(
                        "-fx-background-color: #33ffff;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-family: 'Orbitron';" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 5;" +
                                "-fx-min-width: 30px;" +
                                "-fx-min-height: 30px;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0, 255, 255, 0.8), 10, 0.5, 0, 0);"
                ));
                addButton.setOnMouseExited(e -> addButton.setStyle(
                        "-fx-background-color: #00ffff;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-family: 'Orbitron';" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 5;" +
                                "-fx-min-width: 30px;" +
                                "-fx-min-height: 30px;"
                ));
                cellContainer.setOnMouseEntered(e -> {
                    cellContainer.setStyle("-fx-background-color: rgba(255, 0, 255, 0.1); -fx-cursor: hand;");
                });
                cellContainer.setOnMouseExited(e -> {
                    cellContainer.setStyle("-fx-background-color: transparent;");
                });
                cellContainer.getChildren().addAll(avatarLabel, nameLabel, cellSpacer, addButton);
            }
            @Override
            protected void updateItem(Player player, boolean empty) {
                super.updateItem(player, empty);
                if (empty || player == null) {
                    setGraphic(null);
                } else {
                    avatarLabel.setText(player.getUsername().substring(0, 1).toUpperCase());
                    nameLabel.setText(player.getUsername());
                    addButton.setOnAction(e -> {
                        sendFriendRequest(player.getUsername());
                        getListView().getItems().remove(player);
                    });
                    setGraphic(cellContainer);
                }
            }
        });

        int currentUserId = LoginController.loginId;
        User currentUser = UserDatabase.getUserById(currentUserId);
        List<Player> potentialFriends = PlayerDatabase.getAllPlayers().stream()
                .filter(p -> !p.getUsername().equalsIgnoreCase(currentUser.getUsername()))
                .filter(p -> !FriendDatabase.areFriends(currentUserId, p.getUserID()))
                .collect(Collectors.toList());
        ObservableList<Player> searchResults = FXCollections.observableArrayList(potentialFriends);
        Node resultNode;
        if (searchResults.isEmpty()) {
            Label noFriendsLabel = new Label("No players available to add as friend.");
            noFriendsLabel.setStyle("-fx-text-fill: #ff00ff; -fx-font-family: 'Orbitron'; -fx-font-size: 16px;");
            resultNode = noFriendsLabel;
        } else {
            resultsListView.setItems(searchResults);
            resultNode = resultsListView;
        }
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                resultsListView.setItems(searchResults);
            } else {
                String query = newVal.toLowerCase();
                List<Player> filtered = potentialFriends.stream()
                        .filter(p -> p.getUsername().toLowerCase().contains(query))
                        .collect(Collectors.toList());
                if (filtered.isEmpty()) {
                    Label noResultsLabel = new Label("No players match your search.");
                    noResultsLabel.setStyle("-fx-text-fill: #ff00ff; -fx-font-family: 'Orbitron'; -fx-font-size: 16px;");
                    resultsListView.setPlaceholder(noResultsLabel);
                }
                resultsListView.setItems(FXCollections.observableArrayList(filtered));
            }
        });

        modalContent.getChildren().addAll(headerBox, searchField, resultNode);
        addFriendPopupContainer.getChildren().setAll(modalContent);
        addFriendOverlay.setVisible(true);
        addFriendOverlay.setManaged(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), addFriendOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), modalContent);
        slideIn.setFromY(20);
        slideIn.setToY(0);
        ParallelTransition openTransition = new ParallelTransition(fadeIn, slideIn);
        openTransition.play();
    }

    private void closeAddFriendPopup() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), addFriendOverlay);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            addFriendPopupContainer.getChildren().clear();
            addFriendOverlay.setVisible(false);
            addFriendOverlay.setManaged(false);
            mainContainer.setEffect(null);
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/homePage.fxml");
        });
        fadeOut.play();
    }

    private void confirmRemoveFriend(String friendUsername, Runnable postRemovalAction) {
        // Create a semi-transparent overlay that allows clicking through to background
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.setPickOnBounds(true); // Allows clicks to pass through

        // Create the popup content with enhanced styling
        VBox popupContent = new VBox(20);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setPadding(new Insets(25));
        popupContent.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #1a0033, #330066);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: linear-gradient(to right, #ff00ff, #00ffff);" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 255, 255, 0.8), 20, 0.5, 0, 0);"
        );

        // Add glowing title
        Label title = new Label("CONFIRM REMOVAL");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: linear-gradient(to right, #ff00ff, #00ffff);"
        );

        // Add the friend username with styling
        Label message = new Label("Remove " + friendUsername + " from friends?");
        message.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Rajdhani';" +
                        "-fx-font-weight: bold;"
        );

        // Create button container
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        // Yes button with hover effects
        Button yesButton = new Button("CONFIRM");
        yesButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ff00ff, #cc00ff);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 25;" +
                        "-fx-background-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 0, 255, 0.5), 5, 0.5, 0, 1);"
        );
        yesButton.setOnMouseEntered(e -> {
            yesButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #ff33ff, #cc33ff);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-family: 'Orbitron';" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 8 25;" +
                            "-fx-background-radius: 5;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255, 0, 255, 0.8), 10, 0.5, 0, 2);"
            );
        });
        yesButton.setOnMouseExited(e -> {
            yesButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #ff00ff, #cc00ff);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-family: 'Orbitron';" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 8 25;" +
                            "-fx-background-radius: 5;" +
                            "-fx-effect: dropshadow(gaussian, rgba(255, 0, 255, 0.5), 5, 0.5, 0, 1);"
            );
        });

        // No button with hover effects
        Button noButton = new Button("CANCEL");
        noButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #00ccff, #0099cc);" +
                        "-fx-text-fill: black;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 25;" +
                        "-fx-background-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 204, 255, 0.5), 5, 0.5, 0, 1);"
        );
        noButton.setOnMouseEntered(e -> {
            noButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #33ffff, #00ccff);" +
                            "-fx-text-fill: black;" +
                            "-fx-font-family: 'Orbitron';" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 8 25;" +
                            "-fx-background-radius: 5;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 204, 255, 0.8), 10, 0.5, 0, 2);"
            );
        });
        noButton.setOnMouseExited(e -> {
            noButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #00ccff, #0099cc);" +
                            "-fx-text-fill: black;" +
                            "-fx-font-family: 'Orbitron';" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 8 25;" +
                            "-fx-background-radius: 5;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0, 204, 255, 0.5), 5, 0.5, 0, 1);"
            );
        });

        buttonBox.getChildren().addAll(noButton, yesButton);
        popupContent.getChildren().addAll(title, message, buttonBox);

        // Add the popup content to the overlay
        overlay.getChildren().add(popupContent);
        StackPane.setAlignment(popupContent, Pos.CENTER);

        // Get the root pane and add our overlay
        StackPane rootPane = (StackPane) mainContainer.getScene().getRoot();
        rootPane.getChildren().add(overlay);

        // Set up button actions
        yesButton.setOnAction(e -> {
            int friendID = PlayerDatabase.getPlayerByUsername(friendUsername).getUserID();
            FriendDatabase.removeFriend(LoginController.loginId, friendID);
            if (postRemovalAction != null) {
                postRemovalAction.run();
            }
            loadFriendList();
            rootPane.getChildren().remove(overlay);
        });

        noButton.setOnAction(e -> {
            rootPane.getChildren().remove(overlay);
        });

        // Add animation for appearance
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), popupContent);
        slideIn.setFromY(20);
        slideIn.setToY(0);

        ParallelTransition openTransition = new ParallelTransition(fadeIn, slideIn);
        openTransition.play();
    }
    public void showStatsOverlay(ActionEvent event) {
        String selfUsername = PlayerDatabase.getPlayerByUserID(LoginController.loginId).getUsername();
        showFriendProfilePopup(selfUsername);
    }
}


