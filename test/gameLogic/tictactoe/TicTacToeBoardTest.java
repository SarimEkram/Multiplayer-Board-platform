package gameLogic.tictactoe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import gameLogic.tictactoe.TicTacToeBoard;

public class TicTacToeBoardTest {
    private TicTacToeBoard board;

    // Empty grid initialization
    @BeforeEach
    public void setUp() {
        board = new TicTacToeBoard();
        board.createBoard();
    }

    // Checks for all empty cells in the start
    @Test
    public void testCreateEmptyCell() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertTrue(board.isCellEmpty(i, j));
            }
        }
    }

    // Checks for placing of a piece - without any error
    @Test
    public void testPlacePiece() {
        board.placePiece(0, 0, 'X');
    }

    // Verifies that after placing the piece the empty cell function is updated successfully. As now the grid is not fully empty.
    @Test
    public void testPlacePieceAndEmptyCell() {
        assertTrue(board.isCellEmpty(0, 0));
        board.placePiece(0, 0, 'X');
        assertFalse(board.isCellEmpty(0, 0));
    }

    // Ensures that overwrite of pieces is allowed
    @Test
    public void testOverwritePiece() {
        board.placePiece(0,0,'X');
        board.placePiece(0,0,'O');
        assertFalse(board.isCellEmpty(0,0)); // Cell is not empty
    }

    // Checks for a winning condition - row win
    @Test
    public void testCheckRowWin() {
        board.placePiece(2, 0, 'O');
        board.placePiece(2, 1, 'O');
        board.placePiece(2, 2, 'O');
        assertTrue(board.checkForWin('O'));
    }

    // Checks for a winning condition - Column win
    @Test
    public void testCheckColumnWin() {
        board.placePiece(0, 0, 'X');
        board.placePiece(1, 0, 'X');
        board.placePiece(2, 0, 'X');
        assertTrue(board.checkForWin('X'));
    }

    // Checks for a winning condition - Diagonal win
    @Test
    public void testCheckDiagonalWin() {
        board.placePiece(0, 2, 'O');
        board.placePiece(1, 1, 'O');
        board.placePiece(2, 0, 'O');
        assertTrue(board.checkForWin('O'));

    }

    // Checks for a full board - but no win - draw.
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

    // Checks for a non-empty rid - places one piece to check.
    @Test
    public void testNotFull() {
        board.placePiece(1, 0, 'X');
        assertFalse(board.boardFull());
    }
}