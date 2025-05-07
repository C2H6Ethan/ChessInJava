package main.java.logic.pieces;

import main.java.logic.Board;
import main.java.logic.Square;

public class Rook extends Piece {
    public Rook(String color) {
        super(color);
    }

    public Rook(String color, int moveCount) {
        super(color, moveCount);
    }

    public Rook copy() {
        return new Rook(getColor(), getMoveCount());
    }

    public boolean isValidMove(Square from, Square to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        // Early return for invalid moves
        if ((rowDiff != 0 && colDiff != 0) || from.equals(to) || (to.getPiece() != null && !board.isEnemyOnSquare(to, from.getPiece().getColor()))) {
            return false;
        }

        // Check if a piece is blocking
        if (rowDiff == 0) {
            // horizontal
            int colStep = (to.getCol() > from.getCol()) ? 1 : -1;
            for (int col = from.getCol() + colStep; col != to.getCol(); col += colStep) {
                if (board.getPieceAt(from.getRow(), col) != null) {
                    return false;
                }
            }
        } else {
            // vertical
            int rowStep = (to.getRow() > from.getRow()) ? 1 : -1;
            for (int row = from.getRow() + rowStep; row != to.getRow(); row += rowStep) {
                if (board.getPieceAt(row, from.getCol() ) != null) {
                    return false;
                }
            }
        }


        return true;
    }
}
