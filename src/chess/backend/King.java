package chess.backend;

public class King extends Piece {
    public King(String color) {
        super(color);
    }

    public King(String color, int moveCount) {
        super(color, moveCount);
    }

    public King copy() {
        return new King(getColor(), getMoveCount());
    }

    public boolean isValidMove(Square from, Square to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        // Early return for invalid moves
        if ((rowDiff > 1 || colDiff > 1) && !(rowDiff == 0 && colDiff == 2)) {
            return false;
        }

        if (to.getPiece() != null && !board.isEnemyOnSquare(to, from.getPiece().getColor())) {
            return false;
        }

        if (isCastle(from, to)) {
            if (board.isInCheck(from.getPiece().getColor())) {
                return false;
            }

            Square rookSquare;
            int colStep;
            int row = getColor().equals("white") ? 0 : 7;

            if (to.getCol() > from.getCol()) {
                // king side
                rookSquare = board.getSquare(row, 7);
                colStep = 1;
            } else {
                // queen side
                rookSquare = board.getSquare(row, 0);
                colStep = -1;
            }

            if (!(rookSquare.getPiece() instanceof Rook)
                    || rookSquare.getPiece().getMoveCount() > 0
                    || from.getPiece().getMoveCount() > 0) {
                return false;
            }

            for (int col = from.getCol() + colStep; col != rookSquare.getCol() ; col+=colStep) {
                // check all squares between king and rook for any piece in between
                if (board.getPieceAt(from.getRow(), col) != null) {
                    return false;
                }
            }

            for (int col = from.getCol() + colStep; col != to.getCol() + colStep; col+=colStep) {
                // check squares between and including target square
                if (board.isSquareUnderAttack(board.getSquare(from.getRow(), col), this.getColor().equals("white") ? "black" : "white")) {
                    return false;
                }
            }
        }


        return true;
    }

    public boolean isCastle(Square from, Square to) {
        int colDiff = Math.abs(from.getCol() - to.getCol());
        if (colDiff == 2) {
            return true;
        }
        return false;
    }
}
