package logic.pieces;

import logic.Board;
import logic.Square;

import java.util.Objects;

public abstract class Piece {
    private final String color; // "white" or "black"
    private int moveCount = 0;

    public Piece(String color) {
        this.color = color;
    }

    public Piece(String color, int moveCount) {
        // to keep the move count when promoting a piece
        // probably is not needed but added for piece of mind
        this.color = color;
        this.moveCount = moveCount;
    }

    public String getColor() {
        return color;
    }

    public void incrementMoveCount() {
        moveCount++;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public abstract boolean isValidMove(Square from, Square to, Board board);

    public abstract Piece copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return Objects.equals(color, piece.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, getClass());
    }

    @Override
    public String toString() {
        return color + " " + this.getClass().getSimpleName(); // E.g., "white Pawn"
    }

    public char toChar() {
        // returns 'p' for a black pawn and 'P' for a white pawn, used in FEN string

        char pieceChar;
        // Knights should return N instead of K
        if (this instanceof Knight) pieceChar = 'N';
        else pieceChar = this.getClass().getSimpleName().charAt(0);

        if (color.equals("black")) pieceChar = Character.toLowerCase(pieceChar);

        return pieceChar;
    }
}