package chess.backend;

public class Move {
    private final Square from;
    private final Square to;
    private final Piece movingPiece;

    public Move(Square from, Square to, Piece movingPiece) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
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
}
