package chess.backend;

import java.util.Objects;

public class Square {
    private final String color;
    private Piece piece;
    private final int row;
    private final int col;

    public Square(String color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
    }

    public Square copy() {
        Square copy = new Square(color, row, col);
        if (piece != null) {
            copy.setPiece(piece.copy());
        }

        return copy;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public String getColor() {
        return color;
    }

    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return row == square.row && col == square.col && Objects.equals(color, square.color) && Objects.equals(piece, square.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, piece, row, col);
    }

    @Override
    public String toString() {
        return "{" + row + "," + col + "}";
    }
}
