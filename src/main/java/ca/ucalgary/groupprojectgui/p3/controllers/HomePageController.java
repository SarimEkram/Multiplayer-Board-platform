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

/**
 * Main controller class for the Home Page view. Manages game selection, friend
 * list and requests, launching games, and logout confirmation.
 */
public class HomePageController {

    // ------------------------------
    // FXML-INJECTED UI COMPONENTS
    // ------------------------------

    /** Primary container for the entire home screen layout. */
    @FXML
    public HBox mainContainer;

    /** Overlay shown when choosing an opponent (random or friend) for a selected game. */
    @FXML
    public StackPane opponentOverlay;

    /** Button shown on the opponent selection overlay to switch to another game. */
    @FXML
    public Button switchGameButton;

    /** Button shown on the opponent selection overlay to play against a random opponent. */
    @FXML
    public Button randomButton;

    /** Button shown on the opponent selection overlay to play against a friend. */
    @FXML
    public Button friendButton;

    /** Button that closes the logout confirmation popup. */
    @FXML
    public Button cancelLogoutButton;

    /** Button that confirms the user's intent to logout. */
    @FXML
    public Button confirmLogoutButton;

    /** Overlay for the logout confirmation popup. */
    @FXML
    public StackPane logoutOverlay;

    /** Overlay for the "Add Friend" popup window. */
    @FXML
    public StackPane addFriendOverlay;

    /** Container (VBox) inside the addFriendOverlay, used to hold the popup content. */
    @FXML
    public VBox addFriendPopupContainer;

    /** Button that opens a popup showing the current user's own stats (self-profile). */
    @FXML
    public Button selfStatsButton;

    /** Container for listing players in the "Find Friends" or "Friend Requests" panels. */
    @FXML
    protected VBox playersContainer;

    /** TextField allowing the user to filter or search for available games by name. */
    @FXML
    protected TextField gameSearchField;

    /** HBox that holds the individual "game cards" or tiles (e.g., Connect4, Checkers, TTT). */
    @FXML
    private HBox gameTilePane;

    /** Logo image at the top of the Home Page. */
    @FXML
    private ImageView img;

    /** StackPane that displays popup messages/alerts on top of the main UI. */
    @FXML
    protected StackPane popupContainer;

    /** Overlay that appears when selecting a friend to play against. */
    @FXML
    private StackPane friendSelectionOverlay;

    /** ListView that contains the user's friend names for selection. */
    @FXML
    protected ListView<String> friendListView;

    /** ImageView for the user's profile icon (top-right corner). */
    @FXML
    private ImageView profileIcon;

    /** ImageView used to display a Connect 4 preview image. */
    @FXML
    private ImageView connect4Image;

    /** ImageView used to display a Tic Tac Toe preview image. */
    @FXML
    private ImageView tttImage;

    /** ImageView used to display a Checkers preview image. */
    @FXML
    private ImageView checkersImage;

    /** Button representing the Connect 4 game choice. */
    @FXML
    private Button connect4Btn;

    /** Button representing the Tic Tac Toe game choice. */
    @FXML
    private Button tttBtn;

    /** Button representing the Checkers game choice. */
    @FXML
    private Button checkersBtn;

    /** TextField for searching players to add as friends. */
    @FXML
    private TextField searchField;

    // ------------------------------
    // CLASS-LEVEL FIELDS
    // ------------------------------

    /** Holds all players retrieved from the database (for friend search functionality). */
    private List<Player> allPlayers;

    /** Tracks the friend's user ID if a friend is chosen as an opponent (-1 if none). */
    public static int friendOpponentID;

    /** Stores the name of the current (last clicked) game, e.g. "Connect 4". */
    private String currentGameName;

    /** The ID of the currently logged-in player. */
    private int playerId;

    // ------------------------------
    // INITIALIZATION
    // ------------------------------

    /**
     * Called automatically after FXML loads. Initializes images, sets up friend list,
     * and configures UI event handlers for search, profile icon, etc.
     */
    @FXML
    public void initialize() {
        friendOpponentID = -1; // No friend selected initially

        // Store logged-in player ID for reuse
        playerId = LoginController.loginId;

        // Load images for the 3 game preview cards
        connect4Image.setImage(
                new Image(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/connect4_preview.jpg").toExternalForm()));
        tttImage.setImage(
                new Image(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/tictactoe_preview.jpg").toExternalForm()));
        checkersImage.setImage(
                new Image(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/chess_preview.jpg").toExternalForm()));

        // Setup dynamic searching/filtering for the game tiles
        setupGameSearch();

        // Load main logo image at the top
        var logoUrl = getClass().getResource("/ca/ucalgary/groupprojectgui/p3/images/img.png");
        if (logoUrl != null) {
            img.setImage(new Image(logoUrl.toExternalForm()));
        } else {
            System.err.println("⚠️ Logo image not found.");
        }

        // Load the default profile icon
        loadIcon(profileIcon, "/ca/ucalgary/groupprojectgui/p3/images/profile.png");

        // Add a hover effect to the profile icon
        DropShadow neonShadow = new DropShadow();
        neonShadow.setColor(Color.web("#ff00ff"));
        neonShadow.setRadius(20);
        neonShadow.setSpread(0.5);

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

        // Clicking the icon goes to "Manage Profile"
        profileIcon.setOnMouseClicked((MouseEvent event) ->
                SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/manageProfile.fxml"));

        // If friend search UI is present, initialize it
        if (searchField != null && playersContainer != null) {
            initializeFriendRequests();
        }

        // Load the user's friend list on the home page
        if (playersContainer != null) {
            loadFriendList();
        }
    }

    // ------------------------------
    // FRIEND REQUESTS / FRIEND LIST
    // ------------------------------

    /**
     * Sets up friend request logic: loads all players from the database and
     * filters them based on user-typed text in the search field.
     */
    private void initializeFriendRequests() {
        allPlayers = PlayerDatabase.getAllPlayers();
        loadPlayerList(allPlayers);

        // Filter the list dynamically as the user types
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            List<Player> filtered = allPlayers.stream()
                    .filter(player -> player.getUsername().toLowerCase().contains(newText.toLowerCase()))
                    .collect(Collectors.toList());
            loadPlayerList(filtered);
        });
    }

    /**
     * Loads up to three players into the VBox {@code playersContainer} as a quick sample
     * for friend request or friend search display.
     *
     * @param players the list of players to load
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
     * Creates a single row (HBox) with an avatar letter, username, and an "Add Friend" button.
     *
     * @param username the friend's username to display in this row
     * @return the constructed HBox
     */
    private HBox createPlayerEntry(String username) {
        HBox entry = new HBox(10);
        entry.getStyleClass().add("player-box");
        // Force the HBox to be a specific width
        entry.setPrefWidth(250);
        entry.setMinWidth(250);
        entry.setMaxWidth(250);

        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("avatar");

        Label nameLabel = new Label(username);
        nameLabel.setPrefWidth(300);
        nameLabel.getStyleClass().add("username-label");

        // Spacer pushes the "Add" button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Friend-add button
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
     * Sends a friend request to the specified user by username. Includes checks to see if the
     * user already exists, if they are already friends, etc. Displays an overlay alert.
     *
     * @param username username of the user to add as a friend
     */
    private void sendFriendRequest(String username) {
        int currentUserId = LoginController.loginId;
        Player friendUser = PlayerDatabase.getPlayerByUsername(username);
        if (friendUser == null) {
            showOverlayAlert("Error", "User not found.");
            return;
        }
        int friendId = friendUser.getUserID();

        // If they're already friends, show an info overlay
        if (FriendDatabase.areFriends(currentUserId, friendId)) {
            showOverlayAlert("Info", "You are already friends with this user.");
            return;
        }
        // Otherwise, add friend
        boolean success = FriendDatabase.addFriend(currentUserId, friendId);
        if (success) {
            showOverlayAlert("Success", "Friend added successfully!");
            // Refresh the friend list in the UI so it shows newly added friend
            loadFriendList();
        } else {
            showOverlayAlert("Error", "Failed to add friend.");
        }
    }

    /**
     * Displays a small overlay popup (centered) with a title and message.
     * The user can close the popup, which hides it from view.
     *
     * @param title   the popup's title, e.g., "Success" or "Error"
     * @param message the body content of the popup
     */
    protected void showOverlayAlert(String title, String message) {
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
            popupContainer.setVisible(false);
        });

        overlay.getChildren().addAll(titleLabel, messageLabel, closeButton);
        popupContainer.getChildren().clear();
        popupContainer.getChildren().add(overlay);
        StackPane.setAlignment(overlay, Pos.CENTER);
        popupContainer.setVisible(true);
    }

    /**
     * Loads the current user's friend list into the playersContainer as a ScrollPane,
     * displaying each friend with an avatar, name, and online/offline status indicator.
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

        // Hide the vertical scroll bar
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
     * Creates a single row (HBox) representing a friend: avatar letter, username label,
     * and a colored indicator for online/offline status.
     *
     * @param username the friend's username
     * @param friendId the friend's ID
     * @return an HBox that can be displayed in the friend list
     */
    private HBox createFriendItem(String username, int friendId) {
        HBox friendItem = new HBox(15);
        friendItem.setAlignment(Pos.CENTER_LEFT);
        friendItem.getStyleClass().add("friend-item");
        friendItem.setMaxWidth(Double.MAX_VALUE);
        friendItem.setPrefHeight(40);

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

        Label nameLabel = new Label(username);
        nameLabel.getStyleClass().add("friend-name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.NEVER);

        Label friendStatus = new Label("⚪️");
        friendStatus.setStyle("-fx-font-size: 12px;");

        // Check if friend is currently online
        User onlineUser = UserDatabase.getUserById(friendId);
        if (onlineUser != null && onlineUser.isOnline()) {
            friendStatus.setText("🟢"); // online
        } else {
            friendStatus.setText("🔴"); // offline
        }

        // Hover effect
        friendItem.setOnMouseEntered(e -> friendItem.setStyle("-fx-background-color: rgba(255, 0, 255, 0.1); -fx-cursor: hand;"));
        friendItem.setOnMouseExited(e -> friendItem.setStyle("-fx-background-color: transparent;"));

        // Clicking opens a popup of friend's profile info
        friendItem.setOnMouseClicked(event -> {
            showFriendProfilePopup(username);
        });

        friendItem.getChildren().addAll(avatar, nameLabel, spacer, friendStatus);
        return friendItem;
    }

    /**
     * Displays a popup overlay with profile info about the specified friend (username).
     * Shows email, user ID, level, rank stats, and "REMOVE FRIEND" if not the user themselves.
     *
     * @param username the friend's username whose profile is to be displayed
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
        // Blur background
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);

        StackPane rootPane = (StackPane) scene.getRoot();
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.prefWidthProperty().bind(rootPane.widthProperty());
        overlay.prefHeightProperty().bind(rootPane.heightProperty());

        // Main modal
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

        // Header row
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
        statusLabel.setStyle("-fx-text-fill: " + (friend.isOnline() ? "#00ff00" : "#ff0000") + "; -fx-font-size: 13px;");
        infoBox.getChildren().addAll(nameLabel, statusLabel);
        headerBox.getChildren().addAll(avatarLabel, infoBox);

        // Personal info
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

        // Separator
        Rectangle headerSeparator = new Rectangle(440, 1.5);
        headerSeparator.setFill(new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.3, Color.web("#ff00ff")),
                new Stop(0.7, Color.web("#00ffff")),
                new Stop(1, Color.TRANSPARENT)
        ));
        headerSeparator.setEffect(new DropShadow(4, 0, 2, Color.rgb(0, 0, 0, 0.6)));

        // Game stats tab pane
        TabPane gameStatsTabPane = new TabPane();

        gameStatsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        gameStatsTabPane.setPrefWidth(500);
        gameStatsTabPane.setMinWidth(500);
        gameStatsTabPane.setMaxWidth(500);

        // Build a tab for each game type
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

        // Close button
        Button closeButton = new Button("CLOSE");
        closeButton.setCursor(Cursor.HAND);

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

        // "REMOVE FRIEND" button
        final StackPane finalOverlay = overlay;
        Button extraButton = new Button("REMOVE FRIEND");
        extraButton.setCursor(Cursor.HAND);
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

        // If viewing your own profile, hide "Remove Friend" button
        HBox buttonContainer;
        if (username.equals(PlayerDatabase.getPlayerByUserID(playerId).getUsername())) {
            extraButton.setVisible(false);
            buttonContainer = new HBox(closeButton);
            buttonContainer.setAlignment(Pos.CENTER);
        } else {
            buttonContainer = new HBox(10, extraButton, closeButton);
            buttonContainer.setAlignment(Pos.CENTER);
        }

        Region spacer = new Region();
        spacer.setPrefHeight(20);
        VBox.setMargin(spacer, new Insets(5, 0, 5, 0));

        VBox bottomSection = new VBox(10, spacer, buttonContainer);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10, 0, 0, 0));

        VBox modalContent = new VBox(15, headerBox, personalInfoBox, headerSeparator, gameStatsTabPane);
        modalContent.setAlignment(Pos.CENTER);
        modalContent.setMaxWidth(500);
        modalContent.getChildren().add(bottomSection);

        overlay.getChildren().add(modalContent);
        StackPane.setAlignment(modalContent, Pos.CENTER);

        TranslateTransition tt = new TranslateTransition(Duration.millis(300), modalContent);
        tt.setFromY(20);
        tt.setToY(0);
        tt.play();

        rootPane.getChildren().add(overlay);
        overlay.toFront();
    }

    /**
     * Creates a vertical box (VBox) displaying the player's stats for a given game,
     * including Ranking Points, Tier, and Win Ratio.
     *
     * @param player the Player whose data is displayed
     * @param game   the GameType (e.g., CONNECT_FOUR, CHECKERS, TTT)
     * @return a VBox with the player's relevant stats
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
                String.valueOf(player.getMMR(game)), "#00ffff");

        // Tier
        String tierStr = player.rankForPlayer(game);
        // Convert string rank to enum safely
        RankTier tierEnum;
        try {
            tierEnum = RankTier.valueOf(tierStr.toUpperCase());
        } catch (Exception e) {
            tierEnum = RankTier.BRONZE; // fallback
        }
        HBox tierBox = createStatBox("TIER", tierStr, getTierColor(tierEnum));

        // Win Ratio
        double winRatio = player.getWinRatio(game);
        HBox winRatioBox = createStatBox("WIN RATIO",
                String.format("%.1f%%", winRatio), "#ff00ff");

        contentBox.getChildren().addAll(pointsBox, tierBox, winRatioBox);
        return contentBox;
    }

    /**
     * Creates a horizontal box (HBox) representing a single statistic:
     * label (e.g., "WIN RATIO") plus the value, styled with a neon color.
     *
     * @param label the name of the stat
     * @param value the stat value
     * @param color the color for the value text
     * @return an HBox containing the formatted label/value
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
     * Creates a horizontal container for displaying a key-value pair:
     * e.g. "Email:" "user@domain.com"
     *
     * @param title label/title text
     * @param value content or data text
     * @return HBox containing the detail pair
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
     * Returns the neon color code associated with a RankTier enum.
     *
     * @param tier the player's rank tier
     * @return a hex color string for styling in the UI
     */
    protected String getTierColor(RankTier tier) {
        switch (tier) {
            case DIAMOND:
                return "#00ffff"; // Cyan
            case GOLD:
                return "#ffd700"; // Gold
            case SILVER:
                return "#c0c0c0"; // Silver
            case BRONZE:
                return "#cd7f32"; // Bronze
            default:
                return "white";
        }
    }

    // ------------------------------
    // GAME SELECTION & OPPONENT
    // ------------------------------

    /**
     * Called when the user clicks on the Connect 4 image or button.
     * Shows the opponent choice overlay (friend or random).
     */
    @FXML
    private void onConnect4Click() {
        chooseOpponent("Connect 4");
    }

    /**
     * Called when the user clicks on the Checkers image or button.
     * Shows the opponent choice overlay (friend or random).
     */
    @FXML
    private void onCheckersClick() {
        chooseOpponent("Checkers");
    }

    /**
     * Called when the user clicks on the TicTacToe image or button.
     * Shows the opponent choice overlay (friend or random).
     */
    @FXML
    private void onTicTacToeClick() {
        chooseOpponent("Tic Tac Toe");
    }

    /**
     * Shows the overlay allowing the user to pick an opponent (friend or random).
     * Applies a blur effect to the background.
     *
     * @param forGame the name of the game chosen, e.g. "Connect 4"
     */
    @FXML
    public void chooseOpponent(String forGame) {
        currentGameName = forGame;

        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);

        // Show the overlay
        opponentOverlay.setVisible(true);
        opponentOverlay.setOpacity(1.0);

        // "Switch Game" returns to the main UI
        switchGameButton.setOnAction(e -> {
            opponentOverlay.setVisible(false);
            opponentOverlay.setOpacity(0.0);
            mainContainer.setEffect(null);
        });

        // "Random Opponent"
        randomButton.setOnAction(e -> {
            opponentOverlay.setVisible(false);
            opponentOverlay.setOpacity(0.0);
            mainContainer.setEffect(null);
            launchGame(currentGameName);
        });

        // "Friend Opponent"
        friendButton.setOnAction(e -> {
            opponentOverlay.setVisible(false);
            opponentOverlay.setOpacity(0.0);
            mainContainer.setEffect(null);
            showFriendSelection();
        });
    }

    /**
     * Finalizes the launch of a selected game. Shows a loading screen, then switches to the
     * appropriate FXML file for that game.
     *
     * @param gameName the name of the chosen game, e.g. "Checkers"
     */
    @FXML
    public void launchGame(String gameName) {
        String fxmlFile;
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

    /**
     * Filters the game tiles (Connect4, Checkers, TTT) in real time as the user types
     * in the {@code gameSearchField}, hiding or showing tiles based on substring matches.
     */
    private void setupGameSearch() {
        gameSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal.toLowerCase();
            for (Node node : gameTilePane.getChildren()) {
                if (node instanceof VBox card) {
                    for (Node inner : card.getChildren()) {
                        if (inner instanceof Label label && label.getStyleClass().contains("game-title")) {
                            boolean match = label.getText().toLowerCase().contains(query);
                            card.setVisible(match);
                            card.setManaged(match);
                            break;
                        }
                    }
                }
            }
        });
    }

    // ------------------------------
    // OPPONENT: FRIEND SELECTION
    // ------------------------------

    /**
     * Shows an overlay listing the user's friends who are online, from which they can
     * pick someone to challenge in the currentGameName.
     */
    @FXML
    public void showFriendSelection() {
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);

        friendListView.getItems().clear();

        int currentUserId = LoginController.loginId;
        Set<Integer> friendIds = FriendDatabase.getFriends(currentUserId);
        for (Integer friendId : friendIds) {
            Player friend = PlayerDatabase.getPlayerByUserID(friendId);
            User onlineUser = UserDatabase.getUserById(friendId);

            // Only display friend if they are in the system and currently online
            if (friend != null && onlineUser != null && onlineUser.isOnline()) {
                friendListView.getItems().add(friend.getUsername());
                friendListView.setFixedCellSize(32);
                friendListView.setPrefHeight(friendListView.getItems().size() * 32 + 2);
            }
        }

        friendSelectionOverlay.setVisible(true);
    }

    /**
     * Called when the user confirms the friend they'd like to challenge.
     * Hides the overlay and launches the chosen game.
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
     * Called when the user cancels the friend selection overlay.
     * Simply hides the overlay and removes the blur from the main UI.
     */
    @FXML
    private void onCancelFriendSelection(ActionEvent event) {
        friendSelectionOverlay.setVisible(false);
        mainContainer.setEffect(null);
    }

    // ------------------------------
    // MANAGE PROFILE & LEADERBOARD
    // ------------------------------

    /**
     * Navigates to the Manage Profile screen when the user clicks on the profile icon.
     */
    @FXML
    private void openManageProfile(MouseEvent event) {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/manageProfile.fxml");
    }

    /**
     * Navigates to the leaderboard screen when the user clicks on the "Leaderboard" button.
     */
    @FXML
    private void onLeaderboardClick() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/leaderboard.fxml");
    }

    // ------------------------------
    // LOGOUT & OVERLAY
    // ------------------------------

    /**
     * Shows the logout confirmation overlay, applying a blur effect to the rest of the screen.
     */
    @FXML
    public void showLogoutOverlay() {
        logoutOverlay.setVisible(true);
        BoxBlur blur = new BoxBlur(10, 10, 3);
        mainContainer.setEffect(blur);
    }

    /**
     * Confirms logout, returning the user to the login screen.
     */
    @FXML
    public void confirmLogout() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/login.fxml");
    }

    /**
     * Cancels logout, hiding the overlay and removing the blur effect.
     */
    @FXML
    public void cancelLogout() {
        logoutOverlay.setVisible(false);
        mainContainer.setEffect(null);
    }

    // ------------------------------
    // FRIEND REMOVAL METHODS
    // ------------------------------

    /**
     * Called when clicking a "Remove Friend" button. Locates the friend item
     * in the UI, obtains the username, and shows a confirmation popup.
     */
    @FXML
    public void handleRemoveFriend(ActionEvent event) {
        Button removeButton = (Button) event.getSource();
        HBox friendItem = (HBox) removeButton.getParent();
        Label usernameLabel = (Label) friendItem.getChildren().get(1);
        String friendUsername = usernameLabel.getText();

        confirmRemoveFriend(friendUsername, () -> {
            playersContainer.getChildren().remove(friendItem);
        });
    }

    /**
     * Pops up a confirmation window for removing a friend. If confirmed, calls
     * {@link FriendDatabase#removeFriend(int, int)} and refreshes the friend list.
     *
     * @param friendUsername    friend's username to remove
     * @param postRemovalAction a {@code Runnable} to execute after removal is successful
     */
    private void confirmRemoveFriend(String friendUsername, Runnable postRemovalAction) {
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

        Label title = new Label("CONFIRM REMOVAL");
        title.setStyle(
                "-fx-text-fill: linear-gradient(to right, #ff00ff, #00ffff);" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;"
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

        Button yesButton = new Button("CONFIRM");
        yesButton.setCursor(Cursor.HAND);
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
        yesButton.setOnMouseEntered(e -> yesButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ff33ff, #cc33ff);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 25;" +
                        "-fx-background-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 0, 255, 0.8), 10, 0.5, 0, 2);"
        ));
        yesButton.setOnMouseExited(e -> yesButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #ff00ff, #cc00ff);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 25;" +
                        "-fx-background-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 0, 255, 0.5), 5, 0.5, 0, 1);"
        ));

        Button noButton = new Button("CANCEL");
        noButton.setCursor(Cursor.HAND);

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
        noButton.setOnMouseEntered(e -> noButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #33ffff, #00ccff);" +
                        "-fx-text-fill: black;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 25;" +
                        "-fx-background-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 204, 255, 0.8), 10, 0.5, 0, 2);"
        ));
        noButton.setOnMouseExited(e -> noButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #00ccff, #0099cc);" +
                        "-fx-text-fill: black;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 25;" +
                        "-fx-background-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 204, 255, 0.5), 5, 0.5, 0, 1);"
        ));

        buttonBox.getChildren().addAll(noButton, yesButton);
        popupContent.getChildren().addAll(title, message, buttonBox);
        overlay.getChildren().add(popupContent);
        StackPane.setAlignment(popupContent, Pos.CENTER);

        StackPane rootPane = (StackPane) mainContainer.getScene().getRoot();
        rootPane.getChildren().add(overlay);

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

        // Appear animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), overlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), popupContent);
        slideIn.setFromY(20);
        slideIn.setToY(0);

        ParallelTransition openTransition = new ParallelTransition(fadeIn, slideIn);
        openTransition.play();
    }

    // ------------------------------
    // "ADD FRIEND" POPUP
    // ------------------------------

    /**
     * Opens a popup window that allows the user to search for new friends and add them.
     * Applies a blur effect to the main UI while open.
     */
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
                        "-fx-border-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 0, 255, 0.5), 30, 0.5, 0, 0);"
        );

        // Header with "ADD FRIEND" label + close "✕" button
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(0, 0, 15, 0));

        Label titleLabel = new Label("ADD FRIEND");
        titleLabel.setStyle(
                "-fx-text-fill: #00ffff;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-family: 'Orbitron';" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, #00ffff, 5, 0.1, 0, 0);"
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

        // Search field
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

        // ListView for displaying potential friend results
        ListView<Player> resultsListView = new ListView<>();
        resultsListView.setPrefHeight(300);
        resultsListView.setStyle(
                "-fx-control-inner-background: linear-gradient(#2b0057, #4a0077);" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 0;" +
                        "-fx-background: transparent;"
        );
        resultsListView.setCellFactory(lv -> new ListCell<>() {
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

        // Filter out current user & existing friends
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

        // Dynamic filtering on user input
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

    /**
     * Closes the "Add Friend" popup, removing the blur from the main UI and returning
     * to the normal home screen view.
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

    // ------------------------------
    // SELF STATS
    // ------------------------------

    /**
     * Displays the current user's own profile/stats overlay, same UI method as
     * showing a friend's profile, but for the local user.
     */
    public void showStatsOverlay(ActionEvent event) {
        String selfUsername = PlayerDatabase.getPlayerByUserID(LoginController.loginId).getUsername();
        showFriendProfilePopup(selfUsername);
    }

    // ------------------------------
    // UTILITY
    // ------------------------------

    /**
     * Loads an image from the given path and sets it into the provided ImageView.
     * Prints an error if the resource is not found.
     *
     * @param view the ImageView to load into
     * @param path resource path (e.g., "/ca/ucalgary/groupprojectgui/p3/images/profile.png")
     */
    private void loadIcon(ImageView view, String path) {
        var url = getClass().getResource(path);
        if (url != null) {
            view.setImage(new Image(url.toExternalForm()));
        } else {
            System.err.println("❌ Icon not found: " + path);
        }
    }
}
