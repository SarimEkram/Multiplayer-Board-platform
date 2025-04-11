package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.User;
import Authentication.UserDatabase;
import Authentication.ResetUserPassword;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Controller for the "Change Password" screen. Handles verification of the user's email,
 * checking their old password, and submitting a new password for reset.
 */
public class ChangePasswordController {

    // ----------------------------
    // BACKGROUND & LAYOUT NODES
    // ----------------------------

    /** Pane used to draw a grid pattern in the background. */
    @FXML private Pane gridBackground;

    /** Pane used to create a glowing radial effect. */
    @FXML private Pane cyberGlow;

    /**
     * The container (styled box) holding the "Forgot Password" form and
     * subsequent reset sections.
     */
    @FXML private AnchorPane forgotBox;

    // ----------------------------
    // FORGOT / RESET SECTIONS
    // ----------------------------

    /** The overall VBox containing the form elements (verify section, reset section). */
    @FXML private VBox forgotForm;

    /** The VBox holding the "verify email" UI elements. */
    @FXML private VBox verifyEmailSection;

    /** TextField where the user enters the email for verification. */
    @FXML private TextField forgotEmail;

    /** Button to verify the entered email. */
    @FXML private Button verifyEmailBtn;

    /** Label to display any error or success message for email verification. */
    @FXML private Label forgotErrorLabel;

    /** The VBox containing the reset password fields (initially hidden). */
    @FXML private VBox resetPasswordSection;

    /** Field where the user enters their old (current) password. */
    @FXML private PasswordField oldPassword;

    /** Field where the user enters the new password. */
    @FXML private PasswordField newPassword;

    /** Field to confirm the new password. */
    @FXML private PasswordField confirmNewPassword;

    /** Button to finalize the password reset process. */
    @FXML private Button resetBtn;

    /** Label to display success or error messages for the password reset process. */
    @FXML private Label resetErrorLabel;

    /** Backend service used to handle password reset logic. */
    private final ResetUserPassword resetUserPassword = new ResetUserPassword();

    /**
     * Initializes the controller once the FXML is loaded. Sets up the
     * UI visuals, hides unnecessary panels, and prepares the background effects.
     */
    @FXML
    public void initialize() {
        // Hide error labels and password reset section by default.
        hideNode(forgotErrorLabel);
        hideNode(resetErrorLabel);
        hideNode(resetPasswordSection);

        // Set up the background with neon grid and glow effects.
        setupBackground();

        // Configure the styling and effects of the "forgotBox".
        setupForgotBox();

        // Show the main form (email verification section).
        showNode(forgotForm);
    }

    /**
     * Verifies that the email entered by the user matches the email
     * of the currently logged-in user. If valid, transitions to
     * the password reset form.
     */
    @FXML
    private void proceedForgot() {
        String email = forgotEmail.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            forgotErrorLabel.setText("Please enter a valid email address.");
            forgotErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(forgotErrorLabel);
            return;
        }

        // Retrieve the currently logged-in user from the database.
        User currentUser = UserDatabase.getUserById(LoginController.loginId);
        if (currentUser == null || !currentUser.getEmail().equalsIgnoreCase(email)) {
            forgotErrorLabel.setText("The entered email does not match the current user's email.");
            forgotErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(forgotErrorLabel);
            return;
        }

        // Clear any previous error/success messages.
        hideNode(forgotErrorLabel);

        // Hide email verification controls.
        hideNode(verifyEmailSection);
        hideNode(verifyEmailBtn);

        // Inform user of success and then reveal the reset password section.
        forgotErrorLabel.setText("Email verified. Please enter your old password and new password.");
        forgotErrorLabel.setStyle("-fx-text-fill: green;");
        showNode(forgotErrorLabel);

        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            hideNode(forgotErrorLabel);
            showNode(resetPasswordSection);
            showNode(oldPassword);
            showNode(newPassword);
            showNode(confirmNewPassword);
            showNode(resetBtn);
        }));
        delay.play();
    }

    /**
     * Resets the password after validating old password correctness,
     * ensuring new passwords match, and generating a valid reset token.
     */
    @FXML
    private void resetPassword() {
        String oldPass = oldPassword.getText().trim();
        String newPass = newPassword.getText().trim();
        String confirmPass = confirmNewPassword.getText().trim();

        // Input validation: ensure no fields are empty.
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            resetErrorLabel.setText("All fields must be filled.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }

        // Check that the new password matches its confirmation.
        if (!newPass.equals(confirmPass)) {
            resetErrorLabel.setText("New password and confirmation do not match.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }

        // Clear any previous error messages.
        hideNode(resetErrorLabel);

        // Ensure the current user is found.
        User currentUser = UserDatabase.getUserById(LoginController.loginId);
        if (currentUser == null) {
            resetErrorLabel.setText("User not found.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }

        // Verify correctness of the old password.
        boolean validOld = resetUserPassword.verifyOldPassword(currentUser.getEmail(), oldPass);
        if (!validOld) {
            resetErrorLabel.setText("Old password does not match.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }

        // Generate a reset token.
        String token = resetUserPassword.resetRequest(currentUser.getEmail());
        if (token == null) {
            resetErrorLabel.setText("Unable to generate reset token.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }

        // Attempt the actual password reset using the token.
        boolean success = resetUserPassword.resetPassword(token, newPass);
        if (success) {
            resetErrorLabel.setText("Password reset successful!");
            resetErrorLabel.setStyle("-fx-text-fill: green;");
            showNode(resetErrorLabel);

            // Optionally clear fields or navigate away after delay.
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                // Clear form fields upon success.
                oldPassword.clear();
                newPassword.clear();
                confirmNewPassword.clear();
                // Navigate back to profile or any relevant screen.
                handleBack();
            }));
            delay.play();
        } else {
            resetErrorLabel.setText("Password reset failed. Please try again.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
        }
    }

    /**
     * Navigates back to the Manage Profile screen.
     */
    @FXML
    private void handleBack() {
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/views/manageProfile.fxml");
    }

    /**
     * Sets up the neon grid background by drawing lines onto a Canvas and
     * applying a glowing radial gradient overlay.
     */
    private void setupBackground() {
        // Create a canvas for the grid lines.
        Canvas gridCanvas = new Canvas();
        gridCanvas.widthProperty().bind(gridBackground.widthProperty());
        gridCanvas.heightProperty().bind(gridBackground.heightProperty());
        // Redraw whenever the pane is resized.
        gridCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        gridCanvas.heightProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        drawGrid(gridCanvas);
        gridBackground.getChildren().add(gridCanvas);

        // Create a rectangle for the glow effect.
        Rectangle glowRect = new Rectangle();
        glowRect.widthProperty().bind(cyberGlow.widthProperty());
        glowRect.heightProperty().bind(cyberGlow.heightProperty());

        // Define a radial gradient for the glow.
        RadialGradient gradient = new RadialGradient(
                0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 0, 255, 0.15)),
                new Stop(0.5, Color.rgb(0, 255, 255, 0.1)),
                new Stop(1, Color.rgb(255, 255, 0, 0.28))
        );
        glowRect.setFill(gradient);
        cyberGlow.getChildren().add(glowRect);

        // Animate the glow rectangle to pulse in scale and opacity.
        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(glowRect.opacityProperty(), 0.3),
                        new javafx.animation.KeyValue(glowRect.scaleXProperty(), 1),
                        new javafx.animation.KeyValue(glowRect.scaleYProperty(), 1)
                ),
                new KeyFrame(Duration.seconds(4),
                        new javafx.animation.KeyValue(glowRect.opacityProperty(), 0.5),
                        new javafx.animation.KeyValue(glowRect.scaleXProperty(), 1.05),
                        new javafx.animation.KeyValue(glowRect.scaleYProperty(), 1.05)
                )
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    /**
     * Configures the neon-styled box (forgotBox) that contains
     * the password reset forms.
     */
    private void setupForgotBox() {
        // Dark background with a slight border radius.
        forgotBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 1),
                new CornerRadii(10),
                Insets.EMPTY
        )));
        // Neon magenta border
        forgotBox.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 0, 255, 0.9),
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(1, 0.5, 0.5, 0.5)
        )));

        // Soft neon drop shadows
        DropShadow neonMagenta = new DropShadow();
        neonMagenta.setColor(Color.rgb(255, 255, 255, 0.3));
        neonMagenta.setRadius(20);

        DropShadow neonCyan = new DropShadow();
        neonCyan.setColor(Color.rgb(0, 255, 255, 0.2));
        neonCyan.setRadius(40);

        // Blend the two drop shadows for a layered neon effect
        Blend outerBlend = new Blend();
        outerBlend.setMode(BlendMode.SRC_OVER);
        outerBlend.setBottomInput(neonMagenta);
        outerBlend.setTopInput(neonCyan);

        Blend finalBlend = new Blend();
        finalBlend.setMode(BlendMode.SRC_OVER);
        finalBlend.setBottomInput(outerBlend);
        finalBlend.setTopInput((InnerShadow) null);

        forgotBox.setEffect(finalBlend);

        // Add a top line with a gradient
        Rectangle topLine = new Rectangle();
        topLine.setHeight(2);
        topLine.widthProperty().bind(forgotBox.widthProperty());
        topLine.setLayoutY(0);
        topLine.setManaged(false);

        // Gradient for the top line
        LinearGradient lineGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.33, Color.web("#ff00ff", 1.0)),
                new Stop(0.66, Color.web("#00ffff", 0.6)),
                new Stop(1, Color.TRANSPARENT)
        );
        topLine.setFill(lineGradient);

        // DropShadow for the top line
        DropShadow lineShadow = new DropShadow();
        lineShadow.setBlurType(javafx.scene.effect.BlurType.GAUSSIAN);
        lineShadow.setColor(Color.web("#00ffff"));
        lineShadow.setRadius(10);
        topLine.setEffect(lineShadow);

        forgotBox.getChildren().add(topLine);

        // Subtle breathing animation for the box
        Timeline breathing = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(forgotBox.scaleXProperty(), 1),
                        new javafx.animation.KeyValue(forgotBox.scaleYProperty(), 1)
                ),
                new KeyFrame(Duration.seconds(2),
                        new javafx.animation.KeyValue(forgotBox.scaleXProperty(), 1.02),
                        new javafx.animation.KeyValue(forgotBox.scaleYProperty(), 1.02)
                )
        );
        breathing.setCycleCount(Timeline.INDEFINITE);
        breathing.setAutoReverse(true);
        breathing.play();
    }

    /**
     * Draws a simple neon grid on the provided {@link Canvas}.
     *
     * @param canvas the {@code Canvas} on which to draw the grid lines
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

        // Vertical lines
        for (double x = 0; x <= width; x += step) {
            gc.fillRect(x, 0, lineWidth, height);
        }

        // Horizontal lines
        for (double y = 0; y <= height; y += step) {
            gc.fillRect(0, y, width, lineWidth);
        }
    }

    // ----------------------------
    // HELPER METHODS
    // ----------------------------

    /**
     * Hides the given UI node (removes it from layout flow).
     *
     * @param node the node to hide
     */
    private void hideNode(Node node) {
        if (node != null) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    /**
     * Shows the given UI node (restores it in layout flow).
     *
     * @param node the node to show
     */
    private void showNode(Node node) {
        if (node != null) {
            node.setVisible(true);
            node.setManaged(true);
        }
    }
}
