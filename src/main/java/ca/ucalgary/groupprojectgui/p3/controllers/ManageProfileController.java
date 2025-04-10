package ca.ucalgary.groupprojectgui.p3.controllers;

import Authentication.DeleteUserAccount;
import ca.ucalgary.groupprojectgui.p3.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
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
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;


/**
 * Controller class for managing user profile-related actions.
 * Supports editing profile, changing password, and deleting the user account.
 */
public class ManageProfileController {

    public Pane gridBackground;
    public Pane cyberGlow;
    @FXML
    private VBox profileBox;



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
        // Reference to the main root node
        Node mainRoot = profileBox.getScene().getRoot();
        // Apply a Gaussian blur effect to the background
        GaussianBlur blur = new GaussianBlur(12);
        mainRoot.setEffect(blur);

        // First confirmation dialog
        Dialog<ButtonType> firstDialog = new Dialog<>();
        firstDialog.setTitle("Delete Account");

        // Set owner and modality so the dialog appears as a modal popup on top of the Manage Profile screen
        firstDialog.initOwner(profileBox.getScene().getWindow());
        firstDialog.initModality(Modality.WINDOW_MODAL);
        firstDialog.initStyle(StageStyle.TRANSPARENT);

        DialogPane firstDialogPane = firstDialog.getDialogPane();
        firstDialogPane.setStyle(
                "-fx-background-color: #1a1a1a; " +
                        "-fx-border-color: #ff00ff; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;"
        );
        firstDialogPane.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/styles/manage_profile.css")).toExternalForm()
        );
        firstDialogPane.getStyleClass().add("custom-dialog");

        Label firstConfirmText = new Label("Are you sure you want to delete your account?");
        firstConfirmText.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10px;"
        );

        VBox firstContentBox = new VBox(firstConfirmText);
        firstContentBox.setSpacing(15);
        firstContentBox.setStyle("-fx-alignment: center; -fx-padding: 20;");
        firstDialogPane.setContent(firstContentBox);

        ButtonType yesBtn = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noBtn  = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        firstDialogPane.getButtonTypes().setAll(yesBtn, noBtn);

        firstDialog.showAndWait().ifPresent(response -> {
            if (response == yesBtn) {
                // Second confirmation dialog for final confirmation
                Dialog<ButtonType> secondDialog = new Dialog<>();
                secondDialog.setTitle("Confirm Deletion");

                secondDialog.initOwner(profileBox.getScene().getWindow());
                secondDialog.initModality(Modality.WINDOW_MODAL);
                secondDialog.initStyle(StageStyle.TRANSPARENT);

                DialogPane secondDialogPane = secondDialog.getDialogPane();
                secondDialogPane.setStyle(
                        "-fx-background-color: #1a1a1a; " +
                                "-fx-border-color: #ff00ff; " +
                                "-fx-border-width: 2px; " +
                                "-fx-border-radius: 10px; " +
                                "-fx-background-radius: 10px;"
                );
                secondDialogPane.getStylesheets().add(
                        Objects.requireNonNull(getClass().getResource("/ca/ucalgary/groupprojectgui/p3/styles/manage_profile.css")).toExternalForm()
                );
                secondDialogPane.getStyleClass().add("custom-dialog");

                Label secondConfirmText = new Label("This action cannot be undone.\nAre you really sure you want to delete your account?");
                secondConfirmText.setStyle(
                        "-fx-text-fill: white; " +
                                "-fx-font-size: 16px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 10px;"
                );

                VBox secondContentBox = new VBox(secondConfirmText);
                secondContentBox.setSpacing(15);
                secondContentBox.setStyle("-fx-alignment: center; -fx-padding: 20;");
                secondDialogPane.setContent(secondContentBox);

                ButtonType confirmBtn = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancelBtn  = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                secondDialogPane.getButtonTypes().setAll(confirmBtn, cancelBtn);

                secondDialog.showAndWait().ifPresent(secondResponse -> {
                    if (secondResponse == confirmBtn) {
                        int userId = LoginController.loginId;
                        if (DeleteUserAccount.deleteAccount(userId)) {
                            // Remove blur effect and switch to Login page
                            mainRoot.setEffect(null);
                            SceneManager.switchTo("/ca/ucalgary/groupprojectgui/p3/Login.fxml", "Login", "login.css");
                        } else {
                            // Optionally show error alert if deletion failed
                            showDialog("Error", "Failed to delete account. Please try again.", Alert.AlertType.ERROR);
                            mainRoot.setEffect(null);
                        }
                    } else {
                        // Cancel on second dialog: simply remove blur
                        mainRoot.setEffect(null);
                    }
                });
            } else {
                // Cancel on first dialog: simply remove blur
                mainRoot.setEffect(null);
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
