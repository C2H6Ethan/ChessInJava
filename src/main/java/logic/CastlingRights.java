package logic;

import java.util.Objects;

public class CastlingRights {
    private boolean whiteKingSide;
    private boolean whiteQueenSide;
    private boolean blackKingSide;
    private boolean blackQueenSide;

    public CastlingRights(boolean whiteKingSide, boolean whiteQueenSide, boolean blackKingSide, boolean blackQueenSide) {
        this.whiteKingSide = whiteKingSide;
        this.whiteQueenSide = whiteQueenSide;
        this.blackKingSide = blackKingSide;
        this.blackQueenSide = blackQueenSide;
    }

    public CastlingRights copy() {
        return new CastlingRights(whiteKingSide, whiteQueenSide, blackKingSide, blackQueenSide);
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

    @Override
    public String toString() {
        // used for fen
        StringBuilder s = new StringBuilder();

        if (whiteKingSide) s.append('K');
        if (whiteQueenSide) s.append('Q');
        if (blackKingSide) s.append('k');
        if (blackQueenSide) s.append('q');

        if (s.isEmpty()) return "-";

        System.out.println(s.toString());
        return s.toString();
    }
}