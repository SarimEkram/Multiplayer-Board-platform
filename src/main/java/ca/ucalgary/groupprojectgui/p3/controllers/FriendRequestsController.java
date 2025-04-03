package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class FriendRequestsController {

    @FXML
    private VBox requestsContainer;

    @FXML
    private void initialize() {
        // Dummy data (simulate friend requests)
        addFriendRequest("Player1");
        addFriendRequest("Player2");
    }

    private void addFriendRequest(String username) {
        HBox requestBox = new HBox(10);
        requestBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 10; -fx-background-radius: 10;");

        Label name = new Label(username);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Button accept = new Button("✔");
        Button decline = new Button("✖");

        accept.setStyle("-fx-background-color: #00e676; -fx-text-fill: white;");
        decline.setStyle("-fx-background-color: #ff1744; -fx-text-fill: white;");

        requestBox.getChildren().addAll(name, accept, decline);
        requestsContainer.getChildren().add(requestBox);
    }

    @FXML
    private void closePanel() {
        // This assumes the panel was added to the right of the BorderPane (like NotificationPanel)
        Node panel = requestsContainer.getParent().getParent(); // VBox > StackPane
        ((Pane) panel.getParent()).getChildren().remove(panel);
    }

}
