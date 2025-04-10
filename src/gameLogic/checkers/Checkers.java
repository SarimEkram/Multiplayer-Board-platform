package gameLogic.checkers;

/**
 * This class handles the main logic for a Checkers game, including managing turns,
 * processing moves, and determining the winner.
 */
public class Checkers {

    CheckersBoard board;
    private Turn turn;
    private WINNER winner;

    /**
     * Enum representing the turn of a player.
     */
    public enum Turn {
        WHITE, BLACK
    }

    /**
     * Enum representing the winner of the game.
     */
    public enum WINNER {
        WHITE, BLACK, NONE
    }

    /**
     * Constructs a Checkers game instance with the given board.
     * @param board the CheckersBoard to be used in the game
     */
    public Checkers(CheckersBoard board) {
        this.board = board;
        // Start with Black's turn, matching the FXML initial indicator.
        this.turn = Turn.BLACK;
        this.winner = WINNER.NONE;
    }

    /**
     * Returns the current player's turn.
     * @return the current turn (WHITE or BLACK)
     */
    public Turn getTurn() {
        return this.turn;
    }

    /**
     * Starts the game by placing all pieces on the board.
     */
    public void start() {
        this.board.placeAllPieces();
    }

    /**
     * Ends the game and returns the current winner.
     * @param winner the winner to be set (currently unused)
     * @return the winner of the game
     */
    public WINNER end(WINNER winner) {
        return this.winner;
    }

    /**
     * Processes a move on the board and switches the player's turn.
     * @param piece the piece to move
     * @param startRow the starting row position
     * @param startCol the starting column position
     * @param move the move object containing move details
     */
    public void processMove(CheckersPiece piece, int startRow, int startCol, CheckersMove.Move move) {
        CheckersMove.move(board, piece, startRow, startCol, move);
        switchTurn();
    }

    /**
     * Switches the current player's turn.
     */
    private void switchTurn() {
        if (this.turn == Turn.WHITE) {
            this.turn = Turn.BLACK;
        } else {
            this.turn = Turn.WHITE;
        }
    }

    /**
     * Checks if there is a winner by counting remaining pieces.
     * @return the winner if one exists, otherwise NONE
     */
    public WINNER checkWin() {
        winner = WINNER.NONE;
        int whiteCount = 0;
        int blackCount = 0;
        // Iterate over all squares of the board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CheckersPiece p = board.board[i][j];
                if (p != null) {
                    if (p.getColour() == CheckersPiece.Colour.WHITE)
                        whiteCount++;
                    else if (p.getColour() == CheckersPiece.Colour.BLACK)
                        blackCount++;
                }
            }
        }
        // Determine the winner based on remaining pieces
        if (whiteCount == 0) {
            winner = WINNER.BLACK;
        } else if (blackCount == 0) {
            winner = WINNER.WHITE;
        }
        return winner;          // Return the determined winner
    }
}
