// Controller for the Home page view
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

/**
 * Main controller class for the Home page view.
 * <p>
 * This class manages the interactions for the home page, including game selection,
 * friend requests, profile actions, and handling UI transitions.
 * </p>
 */
public class HomePageController {

    // UI Components related to game selection and friend system
    public HBox mainContainer;
    public StackPane opponentOverlay;
    public Button switchGameButton;
    public Button randomButton;
    public Button friendButton;
    public StackPane logoutOverlay;  // Used for logout confirmation
    public StackPane addFriendOverlay;
    public VBox addFriendPopupContainer;
    public Button selfStatsButton;

    // Container for friends list
    @FXML
    protected VBox playersContainer;
    // Game search field for filtering game tiles dynamically
    @FXML
    protected TextField gameSearchField;
    @FXML
    private HBox gameTilePane;
    @FXML
    private ImageView img;
    @FXML
    protected StackPane popupContainer;
    @FXML
    private StackPane friendSelectionOverlay;
    // ListView for displaying friends
    @FXML
    protected ListView<String> friendListView;
    // Profile Icon for navigation to manage profile
    @FXML
    private ImageView profileIcon;
    @FXML
    private ImageView connect4Image;
    @FXML
    private ImageView tttImage;
    @FXML
    private ImageView checkersImage;

    // Field for friend requests functionality from the home page
    @FXML
    private TextField searchField;

    // List of all players for friend search functionality
    private List<Player> allPlayers;

    /**
     * Static field to store the selected friend's opponent ID.
     */
    public static int friendOpponentID;

    // Currently selected game name (e.g., "Connect 4", "Checkers", "Tic Tac Toe")
    private String currentGameName;
    // Current logged-in player's ID
    private int playerId;

    /**
     * Initializes the Home page controller.
     * <p>
     * This method sets up UI components, images, friend requests functionality, and hover effects.
     * It is automatically called after the FXML file is loaded.
     * </p>
     */
    @FXML
    public void initialize() {
        // No friend is selected initially
        friendOpponentID = -1;

        // Initialize Home Page components
        playerId = LoginController.loginId;

        // Setup game image previews
        connect4Image.setImage(new Image(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/connect4_preview.jpg").toExternalForm()));
        tttImage.setImage(new Image(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/tictactoe_preview.jpg").toExternalForm()));
        checkersImage.setImage(new Image(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/chess_preview.jpg").toExternalForm()));
        setupGameSearch();

        // Load the logo image and set it to the img ImageView
        var logoUrl = getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/img.png");
        if (logoUrl != null) {
            img.setImage(new Image(logoUrl.toExternalForm()));
        } else {
            System.err.println("⚠️ Logo image not found.");
        }

        // Create a drop shadow effect for hovering
        DropShadow neonShadow = new DropShadow();
        neonShadow.setColor(Color.web("#ff00ff"));
        neonShadow.setRadius(20);
        neonShadow.setSpread(0.5);

        // Set up hover effect on the profile icon
        profileIcon.setOnMouseEntered(event -> {
            profileIcon.setScaleX(1.1);
            profileIcon.setScaleY(1.1);
            profileIcon.setCursor(Cursor.HAND);
            profileIcon.setEffect(neonShadow);
        });
        profileIcon.setOnMouseExited(event -> {
            profileIcon.setScaleX(1.0);
            profileIcon.setScaleY(1.0);
            profileIcon.setEffect(null);
        });

        // On profile icon click, navigate to the Manage Profile page
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

    /**
     * Initializes friend request functionality by loading all players from the database,
     * displaying them, and setting up a dynamic search filter.
     */
    private void initializeFriendRequests() {
        // Retrieve all players from the database for friend management
        allPlayers = PlayerDatabase.getAllPlayers();
        loadPlayerList(allPlayers);
        // Add a listener to dynamically filter the list as the user types
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            // Filter players by username, case-insensitive
            List<Player> filtered = allPlayers.stream()
                    .filter(player -> player.getUsername().toLowerCase().contains(newText.toLowerCase()))
                    .collect(Collectors.toList());
            // Update the UI to show only filtered players
            loadPlayerList(filtered);
        });
    }

    /**
     * Loads a list of players into the playersContainer.
     * Clears the current list and shows a maximum of 3 player entries.
     *
     * @param players the list of players to load into the UI
     */
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

    /**
     * Creates a UI component (HBox) representing a single player entry.
     * The entry shows an avatar, username, and an add friend button.
     *
     * @param username the username of the player
     * @return the HBox representing the player entry
     */
    private HBox createPlayerEntry(String username) {
        HBox entry = new HBox(10);
        entry.getStyleClass().add("player-box");

        // Set fixed width for the HBox
        entry.setPrefWidth(250);
        entry.setMinWidth(250);
        entry.setMaxWidth(250);

        // Avatar: first character of the username
        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("avatar");

        // Username label with preferred width
        Label nameLabel = new Label(username);
        nameLabel.setPrefWidth(300);
        nameLabel.getStyleClass().add("username-label");

        // Spacer to push the add friend button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 'Add Friend' button with a friend icon
        Button addBtn = new Button("👤+");
        addBtn.setPrefWidth(50);
        addBtn.setMinWidth(50);
        addBtn.setMaxWidth(50);
        addBtn.getStyleClass().add("add-icon");
        addBtn.setOnAction(e -> sendFriendRequest(username));

        entry.getChildren().addAll(avatar, nameLabel, spacer, addBtn);
        return entry;
    }

    /**
     * Sends a friend request to the specified user.
     * Checks for user existence, duplicate friend connections, and displays an appropriate alert.
     *
     * @param username the username of the friend to add
     */
    private void sendFriendRequest(String username) {
        int currentUserId = LoginController.loginId;
        Player friendUser = PlayerDatabase.getPlayerByUsername(username);
        if (friendUser == null) {
            showOverlayAlert("Error", "User not found.");
            return;
        }
        int friendId = friendUser.getUserID();
        // Check if already friends
        if (FriendDatabase.areFriends(currentUserId, friendId)) {
            showOverlayAlert("Info", "You are already friends with this user.");
            return;
        }
        // Attempt to add friend in the database
        boolean success = FriendDatabase.addFriend(currentUserId, friendId);
        if (success) {
            showOverlayAlert("Success", "Friend added successfully!");
            // Refresh friend list to immediately display new friend
            loadFriendList();
        } else {
            showOverlayAlert("Error", "Failed to add friend.");
        }
    }

    /**
     * Displays an overlay popup with a custom title and message.
     *
     * @param title   the title of the alert
     * @param message the message displayed in the alert
     */
    protected void showOverlayAlert(String title, String message) {
        // Create a vertical container for the popup content
        VBox overlay = new VBox(10);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 20; -fx-background-radius: 10;");
        overlay.setMaxWidth(300);
        overlay.setAlignment(Pos.CENTER);
        overlay.setMaxHeight(100);

        // Title label styling
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        // Message label styling
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(280);

        // Close button to dismiss the popup
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            popupContainer.getChildren().remove(overlay);
            popupContainer.setVisible(false);
        });

        overlay.getChildren().addAll(titleLabel, messageLabel, closeButton);

        popupContainer.getChildren().clear();
        // Add overlay to the container and center it
        popupContainer.getChildren().add(overlay);
        StackPane.setAlignment(overlay, Pos.CENTER);
        popupContainer.setVisible(true);
    }

    /**
     * Closes the friend request panel by removing its parent node.
     */
    private void closePanel() {
        Node popup = playersContainer.getParent().getParent();
        ((Pane) popup.getParent()).getChildren().remove(popup);
    }

    // ---------------- End of Friend Requests Methods ----------------
    // ---------------- Home Page Methods ----------------

    /**
     * Event handler for when the Connect 4 game is clicked.
     */
    @FXML
    private void onConnect4Click() {
        chooseOpponent("Connect 4");
    }

    /**
     * Event handler for when the Checkers game is clicked.
     */
    @FXML
    private void onCheckersClick() {
        chooseOpponent("Checkers");
    }

    /**
     * Event handler for when the Tic Tac Toe game is clicked.
     */
    @FXML
    private void onTicTacToeClick() {
        chooseOpponent("Tic Tac Toe");
    }

    /**
     * Launches the selected game by switching to its respective FXML view.
     * Displays a loading screen before launching the game scene.
     *
     * @param gameName the name of the selected game
     */
    @FXML
    public void launchGame(String gameName) {
        String fxmlFile;
        // Determine the FXML file based on the game name
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
        // Show a loading screen and then load the selected game scene
        SceneManager.showLoadingScreenAndLoadMain(
                "/ca/ucalgary/groupprojectgui/p3/views/loadingScreen.fxml",
                fxmlFile
        );
    }

    /**
     * Sets up the search functionality for filtering available games.
     * Game tiles are dynamically shown or hidden based on the search query.
     */
    private void setupGameSearch() {
        gameSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal.toLowerCase();
            // Loop through each game card (VBox) in the gameTilePane (HBox)
            for (Node node : gameTilePane.getChildren()) {
                if (node instanceof VBox card) {
                    // Check if the card's label contains the game title and matches the search query
                    for (Node inner : card.getChildren()) {
                        if (inner instanceof Label label && label.getStyleClass().contains("game-title")) {
                            String gameName = label.getText().toLowerCase();
                            boolean match = gameName.contains(query);
                            // Set visibility based on match
                            card.setVisible(match);
                            card.setManaged(match);
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Displays the opponent selection overlay when a game is chosen.
     * Provides options for a random opponent or a friend,
     * and applies a blur effect to the background.
     *
     * @param forGame the selected game name
     */
    @FXML
    public void chooseOpponent(String forGame) {
        // Store the currently selected game
        currentGameName = forGame;

        // Apply blur effect to main container
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);

        // Show opponent overlay
        opponentOverlay.setVisible(true);
        opponentOverlay.setOpacity(1.0);

        // Switch game button hides the overlay and removes blur
        switchGameButton.setOnAction(e -> {
            opponentOverlay.setVisible(false);
            opponentOverlay.setOpacity(0.0);
            mainContainer.setEffect(null);
        });

        // Random button launches the game with a random opponent
        randomButton.setOnAction(e -> {
            opponentOverlay.setVisible(false);
            opponentOverlay.setOpacity(0.0);
            mainContainer.setEffect(null);
            launchGame(currentGameName);
        });

        // Friend button opens friend selection overlay
        friendButton.setOnAction(e -> {
            opponentOverlay.setVisible(false);
            opponentOverlay.setOpacity(0.0);
            mainContainer.setEffect(null);
            showFriendSelection();
        });
    }

    /**
     * Shows the friend selection overlay allowing the user to choose a friend to play with.
     */
    @FXML
    public void showFriendSelection() {
        // Apply a blur effect to the main container
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);

        // Clear previous friend selections
        friendListView.getItems().clear();
        int currentUserId = LoginController.loginId;
        Set<Integer> friendIds = FriendDatabase.getFriends(currentUserId);

        // Populate friend list with online friends only
        for (Integer friendId : friendIds) {
            Player friend = PlayerDatabase.getPlayerByUserID(friendId);
            User onlineUser = UserDatabase.getUserById(friendId);
            if (friend != null && onlineUser != null && onlineUser.isOnline()) {
                friendListView.getItems().add(friend.getUsername());
                friendListView.setFixedCellSize(32);
                friendListView.setPrefHeight(friendListView.getItems().size() * 32 + 2);
            }
        }
        friendSelectionOverlay.setVisible(true);
    }

    /**
     * Handles the event when the user confirms their friend selection to play a game.
     * Retrieves the selected friend's ID, hides the overlay, removes the blur, and launches the game.
     *
     * @param event the action event triggering friend selection confirmation
     */
    @FXML
    private void onConfirmFriendSelection(ActionEvent event) {
        String selectedFriend = friendListView.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            Player friendDet = PlayerDatabase.getPlayerByUsername(selectedFriend);
            friendOpponentID = friendDet.getUserID();
            friendSelectionOverlay.setVisible(false);
            mainContainer.setEffect(null);
            launchGame(currentGameName);
        } else {
            System.out.println("Please select a friend.");
        }
    }

    /**
     * Handles the event when the user cancels friend selection.
     * Hides the friend selection overlay and removes the blur effect.
     *
     * @param event the action event triggering cancellation
     */
    @FXML
    private void onCancelFriendSelection(ActionEvent event) {
        friendSelectionOverlay.setVisible(false);
        mainContainer.setEffect(null);
    }

    /**
     * Event handler for clicking on the profile icon.
     * Navigates the user to the Manage Profile page.
     *
     * @param event the mouse event triggered on the profile icon
     */
    @FXML
    private void openManageProfile(MouseEvent event) {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/manageProfile.fxml");
    }

    /**
     * Handles the event when the user clicks the leaderboard button.
     * Navigates the user to the leaderboard page.
     */
    @FXML
    private void onLeaderboardClick() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/leaderboard.fxml");
    }

    /**
     * Loads an icon image from the given path and sets it to the provided ImageView.
     *
     * @param view the ImageView to load the image into
     * @param path the path to the image resource
     */
    private void loadIcon(ImageView view, String path) {
        var url = getClass().getResource(path);
        if (url != null) {
            view.setImage(new Image(url.toExternalForm()));
        } else {
            System.err.println("❌ Icon not found: " + path);
        }
    }

    // ---------------- Nested Logout Confirmation Controller ----------------

    /**
     * Displays the logout confirmation overlay and applies a blur effect to the background.
     */
    @FXML
    public void showLogoutOverlay() {
        logoutOverlay.setVisible(true);
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);
    }

    /**
     * Confirms the logout action and navigates back to the login page.
     */
    @FXML
    public void confirmLogout() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/login.fxml");
    }

    /**
     * Cancels the logout process by hiding the logout overlay and removing the blur effect.
     */
    @FXML
    public void cancelLogout() {
        logoutOverlay.setVisible(false);
        mainContainer.setEffect(null);
    }

    /**
     * Loads the current user's friend list into the Home Page UI.
     * Creates a scrollable list of friends with their usernames and statuses.
     */
    private void loadFriendList() {
        playersContainer.getChildren().clear();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

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

        // Hide the vertical scrollbar by setting its opacity and width to zero
        Platform.runLater(() -> {
            Node vBar = scrollPane.lookup(".scroll-bar:vertical");
            if (vBar != null) {
                vBar.setStyle("-fx-opacity: 0; -fx-pref-width: 0;");
            }
        });

        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
    }

    /**
     * Creates a UI component (HBox) representing a friend item in the friend list.
     * Displays an avatar, username, and an online/offline status indicator.
     *
     * @param username the username of the friend
     * @param friendId the ID of the friend
     * @return the HBox representing the friend item
     */
    private HBox createFriendItem(String username, int friendId) {
        HBox friendItem = new HBox(15);
        friendItem.setAlignment(Pos.CENTER_LEFT);
        friendItem.getStyleClass().add("friend-item");
        friendItem.setMaxWidth(Double.MAX_VALUE);
        friendItem.setPrefHeight(40);

        // Create avatar label with gradient background
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

        // Username label
        Label nameLabel = new Label(username);
        nameLabel.getStyleClass().add("friend-name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        // Spacer to push status indicator to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.NEVER);

        // Online/offline status indicator
        Label friendStatus = new Label("⚪️");
        friendStatus.setStyle("-fx-font-size: 12px;");
        User onlineUser = UserDatabase.getUserById(friendId);
        if (onlineUser != null && onlineUser.isOnline()) {
            friendStatus.setText("🟢");
        } else {
            friendStatus.setText("🔴");
        }

        // Add hover effects for visual feedback
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

    /**
     * Displays a popup overlay showing the selected friend's profile information.
     *
     * @param username the username of the friend to show profile details for
     */
    private void showFriendProfilePopup(String username) {
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
        // Apply blur effect to background
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);
        StackPane rootPane = (StackPane) scene.getRoot();

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Modal container with defined dimensions and styles
        VBox modal = new VBox(18);
        modal.setAlignment(Pos.CENTER);
        modal.setPadding(new Insets(25));
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

        // Header section with avatar and info
        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label avatarLabel = new Label(username.substring(0, 1).toUpperCase());
        avatarLabel.setMinSize(70, 70);
        avatarLabel.setAlignment(Pos.CENTER);
        avatarLabel.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ff00ff, #00ffff);" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 28px;" +
                        "-fx-background-radius: 35px;"
        );

        VBox infoBox = new VBox(4);
        Label nameLabel = new Label(username.toUpperCase());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-family: 'Orbitron';");
        Label statusLabel = new Label(onlineStatus);
        statusLabel.setStyle("-fx-text-fill: " + (friend.isOnline() ? "#00ff00" : "#ff0000") +
                "; -fx-font-size: 13px;");
        infoBox.getChildren().addAll(nameLabel, statusLabel);
        headerBox.getChildren().addAll(avatarLabel, infoBox);

        // Personal details section
        HBox idBox = createDetailLabel("User ID:", String.valueOf(friendId));
        HBox emailBox = createDetailLabel("Email:", email);
        HBox levelBox = createDetailLabel("Level:", String.valueOf(level));
        VBox personalInfoBox = new VBox(10, idBox, emailBox, levelBox);
        personalInfoBox.setPadding(new Insets(10));
        personalInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);" +
                "-fx-background-radius: 5;" +
                "-fx-border-color: rgba(0, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;");

        // Separator for header
        Rectangle headerSeparator = new Rectangle(440, 1.5);
        headerSeparator.setFill(new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.3, Color.web("#ff00ff")),
                new Stop(0.7, Color.web("#00ffff")),
                new Stop(1, Color.TRANSPARENT)
        ));
        headerSeparator.setEffect(new DropShadow(4, 0, 2, Color.rgb(0, 0, 0, 0.6)));

        // TabPane for game statistics (one tab per game)
        TabPane gameStatsTabPane = new TabPane();
        gameStatsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        gameStatsTabPane.setPrefWidth(500);
        gameStatsTabPane.setMinWidth(500);
        gameStatsTabPane.setMaxWidth(500);

        // Create a tab for each game type and add its corresponding stats content
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

        // Overall level bar (decoration)
        Rectangle overallLevelBar = new Rectangle(300, 8);
        overallLevelBar.setFill(new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#00ffff")),
                new Stop(1, Color.web("#ff00ff"))
        ));

        // Close button to dismiss friend profile popup
        Button closeButton = new Button("CLOSE");
        closeButton.setStyle("-fx-background-color: #ff00ff;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Orbitron';" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 8 20;");
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

        // Extra button for friend removal, visible only if not self
        Button extraButton = new Button("REMOVE FRIEND");
        extraButton.setStyle("-fx-background-color: #00ccff;" +
                "-fx-text-fill: black;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Orbitron';" +
                "-fx-background-radius: 5;" +
                "-fx-padding: 8 20;");
        // Use a lambda to confirm removal and update UI accordingly
        extraButton.setOnAction(e -> {
            confirmRemoveFriend(username, () -> {
                Pane parent = (Pane) overlay.getParent();
                parent.getChildren().remove(overlay);
                mainContainer.setEffect(null);
            });
        });
        HBox buttonContainer;
        // Hide the extra button if viewing own profile
        if (username.equals(PlayerDatabase.getPlayerByUserID(playerId).getUsername())) {
            extraButton.setVisible(false);
            buttonContainer = new HBox(closeButton);
            buttonContainer.setAlignment(Pos.CENTER);
        } else {
            buttonContainer = new HBox(10, extraButton, closeButton);
            buttonContainer.setAlignment(Pos.CENTER);
        }

        // Spacer for separation in modal
        Region spacer = new Region();
        spacer.setPrefHeight(20);
        VBox.setMargin(spacer, new Insets(5, 0, 5, 0));

        // Build bottom section with buttons
        VBox bottomSection = new VBox(10, spacer, buttonContainer);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10, 0, 0, 0));

        // Assemble modal content
        VBox modalContent = new VBox(15, headerBox, personalInfoBox, headerSeparator, gameStatsTabPane);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setMaxWidth(500);
        modalContent.getChildren().add(bottomSection);

        overlay.getChildren().add(modalContent);
        StackPane.setAlignment(modalContent, Pos.CENTER);

        // Animate modal appearance
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), modalContent);
        tt.setFromY(20);
        tt.setToY(0);
        tt.play();

        rootPane.getChildren().add(overlay);
        overlay.toFront();
    }

    /**
     * Creates a VBox that displays a player's game statistics for a specific game.
     * Displays Ranking Points, Tier, and Win Ratio.
     *
     * @param player the player whose stats are to be displayed
     * @param game   the game type
     * @return a VBox containing game stats
     */
    private VBox createGameStatsContent(Player player, GameType game) {
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(15, 20, 15, 20));
        contentBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);" +
                "-fx-border-color: rgba(0, 255, 255, 0.2);" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 5;");
        contentBox.setPrefWidth(440);
        contentBox.setMinWidth(440);
        contentBox.setMaxWidth(440);

        // Ranking Points
        HBox pointsBox = createStatBox("RANKING POINTS",
                String.valueOf(player.getMMR(game)),
                "#00ffff");

        // Tier
        String tier = player.rankForPlayer(game);
        RankTier tierEnum = RankTier.valueOf(tier.toUpperCase());

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

    /**
     * Creates an HBox displaying a single game statistic.
     *
     * @param label the label for the stat (e.g., "RANKING POINTS")
     * @param value the value of the stat
     * @param color the text color for the stat value
     * @return an HBox containing the statistic details
     */
    protected HBox createStatBox(String label, String value, String color) {
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

    /**
     * Creates an HBox to display detailed user profile information in a key-value format.
     *
     * @param title the label title (e.g., "User ID:")
     * @param value the corresponding value
     * @return an HBox displaying the detail
     */
    protected HBox createDetailLabel(String title, String value) {
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

    /**
     * Returns the color code associated with a given RankTier.
     *
     * @param tier the RankTier enum
     * @return the corresponding color code as a String
     */
    protected String getTierColor(RankTier tier) {
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

    /**
     * Handles the event for the 'Remove Friend' button.
     * Identifies the friend to be removed and calls confirmRemoveFriend
     * to execute removal and update the UI.
     *
     * @param event the action event triggered by the remove button
     */
    @FXML
    public void handleRemoveFriend(ActionEvent event) {
        Button removeButton = (Button) event.getSource();
        HBox friendItem = (HBox) removeButton.getParent();
        Label usernameLabel = (Label) friendItem.getChildren().get(1);
        String friendUsername = usernameLabel.getText();

        // Remove friend from UI after confirming removal
        confirmRemoveFriend(friendUsername, () -> {
            playersContainer.getChildren().remove(friendItem);
        });
    }

    /**
     * Opens the 'Add Friend' popup window with a blurred background.
     * Dynamically creates and styles the modal content.
     */
    @FXML
    private void openAddFriendPopup() {
        // Apply blur effect to main container
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
        // Header section with title and close button
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

        // Search field for players
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
        // ListView for displaying search results
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
                    // Action for add button: send friend request and remove player from the list
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
        // Generate a list of potential friends (excluding current user and already friends)
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
        // Add listener for dynamic filtering based on user input
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

        // Animate the popup appearance
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), addFriendOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), modalContent);
        slideIn.setFromY(20);
        slideIn.setToY(0);
        ParallelTransition openTransition = new ParallelTransition(fadeIn, slideIn);
        openTransition.play();
    }

    /**
     * Closes the 'Add Friend' popup window.
     */
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

    /**
     * Shows a confirmation popup for removing a friend.
     *
     * @param friendUsername    the username of the friend to remove
     * @param postRemovalAction a Runnable to execute after successful removal
     */
    private void confirmRemoveFriend(String friendUsername, Runnable postRemovalAction) {
        // Create semi-transparent overlay
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.setPickOnBounds(true);

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

        // Title and message for confirmation
        Label title = new Label("CONFIRM REMOVAL");
        title.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: linear-gradient(to right, #ff00ff, #00ffff);"
        );

        Label message = new Label("Remove " + friendUsername + " from friends?");
        message.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-family: 'Rajdhani';" +
                        "-fx-font-weight: bold;"
        );

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        // Yes button for confirming removal
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

        // No button to cancel removal
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
        overlay.getChildren().add(popupContent);
        StackPane.setAlignment(popupContent, Pos.CENTER);

        StackPane rootPane = (StackPane) mainContainer.getScene().getRoot();
        rootPane.getChildren().add(overlay);

        // Action for confirming removal: remove friend from database, update UI, and dismiss overlay
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

        // Animate popup appearance
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), popupContent);
        slideIn.setFromY(20);
        slideIn.setToY(0);

        ParallelTransition openTransition = new ParallelTransition(fadeIn, slideIn);
        openTransition.play();
    }

    /**
     * Opens the stats overlay for the current user by displaying their own friend profile.
     *
     * @param event the action event triggering the display
     */
    public void showStatsOverlay(ActionEvent event) {
        String selfUsername = PlayerDatabase.getPlayerByUserID(LoginController.loginId).getUsername();
        showFriendProfilePopup(selfUsername);
    }
}
