package gameLogic.connect4;

public class ConnectBoard {

    /**
     * Manages the game board for a Connect 4 game. This class is responsible for initializing the board,
     * tracking player moves, and determining the state of the game.
     */
    // User inputs the row size
    private  int row = 6;

    // User inputs the column size
    private  int column = 7;

    // Player 1
    public int piece1;

    // Player 2
    public int piece2;
    private Connect4 gameLogic;

    // Board to keep track of the pieces played
    private  int[][] board;

    /**
     * Constructs a new game board for Connect 4 with specified settings.
     * @param player1 The identifier (usually a specific integer) for Player 1's pieces.
     * @param player2 The identifier (usually a specific integer) for Player 2's pieces.
     */
    public ConnectBoard(int player1, int player2){
        this.gameLogic = new Connect4(this);
        this.piece1 = player1;
        this.piece2 = player2;
        this.board = createBoard();

    }

    /**
     * This function creates a Board using the row and columns inputted by the user.
     * This is called in the constructor after the user decides to play connect 4 and selecting the size.
     * @return a 2D array filled with an integer value which will be considered for an empty spot.
     */
    private int[][] createBoard(){
        return null;
    }

    /**
     * play from connect4 class is being called here
     * @return the 0 if the play is successful else -1
     */
    public int playPiece(int column, int player){
        return -1;
    }

    /**
     * This function checks if the board is full or not.
     * @return false if any column of the first row is EMP/0.
     */
    public static boolean full() {
        return true;
    }



    /**
     * This function determines if the game is complete due to a win or tie by either player
     * if full we return true
     * else we check won() for both blue or red
     * @return True if game is complete, False otherwise
     */
    public boolean isGameOver () {
        return false;
    }

    /**
     * Clears the game board by resetting all its elements.
     * This will be called in the GUI controller class.
     */
    public void clearBoard(){

    }

}
