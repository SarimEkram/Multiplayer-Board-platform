package gameLogic.checkers;

/**
 * Represents the main logic of a Checkers game.
 */
public class Checkers {


    CheckersBoard board;

    private CheckersPlayer redPlayer;
    private CheckersPlayer blackPlayer;
    private Turn turn;
    private WINNER winner;

    /**
     * Enum representing whose turn it is.
     */
    public enum Turn {
        RED, BLACK
    }

    /**
     * Enum representing the winner of the game.
     */
    public enum WINNER {
        RED, BLACK, NONE
    }

    /**
     * Constructs a new Checkers game with the specified board.
     *
     * @param board the board on which the game will be played
     */
    public Checkers(CheckersBoard board) {
        this.board = board;
        this.redPlayer = new CheckersPlayer();
        this.blackPlayer = new CheckersPlayer();
        this.turn = Turn.BLACK;
        this.winner = WINNER.NONE;
    }

    /**
     * Starts the game and runs the game loop till someone wins.
     */
    public void start() {
        this.board.placeAllPieces();
        
    }

    /**
     * Ends the game.
     */
    public WINNER end( WINNER winner) {
        return this.winner;
    }


    /**
     * Moves the given checkers piece.
     * If the piece is normal, performs a normal move; if it is a king, performs a king move.
     *
     * @param piece the checkers piece to be moved
     * 
     * handles the GUI input for selected piece and destination location
     * and then switch turn once the function a turn has been done successfully
     */
    public void movePiece(CheckersPiece piece) {
        
    }

    private void switchTurn(){
        if (this.turn == Turn.RED) {
            this.turn = Turn.BLACK;
        } else {
            this.turn = Turn.RED;
        }
    }

    /**
     * Checks if the red player has won the game.
     *
     * @return true if the red player has won, false otherwise
     */
    private WINNER checkWin() {
        winner = WINNER.NONE;
        int redCount = 0;
        int blackCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CheckersPiece p = this.board.board[i][j];
                if (p != null) {
                    if (p.getColour() == CheckersPiece.Colour.RED) 
                        redCount++;
                    else if (p.getColour() == CheckersPiece.Colour.BLACK) 
                        blackCount++;
                }
            }
        }
        if (redCount == 0) {
            winner = WINNER.BLACK;
        } else if (blackCount == 0) {
            winner = WINNER.RED;
        }
        
        return winner;
    }
}
