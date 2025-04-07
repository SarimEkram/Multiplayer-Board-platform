package gameLogic.connect4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectBoardTest {

    private ConnectBoard board;
    private int player1 = 1;
    private int player2 = 2;

    @BeforeEach
    public void setUp() {
        board = new ConnectBoard(player1, player2);
    }

    @Test
    // Checks if the game starts with player 1
    public void testInitialCurrentPlayer() {
        assertEquals(player1, board.getCurrentPlayer());
    }

    @Test
    public void testSetCurrentPlayer() {
        board.setCurrentPlayer(player2);
        assertEquals(player2, board.getCurrentPlayer());
    }

    @Test
    // Checks if playing a piece updates the correct board position
    public void testPlayPieceUpdatesBoard() {
        int row = board.playPiece(0);
        assertEquals(5, row);
        assertEquals(player1, board.getBoard()[5][0]);
    }

    @Test
    // Checks if current player switches after playing a piece
    public void testPlayPieceSwitchesPlayer() {
        board.playPiece(0);
        assertEquals(player2, board.getCurrentPlayer());
    }

    @Test
    public void testClearBoard() {
        //making a few plays
        board.playPiece(0); // P1
        board.playPiece(1); // P2
        board.playPiece(2); // P1
        board.playPiece(0); // P2
        //clearing it
        board.clearBoard();
        // Iterating through the board to check if everything is set to 0
        int[][] clearedBoard = board.getBoard();
        for (int row = 0; row < clearedBoard.length; row++) {
            for (int col = 0; col < clearedBoard[row].length; col++) {
                assertEquals(0, clearedBoard[row][col]);
            }
        }
    }

    @Test
    public void testIsGameOverByWin() {
        board.playPiece(0); // P1
        board.playPiece(1); // P2
        board.playPiece(0); // P1
        board.playPiece(1); // P2
        board.playPiece(0); // P1
        board.playPiece(1); // P2
        board.playPiece(0); // P1 wins vertically
        assertTrue(board.isGameOver()); // Game is over due to win
    }
}

