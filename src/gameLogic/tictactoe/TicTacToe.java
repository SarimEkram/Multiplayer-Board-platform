package gameLogic.tictactoe;
import java.lang.reflect.Method;
import java.util.Scanner;

public class TicTacToe {

    TicTacToeBoard board;
    private char activePlayer;
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    private int callCount = 0;


    public TicTacToe(TicTacToeBoard board){
        this.board = board;
        this.activePlayer = PLAYER_X;
    }

    /**
     * The main method that starts the game.
     *
     *
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
     * @param player the player who forfeits the game
     */

    public char forfeitGame(char player) {
        callCount++;
        if (player == PLAYER_X) {
           return PLAYER_O;
        }
        else{
            return PLAYER_X;
        }

    }
    public boolean isGameOver(){
        return this.board.checkForWin(activePlayer) || this.board.boardFull() || callCount != 0;
    }

    /**
     * Getter method for active player
     */
    public char getActivePlayer() {
        return activePlayer;
    }

}



