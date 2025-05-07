import logic.Board;
import logic.pieces.Pawn;
import logic.pieces.Queen;
import logic.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueenTest {

    @Test
    void testValidHorizontalMove() {
        Board board = new Board();
        Queen whiteQueen = new Queen("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(0, 7);
        from.setPiece(whiteQueen);

        assertTrue(whiteQueen.isValidMove(from, to, board));
    }

    @Test
    void testValidVerticalMove() {
        Board board = new Board();
        Queen whiteQueen = new Queen("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(7, 0);
        from.setPiece(whiteQueen);

        assertTrue(whiteQueen.isValidMove(from, to, board));
    }

    @Test
    void testValidDiagonalMove() {
        Board board = new Board();
        Queen whiteQueen = new Queen("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(7, 7);
        from.setPiece(whiteQueen);

        assertTrue(whiteQueen.isValidMove(from, to, board));
    }

    @Test
    void testInvalidBlockedMove() {
        Board board = new Board();
        Queen whiteQueen = new Queen("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(7, 7);
        from.setPiece(whiteQueen);
        board.getSquare(7, 7).setPiece(new Pawn("white"));

        assertFalse(whiteQueen.isValidMove(from, to, board));
    }

    @Test
    void testInvalidDiagonalBlockedMove() {
        Board board = new Board();
        Queen whiteQueen = new Queen("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(7, 7);
        from.setPiece(whiteQueen);
        board.getSquare(4, 4).setPiece(new Pawn("black"));

        assertFalse(whiteQueen.isValidMove(from, to, board));
    }

    @Test
    void testValidCaptureMove() {
        Board board = new Board();
        Queen whiteQueen = new Queen("white");
        Pawn blackPawn = new Pawn("black");
        Square from = board.getSquare(0,0);
        Square to = board.getSquare(7,7);
        from.setPiece(whiteQueen);
        to.setPiece(blackPawn);

        assertTrue(whiteQueen.isValidMove(from, to, board));
    }

    @Test
    void testSameColorCaptureMove() {
        Board board = new Board();
        Queen whiteQueen = new Queen("white");
        Pawn whitePawn = new Pawn("white");
        Square from = board.getSquare(0,0);
        Square to = board.getSquare(7,7);
        from.setPiece(whiteQueen);
        to.setPiece(whitePawn);

        assertFalse(whiteQueen.isValidMove(from, to, board));
    }

}