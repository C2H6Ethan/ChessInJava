package backend;

public class MoveRequest {
    // to keep it simple I will not use algebraic notation for move requests and will stick to 0-7 row, col indexing
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;

    public MoveRequest() {}

    public MoveRequest(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public int getToCol() {
        return toCol;
    }
}

