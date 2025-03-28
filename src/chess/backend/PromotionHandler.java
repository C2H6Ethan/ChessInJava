package chess.backend;

public interface PromotionHandler {
    PieceType choosePromotionPiece(Pawn promotingPawn);
}

