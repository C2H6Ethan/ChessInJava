package logic;

import logic.pieces.Piece;
import logic.pieces.PieceType;

public class Move {
    private final Square from;
    private final Square to;
    private final Piece movingPiece;
    private final Piece capturedPiece;
    private final PieceType promotionPiece;
    private final int halfMoveClock;

    public Move(Square from, Square to, Piece movingPiece, Piece capturedPiece, int halfMoveClock) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
        this.halfMoveClock = halfMoveClock;
        this.promotionPiece = null;
    }

    public Move(Square from, Square to, Piece movingPiece, Piece capturedPiece, int halfMoveClock, PieceType promotionPiece) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
        this.halfMoveClock = halfMoveClock;
        this.promotionPiece = promotionPiece;
    }

    public PieceType getPromotionPiece() {
        return this.promotionPiece;
    }

    public Square getFrom() {
        return from;
    }

    public Square getTo() {
        return to;
    }

    public Piece getMovingPiece() {
        return movingPiece;
    }

    public Piece getCapturedPiece() {return capturedPiece;}

    public int getHalfMoveClock() { return halfMoveClock; }
}
