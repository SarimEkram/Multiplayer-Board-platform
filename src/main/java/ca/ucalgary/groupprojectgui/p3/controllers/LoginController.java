package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.User;
import Authentication.UserDatabase;
import Authentication.UserLogin;
import Authentication.UserRegistration;
import Authentication.ResetTokenData;
import Authentication.ResetUserPassword;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class LoginController {

    public static int loginId;

    // Root and background layers
    @FXML private AnchorPane root;
    @FXML private Pane gridBackground;
    @FXML private Pane animatedNeonLines;
    @FXML private Pane cyberGlow;

    // Main container and login box
    @FXML private VBox loginContainer;
    @FXML private AnchorPane loginBox;

    // Forms within the StackPane
    @FXML private VBox loginForm;
    @FXML private VBox forgotForm;
    @FXML private VBox registerForm;

    // --- Login Form Fields ---
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Button loginButton;
    @FXML private Label loginErrorLabel;

    // --- Forgot Password Form Fields ---
    @FXML private VBox verifyEmailSection;
    @FXML private TextField forgotEmail;
    @FXML private Button verifyEmailBtn;
    @FXML private Label forgotErrorLabel; // error message for forgot password validation

    // Forgot form - reset password section (initially hidden)
    @FXML private VBox resetPasswordSection;
    @FXML private TextField resetToken;
    @FXML private PasswordField newPassword;
    @FXML private PasswordField confirmNewPassword;
    @FXML private Button resetBtn;
    @FXML private Label resetErrorLabel; // error message in reset password section

    // --- Register Form Fields ---
    @FXML private TextField registerFullName;
    @FXML private TextField registerUsername;
    @FXML private TextField registerEmail;
    @FXML private PasswordField registerPassword;
    @FXML private PasswordField registerConfirmPassword;
    @FXML private Button registerBtn;
    @FXML private Label registerErrorLabel; // error message for registration validation

    // Instance of backend classes for registration and password reset
    private final ResetUserPassword resetUserPassword = new ResetUserPassword();
    private final UserRegistration userRegistration = new UserRegistration();

    @FXML
    public void initialize() {
        // Show login form by default; hide forgot and register forms.
        hideNode(forgotForm);
        hideNode(registerForm);
        showNode(loginForm);

        // Hide error messages at startup.
        hideNode(loginErrorLabel);
        hideNode(forgotErrorLabel);
        hideNode(resetErrorLabel);
        hideNode(registerErrorLabel);

        // --- Bind button widths to corresponding input fields ---
        if (loginUsername != null && loginButton != null) {
            loginButton.prefWidthProperty().bind(loginUsername.widthProperty());
        }
        if (forgotEmail != null && verifyEmailBtn != null) {
            verifyEmailBtn.prefWidthProperty().bind(forgotEmail.widthProperty());
        }
        if (registerFullName != null && registerBtn != null) {
            registerBtn.prefWidthProperty().bind(registerFullName.widthProperty());
        }

        // --- Set up grid background using a Canvas ---
        Canvas gridCanvas = new Canvas();
        gridCanvas.widthProperty().bind(gridBackground.widthProperty());
        gridCanvas.heightProperty().bind(gridBackground.heightProperty());
        gridCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        gridCanvas.heightProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        drawGrid(gridCanvas);
        gridBackground.getChildren().add(gridCanvas);

        // --- Set up cyber glow background ---
        Rectangle glowRect = new Rectangle();
        glowRect.widthProperty().bind(cyberGlow.widthProperty());
        glowRect.heightProperty().bind(cyberGlow.heightProperty());
        RadialGradient gradient = new RadialGradient(
                0,
                0,
                0.5, 0.5,
                1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(255, 0, 255, 0.15)),
                new Stop(0.5, Color.rgb(0, 255, 255, 0.1)),
                new Stop(1, Color.rgb(255, 255, 0, 0.28))
        );
        glowRect.setFill(gradient);
        cyberGlow.getChildren().add(glowRect);

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

        // --- Set loginBox background, border, and shadow effects ---
        loginBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 1),
                new CornerRadii(10),
                Insets.EMPTY
        )));
        loginBox.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 0, 255, 0.9),
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(1, 0.5, 0.5, 0.5)
        )));

        DropShadow neonMagenta = new DropShadow();
        neonMagenta.setColor(Color.rgb(255, 255, 255, 0.3));
        neonMagenta.setRadius(20);
        neonMagenta.setOffsetX(0);
        neonMagenta.setOffsetY(0);

        DropShadow neonCyan = new DropShadow();
        neonCyan.setColor(Color.rgb(0, 255, 255, 0.2));
        neonCyan.setRadius(40);
        neonCyan.setOffsetX(0);
        neonCyan.setOffsetY(0);

        InnerShadow innerShadow = null;
        Blend outerBlend = new Blend();
        outerBlend.setMode(BlendMode.SRC_OVER);
        outerBlend.setBottomInput(neonMagenta);
        outerBlend.setTopInput(neonCyan);

        Blend finalBlend = new Blend();
        finalBlend.setMode(BlendMode.SRC_OVER);
        finalBlend.setBottomInput(outerBlend);
        finalBlend.setTopInput(innerShadow);

        loginBox.setEffect(finalBlend);

        // --- Add the pseudo-element gradient top line ---
        Rectangle topLine = new Rectangle();
        topLine.setHeight(2);
        topLine.widthProperty().bind(loginBox.widthProperty());
        topLine.setLayoutY(0);
        topLine.setManaged(false);
        LinearGradient lineGradient = new LinearGradient(
                0, 0, 1, 0,
                true,
                CycleMethod.NO_CYCLE,
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
        lineShadow.setOffsetX(0);
        lineShadow.setOffsetY(0);
        topLine.setEffect(lineShadow);
        loginBox.getChildren().add(topLine);

        // --- (Optional) Animate the loginBox ("breathing" effect) ---
        Timeline breathing = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(loginBox.scaleXProperty(), 1),
                        new KeyValue(loginBox.scaleYProperty(), 1)
                ),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(loginBox.scaleXProperty(), 1.02),
                        new KeyValue(loginBox.scaleYProperty(), 1.02)
                )
        );
        breathing.setCycleCount(Timeline.INDEFINITE);
        breathing.setAutoReverse(true);
        breathing.play();
    }

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

    private String generateToken() {
        // For integration with ResetUserPassword, call the reset request.
        String token = resetUserPassword.resetRequest(forgotEmail.getText().trim());
        return (token != null) ? token : "ERROR";
    }

    private void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    // --- Form Switching Methods ---
    @FXML
    private void showLoginForm() {
        hideNode(forgotForm);
        hideNode(registerForm);
        showNode(loginForm);
        hideNode(loginErrorLabel);
    }

    @FXML
    private void showForgotForm() {
        hideNode(loginForm);
        hideNode(registerForm);
        showNode(forgotForm);
        showNode(verifyEmailSection);
        showNode(verifyEmailBtn);
        hideNode(forgotErrorLabel);
        hideNode(resetErrorLabel);
        hideNode(resetPasswordSection);
    }

    @FXML
    private void showRegisterForm() {
        hideNode(loginForm);
        hideNode(forgotForm);
        showNode(registerForm);
        hideNode(registerErrorLabel);
    }

    // --- Login Form Handler (Integrated with backend) ---
    @FXML
    private void handleLogin() {
        String user = loginUsername.getText().trim();
        String pass = loginPassword.getText().trim();
        if (user.isEmpty() || pass.isEmpty()) {
            loginErrorLabel.setText("Username or Password cannot be empty.");
            showNode(loginErrorLabel);
            return;
        }
        User loginUser = UserDatabase.getUserByUsername(user);
        if (loginUser == null) {
            loginErrorLabel.setText("User does not exist.");
            showNode(loginErrorLabel);
            return;
        }
        int loginSuccessful = UserLogin.loginUser(user, pass);
        if (loginSuccessful != -1) {
            hideNode(loginErrorLabel);
            loginId = loginUser.getUserID();
            System.out.println("Login successful for: " + user);
            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/HomePage.fxml", "Home Page", "Home.css");
        } else {
            loginErrorLabel.setText("Invalid username or password.");
            showNode(loginErrorLabel);
        }
    }

    @FXML
    private void proceedForgot() {
        String email = forgotEmail.getText().trim();
        // Simple email validation: must not be empty and must contain '@'
        if (email.isEmpty() || !email.contains("@")) {
            forgotErrorLabel.setText("Please enter a valid email address.");
            forgotErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(forgotErrorLabel);
            return;
        } else {
            hideNode(forgotErrorLabel);
        }

        // Hide the verification section and button so they don't take up layout space.
        hideNode(verifyEmailSection);
        hideNode(verifyEmailBtn);

        // Request the reset token from the backend.
        String token = resetUserPassword.resetRequest(email);
        if (token == null) {
            // If token generation failed (for example, email doesn't exist), show an error message.
            forgotErrorLabel.setText("Reset request failed: email does not exist. Redirecting to login...");
            forgotErrorLabel.setStyle("-fx-text-fill: red;");
            showNode(forgotErrorLabel);
            // After 2 seconds, hide the error and go back to the login form.
            Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                hideNode(forgotErrorLabel);
                showLoginForm();  // Redirecting to login form
            }));
            delayTimeline.play();
            return;
        }

        // If a token is generated, copy it to the clipboard.
        copyToClipboard(token);
        String displayToken = token.substring(0, Math.min(4, token.length())) + "******";
        // Show a temporary info message (green) that the token has been sent.
        forgotErrorLabel.setText("Token sent! (" + displayToken + ") - copied to clipboard");
        forgotErrorLabel.setStyle("-fx-text-fill: #00ff00;");
        showNode(forgotErrorLabel);

        // After 2 seconds, hide the info message and show the reset password section.
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            hideNode(forgotErrorLabel);
            showNode(resetPasswordSection);
        }));
        delayTimeline.play();
    }



    @FXML
    private void resetPassword() {
        if (resetToken.getText().trim().isEmpty() ||
                newPassword.getText().trim().isEmpty() ||
                confirmNewPassword.getText().trim().isEmpty()) {
            resetErrorLabel.setText("All fields must be filled.");
            showNode(resetErrorLabel);
            return;
        }
        if (!newPassword.getText().equals(confirmNewPassword.getText())) {
            resetErrorLabel.setText("Passwords do not match.");
            showNode(resetErrorLabel);
            return;
        }
        hideNode(resetErrorLabel);
        boolean resetSuccess = resetUserPassword.resetPassword(resetToken.getText().trim(), newPassword.getText());
        if (resetSuccess) {
            System.out.println("Password reset successful!");
            showLoginForm();
        } else {
            resetErrorLabel.setText("Invalid token or password error.");
            showNode(resetErrorLabel);
        }
    }

    @FXML
    private void handleRegister() {
        String usernameInput = registerUsername.getText().trim();
        String emailInput = registerEmail.getText().trim();
        String passwordInput = registerPassword.getText().trim();
        String confirmPasswordInput = registerConfirmPassword.getText().trim();

        List<String> errors = new ArrayList<>();
        if (!passwordInput.equals(confirmPasswordInput)) {
            errors.add("Passwords do not match.");
        }

        // Call registerUser which returns a List of error messages.
        List<String> registrationErrors = userRegistration.registerUser(usernameInput, emailInput, passwordInput);
        errors.addAll(registrationErrors);

        if (!errors.isEmpty()) {
            String errorMsg = String.join("\n", errors);
            registerErrorLabel.setText(errorMsg);
            showNode(registerErrorLabel);
            return;
        }
        hideNode(registerErrorLabel);
        System.out.println("Registration successful!");
        // Display a success message and then switch back to login form.
        registerErrorLabel.setText("Registration successful! Redirecting to login...");
        registerErrorLabel.setStyle("-fx-text-fill: #00ff00;");
        showNode(registerErrorLabel);
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            hideNode(registerErrorLabel);
            showLoginForm();
        }));
        delay.play();
    }

}
