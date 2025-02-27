package gameLogic.checkers;

public class CheckersMove {
    

    public static void moveNormal(CheckersBoard board, CheckersPiece piece) {
        CheckersPlayer.availaibleMoves(board, piece);
    }

    public static void moveKing(CheckersBoard board, CheckersPiece piece) {
        
    }
    


}
