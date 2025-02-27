package gameLogic.checkers;

/**
 * Utility class for handling checkers moves.
 */
public class CheckersMove {


    /**
     * Executes a normal move for the given checkers piece on the specified board.
     * It will call the availableMoves method to get the available moves for the piece.
     * 
     * @param board the current state of the checkers board
     * @param piece the checkers piece to move
     */
    public static void moveNormal(CheckersBoard board, CheckersPiece piece) {
        CheckersPlayer.availaibleMoves(board, piece);
    }

    /**
     * Executes a king move for the given checkers piece on the specified board.
     * 
     * @param board the current state of the checkers board
     * @param piece the checkers piece to move as a king
     */
    public static void moveKing(CheckersBoard board, CheckersPiece piece) {
        
    }

}