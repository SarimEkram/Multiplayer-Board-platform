package gameLogic.tictactoe;
import java.lang.reflect.Method;
import java.util.Scanner;

/**
 * This class represents the game logic for Tic Tac Toe.
 * It manages player turns, handles forfeits, and checks game status.
 */
public class TicTacToe {

    TicTacToeBoard board;   // Board instance to manage game state
    private char activePlayer;  // Current active player ('X' or 'O')
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    private int callCount = 0;

    /**
     * Constructs a new TicTacToe game with the specified board.
     *
     * @param board the TicTacToeBoard instance used for the game
     */
    public TicTacToe(TicTacToeBoard board){
        this.board = board;
        this.activePlayer = PLAYER_X;
    }

    /**
     * Starts the game by initializing the board.
     */
    public void start() {
        board.createBoard();
    }

    /**
     * Switches the active player between 'X' and 'O'.
     */
    public  void changeActivePlayer() {
        this.activePlayer = (this.activePlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
    }

    /**
     * Allows the current player to forfeit the game.
     *
     * @param player the player who forfeits the game
     * @return the opponent player as the winner
     */
    public char forfeitGame(char player) {
        callCount++;        // Increment the forfeit call counter
        if (player == PLAYER_X) {
           return PLAYER_O;
        }
        else{
            return PLAYER_X;
        }

    }

    /**
     * Checks if the game is over.
     *
     * @return true if a player has won, the board is full, or a forfeit occurred
     */
    public boolean isGameOver(){
        return this.board.checkForWin(activePlayer) || this.board.boardFull() || callCount != 0;
    }

    /**
     * Returns the active player.
     *
     * @return the current active player ('X' or 'O')
     */
    public char getActivePlayer() {
        return activePlayer;
    }

}



