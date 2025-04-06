package gameLogic.tictactoe;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gameLogic.tictactoe.TicTacToe;
import gameLogic.tictactoe.TicTacToeBoard;

public class TicTacToeTest {

    private TicTacToeBoard board;
    private TicTacToe game;

    @BeforeEach
    public void setUp() {
        board = new TicTacToeBoard();
        game = new TicTacToe(board);
        board.creatBoard();
    }

    @Test
    public void testStartEmptyBoard() {
        game.start();
        for  (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertTrue(board.isCellEmpty(i, j), "Empty cell at start.");
            }
        }
    }

    @Test
    public void testChangeActivePlayer() throws Exception {
        char initialPlayer = getActivePlayer(game);
        game.changeActivePlayer();
        char newPlayer = getActivePlayer(game);
        asserNotEquals(initialPlayer, newPlayer);
    }

    @Test
    public void testForfeit() {
        assertEquals('X', game.forfeitGame('O'));
        assertEquals('O', game.forfeitGame('X'));
    }

    @Test
    public void testGameOverPlayerWins() {
        board.placePiece(0, 0, 'X');
        board.placePiece(0, 1, 'X');
        board.placePiece(0, 2, 'X');
        assertTrue(game.isGameOver());
    }

    @Test
    public void testGameOverFullBoard() {
        fillBoardWithoutWin();
        assertTrue(game.isGameOver());
    }

    private void fillBoardWithoutWin() {
        char[][] pattern = {
                {'X', 'O', 'X'},
                {'X', 'X', 'O'},
                {'O', 'X', 'O'},
        }
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board.placePiece(i, j, pattern[i][j]);
    }
}