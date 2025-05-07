import logic.Board;
import logic.pieces.Pawn;
import logic.pieces.Rook;
import logic.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RookTest {

    @Test
    void testValidHorizontalMove() {
        Board board = new Board();
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(0, 7);
        from.setPiece(whiteRook);

        assertTrue(whiteRook.isValidMove(from, to, board));
    }

    @Test
    void testValidVerticalMove() {
        Board board = new Board();
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(1, 4);
        Square to = board.getSquare(4, 4);
        from.setPiece(whiteRook);

        assertTrue(whiteRook.isValidMove(from, to, board));
    }

    @Test
    void testInvalidDiagonalMove() {
        Board board = new Board();
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(1, 4);
        Square to = board.getSquare(3, 6);
        from.setPiece(whiteRook);

        assertFalse(whiteRook.isValidMove(from, to, board));
    }

    @Test
    void testInvalidBlockedMove() {
        Board board = new Board();
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(7, 0);
        from.setPiece(whiteRook);
        board.getSquare(4, 0).setPiece(new Pawn("black"));

        assertFalse(whiteRook.isValidMove(from, to, board));
    }

    @Test
    void testValidCaptureMove() {
        Board board = new Board();
        Rook whiteRook = new Rook("white");
        Pawn blackPawn = new Pawn("black");
        Square from = board.getSquare(0,0);
        Square to = board.getSquare(0,7);
        from.setPiece(whiteRook);
        to.setPiece(blackPawn);

        assertTrue(whiteRook.isValidMove(from, to, board));
    }

    @Test
    void testSameColorCaptureMove() {
        Board board = new Board();
        Rook whiteRook = new Rook("white");
        Pawn whitePawn = new Pawn("white");
        Square from = board.getSquare(0,0);
        Square to = board.getSquare(0,7);
        from.setPiece(whiteRook);
        to.setPiece(whitePawn);

        assertFalse(whiteRook.isValidMove(from, to, board));
    }
}
