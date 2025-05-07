import logic.Board;
import logic.pieces.Pawn;
import logic.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    @Test
    void testIsValidOpeningMove() {
        Board board = new Board();
        Square from = board.getSquare(1, 0);
        from.setPiece(new Pawn("white"));
        Square toShort = board.getSquare(2, 0);
        Square toLong = board.getSquare(3, 0);

        assertTrue(from.getPiece().isValidMove(from, toShort, board));
        assertTrue(from.getPiece().isValidMove(from, toLong, board));
    }

    @Test
    void testIsValidNonOpeningMove() {
        Board board = new Board();
        Square from = board.getSquare(2, 0);
        Square toShort = board.getSquare(3, 0);
        Square toLong = board.getSquare(4, 0);

        board.getSquare(1, 0).setPiece(new Pawn("white"));
        board.move(board.getSquare(1, 0), from);

        assertTrue(from.getPiece().isValidMove(from, toShort, board));
        assertFalse(from.getPiece().isValidMove(from, toLong, board));
    }

    @Test
    void testIsValidCaptureMove() {
        Board board = new Board();
        Pawn whitePawn = new Pawn("white");
        Pawn blackPawn = new Pawn("black");
        Square from = board.getSquare(0, 0);
        Square attackableSquare = board.getSquare(1, 1);
        from.setPiece(whitePawn);
        attackableSquare.setPiece(blackPawn);

        assertTrue(from.getPiece().isValidMove(from, attackableSquare, board));
    }

    @Test
    void testPieceInTheWay() {
        Board board = new Board();
        Pawn blackPawn = new Pawn("black");
        Square from = board.getSquare(1, 0);
        from.setPiece(new Pawn("white"));
        Square blockedSquare = board.getSquare(2, 0);
        Square toLong = board.getSquare(3,0);
        blockedSquare.setPiece(blackPawn);

        assertFalse(from.getPiece().isValidMove(from, blockedSquare, board));
        assertFalse(from.getPiece().isValidMove(from, toLong, board));
    }

    @Test
    void testWhiteInvalidBackwardsMove() {
        Board board = new Board();
        Square from = board.getSquare(1,0);
        from.setPiece(new Pawn("white"));
        Square to = board.getSquare(0,0);
        to.setPiece(null);

        assertFalse(from.getPiece().isValidMove(from, to, board));
    }

    @Test
    void testBlackInvalidBackwardsMove() {
        Board board = new Board();
        Square from = board.getSquare(6,0);
        Square to = board.getSquare(7,0);
        from.setPiece(new Pawn("black"));
        to.setPiece(null);

        assertFalse(from.getPiece().isValidMove(from, to, board));
    }

    @Test
    void testEnPassant() {
        Board board = new Board();
        Pawn whitePawn = new Pawn("white");
        Pawn blackPawn = new Pawn("black");
        board.getSquare(3,1).setPiece(blackPawn);
        Square from = board.getSquare(1, 0);
        from.setPiece(whitePawn);
        board.move(from, board.getSquare(3, 0));

        assertTrue(blackPawn.isValidMove(board.getSquare(3,1), board.getSquare(2, 0), board));
        assertFalse(blackPawn.isValidMove(board.getSquare(3,1), board.getSquare(2, 2), board));
    }

    @Test
    void testNoEnPassantAfterMove() {
        Board board = new Board();
        Pawn whitePawn = new Pawn("white");
        Pawn blackPawn = new Pawn("black");
        Pawn otherPawn = new Pawn("white");
        board.getSquare(3,1).setPiece(blackPawn);
        Square from = board.getSquare(1, 0);
        Square other = board.getSquare(1, 4);
        from.setPiece(whitePawn);
        other.setPiece(otherPawn);
        board.move(board.getSquare(1, 0), board.getSquare(3, 0));
        board.move(board.getSquare(1, 4), board.getSquare(3, 4));

        assertFalse(blackPawn.isValidMove(board.getSquare(3,1), board.getSquare(2, 0), board));
    }
}