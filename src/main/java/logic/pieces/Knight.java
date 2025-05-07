package main.java.logic.pieces;

import main.java.logic.Board;
import main.java.logic.Square;

public class Knight extends Piece {
    public Knight(String color) {
        super(color);
    }

    public Knight(String color, int moveCount) {
        super(color, moveCount);
    }

    public Knight copy() {
        return new Knight(getColor(), getMoveCount());
    }

    public boolean isValidMove(Square from, Square to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        // Early return for invalid moves
        if (!((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2))) {
            return false;
        }

        if (to.getPiece() != null && !board.isEnemyOnSquare(to, from.getPiece().getColor())) {
            return false;
        }


        return true;
    }
}
