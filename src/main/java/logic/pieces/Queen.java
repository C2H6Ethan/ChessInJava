package logic.pieces;

import logic.Board;
import logic.Square;

public class Queen extends Piece {
    public Queen(String color) {
        super(color);
    }

    public Queen(String color, int moveCount) {
        super(color, moveCount);
    }

    public Queen copy() {
        return new Queen(getColor(), getMoveCount());
    }

    public boolean isValidMove(Square from, Square to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        // Early return for invalid moves
        if (((rowDiff != 0 && colDiff != 0) && rowDiff != colDiff) || from.equals(to) || (to.getPiece() != null && !board.isEnemyOnSquare(to, from.getPiece().getColor()))) {
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
        }
        else if (colDiff == 0) {
            // vertical
            int rowStep = (to.getRow() > from.getRow()) ? 1 : -1;
            for (int row = from.getRow() + rowStep; row != to.getRow(); row += rowStep) {
                if (board.getPieceAt(row, from.getCol() ) != null) {
                    return false;
                }
            }
        } else {
            // diagonal
            int rowStep = (to.getRow() > from.getRow()) ? 1 : -1;
            int colStep = (to.getCol() > from.getCol()) ? 1 : -1;
            int row = from.getRow() + rowStep;
            int col = from.getCol() + colStep;

            while(row != to.getRow() && col != to.getCol()) {
                if (board.getPieceAt(row, col) != null) {
                    return false;
                }

                row += rowStep;
                col += colStep;
            }
        }

        return true;
    }
}
