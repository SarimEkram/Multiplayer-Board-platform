package gameLogic.checkers;

import gameLogic.checkers.CheckersPiece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CheckersPieceTest {

    @Test
    public void testGetColour() {
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);
        assertEquals(CheckersPiece.Colour.WHITE, white.getColour());

    }
    @Test
    public void testGetType() {
        CheckersPiece white = new CheckersPiece(CheckersPiece.Colour.WHITE);

        assertEquals(CheckersPiece.Type.NORMAL, white.getType());
    }

    @Test
    public void testPiecePromotionToKing() {
        CheckersPiece piece = new CheckersPiece(CheckersPiece.Colour.BLACK);
        assertFalse(piece.isKing());
        piece.promoteToKing();
        assertTrue(piece.isKing());
        assertEquals(CheckersPiece.Type.KING, piece.getType());
    }

    @Test
    public void testHasAnimatedKingSetter() {
        CheckersPiece piece = new CheckersPiece(CheckersPiece.Colour.BLACK);
        piece.setHasAnimatedKing(true);
        assertTrue(piece.hasAnimatedKing);
    }
}
