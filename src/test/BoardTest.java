package test;

import chess.backend.*;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testGetSquare() {
        Board board = new Board();
        int row = 3;
        int col = 5;
        Square square = board.getSquare(row, col);

        assertEquals(square.getRow(), row);
        assertEquals(square.getCol(), col);
    }

    @Test
    void testGetSquareOutOfBounds() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class, () -> board.getSquare(8, 0));
    }

    @Test
    void testGetPieceAt() {
        Board board = new Board();
        Rook whiteRook = new Rook("white");
        board.getSquare(0, 0).setPiece(whiteRook);

        assertEquals(board.getPieceAt(0, 0), whiteRook);
    }

    @Test
    void testGetPieceAtOutOfBounds() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class, () -> board.getPieceAt(8, 0)); // Out of bounds
    }

    @Test
    void testGetNullPieceAt() {
        Board board = new Board();
        Piece piece = board.getPieceAt(4, 4);

        assertNull(piece);
    }

    @Test
    void testIsEnemyOnSquare() {
        Board board = new Board();
        Rook blackRook = new Rook("black");
        Square square = board.getSquare(7, 7);
        square.setPiece(blackRook);

        assertTrue(board.isEnemyOnSquare(square, "white"));
    }

    @Test
    void testMove() {
        Board board = new Board();
        board.getSquare(1, 0).setPiece(new Pawn("white"));

        board.move(board.getSquare(1, 0), board.getSquare(3, 0));
        assertNull(board.getPieceAt(1,0));
        assertEquals(board.getPieceAt(3, 0), new Pawn("white"));
    }

    @Test
    void nonLegalMove() {
        Board board = new Board();
        board.getSquare(1, 0).setPiece(new Pawn("white"));

        assertFalse(board.isLegalMove(board.getSquare(1, 0), board.getSquare(5, 0)));
        assertFalse(board.isLegalMove(board.getSquare(4, 0), board.getSquare(5, 0)));
    }

    @Test
    void nonLegalColorMove() {
        Board board = new Board();
        board.getSquare(1, 0).setPiece(new Pawn("white"));
        board.getSquare(6, 0).setPiece(new Pawn("black"));

        assertTrue(board.isLegalMove(board.getSquare(1,0), board.getSquare(2,0)));
        board.move(board.getSquare(1,0), board.getSquare(2,0));
        assertFalse(board.isLegalMove(board.getSquare(2,0), board.getSquare(3,0)));
    }

    @Test
    void testCaptureMove() {
        Board board = new Board();
        board.getSquare(1, 0).setPiece(new Pawn("white"));
        board.getSquare(2, 1).setPiece(new Pawn("black"));

        board.move(board.getSquare(1, 0), board.getSquare(2, 1));
        assertNull(board.getPieceAt(1,0));
        assertEquals(board.getPieceAt(2, 1), new Pawn("white"));
    }

    @Test
    void testEnPassantCaptureMove() {
        Board board = new Board();
        board.getSquare(1, 0).setPiece(new Pawn("white"));
        board.getSquare(3,1).setPiece(new Pawn("black"));
        board.move(board.getSquare(1, 0), board.getSquare(3, 0));
        board.move(board.getSquare(3, 1), board.getSquare(2, 0));

        assertNull(board.getPieceAt(3, 0));
        assertEquals(board.getPieceAt(2, 0).getColor(), "black");
    }

    @Test
    void testGetPossibleDestinationSquaresForPawn() {
        Board board = new Board();
        Square sourceSquare = board.getSquare(1, 0);
        sourceSquare.setPiece(new Pawn("white"));
        board.getSquare(2, 1).setPiece(new Pawn("black"));
        Collection<Square> possibleSquares = board.getPossibleDestinationSquares(sourceSquare);

        assertEquals(3, possibleSquares.size());
        assertTrue(possibleSquares.contains(board.getSquare(2, 0))); // One step forward
        assertTrue(possibleSquares.contains(board.getSquare(3, 0))); // Two steps forward
        assertTrue(possibleSquares.contains(board.getSquare(2, 1))); // Capture
    }

    @Test
    void testGetPossibleDestinationSquaresForEmptySquare() {
        Board board = new Board();
        Square emptySquare = board.getSquare(4, 4); // An empty square

        Collection<Square> possibleSquares = board.getPossibleDestinationSquares(emptySquare);

        assertEquals(0, possibleSquares.size());
    }

    @Test
    void testWhitePromotion() {
        Board board = new Board();
        Square from = board.getSquare(6, 0);
        Square to = board.getSquare(7, 0);
        from.setPiece(new Pawn("white"));

        board.move(from, to);

        assertEquals(to.getPiece(), new Queen("white"));
    }

    @Test
    void testBlackPromotion() {
        Board board = new Board();
        Square from = board.getSquare(1, 0);
        Square to = board.getSquare(0, 0);
        from.setPiece(new Pawn("black"));

        board.move(from, to);

        assertEquals(to.getPiece(), new Queen("black"));
    }

    @Test
    void testPromotionWithoutPawn() {
        Board board = new Board();
        Square from = board.getSquare(6, 0);
        Square to = board.getSquare(7, 0);
        from.setPiece(new Rook("white"));

        board.move(from, to);

        assertEquals(to.getPiece(), new Rook("white"));
    }

    @Test
    void testCapturePromotion() {
        Board board = new Board();
        Square from = board.getSquare(6, 0);
        Square to = board.getSquare(7, 1);
        from.setPiece(new Pawn("white"));
        to.setPiece(new Bishop("black"));

        board.move(from, to);

        assertEquals(to.getPiece(), new Queen("white"));
    }

    @Test
    void testPromotionCheck() {
        Board board = new Board();
        Square from = board.getSquare(6, 0);
        Square to = board.getSquare(7,0);
        from.setPiece(new Pawn("white"));
        board.getSquare(7,7).setPiece(new King("black"));

        board.move(from, to);

        assertTrue(board.isInCheck("black"));
    }

    @Test
    void testWhiteKingSideCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 6);
        from.setPiece(whiteKing);
        board.getSquare(0, 7).setPiece(whiteRook);

        board.move(from, to);
        assertNotNull(board.getPieceAt(0, 5));
    }

    @Test
    void testWhiteQueenSideCastle() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 2);
        from.setPiece(whiteKing);
        board.getSquare(0, 0).setPiece(whiteRook);

        board.move(from, to);
        assertNotNull(board.getPieceAt(0, 3));
    }

    @Test
    void testBlackKingSideCastle() {
        Board board = new Board();
        King whiteKing = new King("black");
        Rook whiteRook = new Rook("black");
        Square from = board.getSquare(7, 4);
        Square to = board.getSquare(7, 6);
        from.setPiece(whiteKing);
        board.getSquare(7, 7).setPiece(whiteRook);

        board.move(from, to);
        assertNotNull(board.getPieceAt(7, 5));
    }

    @Test
    void testBlackQueenSideCastle() {
        Board board = new Board();
        King whiteKing = new King("black");
        Rook whiteRook = new Rook("black");
        Square from = board.getSquare(7, 4);
        Square to = board.getSquare(7, 2);
        from.setPiece(whiteKing);
        board.getSquare(7, 0).setPiece(whiteRook);

        board.move(from, to);
        assertNotNull(board.getPieceAt(7, 3));
    }

    @Test
    void testInvalidMoveBecauseOfCheck() {
        Board board = new Board();
        King whiteKing = new King("white");
        Queen blackQueen = new Queen("black");
        Pawn whitePawn = new Pawn("white");

        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(1, 0);
        board.getSquare(0, 3).setPiece(whiteKing);
        board.getSquare(1, 3).setPiece(blackQueen);
        from.setPiece(whitePawn);

        assertFalse(board.getPossibleDestinationSquares(from).contains(to));
    }

    @Test
    void testInvalidMoveResultingInCheck() {
        Board board = new Board();
        King whiteKing = new King("white");
        Queen blackQueen = new Queen("black");
        Bishop whiteBishop = new Bishop("white");

        Square from = board.getSquare(1, 3);
        Square to = board.getSquare(2, 2);
        board.getSquare(0, 3).setPiece(whiteKing);
        board.getSquare(5, 3).setPiece(blackQueen);
        from.setPiece(whiteBishop);

        assertFalse(board.getPossibleDestinationSquares(from).contains(to));
    }

    @Test
    void testIsCheckmate() {
        Board board = new Board();
        King whiteKing = new King("white");
        Queen blackQueen = new Queen("black");

        board.getSquare(0, 0).setPiece(whiteKing);
        board.getSquare(0, 7).setPiece(blackQueen);
        board.getSquare(1, 7).setPiece(blackQueen);

        assertTrue(board.isCheckmate("white"));
    }

    @Test
    void testIsStalemate() {
        Board board = new Board();
        King whiteKing = new King("white");
        Queen blackQueen = new Queen("black");

        board.getSquare(0, 0).setPiece(whiteKing);
        board.getSquare(2, 1).setPiece(blackQueen);

        assertTrue(board.isStalemate("white"));
    }

    @Test
    void testIsInsufficientMaterial() {
        Board board = new Board();

        // King vs King
        board.getSquare(0,4).setPiece(new King("white"));
        board.getSquare(7,4).setPiece(new King("black"));
        assertTrue(board.isInsufficientMaterial());

        // King + Minor Piece (Knight or Bishop) vs King
        board.getSquare(0,0).setPiece(new Knight("white"));
        assertTrue(board.isInsufficientMaterial());

        board.getSquare(0,0).setPiece(new Bishop("white"));
        assertTrue(board.isInsufficientMaterial());

        // King + Bishop vs King + Bishop (Same Color Bishops)
        board.getSquare(0,0).setPiece(new Bishop("white"));
        board.getSquare(7,1).setPiece(new Bishop("black"));
        assertTrue(board.isInsufficientMaterial());

        // Non-insufficient material

        // King + Queen vs King
        board.getSquare(0,0).setPiece(new Queen("white"));
        board.getSquare(7,1).setPiece(null);
        assertFalse(board.isInsufficientMaterial());

        // King + Rook vs King
        board.getSquare(0,0).setPiece(new Rook("white"));
        assertFalse(board.isInsufficientMaterial());

        // King + 2 Knights vs King
        board.getSquare(0,0).setPiece(new Knight("white"));
        board.getSquare(0,1).setPiece(new Knight("white"));
        assertFalse(board.isInsufficientMaterial());

        // King + Bishop + Knight vs King
        board.getSquare(0,0).setPiece(new Bishop("white"));
        assertFalse(board.isInsufficientMaterial());
    }

    @Test
    void testThreefoldRepetition() {
        Board board = new Board();
        board.setupPieces();

        // Make repeating moves
        board.move(board.getSquare(0, 1), board.getSquare(2, 2)); // White knight
        board.move(board.getSquare(7, 1), board.getSquare(5, 2)); // Black knight
        board.move(board.getSquare(2, 2), board.getSquare(0, 1)); // White knight back
        board.move(board.getSquare(5, 2), board.getSquare(7, 1)); // Black knight back

        // Repeat once more
        board.move(board.getSquare(0, 1), board.getSquare(2, 2));
        board.move(board.getSquare(7, 1), board.getSquare(5, 2));
        board.move(board.getSquare(2, 2), board.getSquare(0, 1));
        board.move(board.getSquare(5, 2), board.getSquare(7, 1));

        assertFalse(board.isThreefoldRepetition()); // Only 2 repeats

        board.move(board.getSquare(0, 1), board.getSquare(2, 2));

        assertTrue(board.isThreefoldRepetition()); // Now 3 repeats
    }

    @Test
    void testThreefoldRepetitionCastlingRights() {
        Board board = new Board();

        board.getSquare(0,1).setPiece(new Knight("white"));
        board.getSquare(7,1).setPiece(new Knight("black"));
        board.getSquare(0,0).setPiece(new Rook("white"));


        board.move(board.getSquare(0, 1), board.getSquare(2, 2)); // White knight
        board.move(board.getSquare(7, 1), board.getSquare(5, 2)); // Black knight
        board.move(board.getSquare(2, 2), board.getSquare(0, 1)); // White knight back
        board.move(board.getSquare(5, 2), board.getSquare(7, 1)); // Black knight back

        // rook move
        board.move(board.getSquare(0,0), board.getSquare(1,0));
        board.move(board.getSquare(1,0), board.getSquare(0,0));

        board.move(board.getSquare(0, 1), board.getSquare(2, 2));
        board.move(board.getSquare(7, 1), board.getSquare(5, 2));
        board.move(board.getSquare(2, 2), board.getSquare(0, 1));
        board.move(board.getSquare(5, 2), board.getSquare(7, 1));

        board.move(board.getSquare(0, 1), board.getSquare(2, 2));

        assertFalse(board.isThreefoldRepetition());

        board.move(board.getSquare(7, 1), board.getSquare(5, 2));
        board.move(board.getSquare(2, 2), board.getSquare(0, 1));
        board.move(board.getSquare(5, 2), board.getSquare(7, 1));

        board.move(board.getSquare(0, 1), board.getSquare(2, 2));

        assertTrue(board.isThreefoldRepetition());
    }

    @Test
    void testThreefoldRepetitionEnPassant() {
        Board board = new Board();

        board.getSquare(3,4).setPiece(new Pawn("white"));
        board.getSquare(6,3).setPiece(new Pawn("black"));
        board.getSquare(0,1).setPiece(new Knight("white"));
        board.getSquare(7,1).setPiece(new Knight("black"));

        // setting up first position with en passant possible
        board.move(board.getSquare(3,4), board.getSquare(4,4));
        board.move(board.getSquare(6,3), board.getSquare(4,3));

        // 2nd repetition
        board.move(board.getSquare(0, 1), board.getSquare(2, 2));
        board.move(board.getSquare(7, 1), board.getSquare(5, 2));
        board.move(board.getSquare(2, 2), board.getSquare(0, 1));
        board.move(board.getSquare(5, 2), board.getSquare(7, 1));

        // 3rd repetition but should be false because of en passant no longer possible
        board.move(board.getSquare(0, 1), board.getSquare(2, 2));
        board.move(board.getSquare(7, 1), board.getSquare(5, 2));
        board.move(board.getSquare(2, 2), board.getSquare(0, 1));
        board.move(board.getSquare(5, 2), board.getSquare(7, 1));

        assertFalse(board.isThreefoldRepetition());

        // actual 3rd repetiton of non en passant
        board.move(board.getSquare(0, 1), board.getSquare(2, 2));
        board.move(board.getSquare(7, 1), board.getSquare(5, 2));
        board.move(board.getSquare(2, 2), board.getSquare(0, 1));
        board.move(board.getSquare(5, 2), board.getSquare(7, 1));

        assertTrue(board.isThreefoldRepetition());
    }

    @Test
    void testHalfMoveClock() {
        // ignoring threefold repetition
        Board board = new Board();

        int moveCounter = 0;

        board.getSquare(0,1).setPiece(new Knight("white"));
        board.getSquare(7,1).setPiece(new Knight("black"));

        // 12 * 4 = 48 moves
        for (int i = 0; i < 12; i++) {
            board.move(board.getSquare(0, 1), board.getSquare(2, 2));
            board.move(board.getSquare(7, 1), board.getSquare(5, 2));
            board.move(board.getSquare(2, 2), board.getSquare(0, 1));
            board.move(board.getSquare(5, 2), board.getSquare(7, 1));
            moveCounter += 4;
        }

        // 49th move
        board.move(board.getSquare(0, 1), board.getSquare(2, 2));
        assertFalse(board.isHalfMoveClockAtLeast50());
        moveCounter++;

        // 50th move
        board.move(board.getSquare(7, 1), board.getSquare(5, 2));
        moveCounter++;

        assertTrue(board.isHalfMoveClockAtLeast50());
        assertEquals(50, moveCounter);
    }

    @Test
    void testHalfMoveClockPawnReset() {
        // ignoring threefold repetition
        Board board = new Board();

        int moveCounter = 0;

        board.getSquare(0,1).setPiece(new Knight("white"));
        board.getSquare(7,1).setPiece(new Knight("black"));
        board.getSquare(0,0).setPiece(new Pawn("white"));

        // 12 * 4 = 48 moves
        for (int i = 0; i < 12; i++) {
            board.move(board.getSquare(0, 1), board.getSquare(2, 2));
            board.move(board.getSquare(7, 1), board.getSquare(5, 2));
            board.move(board.getSquare(2, 2), board.getSquare(0, 1));
            board.move(board.getSquare(5, 2), board.getSquare(7, 1));
            moveCounter += 4;
        }

        // 49th move (pawn move)
        board.move(board.getSquare(0,0), board.getSquare(1,0));
        moveCounter++;

        // 50th move
        board.move(board.getSquare(0, 1), board.getSquare(2, 2));
        moveCounter++;

        assertFalse(board.isHalfMoveClockAtLeast50());
        assertEquals(50, moveCounter);
    }

    @Test
    void testHalfMoveClockCaptureReset() {
        // ignoring threefold repetition
        Board board = new Board();

        int moveCounter = 0;

        board.getSquare(0,1).setPiece(new Knight("white"));
        board.getSquare(7,1).setPiece(new Knight("black"));
        board.getSquare(2,0).setPiece(new Pawn("black"));

        // 12 * 4 = 48 moves
        for (int i = 0; i < 12; i++) {
            board.move(board.getSquare(0, 1), board.getSquare(2, 2));
            board.move(board.getSquare(7, 1), board.getSquare(5, 2));
            board.move(board.getSquare(2, 2), board.getSquare(0, 1));
            board.move(board.getSquare(5, 2), board.getSquare(7, 1));
            moveCounter += 4;
        }

        // 49th move (capture)
        board.move(board.getSquare(0, 1), board.getSquare(2, 0));
        moveCounter++;

        // 50th move
        board.move(board.getSquare(7, 1), board.getSquare(5, 2));
        moveCounter++;

        assertFalse(board.isHalfMoveClockAtLeast50());
        assertEquals(50, moveCounter);
    }

    @Test
    void undoCastleMove() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 4);
        Square to = board.getSquare(0, 6);
        from.setPiece(whiteKing);
        board.getSquare(0, 7).setPiece(whiteRook);
        board.getSquare(0,0).setPiece(whiteRook);

        GameState stateBeforeCastle = board.getCurrentState();
        board.move(from, to);
        assertNotNull(board.getPieceAt(0, 5));

        // undo
        board.undoLastMove();
        assertEquals(board.getPieceAt(0,7), whiteRook);
        assertEquals(board.getPieceAt(0,4), whiteKing);

        GameState stateAfterUndo = board.getCurrentState();
        //positions should be the same
        assertEquals(stateBeforeCastle, stateAfterUndo);
    }

    @Test
    void undoEnPassantTarget() {
        Board board = new Board();
        board.getSquare(1, 0).setPiece(new Pawn("white"));
        board.getSquare(3,1).setPiece(new Pawn("black"));
        board.move(board.getSquare(1, 0), board.getSquare(3, 0));

        GameState beforeEnPassantMove = board.getCurrentState();
        board.move(board.getSquare(3, 1), board.getSquare(2, 0));
        assertNull(board.getPieceAt(3, 0));
        assertEquals(board.getPieceAt(2, 0).getColor(), "black");

        board.undoLastMove();
        GameState afterUndo = board.getCurrentState();
        assertEquals(beforeEnPassantMove, afterUndo);
    }

    @Test
    void undoRookMoveCastleRights() {
        Board board = new Board();
        King whiteKing = new King("white");
        Rook whiteRook = new Rook("white");
        Square from = board.getSquare(0, 0);
        Square to = board.getSquare(0, 1);
        board.getSquare(0,4).setPiece(whiteKing);
        board.getSquare(0, 7).setPiece(whiteRook);
        board.getSquare(0,0).setPiece(whiteRook);

        GameState stateBeforeRookMove = board.getCurrentState();
        board.move(from, to);

        // undo
        board.undoLastMove();

        GameState stateAfterUndo = board.getCurrentState();
        //positions should be the same
        assertEquals(stateBeforeRookMove, stateAfterUndo);
    }

    @Test
    void undoPromotion() {
        Board board = new Board();
        Square from = board.getSquare(6, 0);
        Square to = board.getSquare(7, 0);
        from.setPiece(new Pawn("white"));

        GameState beforePromotion = board.getCurrentState();

        board.move(from, to, PieceType.BISHOP);
        assertEquals(to.getPiece(), new Bishop("white"));

        board.undoLastMove();
        GameState afterUndo = board.getCurrentState();

        assertEquals(beforePromotion, afterUndo);
    }
}