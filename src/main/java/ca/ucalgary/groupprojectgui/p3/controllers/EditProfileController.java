package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.UpdateUserProfile;
import Authentication.User;
import Authentication.UserDatabase;
import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class EditProfileController {

    // Background layers from FXML
    @FXML private Pane gridBackground;
    @FXML private Pane cyberGlow;

    // Form elements
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private Button saveBtn;

    // New info label to display current username and email
    @FXML private Label infoLabel;

    // Status label for messages
    @FXML private Label statusLabel;
    @FXML private Label userIdLabel;
    @FXML private Label winRatioLabel;
    @FXML private Label levelLabel;
    @FXML private Label onlineStatusLabel;

    private User currentUser; // Holds the currently logged-in user's data

    @FXML
    public void initialize() {
        // Set up the neon background (grid and pulsing gradient).
        setupBackground();

        // Optional: Bind the save button width to the text field if desired.
        if (usernameField != null) {
            saveBtn.prefWidthProperty().bind(usernameField.widthProperty());
        }

        // Load the current user.
        currentUser = UserDatabase.getUserById(LoginController.loginId);
        if (currentUser == null) {
            statusLabel.setText("Error: No user loaded.");
            return;
        }
        // Instead of pre-filling the text fields, show the current info at the top.
        loadUserInfo();
    }
    private void loadUserInfo() {
        userIdLabel.setText(String.valueOf(currentUser.getUserID()));
        // Editable fields remain empty for new input but you can also set prompt texts.
        usernameField.setPromptText(currentUser.getUsername());
        emailField.setPromptText(currentUser.getEmail());
        winRatioLabel.setText(String.format("%.2f", currentUser.getWinRatio()));
        levelLabel.setText(String.valueOf(currentUser.getLevel()));
        onlineStatusLabel.setText(currentUser.isOnline() ? "Yes" : "No");
    }

    /**
     * Displays the current user's username and email as informational text.
     */
    private void loadUserProfileInfo() {
        infoLabel.setText("Current Username: " + currentUser.getUsername() + "\n"
                + "Current Email: " + currentUser.getEmail());
    }

    /**
     * Sets up the background to match the login scene:
     * - A grid drawn on a Canvas (in gridBackground)
     * - A pulsing radial gradient overlay (in cyberGlow)
     */
    private void setupBackground() {
        // Create and bind a Canvas to the gridBackground pane.
        Canvas gridCanvas = new Canvas();
        gridCanvas.widthProperty().bind(gridBackground.widthProperty());
        gridCanvas.heightProperty().bind(gridBackground.heightProperty());
        gridCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        gridCanvas.heightProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        drawGrid(gridCanvas);
        gridBackground.getChildren().add(gridCanvas);

        // Create a Rectangle for the pulsing radial gradient overlay.
        Rectangle glowRect = new Rectangle();
        glowRect.widthProperty().bind(cyberGlow.widthProperty());
        glowRect.heightProperty().bind(cyberGlow.heightProperty());
        RadialGradient gradient = new RadialGradient(
                0,                   // focusAngle
                0,                   // focusDistance
                0.5, 0.5,            // centerX, centerY (relative coordinates)
                1,                   // radius
                true,                // proportional
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 0, 255, 0.15)),
                new Stop(0.5, Color.rgb(0, 255, 255, 0.1)),
                new Stop(1, Color.rgb(255, 255, 0, 0.28))
        );
        glowRect.setFill(gradient);
        cyberGlow.getChildren().add(glowRect);

        // Animate the glow with a pulsing effect.
        Timeline pulseTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(glowRect.opacityProperty(), 0.3),
                        new KeyValue(glowRect.scaleXProperty(), 1),
                        new KeyValue(glowRect.scaleYProperty(), 1)
                ),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(glowRect.opacityProperty(), 0.5),
                        new KeyValue(glowRect.scaleXProperty(), 1.05),
                        new KeyValue(glowRect.scaleYProperty(), 1.05)
                )
        );
        pulseTimeline.setCycleCount(Timeline.INDEFINITE);
        pulseTimeline.setAutoReverse(true);
        pulseTimeline.play();
    }

    /**
     * Draws a grid onto the provided Canvas.
     */
    private void drawGrid(Canvas canvas) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#0a0a12"));
        gc.fillRect(0, 0, width, height);
        gc.setFill(Color.web("#ff00ff", 0.05));
        double step = 40.0;
        double lineWidth = 2;
        for (double x = 0; x <= width; x += step) {
            gc.fillRect(x, 0, lineWidth, height);
        }
        for (double y = 0; y <= height; y += step) {
            gc.fillRect(0, y, width, lineWidth);
        }
    }

    /**
     * Handles the Save Changes button action.
     * Validates input and attempts to update the user's profile.
     */
    @FXML
    private void handleSaveChanges() {
        String newUsername = usernameField.getText().trim();
        String newEmail = emailField.getText().trim();

        // Basic input validation
        if (newUsername.isEmpty() || newEmail.isEmpty() || !newEmail.contains("@")) {
            statusLabel.setText("Please enter a valid username and email.");
            return;
        }
        // Check if changes were actually made
        if (newUsername.equals(currentUser.getUsername()) && newEmail.equals(currentUser.getEmail())) {
            statusLabel.setText("No changes were made.");
            return;
        }
        // Attempt to update the user's profile
        UpdateUserProfile updater = new UpdateUserProfile();
        boolean updateResult = updater.updateUser(currentUser.getUserID(), newUsername, newEmail);

        if (updateResult) {
            statusLabel.setText("Profile updated successfully: Username and Email have been changed.");
            // Update the currentUser and Player objects, if applicable.
            currentUser.setUsername(newUsername);
            currentUser.setEmail(newEmail);
            Player player = PlayerDatabase.getPlayerByUserID(currentUser.getUserID());
            if (player != null) {
                player.setUsername(newUsername);
                PlayerDatabase.savePlayer(player);
            }
            // Optionally refresh the info label
            loadUserProfileInfo();
        } else {
            statusLabel.setText("Failed to update profile. Please try again.");
        }
    }

    /**
     * Navigates back to the Manage Profile screen.
     */
    @FXML
    private void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/ManageProfile.fxml", "Manage Profile", "manage_profile.css");
    }
}
