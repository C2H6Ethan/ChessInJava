package chess.backend;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        board.setupPieces();

        int depth = 6;
        long startTime = System.nanoTime();

        long totalNodes = perft(depth, board);

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        double nodesPerSecond = (totalNodes * 1000.0) / durationMs;
        double millionNodesPerSecond = nodesPerSecond / 1_000_000;

        System.out.println("Depth: " + depth);
        System.out.println("Nodes: " + totalNodes);
        System.out.println("Time: " + durationMs + " ms");
        System.out.printf("Speed: %.2f million nodes/second\n", millionNodesPerSecond);
    }

    public static long perft(int depth, Board board) {

        long nodes = 0;

        if (depth == 0) {
            return 1;
        }

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

    public static void setupPromotionPerft(Board board) {
        Pawn whitePawn = new Pawn("white");
        board.getSquare(6,0).setPiece(whitePawn);
        board.getSquare(6,1).setPiece(whitePawn);
        board.getSquare(6,2).setPiece(whitePawn);

        Pawn blackPawn = new Pawn("black");
        board.getSquare(1, 5).setPiece(blackPawn);
        board.getSquare(1, 6).setPiece(blackPawn);
        board.getSquare(1, 7).setPiece(blackPawn);

        Knight whiteKnight = new Knight("white");
        board.getSquare(0, 5).setPiece(whiteKnight);
        board.getSquare(0, 7).setPiece(whiteKnight);

        Knight blackKnight = new Knight("black");
        board.getSquare(7, 0).setPiece(blackKnight);
        board.getSquare(7, 2).setPiece(blackKnight);

        King whiteKing = new King("white");
        board.getSquare(1,4).setPiece(whiteKing);

        King blackKing = new King("black");
        board.getSquare(6,3).setPiece(blackKing);

        board.setNextPlayerColor("black");

        // investigating
       // board.move(board.getSquare(1,6), board.getSquare(0,5), PieceType.ROOK);
    }
}
