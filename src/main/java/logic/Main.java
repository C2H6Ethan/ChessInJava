package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
       Board board = new Board();
       board.setupPieces();
       System.out.println(board.toString());
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