package gameLogic.tictactoe;
import java.util.Scanner;

public class TicTacToe {

    TicTacToeBoard board;
    private char activePlayer;
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
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
        System.out.println("Welcome to Tic-Tac-Toe!");

        while (true) {
            board.createBoard();
            board.displayBoard();
            while (true) {
                board.displayBoard();
                if (board.checkForWin(activePlayer)) {
                    System.out.println("Player " + activePlayer + " wins! Congratulations!");
                    break;
                }
                if (board.boardFull()) {
                    System.out.println("It’s a tie");
                    break;
                }
                if(this.forfeitGame()){
                    break;
                }
                this.changeActivePlayer();

            }
        }
    }

    /**
     * Switches the active player between 'X' and 'O'.
     */
    public  void changeActivePlayer() {
        this.activePlayer = (this.activePlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
    }

    /**
     * Allows the current player to forfeit the game.
     */
    public boolean forfeitGame() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Player " + this.activePlayer + ", do you want to forfeit? (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("yes")) {
                char winner = (this.activePlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
                System.out.println("Player " + this.activePlayer + " forfeited the game.");
                System.out.println("Player " + winner + " wins by forfeit!");
                return true;
            }
            return false;
        }
    public boolean GameOver(){
        return board.boardFull() || board.checkForWin(activePlayer) || forfeitGame();
    }

    }



