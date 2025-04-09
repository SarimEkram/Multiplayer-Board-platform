package networking.game;

import gameLogic.checkers.CheckersBoard;
import gameLogic.connect4.ConnectBoard;
import gameLogic.tictactoe.TicTacToeBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServerTest {

    GameServer gameServer;
    CheckersBoard checkers;
    ConnectBoard connect4;
    TicTacToeBoard ticTacToe;

    @BeforeEach
    void setUp() {
        gameServer = new GameServer<>();
        checkers = new CheckersBoard();
        connect4 = new ConnectBoard(1, 2);
        ticTacToe = new TicTacToeBoard();
    }

    @Test
    void saveGameState() {
        assertTrue(gameServer.saveGameState("1234checkers", checkers), "Saving a valid game should return true.");
        assertFalse(gameServer.saveGameState(null, checkers), "A null gameId should return false.");
        assertFalse(gameServer.saveGameState("2345", null), "A null board should return false.");
    }

    @Test
    void getGameState() {
        assertTrue(gameServer.saveGameState("1234connect", connect4), "Saving a valid game should return true.");
        assertEquals(connect4, gameServer.getGameState("1234connect"), "Saved game should be equal to retrieved game");
        assertNull(gameServer.getGameState("xyzNoGame"), "Invalid gameId should return null game");
    }

    @Test
    void removeGame() {
        assertTrue(gameServer.saveGameState("1234tictactoe", ticTacToe), "Saving a valid game should return true.");
        assertFalse(gameServer.removeGame("xyzNoGame"), "Non existent gameId should return false");
        assertTrue(gameServer.removeGame("1234tictactoe"), "Valid game should be removed successfully");
        assertFalse(gameServer.removeGame("1234tictactoe"), "Game should already be removed before");
    }
}