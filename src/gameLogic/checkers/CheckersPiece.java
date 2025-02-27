package gameLogic.checkers;

public class CheckersPiece {

    public enum Colour {
        RED, BLACK
    }
    
    public enum Type {
        NORMAL, KING
    }

    private final Colour colour;
    private final Type type;

    
    public CheckersPiece(Colour colour) {
        this.colour = colour;
        this.type = Type.NORMAL;
    }

    public boolean isKing() {
        return type == Type.KING;
    }

    public void promoteToKing() {
        
    }

    public Colour getColour() {
        return colour;
    }

    public Type getType() {
        return type;
    }
    


}
