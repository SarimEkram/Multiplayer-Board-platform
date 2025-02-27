package gameLogic.checkers;

public class Checkers {

    
    CheckersBoard board;

    private CheckersPlayer redPlayer;
    private CheckersPlayer blackPlayer;
    private Turn turn;
    private WINNER winner;

    public enum Turn {
        RED, BLACK
    }

    public enum WINNER {
        RED, BLACK, NONE
    }



    public Checkers(CheckersBoard board){
        this.board = board;
        this.redPlayer = new CheckersPlayer();
        this.blackPlayer = new CheckersPlayer();
        this.turn = Turn.BLACK;
        this.winner = WINNER.NONE;
    }

    

    public void start(){
        board.placeAllPieces();
    }


    public void end(){
    
    }
    
    public void RedTurn(){
        CheckersPiece chosenPiece= redPlayer.choosePieceToMove(board);
        movePiece(chosenPiece);
    }

    public void BlackTurn(){
        CheckersPiece chosenPiece= blackPlayer.choosePieceToMove(board);
        movePiece(chosenPiece);
    }

    public void switchTurn(){
    
    }



    public void movePiece(CheckersPiece piece){
        CheckersPiece.Type type=piece.getType();
        if(type==CheckersPiece.Type.NORMAL){
            CheckersMove.moveNormal(board,piece);
        }else
            CheckersMove.moveKing(board, piece);
    }


    public boolean checkRedWin(){
        return false;
    }

    public boolean checkBlackWin(){
        return false;
    }

}
