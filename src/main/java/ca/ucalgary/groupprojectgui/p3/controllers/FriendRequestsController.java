package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FriendRequestsController {

    @FXML private TextField searchField;
    @FXML private VBox playersContainer;

    private final List<String> allPlayers = List.of("MatrixMaster", "GlitchGamer", "SynthSamurai");

    @FXML
    public void initialize() {
        loadPlayerList(allPlayers);

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            List<String> filtered = allPlayers.stream()
                    .filter(name -> name.toLowerCase().contains(newText.toLowerCase()))
                    .collect(Collectors.toList());
            loadPlayerList(filtered);
        });
    }

    private void loadPlayerList(List<String> players) {
        playersContainer.getChildren().clear();
        for (String player : players) {
            playersContainer.getChildren().add(createPlayerEntry(player));
        }
    }

    private HBox createPlayerEntry(String username) {
        HBox entry = new HBox(10);
        entry.getStyleClass().add("player-entry");

        Label avatar = new Label(username.substring(0, 1).toUpperCase());
        avatar.getStyleClass().add("avatar");

        Label nameLabel = new Label(username);
        nameLabel.getStyleClass().add("username-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("👤+");
        addBtn.getStyleClass().add("add-icon");
        addBtn.setOnAction(e -> sendFriendRequest(username));

        entry.getChildren().addAll(avatar, nameLabel, spacer, addBtn);
        return entry;
    }

    private void sendFriendRequest(String username) {
        System.out.println("✅ Friend request sent to: " + username);
        // logic to send actual friend request goes here
    }

    @FXML
    private void closePanel(MouseEvent event) {
        StackPane root = (StackPane) playersContainer.getScene().getRoot();
        root.getChildren().remove(playersContainer.getParent().getParent());
    }
}
