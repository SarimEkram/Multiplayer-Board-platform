package gameLogic.tictactoe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import gameLogic.tictactoe.TicTacToeBoard;

public class TicTacToeBoardTest {

    private TicTacToeBoard board;

    @BeforeEach
    public void setUp() {
        board = new TicTacToeBoard();
        board.createBoard();
    }

    @Test
    void testEmptyCell() {
    }

    @Test
    void testPlacePiece() {
    }

    @Test
    public void testCheckRowWin() {
    }

    @Test
    public void testCheckColumnWin() {

    }

    @Test
    public void testCheckDiagonalWin() {

    }

    @Test
    public void testBoardFull()

    @Test
    public void testBoardNotFull() {
    }
}