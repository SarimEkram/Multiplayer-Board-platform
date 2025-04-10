package gameLogic.checkers;

import gameLogic.checkers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CheckersMoveTest {
    private CheckersBoard board;

    @BeforeEach
    public void setup() {
        board = new CheckersBoard();
    }

    @Test
    public void testAvailableSimpleMovesForWhite() {
        board.clearBoard();
        CheckersPiece piece = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[4][3] = piece;
        CheckersMove.Move[] moves = CheckersMove.availableMoves(board, piece, 4, 3);
        assertEquals(2, moves.length);
    }

    @Test
    public void testAvailableSimpleMovesForBlack() {
        board.clearBoard();
        CheckersPiece piece = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.board[3][4] = piece;
        CheckersMove.Move[] moves = CheckersMove.availableMoves(board, piece, 3, 4);
        assertEquals(2, moves.length);
    }

    @Test
    public void testAvailableMovesForKing() {
        board.clearBoard();
        CheckersPiece king = new CheckersPiece(CheckersPiece.Colour.WHITE);
        king.promoteToKing();
        board.board[3][3] = king;
        CheckersMove.Move[] moves = CheckersMove.availableMoves(board, king, 3, 3);
        assertEquals(4, moves.length);
    }

    @Test
    public void testMoveExecutionWithCapture() {
        board.clearBoard();
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[2][1] = black;
        board.board[3][2] = white;
        List<int[]> captured = List.of(new int[]{3, 2});
        CheckersMove.Move move = new CheckersMove.Move(4, 3, captured);
        CheckersMove.move(board, black, 2, 1, move);
        assertEquals(black, board.board[4][3]);
        assertNull(board.board[2][1]);
        assertNull(board.board[3][2]);
    }

    @Test
    public void testMovePromotion() {
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[1][0] = white;
        CheckersMove.Move move = new CheckersMove.Move(0, 1, new ArrayList<>());
        CheckersMove.move(board, white, 1, 0, move);
        assertTrue(white.isKing());
    }

    @Test
    public void testMoveClassAttributes() {
        List<int[]> captured = List.of(new int[]{3, 2});
        CheckersMove.Move move = new CheckersMove.Move(4, 3, captured);
        assertEquals(4, move.destRow);
        assertEquals(3, move.destCol);
        assertEquals(1, move.capturedPositions.size());
        assertArrayEquals(new int[]{3, 2}, move.capturedPositions.getFirst());
    }

    @Test
    public void testFindJumpsSingleCapture() {
        board.clearBoard();
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[2][1] = black;
        board.board[3][2] = white;
        var result = List.of(CheckersMove.availableMoves(board, black, 2, 1));
        assertTrue(result.stream().anyMatch(m -> m.capturedPositions.size() == 1));
    }

    @Test
    public void testFindJumpsChainCapture() {
        board.clearBoard();
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.board[2][1] = black;
        board.board[3][2] = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[5][4] = new CheckersPiece(CheckersPiece.Colour.WHITE);
        var result = List.of(CheckersMove.availableMoves(board, black, 2, 1));
        assertTrue(result.stream().anyMatch(m -> m.capturedPositions.size() == 2));
    }

    @Test
    public void testFindJumpsKingMultiDirection() {
        board.clearBoard();
        CheckersPiece king = new CheckersPiece(CheckersPiece.Colour.WHITE);
        king.promoteToKing();
        board.board[4][4] = king;
        board.board[3][3] = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.board[5][3] = new CheckersPiece(CheckersPiece.Colour.BLACK);
        var result = List.of(CheckersMove.availableMoves(board, king, 4, 4));
        assertTrue(result.stream().anyMatch(m -> m.destRow == 2 && m.destCol == 2));
        assertTrue(result.stream().anyMatch(m -> m.destRow == 6 && m.destCol == 2));
    }

    @Test
    public void testKingJumpExecution() {
        board.clearBoard();
        CheckersPiece king = new CheckersPiece(CheckersPiece.Colour.BLACK);
        king.promoteToKing();
        board.board[4][4] = king;
        board.board[3][3] = new CheckersPiece(CheckersPiece.Colour.WHITE);
        List<int[]> captured = List.of(new int[]{3, 3});
        CheckersMove.Move move = new CheckersMove.Move(2, 2, captured);
        CheckersMove.move(board, king, 4, 4, move);
        assertEquals(king, board.board[2][2]);
    }
    @Test
    public void testNoAvailableMovesWhenBlocked() {
        board.clearBoard();
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);
        CheckersPiece black1 = new CheckersPiece(CheckersPiece.Colour.BLACK);
        CheckersPiece black2 = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.board[4][3] = white;
        board.board[3][4] = black1;
        board.board[2][5] = black2;

        CheckersMove.Move[] moves = CheckersMove.availableMoves(board, white, 4, 3);
        assertEquals(1, moves.length);
    }

    @Test
    public void testNoMoveOutsideBoard() {
        board.clearBoard();
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[0][0] = white;
        CheckersMove.Move[] moves = CheckersMove.availableMoves(board, white, 0, 0);
        assertEquals(0, moves.length);
    }

    @Test
    public void testSimpleMoveNoCapture() {
        board.clearBoard();
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.board[3][4] = black;
        CheckersMove.Move move = new CheckersMove.Move(4, 3, new ArrayList<>());
        CheckersMove.move(board, black, 3, 4, move);
        assertEquals(black, board.board[4][3]);
        assertNull(board.board[3][4]);
    }

    @Test
    public void testInvalidChainCaptureNotListed() {
        board.clearBoard();
        CheckersPiece black = new CheckersPiece(CheckersPiece.Colour.BLACK);
        board.board[2][1] = black;
        board.board[3][2] = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[4][3] = new CheckersPiece(CheckersPiece.Colour.BLACK); // blocks chain
        CheckersMove.Move[] moves = CheckersMove.availableMoves(board, black, 2, 1);
        for (CheckersMove.Move move : moves) {
            assertTrue(move.capturedPositions.size() <= 1); // Chain is blocked
        }
    }

    @Test
    public void testJumpOverOwnPieceInvalid() {
        board.clearBoard();
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);
        board.board[4][4] = white;
        board.board[3][3] = new CheckersPiece(CheckersPiece.Colour.WHITE);
        CheckersMove.Move[] moves = CheckersMove.availableMoves(board, white, 4, 4);
        for (CheckersMove.Move move : moves) {
            assertFalse(move.destRow == 2 && move.destCol == 2); // can't jump over own piece
        }
    }
}
