package gameLogic.checkers;

import gameLogic.checkers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        int blackCount = 0, whiteCount = 0;
        for (CheckersPiece[] row : board.board) {
            for (CheckersPiece piece : row) {
                if (piece != null) {
                    if (piece.getColour() == CheckersPiece.Colour.BLACK) blackCount++;
                    else if (piece.getColour() == CheckersPiece.Colour.WHITE) whiteCount++;
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
        CheckersMove.Move move = new CheckersMove.Move(4, 1, new java.util.ArrayList<>());
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
}
