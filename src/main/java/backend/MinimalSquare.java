package backend;

public class MinimalSquare {
    // Square class with only row and col data to use in requests
    private int row;
    private int col;

    public MinimalSquare () {}

    public MinimalSquare (int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
