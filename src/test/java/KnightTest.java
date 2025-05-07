import logic.Board;
import logic.pieces.Knight;
import logic.pieces.Pawn;
import logic.Square;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    @Test
    void testValidMove() {
        Board board = new Board();
        Knight whiteKnight = new Knight("white");
        Square from = board.getSquare(0, 1);
        Square to = board.getSquare(2, 2);
        from.setPiece(whiteKnight);

        assertTrue(whiteKnight.isValidMove(from, to, board));
    }

    @Test
    void test8ValidMoves() {
        Board board = new Board();
        Knight whiteKnight = new Knight("white");
        Square from = board.getSquare(4, 4);
        from.setPiece(whiteKnight);

        Collection<Square> possibleSquares = board.getPossibleDestinationSquares(from);

        assertEquals(possibleSquares.size(), 8);
    }

    @Test
    void testInvalidBlockedMove() {
        Board board = new Board();
        Knight whiteKnight = new Knight("white");
        Pawn whitePawn = new Pawn("white");
        Square from = board.getSquare(0, 1);
        Square to = board.getSquare(2, 2);
        from.setPiece(whiteKnight);
        to.setPiece(whitePawn);

        assertFalse(whiteKnight.isValidMove(from, to, board));
    }

    @Test
    void testValidCaptureMove() {
        Board board = new Board();
        Knight whiteKnight = new Knight("white");
        Pawn blackPawn = new Pawn("black");
        Square from = board.getSquare(0, 1);
        Square to = board.getSquare(2, 2);
        from.setPiece(whiteKnight);
        to.setPiece(blackPawn);

        assertTrue(whiteKnight.isValidMove(from, to, board));
    }

}