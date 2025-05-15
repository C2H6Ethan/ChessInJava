package bots;

import logic.Board;
import logic.Move;
import logic.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SmartBot {
    private final Board board;

    public SmartBot(Board board) {
        this.board = board;
    }

    public void makeMove() {
        Move bestMove = getBestMove(4);

        board.move(bestMove.getFrom(), bestMove.getTo(), bestMove.getPromotionPiece());
    }

    public Move getBestMove(int depth) {
        String currentPlayer = board.getNextPlayerColor();
        boolean isMaximizing = currentPlayer.equals("white");

        List<Move> bestMoves = new ArrayList<>(); // if moves are just as good then pick a random one to not have the same exact games
        double bestScore = isMaximizing ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        for (Move move : board.generateAllLegalMoves()) {
            board.move(move.getFrom(), move.getTo(), move.getPromotionPiece());
            double score = minimax(board, depth - 1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !isMaximizing);
            board.undoLastMove();

            if ((isMaximizing && score > bestScore) || (!isMaximizing && score < bestScore)) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        if (bestMoves.isEmpty()) return null;
        return bestMoves.get(new Random().nextInt(bestMoves.size()));
    }



    public double minimax(Board board, int depth, double alpha, double beta, boolean maximizingPlayer) {
        if (depth == 0 || isGameOver(board)) {
            return evaluateBoard(board);
        }

        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            List<Move> moves = board.generateAllLegalMoves();
            for (Move move : moves) {
                board.move(move.getFrom(), move.getTo(), move.getPromotionPiece());
                double eval = minimax(board, depth - 1, alpha, beta, false);
                board.undoLastMove();
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            List<Move> moves = board.generateAllLegalMoves();
            for (Move move : moves) {
                board.move(move.getFrom(), move.getTo(), move.getPromotionPiece());
                double eval = minimax(board, depth - 1, alpha , beta, true);
                board.undoLastMove();
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    private static final int PAWN = 100;
    private static final int KNIGHT = 300;
    private static final int BISHOP = 310;
    private static final int ROOK = 500;
    private static final int QUEEN = 900;

    private double evaluateBoard(Board board) {
        // Terminal states
        String currentPlayer = board.getNextPlayerColor();

        if (board.isCheckmate(currentPlayer)) return currentPlayer.equals("white") ? -10_000 : 10_000;
        if (board.isStalemate(currentPlayer) || board.isInsufficientMaterial() || board.isThreefoldRepetition() || board.isHalfMoveClockAtLeast50()) return 0;

        // Material evaluation
        int evaluation = 0;

        for (Piece piece : board.getAllPieces()) {
            int value = switch (piece.getClass().getSimpleName()) {
                case "Pawn" -> PAWN;
                case "Knight" -> KNIGHT;
                case "Bishop" -> BISHOP;
                case "Rook" -> ROOK;
                case "Queen" -> QUEEN;
                default -> 0;
            };

            if (piece.getColor().equals("black")) value = -value;
            evaluation += value;
        }

        return evaluation;
    }

    private boolean isGameOver(Board board) {
        return board.isCheckmate(board.getNextPlayerColor()) || board.isStalemate(board.getNextPlayerColor()) || board.isInsufficientMaterial() || board.isThreefoldRepetition() || board.isHalfMoveClockAtLeast50();
    }
}
