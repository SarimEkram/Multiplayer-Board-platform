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
    public void testHorizontalWin() {
        board.playPiece(0); // P1
        board.playPiece(0); // P2
        board.playPiece(1); // P1
        board.playPiece(1); // P2
        board.playPiece(2); // P1
        board.playPiece(2); // P2
        board.playPiece(3); // P1 - horizontal win
        assertTrue(board.isGameOver());
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
    }
}