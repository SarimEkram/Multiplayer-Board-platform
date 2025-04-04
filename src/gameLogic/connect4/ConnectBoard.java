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
    private int[][] board;

    // Keeps track of current player
    private int currentPlayer;

    /**
     * Constructs a new game board for Connect 4 with specified settings.
     * creates a connect4 instance as gameLogic
     * @param player1 The identifier (usually a specific integer) for Player 1's pieces.
     * @param player2 The identifier (usually a specific integer) for Player 2's pieces.
     */
    public ConnectBoard(int player1, int player2){
        this.gameLogic = new Connect4(this);
        this.piece1 = player1;
        this.piece2 = player2;
        this.currentPlayer = player1; // Initialize turn
        board = new int[row][column];
        createBoard();

    }

    /**
     * This function creates a Board using the row and columns inputted by the user.
     * This is called in the constructor after the user decides to play connect 4 and selecting the size.
     * creates a 2D array filled with an integer value 0 which will be considered for an empty spot.
     */
    private void createBoard(){
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = 0;
            }
        }
    }

    public int getCurrentPlayer() { //D
        return currentPlayer; // Returns the player whose turn it currently is
    }

    public void setCurrentPlayer(int player) { //D
        this.currentPlayer = player; // Updates the current player to the specified player
    }

    /**
     * play from connect4 class is being called here
     * @return the 0 if the play is successful else -1
     */
    public int playPiece(int column){
        int row = Connect4.play(board, column, currentPlayer);
        if (row >= 0) {
            setCurrentPlayer(currentPlayer == piece1 ? piece2 : piece1);
        }
        return row;
    }

    public int[][] getBoard() {
        return board;
    }


    public Connect4 getGameLogic() {
        return gameLogic; // Added to allow access to gameLogic for testing
    }


    /**
     * This function determines if the game is complete due to a win or tie by either player
     * if full we return true
     * else we check won() for both blue or red
     * @return True if game is complete, False otherwise
     */
    public boolean isGameOver () { //D
        // Checks if the game is over by verifying if the board is full or if either player has won
         return gameLogic.isFull(board) || gameLogic.won(board, piece1) || gameLogic.won(board, piece2);
    }

    /**
     * Clears the game board by resetting all its elements.
     * This will be called in the GUI controller class.
     */
    public void clearBoard(){ //D
        createBoard(); //D // just creates new board to clear the old one
        gameLogic.resetGame();
        currentPlayer = piece1;
    }

}

