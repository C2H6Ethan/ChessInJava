import logic.pieces.Bishop;
import logic.Board;
import logic.pieces.Pawn;
import logic.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

    @Test
    void testValidDiagonalMove() {
        Board board = new Board();
        Bishop whiteBishop = new Bishop("white");
        Square from = board.getSquare(0,0);
        Square to = board.getSquare(7,7);
        from.setPiece(whiteBishop);

        assertTrue(whiteBishop.isValidMove(from, to, board));
    }

    @Test
    void testInvalidHorizontalMove() {
        Board board = new Board();
        Bishop whiteBishop = new Bishop("white");
        Square from = board.getSquare(0,0);
        Square to = board.getSquare(7,0);
        from.setPiece(whiteBishop);

        assertFalse(whiteBishop.isValidMove(from, to, board));
    }

    @Test
    void testInvalidBlockedMove() {
        Board board = new Board();
        Bishop whiteBishop = new Bishop("white");
        Square from = board.getSquare(0,0);
        Square to = board.getSquare(7,7);
        from.setPiece(whiteBishop);
        board.getSquare(4,4).setPiece(new Pawn("white"));

        assertFalse(whiteBishop.isValidMove(from, to, board));
    }

    @Test
    void testValidCaptureMove() {
        Board board = new Board();
        Bishop whiteBishop = new Bishop("white");
        Pawn blackPawn = new Pawn("black");
        Square from = board.getSquare(0,0);
        Square to = board.getSquare(7,7);
        from.setPiece(whiteBishop);
        to.setPiece(blackPawn);

        assertTrue(whiteBishop.isValidMove(from, to, board));
    }

    @Test
    void testSameColorCaptureMove() {
        Board board = new Board();
        Bishop whiteBishop = new Bishop("white");
        Pawn whitePawn = new Pawn("white");
        Square from = board.getSquare(0,0);
        Square to = board.getSquare(7,7);
        from.setPiece(whiteBishop);
        to.setPiece(whitePawn);

        assertFalse(whiteBishop.isValidMove(from, to, board));
    }

    @Test
    void testBishopOnLightSquare() {
        Bishop bishop = new Bishop("white", true);
        assertTrue(bishop.isOnLightSquare());
    }

}