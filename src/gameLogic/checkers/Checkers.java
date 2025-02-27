package gameLogic.checkers;

public class Checkers {

    
    CheckersBoard board;

    private CheckersPlayer redPlayer;
    private CheckersPlayer blackPlayer;
    private Turn turn;

    public enum Turn {
        RED, BLACK
    }



    public Checkers(CheckersBoard board){
        this.board = board;
        this.redPlayer = new CheckersPlayer();
        this.blackPlayer = new CheckersPlayer();
        turn = Turn.BLACK;
    }

    

    public void start(){
        
    }


    public void end(){
    
    }
    
    public void RedTurn(){
        CheckersPiece chosenPiece= redPlayer.choosePieceToMove(board, redPlayer);
        movePiece(null);
    }

    public void BlackTurn(){
        CheckersPiece chosenPiece= blackPlayer.choosePieceToMove(board, redPlayer);
        movePiece(null);
    }

    public void switchTurn(){
    
    }



    public void movePiece(CheckersPiece piece){
    
    }


    public boolean checkRedWin(){
        return false;
    }

    public boolean checkBlackWin(){
        return false;
    }

}
