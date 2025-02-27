package gameLogic.tictactoe;

import java.util.Scanner;

public class TicTacToe {

    TicTacToeBoard board;
    private char activePlayer;
    public static final char PLAYER_X = 'X';
    public static final char PLAYER_O = 'O';
    public TicTacToe(TicTacToeBoard board){
        this.board = board;
        this.activePlayer = PLAYER_X;
    }

    /**
     * The main method that starts the game.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
    }

    /**
     * Handles a player's move.
     */
    public static void playMove() {
    }

    /**
     * Switches the active player between 'X' and 'O'.
     */
    public static void changeActivePlayer() {
    }

    /**
     * Allows the current player to forfeit the game.
     */
    public static void forfeitGame() {

    }
    
}
