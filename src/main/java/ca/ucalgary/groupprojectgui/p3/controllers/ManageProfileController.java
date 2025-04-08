package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.DeleteUserAccount;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Objects;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


/**
 * Controller class for managing user profile-related actions.
 * Supports editing profile, changing password, and deleting the user account.
 */
public class ManageProfileController {

    public Pane gridBackground;
    public Pane cyberGlow;


    @FXML
    public void initialize() {

        setupBackground();
    }
    /**
     * Navigates the user back to the home page.
     */
    @FXML
    private void handleBackButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/HomePage.fxml",
                "Home Page",
                "home.css"
        );
    }
    /**
     * Navigates to the Edit Profile screen.
     */
    @FXML
    private void handleEditProfileButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/EditProfile.fxml",
                "Edit Profile",
                "edit_profile.css"
        );
    }
    /**
     * Navigates to the Change Password screen.
     */
    @FXML
    private void handleChangePasswordButtonClick() {
        SceneManager.switchTo(
                "/ca/ucalgary/groupprojectgui/p3/ChangePassword.fxml",
                "Change Password",
                "change_password.css"
        );
    }
    /**
     * Handles account deletion with confirmation dialog.
     * If confirmed, deletes the user's account and navigates to login screen.
     */
    @FXML
    private void handleDeleteProfile() {
        // Create a confirmation dialog for deleting the account
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Delete Account");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/styles/manage_profile.css")).toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");

        Label confirmText = new Label("Are you sure you want to delete your account?");
        confirmText.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox contentBox = new VBox(confirmText);
        contentBox.setSpacing(15);
        contentBox.setStyle("-fx-alignment: center; -fx-padding: 20;");
        dialogPane.setContent(contentBox);

        ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().setAll(yesBtn, noBtn);

        dialog.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                int userId = LoginController.loginId;
                if (DeleteUserAccount.deleteAccount(userId)) {
                    showDialog("Success", "Your account has been successfully deleted.", Alert.AlertType.INFORMATION);
                    SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/Login.fxml", "Login", "login.css");
                } else {
                    showDialog("Error", "Failed to delete account. Please try again.", Alert.AlertType.ERROR);
                }
            } else {
                showDialog("Cancelled", "Account deletion cancelled.", Alert.AlertType.INFORMATION);
            }
        });
    }

    /**
     * Utility method to show alert dialogs.
     *
     * @param title   The title of the alert window.
     * @param content The message content.
     * @param type    The type of alert
     */
    private void showDialog(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void setupBackground() {
        // Instead of drawing a grid, set the background of gridBackground to pure black.
        gridBackground.setStyle("-fx-background-color: black;");

        // -- Cyber glow overlay: create a pulsing gradient on top --
        Rectangle glowRect = new Rectangle();
        glowRect.widthProperty().bind(cyberGlow.widthProperty());
        glowRect.heightProperty().bind(cyberGlow.heightProperty());
        RadialGradient gradient = new RadialGradient(
                0, 0,
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

        // -- Animate the glow (pulse effect) --
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



}
