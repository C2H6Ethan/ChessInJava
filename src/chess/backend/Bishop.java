package chess.backend;

public class Bishop extends Piece {
    private final boolean isOnLightSquare;

    public Bishop(String color) {
        super(color);
        this.isOnLightSquare = false; // default to black square for testing
    }

    public Bishop(String color, boolean isOnLightSquare) {
        super(color);
        this.isOnLightSquare = isOnLightSquare;
    }

    public Bishop(String color, int moveCount, boolean isOnLightSquare) {
        // for copy to keep move count
        super(color, moveCount);
        this.isOnLightSquare = isOnLightSquare;
    }

    public Bishop copy() {
        return new Bishop(getColor(), getMoveCount(), isOnLightSquare());
    }

    public boolean isValidMove(Square from, Square to, Board board) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());
        int colDiff = Math.abs(from.getCol() - to.getCol());

        // Early return for invalid moves
        if (rowDiff != colDiff || from.equals(to) || (to.getPiece() != null && !board.isEnemyOnSquare(to, from.getPiece().getColor()))) {
            return false;
        }

        // Check if a piece is blocking
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

        return true;
    }

    public boolean isOnLightSquare() {
        return isOnLightSquare;
    }
}
