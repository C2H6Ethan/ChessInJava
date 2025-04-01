package chess.backend;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        board.setupPieces();
        for (int depth = 1; depth <= 5; depth++) {
            long startTime = System.nanoTime();
            long nodes = perft(depth, board);
            long endTime = System.nanoTime();

            System.out.println("Depth " + depth + ": " + nodes + " positions in "
                    + (endTime - startTime) / 1_000_000 + " ms");
        }

    }

    public static long perft(int depth, Board board) {
        if (depth == 0) {
            return 1;
        }

        long nodes = 0;
        List<Move> moves = board.generateAllLegalMoves(); // Get all legal moves

        for (Move move : moves) {
            board.move(move.getFrom(), move.getTo());   // Apply the move
            nodes += perft(depth - 1, board); // Recursively explore further moves
            board.undoLastMove();   // Undo move to restore board state
        }

        return nodes;
    }

}