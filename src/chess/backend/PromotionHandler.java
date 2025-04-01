package chess.backend;

import java.util.function.Consumer;

public interface PromotionHandler {
    void choosePromotionPiece(Pawn promotingPawn, Consumer<PieceType> callback);
}

