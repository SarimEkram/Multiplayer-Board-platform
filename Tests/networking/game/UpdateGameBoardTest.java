package networking.game;

import gameLogic.checkers.CheckersBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UpdateGameBoardTest {

    private GameServer gameServer;
    UpdateGameBoard validGame;
    UpdateGameBoard nullGameId;
    UpdateGameBoard nullBoard;
    UpdateGameBoard nonexistentGame;

    @BeforeEach
    void setUp() {
        gameServer = new GameServer<>(); //Initialize gameServer
        CheckersBoard checkers = new CheckersBoard(); //Create a checkers game to test
        gameServer.saveGameState("checkers1234", checkers); // Add the checkers game to server.

        validGame = new UpdateGameBoard<>(gameServer, "checkers1234", checkers); // A valid game on the server
        nullGameId = new UpdateGameBoard<>(gameServer, "1234xyz", null);
        nullBoard = new UpdateGameBoard<>(gameServer, null, checkers);
        nonexistentGame = new UpdateGameBoard<>(gameServer, "checkers5678", checkers); // A game whose gameId is not in the server
    }

    @Test
    void fetchGameBoard() {
        assertTrue(validGame.fetchGameBoard(), "Fetching a valid game from server should return true");
        assertFalse(nullGameId.fetchGameBoard(), "A null gameId cannot be fetched from server");
        assertFalse(nullBoard.fetchGameBoard(), "A null game board cannot be fetched from server");
        assertFalse(nonexistentGame.fetchGameBoard(), "A nonexistent game cannot be fetched from server");
    }

    @Test
    void uploadGameBoard() {
        assertTrue(validGame.uploadGameBoard(), "Uploading a valid game to server should return true");
        assertFalse(nullGameId.uploadGameBoard(), "A null gameId cannot be uploaded to server");
        assertFalse(nullBoard.uploadGameBoard(), "A null game board cannot be uploaded to server");
        assertTrue(nonexistentGame.uploadGameBoard(), "A nonexistent game can be uploaded to server as a new game");
    }
}