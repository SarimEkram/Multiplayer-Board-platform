package ca.ucalgary.groupprojectgui.p3.controllers;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the LeaderboardController.
 * Uses JavaFX reflection-based testing strategy to simulate UI logic without FXML loading.
 */
public class LeaderboardControllerTest {

    private LeaderboardController controller;

    /**
     * Initializes JavaFX toolkit once before all tests run.
     */
    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        new JFXPanel(); // Initializes JavaFX toolkit
        TimeUnit.MILLISECONDS.sleep(200);
    }

    /**
     * Sets up the LeaderboardController and mocks essential components using reflection.
     */
    @BeforeEach
    void setUp() throws Exception {
        controller = new LeaderboardController();

        // Inject dummy VBox and ScrollPanes via reflection
        injectField(controller, "ticTacToeLeaderboard", new VBox());
        injectField(controller, "checkersLeaderboard", new VBox());
        injectField(controller, "connect4Leaderboard", new VBox());

        injectField(controller, "ticTacToeScroll", new ScrollPane());
        injectField(controller, "checkersScroll", new ScrollPane());
        injectField(controller, "connect4Scroll", new ScrollPane());
    }

    /**
     * Tests whether openTicTacToeLeaderboard shows the correct scroll and hides others.
     */
    @Test
    public void testOpenTicTacToeLeaderboardVisibility() throws Exception {
        Platform.runLater(() -> {
            controller.openTicTacToeLeaderboard();
            assertTrue(getFieldValue(controller, "ticTacToeScroll", ScrollPane.class).isVisible());
            assertFalse(getFieldValue(controller, "checkersScroll", ScrollPane.class).isVisible());
            assertFalse(getFieldValue(controller, "connect4Scroll", ScrollPane.class).isVisible());
        });
        TimeUnit.MILLISECONDS.sleep(200);
    }

    /**
     * Tests whether openCheckersLeaderboard shows the correct scroll and hides others.
     */
    @Test
    public void testOpenCheckersLeaderboardVisibility() throws Exception {
        Platform.runLater(() -> {
            controller.openCheckersLeaderboard();
            assertTrue(getFieldValue(controller, "checkersScroll", ScrollPane.class).isVisible());
            assertFalse(getFieldValue(controller, "ticTacToeScroll", ScrollPane.class).isVisible());
            assertFalse(getFieldValue(controller, "connect4Scroll", ScrollPane.class).isVisible());
        });
        TimeUnit.MILLISECONDS.sleep(200);
    }

    /**
     * Tests whether openConnect4Leaderboard shows the correct scroll and hides others.
     */
    @Test
    public void testOpenConnect4LeaderboardVisibility() throws Exception {
        Platform.runLater(() -> {
            controller.openConnect4Leaderboard();
            assertTrue(getFieldValue(controller, "connect4Scroll", ScrollPane.class).isVisible());
            assertFalse(getFieldValue(controller, "ticTacToeScroll", ScrollPane.class).isVisible());
            assertFalse(getFieldValue(controller, "checkersScroll", ScrollPane.class).isVisible());
        });
        TimeUnit.MILLISECONDS.sleep(200);
    }

    // ------------------------
    // UTILITY METHODS
    // ------------------------

    /**
     * Uses reflection to inject a value into a private field.
     */
    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Uses reflection to get the value of a private field.
     */
    private <T> T getFieldValue(Object target, String fieldName, Class<T> type) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return type.cast(field.get(target));
        } catch (Exception e) {
            fail("Failed to get field '" + fieldName + "': " + e.getMessage());
            return null;
        }
    }
}
