package test;

import chess.backend.Board;
import chess.backend.King;
import chess.backend.Pawn;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    @Test
    void testGetColor() {
        King whiteKing = new King("white");

        assertEquals("white", whiteKing.getColor());
    }

    @Test
    void testMoveCount() {
        Board board = new Board();
        board.getSquare(1, 0).setPiece(new Pawn("white"));

        assertEquals(board.getPieceAt(1, 0).getMoveCount(), 0);
        board.move(board.getSquare(1,0), board.getSquare(2, 0));
        assertEquals(board.getPieceAt(2, 0).getMoveCount(), 1);
    }

}