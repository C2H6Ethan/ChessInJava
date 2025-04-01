package chess.backend;

public class Move {
    private final Square from;
    private final Square to;
    private final Piece movingPiece;
    private final Piece capturedPiece;

    public Move(Square from, Square to, Piece movingPiece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
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
