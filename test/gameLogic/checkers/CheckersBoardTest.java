package gameLogic.checkers;

import gameLogic.checkers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CheckersBoardTest {
    private CheckersBoard board;

    @BeforeEach
    public void setup() {
        board = new CheckersBoard();
    }

    @Test
    public void testClearBoard() {
        board.placeAllPieces();
        board.clearBoard();
        for (CheckersPiece[] row : board.board) {
            for (CheckersPiece piece : row) {
                assertNull(piece);
            }
        }
    }

    @Test
    public void testRemovePiece() {
        board.board[2][1] = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.removePiece(2, 1);
        assertNull(board.board[2][1]);
    }

    @Test
    public void testPlaceAllPieces() {
        board.placeAllPieces();
        int black = 0, white = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CheckersPiece piece = board.board[i][j];
                if (piece != null) {
                    if (piece.getColour() == CheckersPiece.Colour.BLACK) black++;
                    if (piece.getColour() == CheckersPiece.Colour.WHITE) white++;
                }
            }
        }
        assertEquals(12, black);
        assertEquals(12, white);
    }
}
