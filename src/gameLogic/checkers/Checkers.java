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
        board.placeAllPieces();
        while (checkBlackWin()||checkRedWin()) {
    
        }
    }

    /**
     * Ends the game.
     */
    public void end() {
        
    }

    /**
     * Executes the turn for the red player.
     * The red player chooses a piece to move and then moves it.
     */
    public void RedTurn() {
        CheckersPiece chosenPiece = redPlayer.choosePieceToMove(board);
        movePiece(chosenPiece);
    }

    /**
     * Executes the turn for the black player.
     * The black player chooses a piece to move and then moves it.
     */
    public void BlackTurn() {
        CheckersPiece chosenPiece = blackPlayer.choosePieceToMove(board);
        movePiece(chosenPiece);
    }

    /**
     * Switches the turn to the other player.
     */
    public void switchTurn() {
        
    }

    /**
     * Moves the given checkers piece.
     * If the piece is normal, performs a normal move; if it is a king, performs a king move.
     *
     * @param piece the checkers piece to be moved
     */
    public void movePiece(CheckersPiece piece) {
        CheckersPiece.Type type = piece.getType();
        if (type == CheckersPiece.Type.NORMAL) {
            CheckersMove.moveNormal(board, piece);
        } else {
            CheckersMove.moveKing(board, piece);
        }
    }

    /**
     * Checks if the red player has won the game.
     *
     * @return true if the red player has won, false otherwise
     */
    public boolean checkRedWin() {
        return false;
    }

    /**
     * Checks if the black player has won the game.
     *
     * @return true if the black player has won, false otherwise
     */
    public boolean checkBlackWin() {
        return false;
    }
}
