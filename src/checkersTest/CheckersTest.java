package checkersTest;

import gameLogic.checkers.Checkers;
import gameLogic.checkers.CheckersBoard;
import gameLogic.checkers.CheckersMove;
import gameLogic.checkers.CheckersPiece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import static org.junit.jupiter.api.Assertions.*;

public class CheckersTest {

    CheckersBoard board;
    Checkers game;

    @BeforeEach
    void setUp() {
        board = new CheckersBoard();
        game = new Checkers(board);
    }

    // === CheckersBoard Tests ===

    @Test
    void testPlaceAllPieces() {
        board.placeAllPieces();
        int red = 0, black = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CheckersPiece p = board.board[i][j];
                if (p != null) {
                    if (p.getColour() == CheckersPiece.Colour.RED) red++;
                    if (p.getColour() == CheckersPiece.Colour.BLACK) black++;
                }
            }
        }

        assertEquals(12, red);
        assertEquals(12, black);
    }

    @Test
    void testClearBoard() {
        board.placeAllPieces();
        board.clearBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                assertNull(board.board[i][j]);
            }
        }
    }

    @Test
    void testRemovePiece() {
        board.board[2][3] = new CheckersPiece(CheckersPiece.Colour.RED);
        assertNotNull(board.board[2][3]);
        board.removePiece(2, 3);
        assertNull(board.board[2][3]);
    }

    // === CheckersPiece Tests ===

    @Test
    void testPieceColorAndType() {
        CheckersPiece red = new CheckersPiece(CheckersPiece.Colour.RED);
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);

        assertEquals(CheckersPiece.Colour.RED, red.getColour());
        assertEquals(CheckersPiece.Type.NORMAL, red.getType());

        assertEquals(CheckersPiece.Colour.BLACK, black.getColour());
        assertEquals(CheckersPiece.Type.NORMAL, black.getType());
    }

    @Test
    void testPromoteToKing() {
        CheckersPiece piece = new CheckersPiece(CheckersPiece.Colour.RED);
        assertFalse(piece.isKing());

        piece.promoteToKing();
        assertTrue(piece.isKing());
        assertEquals(CheckersPiece.Type.KING, piece.getType());
    }

    // === CheckersMove Tests ===

    @Test
    void testAvailableMovesNormalBlack() {
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.clearBoard();
        board.board[2][3] = black;

        int[][] moves = CheckersMove.availableMoves(board, black, 2, 3);
        assertArrayEquals(new int[][]{{3, 2}, {3, 4}}, moves);
    }

    @Test
    void testAvailableMovesNormalRed() {
        CheckersPiece red = new CheckersPiece(CheckersPiece.Colour.RED);
        board.clearBoard();
        board.board[5][2] = red;

        int[][] moves = CheckersMove.availableMoves(board, red, 5, 2);
        assertArrayEquals(new int[][]{{4, 1}, {4, 3}}, moves);
    }

    @Test
    void testAvailableMovesKing() {
        CheckersPiece king = new CheckersPiece(CheckersPiece.Colour.BLACK);
        king.promoteToKing();
        board.clearBoard();
        board.board[4][4] = king;

        int[][] moves = CheckersMove.availableMoves(board, king, 4, 4);
        assertArrayEquals(new int[][]{{5, 3}, {5, 5},{3, 3}, {3, 5}}, moves);
    }

    @Test
    void testMoveAndCapture() {
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        CheckersPiece red = new CheckersPiece(CheckersPiece.Colour.RED);
        board.clearBoard();

        board.board[2][3] = black;
        board.board[3][4] = red;

        CheckersMove.move(board, black, 2, 3, 4, 5);

        assertNull(board.board[2][3]);
        assertEquals(black, board.board[4][5]);
        assertNull(board.board[3][4]);
    }

    @Test
    void testMoveToPromotionRed() {
        CheckersPiece red = new CheckersPiece(CheckersPiece.Colour.RED);
        board.clearBoard();
        board.board[1][2] = red;

        CheckersMove.move(board, red, 1, 2, 0, 1);
        assertTrue(red.isKing());
    }

    @Test
    void testMoveToPromotionBlack() {
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.clearBoard();
        board.board[6][1] = black;

        CheckersMove.move(board, black, 6, 1, 7, 0);
        assertTrue(black.isKing());
    }

    @Test
    void testCheckWinNone() {
        board.clearBoard();
        board.board[0][1] = new CheckersPiece(CheckersPiece.Colour.RED);
        board.board[7][6] = new CheckersPiece(CheckersPiece.Colour.BLACK);

        Checkers checkers = new Checkers(board);
        Checkers.WINNER winner = checkers.checkWin();
        assertEquals(Checkers.WINNER.NONE, winner);
    }

    @Test
    void testCheckWinRedWins() {
        board.clearBoard();
        board.board[0][1] = new CheckersPiece(CheckersPiece.Colour.RED);

        Checkers checkers = new Checkers(board);
        Checkers.WINNER winner = checkers.checkWin();
        assertEquals(Checkers.WINNER.RED, winner);
    }

    @Test
    void testCheckWinBlackWins() {
        board.clearBoard();
        board.board[7][6] = new CheckersPiece(CheckersPiece.Colour.BLACK);

        Checkers checkers = new Checkers(board);
        Checkers.WINNER winner = checkers.checkWin();
        assertEquals(Checkers.WINNER.BLACK, winner);
    }
}
