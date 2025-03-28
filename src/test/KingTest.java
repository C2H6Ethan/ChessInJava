package test;

import chess.backend.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KingTest {

    @Test
    void testValidVerticalMove() {
        Board board = new Board();
        King whiteKing = new King("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(1, 0);
        from.setPiece(whiteKing);

        assertTrue(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testValidHorizontalMove() {
        Board board = new Board();
        King whiteKing = new King("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(0, 1);
        from.setPiece(whiteKing);

        assertTrue(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testValidDiagonalMove() {
        Board board = new Board();
        King whiteKing = new King("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(1, 1);
        from.setPiece(whiteKing);

        assertTrue(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testValidCaptureMove() {
        Board board = new Board();
        King whiteKing = new King("white");
        Pawn blackPawn = new Pawn("black");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(1, 0);
        from.setPiece(whiteKing);
        to.setPiece(blackPawn);

        assertTrue(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testInvalidBlockedMove() {
        Board board = new Board();
        King whiteKing = new King("white");
        Pawn whitePawn = new Pawn("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(1, 0);
        from.setPiece(whiteKing);
        to.setPiece(whitePawn);

        assertFalse(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testValidKingSideCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 6);
        from.setPiece(whiteKing);
        board.getSquare(0, 7).setPiece(whiteRook);

        assertTrue(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testValidQueenSideCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 2);
        from.setPiece(whiteKing);
        board.getSquare(0, 0).setPiece(whiteRook);

        assertTrue(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testInvalidKingAlreadyMovedCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 2);

        // make a move with king
        board.getSquare(0, 5).setPiece(whiteKing);
        board.move(board.getSquare(0, 5), board.getSquare(0, 4));

        board.getSquare(0, 0).setPiece(whiteRook);

        assertFalse(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testInvalidRookAlreadyMovedCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 4);
        from.setPiece(whiteKing);
        Square to = board.getSquare(0, 2);

        // make a move with rook
        board.getSquare(0, 1).setPiece(whiteRook);
        board.move(board.getSquare(0, 1), board.getSquare(0, 0));

        assertFalse(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testInvalidBlockedCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Bishop whiteBishop = new Bishop("white");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 6);
        from.setPiece(whiteKing);
        board.getSquare(0, 7).setPiece(whiteRook);
        board.getSquare(0, 5).setPiece(whiteBishop);

        assertFalse(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testInvalidAttackedInBetweenSquaresCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Bishop blackBishop = new Bishop("black");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 6);
        from.setPiece(whiteKing);
        board.getSquare(0, 7).setPiece(whiteRook);
        board.getSquare(2, 3).setPiece(blackBishop);

        assertFalse(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testInvalidAttackedDestinationSquareCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Bishop blackBishop = new Bishop("black");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 2);
        from.setPiece(whiteKing);
        board.getSquare(0, 0).setPiece(whiteRook);
        board.getSquare(2, 4).setPiece(blackBishop);

        assertFalse(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testValidAttackedInBetweenRookSquaresCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Bishop blackBishop = new Bishop("black");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 2);
        from.setPiece(whiteKing);
        board.getSquare(0, 0).setPiece(whiteRook);
        board.getSquare(2, 3).setPiece(blackBishop);

        assertTrue(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testValidAttackedRookCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Bishop blackBishop = new Bishop("black");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 2);
        from.setPiece(whiteKing);
        board.getSquare(0, 0).setPiece(whiteRook);
        board.getSquare(3, 3).setPiece(blackBishop);

        assertTrue(whiteKing.isValidMove(from, to, board));
    }

    @Test
    void testValidBlackKingSideCastle() {
        Board board = new Board();
        King blackKing = new King("black");
        Rook blackRook = new Rook("black");
        Square from = board.getSquare(7,4);
        Square to = board.getSquare(7,6);
        from.setPiece(blackKing);
        board.getSquare(7,7).setPiece(blackRook);

        assertTrue(blackKing.isValidMove(from, to, board));
    }

    @Test
    void testValidBlackQueenSideCastle() {
        Board board = new Board();
        King blackKing = new King("black");
        Rook blackRook = new Rook("black");
        Square from = board.getSquare(7,4);
        Square to = board.getSquare(7,2);
        from.setPiece(blackKing);
        board.getSquare(7,0).setPiece(blackRook);

        assertTrue(blackKing.isValidMove(from, to, board));
    }

}