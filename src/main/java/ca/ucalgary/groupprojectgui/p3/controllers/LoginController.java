package ca.ucalgary.groupprojectgui.p3.controllers;

import ca.ucalgary.groupprojectgui.p3.Fonts;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;


public class LoginController {

    public static int loginId;
    // Form Containers
    @FXML
    private VBox loginForm;
    @FXML
    private VBox forgotForm;
    @FXML
    private VBox registerForm;
    @FXML
    private VBox verifyEmailSection;
    @FXML
    private VBox resetPasswordSection;
    @FXML
    private Pane animatedNeonLines;
    @FXML
    private Pane cyberGlow;

    // Login Form Fields
    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private CheckBox rememberMe;

    // Forgot Form Fields
    @FXML
    private TextField forgotEmail;
    @FXML
    private TextField resetToken;
    @FXML
    private PasswordField newPassword;
    @FXML
    private PasswordField confirmNewPassword;
    @FXML
    private Pane gridBackground;


    // Register Form Fields
    @FXML
    private TextField registerFullName;
    @FXML
    private TextField registerUsername;
    @FXML
    private TextField registerEmail;
    @FXML
    private PasswordField registerPassword;
    @FXML
    private PasswordField registerConfirmPassword;
    @FXML
    private AnchorPane loginBox;
    @FXML
    private VBox loginContainer;


    @FXML
    private Button verifyEmailBtn;
    @FXML
    private Button registerBtn;
    @FXML
    private Button resetBtn;



    @FXML
    private AnchorPane root;



    @FXML
    public void initialize() {

        forgotForm.setVisible(false);
        forgotForm.setManaged(false);
        registerForm.setVisible(false);
        registerForm.setManaged(false);

        loginForm.setVisible(true);
        loginForm.setManaged(true);



        // Bind the verify email button's width to the forgot email field
        verifyEmailBtn.prefWidthProperty().bind(forgotEmail.widthProperty());
        ReadOnlyDoubleProperty width= registerConfirmPassword.widthProperty();

        registerBtn.prefWidthProperty().bind(width);


        Canvas gridCanvas = new Canvas();
        // Bind canvas size to gridBackground so it always fills the container.
        gridCanvas.widthProperty().bind(gridBackground.widthProperty());
        gridCanvas.heightProperty().bind(gridBackground.heightProperty());
        // Redraw the grid whenever the size changes.
        gridCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        gridCanvas.heightProperty().addListener((obs, oldVal, newVal) -> drawGrid(gridCanvas));
        drawGrid(gridCanvas);
        // Add the grid canvas to gridBackground.
        gridBackground.getChildren().add(gridCanvas);

        // Create a Rectangle to fill the cyberGlow Pane.
        Rectangle glowRect = new Rectangle();
        glowRect.widthProperty().bind(cyberGlow.widthProperty());
        glowRect.heightProperty().bind(cyberGlow.heightProperty());

        // Create a RadialGradient with a radius of 1 to cover the entire rectangle.
        RadialGradient gradient = new RadialGradient(
                0,                      // focusAngle
                0,                      // focusDistance
                0.5, 0.5,               // centerX, centerY (proportional; center of pane)
                1,                      // radius: 1 means the gradient fills the entire rectangle
                true,                   // proportional coordinates
                CycleMethod.NO_CYCLE,   // no repeating
                new Stop(0, Color.rgb(255, 0, 255, 0.15)),   // at 0% (center): neon magenta, 10% opacity
                new Stop(0.5, Color.rgb(0, 255, 255, 0.1)),   // at 50%: neon cyan, 10% opacity
                new Stop(1, Color.rgb(255, 255, 0, 0.28))        // at 100%: neon yellow, 10% opacity
        );
        glowRect.setFill(gradient);

        // Add the glow rectangle to the cyberGlow Pane.
        cyberGlow.getChildren().add(glowRect);

        // Animate the glow rectangle to pulse more noticeably.
        Timeline pulseTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(glowRect.opacityProperty(), 0.3),
                        new KeyValue(glowRect.scaleXProperty(), 1),
                        new KeyValue(glowRect.scaleYProperty(), 1)
                ),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(glowRect.opacityProperty(), 0.5),
                        // Increase scaling to 1.05 for a more pronounced pulse effect.
                        new KeyValue(glowRect.scaleXProperty(), 1.05),
                        new KeyValue(glowRect.scaleYProperty(), 1.05)
                )
        );
        pulseTimeline.setCycleCount(Timeline.INDEFINITE);
        pulseTimeline.setAutoReverse(true);
        pulseTimeline.play();

        // --- 3. Ensure Correct Z-Order ---
        // The FXML node order matters. Typically the first child is rendered at the bottom.
        // In your FXML, ensure that 'gridBackground' is below any nodes that should appear on top,
        // and 'cyberGlow' is placed as needed (often behind the login form).
        // You can also use methods like toFront() or toBack() to adjust ordering dynamically:
        // gridBackground.toBack();
        // cyberGlow.toBack();


        // --- 1. Set Background & Border (matching design) ---
        loginBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 1),
                new CornerRadii(10),
                Insets.EMPTY)));
        // Apply a border with no top stroke and thin, less opaque strokes on left/right/bottom.
        loginBox.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 0, 255, 0.9), // lower opacity neon magenta
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(1, 0.5, 0.5, 0.5)  // Top: 0, Right: 0.5, Bottom: 0.5, Left: 0.5
        )));


        // --- 2. Create Shadow Effects and Combine via Blend ---
        // Outer neon magenta shadow (simulate 0 0 20px rgba(255,0,255,0.3))
        DropShadow neonMagenta = new DropShadow();
        neonMagenta.setColor(Color.rgb(255, 255, 255, 0.3));
        neonMagenta.setRadius(20);
        neonMagenta.setOffsetX(0);
        neonMagenta.setOffsetY(0);

        // Outer neon cyan shadow (simulate 0 0 40px rgba(0,255,255,0.2))
        DropShadow neonCyan = new DropShadow();
        neonCyan.setColor(Color.rgb(0, 255, 255, 0.2));
        neonCyan.setRadius(40);
        neonCyan.setOffsetX(0);
        neonCyan.setOffsetY(0);

//        // Inner shadow (simulate inset 0 0 30px rgba(0,0,0,0.8))
//        InnerShadow innerShadow = new InnerShadow();
//        innerShadow.setColor(Color.rgb(255, 255, 255, 0.8));
//        innerShadow.setRadius(30);
//        innerShadow.setOffsetX(0);
//        innerShadow.setOffsetY(0);
        // Either remove it entirely:
        InnerShadow innerShadow = null;

//// Or reduce its radius/opacity
//        InnerShadow innerShadow = new InnerShadow();
//        innerShadow.setBlurType(BlurType.GAUSSIAN);
//        innerShadow.setColor(Color.rgb(0, 0, 0, 0.5)); // less opacity
//        innerShadow.setRadius(10);                     // smaller radius


        // Blend the two outer shadows.
        Blend outerBlend = new Blend();
        outerBlend.setMode(BlendMode.SRC_OVER);
        // Here we combine the magenta and cyan glows.
        outerBlend.setBottomInput(neonMagenta);
        outerBlend.setTopInput(neonCyan);

        // Now blend that result with the inner shadow.
        Blend finalBlend = new Blend();
        finalBlend.setMode(BlendMode.SRC_OVER);
        finalBlend.setBottomInput(outerBlend);
        finalBlend.setTopInput(innerShadow);

        // Set the composite effect on the loginBox.
        loginBox.setEffect(finalBlend);

        // --- 3. Add the "Pseudo-element" Gradient Top Line (Login Box ::before) ---
        Rectangle topLine = new Rectangle();
        topLine.setHeight(2);
// Bind the width to the loginBox width.
        topLine.widthProperty().bind(loginBox.widthProperty());
// Position it at the top.
        topLine.setLayoutY(0);
// Mark it as unmanaged so it doesn't affect loginBox's calculated size.
        topLine.setManaged(false);

        LinearGradient lineGradient = new LinearGradient(
                0, 0, 1, 0,      // from left to right (proportional)
                true,            // using proportional coordinates
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.33, Color.web("#ff00ff", 1.0)),  // neon magenta fully opaque
                new Stop(0.66, Color.web("#00ffff", 0.6)),   // neon cyan at 50% opacity
                new Stop(1, Color.TRANSPARENT)
        );

        topLine.setFill(lineGradient);

        DropShadow lineShadow = new DropShadow();
        lineShadow.setBlurType(BlurType.GAUSSIAN);
        lineShadow.setColor(Color.web("#00ffff"));
        lineShadow.setRadius(10);
        lineShadow.setOffsetX(0);
        lineShadow.setOffsetY(0);
        topLine.setEffect(lineShadow);

// Add the pseudo-element to the loginBox.
        loginBox.getChildren().add(topLine);


        // --- (Optional) Animate the loginBox ("breathing" effect) if desired ---
        // For example, to subtly animate scale:
        Timeline breathing = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(loginBox.scaleXProperty(), 1),
                        new KeyValue(loginBox.scaleYProperty(), 1)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(loginBox.scaleXProperty(), 1.02),
                        new KeyValue(loginBox.scaleYProperty(), 1.02))
        );
        breathing.setCycleCount(Timeline.INDEFINITE);
        breathing.setAutoReverse(true);
        breathing.play();
    }

    private void drawGrid(Canvas canvas) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Clear the canvas; if you want the dark background to be visible,
        // fill with the base color:
        gc.setFill(Color.web("#0a0a12"));
        gc.fillRect(0, 0, width, height);

        // Set grid line color: subtle neon magenta at 5% opacity.
        gc.setFill(Color.web("#ff00ff", 0.05));
        double step = 40.0;
        double lineWidth = 2;  // Reduced line width

        // Draw vertical grid lines.
        for (double x = 0; x <= width; x += step) {
            gc.fillRect(x, 0, lineWidth, height);
        }
        // Draw horizontal grid lines.
        for (double y = 0; y <= height; y += step) {
            gc.fillRect(0, y, width, lineWidth);
        }
    }
    @FXML
    private void showRegisterForm() {
        // Hide the login and forgot forms completely so they don’t influence layout.
        loginForm.setVisible(false);
        loginForm.setManaged(false);

        forgotForm.setVisible(false);
        forgotForm.setManaged(false);

        // Now show the register form and allow it to participate in layout.
        registerForm.setVisible(true);
        registerForm.setManaged(true);
    }

    @FXML
    private void showLoginForm() {
        // When showing login form, hide the others.
        registerForm.setVisible(false);
        registerForm.setManaged(false);

        forgotForm.setVisible(false);
        forgotForm.setManaged(false);

        loginForm.setVisible(true);
        loginForm.setManaged(true);
    }

    @FXML
    private void showForgotForm() {
        // When showing forgot form, hide the others.
        loginForm.setVisible(false);
        loginForm.setManaged(false);

        registerForm.setVisible(false);
        registerForm.setManaged(false);

        forgotForm.setVisible(true);
        forgotForm.setManaged(true);

        // Additionally, for multi‑step forms you might need to mark sub‑sections accordingly:
        verifyEmailSection.setVisible(true);
        verifyEmailSection.setManaged(true);

        resetPasswordSection.setVisible(false);
        resetPasswordSection.setManaged(false);

        // Reset the verify button also.
        verifyEmailBtn.setVisible(true);
        verifyEmailBtn.setManaged(true);


    }


    @FXML
    private void handleLogin() {
        System.out.println("Login attempted with username: " + loginUsername.getText());
        // Add login validation and processing logic here.
    }

    @FXML
    private void handleRegister() {
        System.out.println("Register attempted with username: " + registerUsername.getText());
        // Add registration logic here.
        showLoginForm();
    }

    @FXML
    private void proceedForgot() {
        if (forgotEmail.getText().trim().isEmpty()) {
            System.out.println("Please enter a valid email address.");
            return;
        }
        // Hide the email verification section and its associated button so they do not take space.
        verifyEmailSection.setVisible(false);
        verifyEmailSection.setManaged(false);

        verifyEmailBtn.setVisible(false);
        verifyEmailBtn.setManaged(false);


        resetPasswordSection.setVisible(true);
        resetPasswordSection.setManaged(true);

    }


    @FXML
    private void resetPassword() {
        if (resetToken.getText().trim().isEmpty() ||
                newPassword.getText().trim().isEmpty() ||
                !newPassword.getText().equals(confirmNewPassword.getText())) {
            System.out.println("Please ensure all fields are valid and both passwords match.");
            return;
        }
        System.out.println("Password reset successful!");
        showLoginForm();
    }
}
