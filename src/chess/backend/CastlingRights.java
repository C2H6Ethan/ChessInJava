package chess.backend;

import java.util.Objects;

public class CastlingRights {
    private boolean whiteKingSide;
    private boolean whiteQueenSide;
    private boolean blackKingSide;
    private boolean blackQueenSide;

    public CastlingRights(boolean whiteQueenSide, boolean blackKingSide, boolean blackQueenSide, boolean whiteKingSide) {
        this.whiteQueenSide = whiteQueenSide;
        this.blackKingSide = blackKingSide;
        this.blackQueenSide = blackQueenSide;
        this.whiteKingSide = whiteKingSide;
    }

    public CastlingRights copy() {
        return new CastlingRights(whiteQueenSide, blackKingSide, blackQueenSide, whiteKingSide);
    }

    public void setWhiteKingSide(boolean canCastle) {
        whiteKingSide = canCastle;
    }

    public void setWhiteQueenSide(boolean canCastle) {
        whiteQueenSide = canCastle;
    }

    public void setBlackKingSide(boolean canCastle) {
        blackKingSide = canCastle;
    }

    public void setBlackQueenSide(boolean canCastle) {
        blackQueenSide = canCastle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CastlingRights that = (CastlingRights) o;
        return whiteKingSide == that.whiteKingSide && whiteQueenSide == that.whiteQueenSide && blackKingSide == that.blackKingSide && blackQueenSide == that.blackQueenSide;
    }

    @Override
    public int hashCode() {
        return Objects.hash(whiteKingSide, whiteQueenSide, blackKingSide, blackQueenSide);
    }
}