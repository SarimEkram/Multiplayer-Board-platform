package gameLogic.checkersTest;
import gameLogic.checkers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CheckersTest {

    private CheckersBoard board;
    private Checkers game;

    @BeforeEach
    public void setup() {
        board = new CheckersBoard();
        game = new Checkers(board);
    }

    @Test
    public void testInitialTurnIsBlack() {
        assertEquals(Checkers.Turn.BLACK, game.getTurn());
    }

    @Test
    public void testStartPlacesAllPieces() {
        game.start();
        int blackCount = 0;
        int whiteCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CheckersPiece piece = board.board[i][j];
                if (piece != null) {
                    if (piece.getColour() == CheckersPiece.Colour.BLACK) blackCount++;
                    if (piece.getColour() == CheckersPiece.Colour.WHITE) whiteCount++;
                }
            }
        }
        assertEquals(12, blackCount);
        assertEquals(12, whiteCount);
    }

    @Test
    public void testProcessMoveSwitchesTurn() {
        game.start();
        CheckersPiece piece = board.board[5][0];
        List<int[]> captured = new ArrayList<>();
        CheckersMove.Move move = new CheckersMove.Move(4, 1, captured);
        game.processMove(piece, 5, 0, move);
        assertEquals(Checkers.Turn.WHITE, game.getTurn());
    }

    @Test
    public void testCheckWinWhiteWins() {
        board.clearBoard();
        board.board[0][1] = new CheckersPiece(CheckersPiece.Colour.WHITE);
        assertEquals(Checkers.WINNER.WHITE, game.checkWin());
    }

    @Test
    public void testCheckWinBlackWins() {
        board.clearBoard();
        board.board[7][0] = new CheckersPiece(CheckersPiece.Colour.BLACK);
        assertEquals(Checkers.WINNER.BLACK, game.checkWin());
    }

    @Test
    public void testCheckWinNoWinner() {
        board.clearBoard();
        board.board[7][0] = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.board[0][1] = new CheckersPiece(CheckersPiece.Colour.WHITE);
        assertEquals(Checkers.WINNER.NONE, game.checkWin());
    }

    @Test
    public void testClearBoard() {
        board.placeAllPieces();
        board.clearBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                assertNull(board.board[i][j]);
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
    public void testPiecePromotionToKing() {
        CheckersPiece piece = new CheckersPiece(CheckersPiece.Colour.WHITE);
        assertFalse(piece.isKing());
        piece.promoteToKing();
        assertTrue(piece.isKing());
        assertEquals(CheckersPiece.Type.KING, piece.getType());
    }

    @Test
    public void testGetColourAndType() {
        CheckersPiece whitePiece = new CheckersPiece(CheckersPiece.Colour.WHITE);
        assertEquals(CheckersPiece.Colour.WHITE, whitePiece.getColour());
        assertEquals(CheckersPiece.Type.NORMAL, whitePiece.getType());
    }

    @Test
    public void testSetHasAnimatedKing() {
        CheckersPiece piece = new CheckersPiece(CheckersPiece.Colour.BLACK);
        piece.setHasAnimatedKing(true);
        assertTrue(piece.hasAnimatedKing);
    }

    @Test
    public void testAvailableSimpleMovesForWhite() {
        board.clearBoard();
        CheckersPiece piece = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[4][3] = piece;
        CheckersMove.Move[] moves = CheckersMove.availableMoves(board, piece, 4, 3);
        assertEquals(2, moves.length);  // both diagonals should be available
    }

    @Test
    public void testAvailableSimpleMovesForBlack() {
        board.clearBoard();
        CheckersPiece piece = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.board[3][4] = piece;
        CheckersMove.Move[] moves = CheckersMove.availableMoves(board, piece, 3, 4);
        assertEquals(2, moves.length);  // both diagonals should be available
    }

    @Test
    public void testMoveExecutionWithCapture() {
        board.clearBoard();
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[2][1] = black;
        board.board[3][2] = white;
        List<int[]> captured = new ArrayList<>();
        captured.add(new int[]{3, 2});
        CheckersMove.Move move = new CheckersMove.Move(4, 3, captured);
        CheckersMove.move(board, black, 2, 1, move);
        assertEquals(black, board.board[4][3]);
        assertNull(board.board[2][1]);
        assertNull(board.board[3][2]);
    }

    @Test
    public void testMovePromotionWhite() {
        board.clearBoard();
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[1][0] = white;
        CheckersMove.Move move = new CheckersMove.Move(0, 1, new ArrayList<>());
        CheckersMove.move(board, white, 1, 0, move);
        assertTrue(white.isKing());
    }

    @Test
    public void testMovePromotionBlack() {
        board.clearBoard();
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.board[6][1] = black;
        CheckersMove.Move move = new CheckersMove.Move(7, 0, new ArrayList<>());
        CheckersMove.move(board, black, 6, 1, move);
        assertTrue(black.isKing());
    }
}
