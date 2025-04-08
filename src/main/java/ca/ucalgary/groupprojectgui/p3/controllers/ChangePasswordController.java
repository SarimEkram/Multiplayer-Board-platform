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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ChangePasswordController {

    // Root and background layers
    @FXML private AnchorPane root;
    @FXML private Pane gridBackground;
    @FXML private Pane animatedNeonLines;
    @FXML private Pane cyberGlow;

    // Container for the form (styled as the login box)
    @FXML private AnchorPane forgotBox;

    // Forgot Password Form Container
    @FXML private VBox forgotForm;

    // Email Verification Fields
    @FXML private VBox verifyEmailSection;
    @FXML private TextField forgotEmail;
    @FXML private Button verifyEmailBtn;
    @FXML private Label forgotErrorLabel;

    // Reset Password Section (initially hidden in FXML)
    @FXML private VBox resetPasswordSection;
    @FXML private PasswordField oldPassword;
    @FXML private PasswordField newPassword;
    @FXML private PasswordField confirmNewPassword;
    @FXML private Button resetBtn;
    @FXML private Label resetErrorLabel;

    // Backend service instance
    private final ResetUserPassword resetUserPassword = new ResetUserPassword();

    @FXML
    public void initialize() {
        // Hide error messages and reset password fields on startup.
        hideNode(forgotErrorLabel);
        hideNode(resetErrorLabel);
        hideNode(resetPasswordSection);

        // Setup visual background and container effects.
        setupBackground();
        setupForgotBox();

        // Show the forgot password form.
        showNode(forgotForm);
    }

    /**
     * Verifies the entered email against the current user's email.
     * If valid, hides the email verification controls and reveals the reset password section.
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

        User currentUser = UserDatabase.getUserById(LoginController.loginId);
        if (currentUser == null || !currentUser.getEmail().equalsIgnoreCase(email)) {
            forgotErrorLabel.setText("The entered email does not match the current user's email.");
            forgotErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(forgotErrorLabel);
            return;
        }

        // Clear any previous error/success message.
        hideNode(forgotErrorLabel);

        // Hide the email verification controls.
        hideNode(verifyEmailSection);
        hideNode(verifyEmailBtn);

        // Inform the user and then reveal the reset password section after a brief delay.
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
     * Resets the password by verifying that all fields are filled, that the new password and confirmation match,
     * and that the provided old password is correct before generating a valid reset token.
     * Displays proper error or success messages.
     */
    @FXML
    private void resetPassword() {
        String oldPass = oldPassword.getText().trim();
        String newPass = newPassword.getText().trim();
        String confirmPass = confirmNewPassword.getText().trim();

        // Input validation.
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            resetErrorLabel.setText("All fields must be filled.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }
        if (!newPass.equals(confirmPass)) {
            resetErrorLabel.setText("New password and confirmation do not match.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }

        // Hide previous error if any.
        hideNode(resetErrorLabel);

        User currentUser = UserDatabase.getUserById(LoginController.loginId);
        if (currentUser == null) {
            resetErrorLabel.setText("User not found.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }

        // Verify if the provided old password is correct.
        boolean validOld = resetUserPassword.verifyOldPassword(currentUser.getEmail(), oldPass);
        if (!validOld) {
            resetErrorLabel.setText("Old password does not match.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }

        // Generate a valid reset token for the user.
        String token = resetUserPassword.resetRequest(currentUser.getEmail());
        if (token == null) {
            resetErrorLabel.setText("Unable to generate reset token.");
            resetErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(resetErrorLabel);
            return;
        }

        // Attempt to reset the password using the token and the new password.
        boolean success = resetUserPassword.resetPassword(token, newPass);
        if (success) {
            resetErrorLabel.setText("Password reset successful!");
            resetErrorLabel.setStyle("-fx-text-fill: green;");
            showNode(resetErrorLabel);
            System.out.println("Password reset successful!");

            // Optionally, clear the fields or navigate to a different screen after a delay.
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
                // Clear fields after successful reset
                oldPassword.clear();
                newPassword.clear();
                confirmNewPassword.clear();
                // You can add navigation logic here if needed.
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
        SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/ManageProfile.fxml", "Manage Profile", "manage_profile.css");
    }

    /**
     * Sets up the grid background and neon glow effect.
     */
    private void setupBackground() {
        Canvas gridCanvas = new Canvas();
        gridCanvas.widthProperty().bind(gridBackground.widthProperty());
        gridCanvas.heightProperty().bind(gridBackground.heightProperty());
        gridCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        gridCanvas.heightProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        drawGrid(gridCanvas);
        gridBackground.getChildren().add(gridCanvas);

        Rectangle glowRect = new Rectangle();
        glowRect.widthProperty().bind(cyberGlow.widthProperty());
        glowRect.heightProperty().bind(cyberGlow.heightProperty());
        RadialGradient gradient = new RadialGradient(
                0, 0, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 0, 255, 0.15)),
                new Stop(0.5, Color.rgb(0, 255, 255, 0.1)),
                new Stop(1, Color.rgb(255, 255, 0, 0.28))
        );
        glowRect.setFill(gradient);
        cyberGlow.getChildren().add(glowRect);

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
     * Sets up the styling and visual effects for the forgotBox container.
     */
    private void setupForgotBox() {
        forgotBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 1),
                new CornerRadii(10),
                Insets.EMPTY
        )));
        forgotBox.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 0, 255, 0.9),
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(1, 0.5, 0.5, 0.5)
        )));

        DropShadow neonMagenta = new DropShadow();
        neonMagenta.setColor(Color.rgb(255, 255, 255, 0.3));
        neonMagenta.setRadius(20);

        DropShadow neonCyan = new DropShadow();
        neonCyan.setColor(Color.rgb(0, 255, 255, 0.2));
        neonCyan.setRadius(40);

        Blend outerBlend = new Blend();
        outerBlend.setMode(BlendMode.SRC_OVER);
        outerBlend.setBottomInput(neonMagenta);
        outerBlend.setTopInput(neonCyan);

        Blend finalBlend = new Blend();
        finalBlend.setMode(BlendMode.SRC_OVER);
        finalBlend.setBottomInput(outerBlend);
        finalBlend.setTopInput((InnerShadow) null);

        forgotBox.setEffect(finalBlend);

        Rectangle topLine = new Rectangle();
        topLine.setHeight(2);
        topLine.widthProperty().bind(forgotBox.widthProperty());
        topLine.setLayoutY(0);
        topLine.setManaged(false);
        LinearGradient lineGradient = new LinearGradient(
                0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.33, Color.web("#ff00ff", 1.0)),
                new Stop(0.66, Color.web("#00ffff", 0.6)),
                new Stop(1, Color.TRANSPARENT)
        );
        topLine.setFill(lineGradient);
        DropShadow lineShadow = new DropShadow();
        lineShadow.setBlurType(javafx.scene.effect.BlurType.GAUSSIAN);
        lineShadow.setColor(Color.web("#00ffff"));
        lineShadow.setRadius(10);
        topLine.setEffect(lineShadow);
        forgotBox.getChildren().add(topLine);

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
     * Draws a neon grid on the provided Canvas.
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

    // --- Helper Methods ---
    private void hideNode(Node node) {
        if (node != null) {
            node.setVisible(false);
            node.setManaged(false);
        }
    }

    private void showNode(Node node) {
        if (node != null) {
            node.setVisible(true);
            node.setManaged(true);
        }
    }
}
