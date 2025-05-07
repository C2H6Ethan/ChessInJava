package test.java;

import main.java.logic.pieces.King;
import main.java.logic.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    @Test
    void testGetPiece() {;
        Square square = new Square("black", 0, 4);
        King king = new King("white");
        square.setPiece(king);

        assertEquals(square.getPiece(), king);
    }

    @Test
    void testGetPieceNull() {
        Square square = new Square("black", 0, 4);

        assertNull(square.getPiece());
    }

    @Test
    void testSetPiece() {
        Square square = new Square("black", 0, 4);
        King king = new King("white");
        square.setPiece(king);

        assertEquals(square.getPiece(), king);
    }

    @Test
    void testGetColor() {
        Square square = new Square("black", 0, 4);

        assertEquals(square.getColor(), "black");
    }

    @Test
    void testGetRow() {
        Square square = new Square("black", 0, 4);

        assertEquals(square.getRow(), 0);
    }

    @Test
    void testGetCol() {
        Square square = new Square("black", 0, 4);

        assertEquals(square.getCol(), 4);
    }
}