package logic;

import logic.pieces.Pawn;
import logic.pieces.PieceType;

import java.util.function.Consumer;

public interface PromotionHandler {
    void choosePromotionPiece(Pawn promotingPawn, Consumer<PieceType> callback);
}

