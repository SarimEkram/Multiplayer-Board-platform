package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class NotificationPanelController {

    @FXML private ListView<String> notificationList;
    @FXML private VBox notificationPanel;
    @FXML private Button closeButton;

    public void initialize() {
        notificationList.setItems(FXCollections.observableArrayList(
                "🎮 Match Found!", "❤️ New friend request", "📢 Game update available!"
        ));
    }

    @FXML
    private void closePanel() {
        ((Pane) notificationPanel.getParent()).getChildren().remove(notificationPanel);
    }

    @FXML
    private void hoverClose(MouseEvent event) {
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 14px; -fx-cursor: hand;");
    }

    @FXML
    private void unhoverClose(MouseEvent event) {
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
    }
}


