package chess.backend;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        //setupPromotionPerft(board);
        board.setupPieces();
        //board.move(board.getSquare(1,0), board.getSquare(3,0));
        //board.move(board.getSquare(6,0), board.getSquare(5,0));
        //board.move(board.getSquare(3,0), board.getSquare(4,0));
        //board.move(board.getSquare(6,1), board.getSquare(4,1));
        //board.move(board.getSquare(1,2), board.getSquare(2,2));

        for (int depth = 6; depth <= 6; depth++) {
            long startTime = System.nanoTime();
            long totalNodes = 0;

            List<Move> moves = board.generateAllLegalMoves();
            for (Move move : moves) {
                board.move(move.getFrom(), move.getTo(), move.getPromotionPiece()); // Apply move
                long nodes = perft(depth - 1, board); // Recursively explore further moves
                board.undoLastMove(); // Undo move to restore board state
                totalNodes += nodes;
                if (move.getPromotionPiece() != null) {
                    System.out.println(move.getFrom() + " " + move.getTo() + " " + nodes + " promotes to " + move.getPromotionPiece());
                } else {
                    System.out.println(move.getFrom() + " " + move.getTo() + " " + nodes);
                }
            }

            long endTime = System.nanoTime();
            System.out.println("Moves: " + moves.size());
            System.out.println("Nodes: " + totalNodes);
            System.out.println("Depth " + depth + " completed in " + (endTime - startTime) / 1_000_000 + " ms");
            System.out.println();
        }
    }

    public static long perft(int depth, Board board) {
        if (depth == 0) {
            return 1;
        }

        long nodes = 0;
        List<Move> moves = board.generateAllLegalMoves();

        for (Move move : moves) {
            board.move(move.getFrom(), move.getTo(), move.getPromotionPiece()); // Apply the move
            nodes += perft(depth - 1, board); // Recursively explore further moves
            board.undoLastMove(); // Undo move to restore board state
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
