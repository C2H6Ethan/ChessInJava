package main.java.logic.pieces;

import main.java.logic.Board;
import main.java.logic.Square;

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
}