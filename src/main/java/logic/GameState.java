package logic;

import java.util.Arrays;
import java.util.Objects;

public class GameState {
    private final Square[][] squares;
    private final String player;
    private final CastlingRights castlingRights;
    private final Square enPassantTarget;

    public GameState(Square[][] squares, String player, CastlingRights castlingRights, Square enPassantTarget) {
        this.squares = deepCopySquares(squares);
        this.player = player;
        this.castlingRights = castlingRights.copy();
        this.enPassantTarget = enPassantTarget != null ? enPassantTarget.copy() : null;
    }

    private Square[][] deepCopySquares(Square[][] original) {
        Square[][] copy = new Square[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                copy[row][col] = original[row][col].copy();
            }
        }
        return copy;
    }

    public Square getEnPassantTarget() {
        return this.enPassantTarget;
    }

    @Override
    public boolean equals(Object o) {
        // First condition
        if (this == o) {
            return true;
        }

        // Second condition
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GameState gameState = (GameState) o;

        // Third condition
        boolean squaresEqual = Objects.deepEquals(squares, gameState.squares);
        if (!squaresEqual) {
            return false;
        }

        // Fourth condition
        boolean playersEqual = Objects.equals(player, gameState.player);
        if (!playersEqual) {
            return false;
        }

        // Fifth condition
        boolean castlingRightsEqual = Objects.equals(castlingRights, gameState.castlingRights);
        if (!castlingRightsEqual) {
            return false;
        }

        // Sixth condition
        boolean enPassantTargetEqual = Objects.equals(enPassantTarget, gameState.enPassantTarget);
        if (!enPassantTargetEqual) {
            return false;
        }

        // All conditions passed
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(squares), player, castlingRights, enPassantTarget);
    }

}
