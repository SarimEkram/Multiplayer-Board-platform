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
    public void testCreateEmptyCell() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertTrue(board.isCellEmpty(i, j));
            }
        }
    }

    @Test
    public void testPlacePiece() {
        board.placePiece(0, 0, 'X');
    }

    @Test
    public void testPlacePieceAndEmptyCell() {
        assertTrue(board.isCellEmpty(0, 0));
        board.placePiece(0, 0, 'X');
        assertFalse(board.isCellEmpty(0, 0));
    }

    @Test
    public void testCheckRowWin() {
        board.placePiece(2, 0, 'O');
        board.placePiece(2, 1, 'O');
        board.placePiece(2, 2, 'O');
        assertTrue(board.checkForWin('O'));
    }

    @Test
    public void testCheckColumnWin() {
        board.placePiece(0, 0, 'X');
        board.placePiece(1, 0, 'X');
        board.placePiece(2, 0, 'X');
        assertTrue(board.checkForWin('X'));
    }

    @Test
    public void testCheckDiagonalWin() {
        board.placePiece(0, 2, 'O');
        board.placePiece(1, 1, 'O');
        board.placePiece(2, 0, 'O');
        assertTrue(board.checkForWin('O'));

    }

    @Test
    public void testBoardFull() {
        char[][] pattern = {
                {'X', 'O', 'X'},
                {'O', 'X', 'O'},
                {'O', 'X', 'O'},
        };
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board.placePiece(i, j, pattern[i][j]);
        assertTrue(board.boardFull());
    }

    @Test
    public void testNotFull() {
        board.placePiece(1, 0, 'X');
        assertFalse(board.boardFull());
    }
}