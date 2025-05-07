package logic;

import logic.pieces.Piece;
import logic.pieces.PieceType;

public class Move {
    private final Square from;
    private final Square to;
    private final Piece movingPiece;
    private final Piece capturedPiece;
    private final PieceType promotionPiece;

    public Move(Square from, Square to, Piece movingPiece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
        this.promotionPiece = null;
    }

    public Move(Square from, Square to, Piece movingPiece, Piece capturedPiece, PieceType promotionPiece) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
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
}
