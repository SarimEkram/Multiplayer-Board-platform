package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class NotificationPanelController {
    @FXML
    private ListView<String> notificationList;

    public void initialize() {
        notificationList.setItems(FXCollections.observableArrayList(
                "🎮 Match Found!", "❤️ New friend request", "📢 Game update available!"
        ));
    }
    @FXML
    private VBox notificationPanel;

    @FXML
    private void closePanel() {
        ((Pane) notificationPanel.getParent()).getChildren().remove(notificationPanel);
    }
    @FXML
    private Button closeButton;

    @FXML
    private void hoverClose() {
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 14px;");
    }

    @FXML
    private void unhoverClose() {
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
    }



}

