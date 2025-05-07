package main.java.logic;

import main.java.logic.pieces.Pawn;
import main.java.logic.pieces.PieceType;

import java.util.function.Consumer;

public interface PromotionHandler {
    void choosePromotionPiece(Pawn promotingPawn, Consumer<PieceType> callback);
}

