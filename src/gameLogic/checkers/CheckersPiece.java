package gameLogic.checkers;

/**
 * Represents a single checkers piece in the game.
 */
public class CheckersPiece {

    /**
     * Enum representing the possible colours of a checkers piece.
     */
    public enum Colour {
        WHITE, BLACK
    }
    
    /**
     * Enum representing the type of a checkers piece.
     */
    public enum Type {
        NORMAL, KING
    }

    private final Colour colour;
    private Type type;


    /**
     * Constructs a new CheckersPiece with the specified colour.
     * The piece is initialized as a normal piece.
     *
     * @param colour the colour of the checkers piece
     */
    public CheckersPiece(Colour colour) {
        this.colour = colour;
        this.type = Type.NORMAL;
    }

    /**
     * Checks if this checkers piece has been promoted to a king.
     *
     * @return true if the piece is a king, false otherwise
     */
    public boolean isKing() {
        return type == Type.KING;
    }

    /**
     * Promotes the checkers piece to a king.
     * 
     */
    public void promoteToKing() {
        this.type = Type.KING;
    }

    /**
     * Retrieves the colour of this checkers piece.
     *
     * @return the colour of the piece
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * Retrieves the type of this checkers piece.
     *
     * @return the type of the piece (NORMAL or KING)
     */
    public Type getType() {
        return type;
    }
}
