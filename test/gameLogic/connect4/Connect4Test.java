package gameLogic.connect4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Connect4Test {

    private ConnectBoard board;
    private int player1 = 1;
    private int player2 = 2;

    @BeforeEach
    public void setUp() {
        board = new ConnectBoard(player1, player2);
    }

    @Test
    public void testPlayValidMove() { // Checks if a valid move lands
        int row = Connect4.play(board.getBoard(), 1, player1);
        assertEquals(5, row); // Expecting it to land at bottom row
        assertEquals(player1, board.getBoard()[5][1]);
    }

    @Test
    public void testSwitchPlayer() { // test if current player changes after calling switchPlayer
        int initialPlayer = board.getCurrentPlayer();
        board.getGameLogic().switchPlayer();
        assertNotEquals(initialPlayer, board.getCurrentPlayer());
    }

    @Test
    public void testHorizontalWin() {
        board.playPiece(0); // P1
        board.playPiece(0); // P2
        board.playPiece(1); // P1
        board.playPiece(1); // P2
        board.playPiece(2); // P1
        board.playPiece(2); // P2
        board.playPiece(3); // P1 - horizontal win
        assertTrue(board.isGameOver());
        assertTrue(board.getGameLogic().won(board.getBoard(), player1)); // Confirms P1 wins and P2 lost
        assertFalse(board.getGameLogic().won(board.getBoard(), player2));
    }

    @Test
    public void testHorizontalWinRightEdge() {
        board.playPiece(3); // P1
        board.playPiece(0); // P2
        board.playPiece(4); // P1
        board.playPiece(0); // P2
        board.playPiece(5); // P1
        board.playPiece(0); // P2
        board.playPiece(6); // P1 creates a horizontal win at the far right
        // Shows it does not go out of bounds // no errors
        assertTrue(board.isGameOver());
        assertTrue(board.getGameLogic().won(board.getBoard(), player1)); // Confirms P1 wins and P2 lost
        assertFalse(board.getGameLogic().won(board.getBoard(), player2));
    }

    @Test
    public void testVerticalWin() {
        board.playPiece(3); // P1
        board.playPiece(1); // P2
        board.playPiece(3); // P1
        board.playPiece(1); // P2
        board.playPiece(3); // P1
        board.playPiece(1); // P2
        board.playPiece(3); // P1 wins vertically
        assertTrue(board.isGameOver());
        assertTrue(board.getGameLogic().won(board.getBoard(), player1)); // Confirms P1 wins and P2 lost
        assertFalse(board.getGameLogic().won(board.getBoard(), player2));
    }

    @Test
    public void testVerticalWinAtLastColumn() {
        board.playPiece(6); // P1
        board.playPiece(5); // P2
        board.playPiece(6); // P1
        board.playPiece(5); // P2
        board.playPiece(6); // P1
        board.playPiece(5); // P2
        board.playPiece(6); // P1 wins vertically in last column
        // Shows it does not go out of bounds // no errors
        assertTrue(board.isGameOver());
        assertTrue(board.getGameLogic().won(board.getBoard(), player1)); // Confirms P1 wins and P2 lost
        assertFalse(board.getGameLogic().won(board.getBoard(), player2));
    }

    @Test
    public void testDiagonalBackslashWinBottomRight() {
        board.playPiece(3); // P1
        board.playPiece(2); // P2
        board.playPiece(2); // P1
        board.playPiece(1); // P2
        board.playPiece(1); // P1
        board.playPiece(0); // P2
        board.playPiece(1); // P1
        board.playPiece(0); // P2
        board.playPiece(0); // P1
        board.playPiece(6); // P2
        board.playPiece(0); // P1 wins, creates a backslash diagonal bottom right
        // Shows it does not go out of bounds // no errors
        assertTrue(board.isGameOver());
        assertTrue(board.getGameLogic().won(board.getBoard(), player1)); // Confirms P1 wins and P2 lost
        assertFalse(board.getGameLogic().won(board.getBoard(), player2));
    }

    @Test
    public void testDiagonalForwardSlashWinBottomLeft() {
        board.playPiece(0); // P1
        board.playPiece(1); // P2
        board.playPiece(1); // P1
        board.playPiece(2); // P2
        board.playPiece(2); // P1
        board.playPiece(3); // P2
        board.playPiece(2); // P1
        board.playPiece(3); // P2
        board.playPiece(3); // P1
        board.playPiece(6); // P2
        board.playPiece(3); // P1 wins creates a forward slash diagonal bottom left
        // Shows it does not go out of bounds // no errors
        assertTrue(board.isGameOver());
        assertTrue(board.getGameLogic().won(board.getBoard(), player1)); // Confirms P1 wins and P2 lost
        assertFalse(board.getGameLogic().won(board.getBoard(), player2));
    }

    @Test
    public void testP2Wins() { // checks if P2 can get a win
        board.playPiece(5); // P1
        board.playPiece(0); // P2
        board.playPiece(5); // P1
        board.playPiece(1); // P2
        board.playPiece(5); // P1
        board.playPiece(2); // P2
        board.playPiece(4); // P1
        board.playPiece(3); // P2
        // P2 horizontal win
        assertTrue(board.isGameOver());
        assertTrue(board.getGameLogic().won(board.getBoard(), player2)); // Confirms P2 wins and P1 lost
        assertFalse(board.getGameLogic().won(board.getBoard(), player1));
    }

    @Test
    public void testForfeit() {
        int winner = board.getGameLogic().forfeit(player1);
        assertEquals(player2, winner); // P1 forfeits, P2 wins
    }

    @Test
    public void testBoardIsFull() {
        // Fills entire board to trigger full board
        for (int col = 0; col < 7; col++) {
            for (int i = 0; i < 6; i++) {
                board.playPiece(col);
            }
        }
        assertTrue(board.getGameLogic().isFull(board.getBoard()));
    }

    @Test
    public void testNoWin() {
        board.playPiece(0); // P1
        board.playPiece(1); // P2
        board.playPiece(2); // P1
        board.playPiece(3); // P2
        // No win yet // No false wins given
        assertFalse(board.isGameOver());
    }

}