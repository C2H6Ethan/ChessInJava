package chess.backend;

import java.util.Objects;

public class Pawn extends Piece {
    public Pawn(String color) {
        super(color);
    }

    public Pawn(String color, int moveCount) {
        super(color, moveCount);
    }

    public Pawn copy() {
        return new Pawn(getColor(), getMoveCount());
    }

    public boolean isValidMove(Square from, Square to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());
        String sourcePieceColor = from.getPiece().getColor();

        // Early return for invalid moves
        if (colDiff > 1 || rowDiff > 2 || (rowDiff == 2 && this.getMoveCount() != 0) || from.equals(to)
                || (Objects.equals(sourcePieceColor, "white") && (from.getRow() - to.getRow()) > -1)
                || (Objects.equals(sourcePieceColor, "black") && (from.getRow() - to.getRow()) < 1)) {
            return false;
        }

        // 2-square move at start
        if (rowDiff == 2 && colDiff == 0 && to.getPiece() == null) {
            Square squareToJumpOver = board.getSquare(from.getRow() + (Objects.equals(sourcePieceColor, "white") ? 1 : -1), from.getCol());
            if (squareToJumpOver.getPiece() == null) {
                return true;
            }
        }

        // Normal 1-square move
        if (rowDiff == 1 && colDiff == 0 && to.getPiece() == null) {
            return true;
        }

        // En Passant
        if (isEnPassant(from, to, board)) {
            return true;
        }

        // Capture move (diagonal)
        if (rowDiff == 1 && colDiff == 1 && board.isEnemyOnSquare(to, from.getPiece().getColor())) {
            return true;
        }

        // All other cases are invalid
        return false;
    }

    public boolean isEnPassant(Square from, Square to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());
        String sourcePieceColor = from.getPiece().getColor();

        if (sourcePieceColor.equals("white") && from.getRow() != 4) return false;
        if (sourcePieceColor.equals("black") && from.getRow() != 3) return false;

        if (rowDiff == 1 && colDiff == 1 && to.getPiece() == null) {
            Move lastMove = board.getLastMove();
            Square enPassantSquare = board.getSquare(to.getRow() + (Objects.equals(sourcePieceColor, "white") ? -1 : 1), to.getCol());

            return lastMove != null
                    && lastMove.getMovingPiece() instanceof Pawn
                    && Math.abs(lastMove.getFrom().getRow() - lastMove.getTo().getRow()) == 2
                    && enPassantSquare.equals(lastMove.getTo());
        }

        return false;
    }
}
