package backend;

public class MoveRequest {
    private int gameId;
    // to keep it simple I will not use algebraic notation for move requests and will stick to 0-7 row, col indexing
    private MinimalSquare from;
    private MinimalSquare to;

    public MoveRequest() {}

    public MoveRequest(int gameId, MinimalSquare from, MinimalSquare to) {
        this.from = from;
        this.to = to;
    }

    public int getGameId() {
        return gameId;
    }

    public MinimalSquare getFrom() {
        return from;
    }

    public MinimalSquare getTo() {
        return to;
    }
}

