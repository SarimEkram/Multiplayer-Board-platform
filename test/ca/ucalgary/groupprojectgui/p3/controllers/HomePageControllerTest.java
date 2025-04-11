package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import MatchmakingLeaderboard.RankTier;

public class HomePageControllerTest {

    private HomePageController controller;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Initialize JavaFX toolkit
        new JFXPanel();
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @BeforeEach
    void setUp() {
        controller = new HomePageController();

        // Dummy setup of required FXML injected components
        controller.playersContainer = new VBox();
        controller.friendListView = new ListView<>();
        controller.gameSearchField = new TextField();
        controller.popupContainer = new StackPane();
    }

    @Test
    public void testCreateStatBox() throws InterruptedException {
        Platform.runLater(() -> {
            String label = "RANKING POINTS";
            String value = "1500";
            String color = "#00ffff";

            HBox statBox = controller.createStatBox(label, value, color);

            assertNotNull(statBox);
            assertEquals(2, statBox.getChildren().size());

            Label nameLabel = (Label) statBox.getChildren().get(0);
            Label valueLabel = (Label) statBox.getChildren().get(1);

            assertEquals(label, nameLabel.getText());
            assertEquals(value, valueLabel.getText());
            assertTrue(valueLabel.getStyle().contains(color));
        });
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @Test
    public void testGetTierColor() {
        assertEquals("#00ffff", controller.getTierColor(RankTier.DIAMOND));
        assertEquals("#ffd700", controller.getTierColor(RankTier.GOLD));
        assertEquals("#c0c0c0", controller.getTierColor(RankTier.SILVER));
        assertEquals("#cd7f32", controller.getTierColor(RankTier.BRONZE));
    }

    @Test
    public void testCreateDetailLabel() throws InterruptedException {
        Platform.runLater(() -> {
            String title = "Email:";
            String value = "test@example.com";

            HBox detailBox = controller.createDetailLabel(title, value);

            assertNotNull(detailBox);
            assertEquals(2, detailBox.getChildren().size());

            Label titleLabel = (Label) detailBox.getChildren().get(0);
            Label valueLabel = (Label) detailBox.getChildren().get(1);

            assertEquals(title, titleLabel.getText());
            assertEquals(value, valueLabel.getText());
        });
        TimeUnit.MILLISECONDS.sleep(200);
    }

    @Test
    public void testShowOverlayAlertAddsNodeToPopupContainer() throws InterruptedException {
        Platform.runLater(() -> {
            int initialCount = controller.popupContainer.getChildren().size();

            controller.showOverlayAlert("Test Title", "Test Message");

            assertEquals(initialCount + 1, controller.popupContainer.getChildren().size());
        });
        TimeUnit.MILLISECONDS.sleep(200);
    }
}
