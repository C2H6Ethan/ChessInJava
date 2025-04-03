package chess.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        int depth = 6;
        long startTime = System.nanoTime();

        // Create thread pool
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        // Generate root moves from a temporary board
        Board tempBoard = new Board();
        tempBoard.setupPieces();
        List<Move> rootMoves = tempBoard.generateMoves();

        // List to hold results
        List<Future<Long>> futures = new ArrayList<>();

        // Submit tasks
        for (Move move : rootMoves) {
            futures.add(executor.submit(() -> {
                Board threadBoard = new Board();
                threadBoard.setupPieces(); // Fresh starting position

                // Apply the move
                threadBoard.move(threadBoard.getSquare(move.getFrom().getRow(), move.getFrom().getCol()), threadBoard.getSquare(move.getTo().getRow(), move.getTo().getCol()), move.getPromotionPiece());


                if (!threadBoard.isInCheck(move.getMovingPiece().getColor())) {
                    return perft(depth - 1, threadBoard);
                }
                return 0L;
            }));
        }

        // Sum results
        long totalNodes = 0;
        for (Future<Long> future : futures) {
            totalNodes += future.get();
        }

        executor.shutdown();

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        double mnps = (totalNodes / (double)durationMs) / 1000.0;

        System.out.println("Depth: " + depth);
        System.out.println("Nodes: " + totalNodes);
        System.out.println("Time: " + durationMs + " ms");
        System.out.println("Threads used: " + threads);
        System.out.printf("Speed: %.2f million nodes/second\n", mnps);
    }

    public static long perft(int depth, Board board) {
        if (depth == 0) return 1;

        long nodes = 0;
        List<Move> moves = board.generateMoves();

        for (Move move : moves) {
            board.move(move.getFrom(), move.getTo(), move.getPromotionPiece());
            if (!board.isInCheck(move.getMovingPiece().getColor())) {
                nodes += perft(depth - 1, board);
            }
            board.undoLastMove();
        }

        return nodes;
    }
}