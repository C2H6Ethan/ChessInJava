package main.java.logic.pieces;

public enum PieceType {
    QUEEN, ROOK, BISHOP, KNIGHT;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}