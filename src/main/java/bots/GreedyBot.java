package bots;

import logic.Board;
import logic.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GreedyBot {
    private final Board board;

    public GreedyBot(Board board) {
        this.board = board;
    }

    public void makeMove() {
        List<Move> allMoves = board.generateAllLegalMoves();
        List<Move> captureMoves = new ArrayList<>();
        List<Move> checkMoves = new ArrayList<>();

        for (Move move : allMoves) {
            if (move.getCapturedPiece() != null) {
                captureMoves.add(move);
            }
            board.move(move.getFrom(), move.getTo(), move.getPromotionPiece());
            if (board.isInCheck(move.getMovingPiece().getColor().equals("white") ? "black" : "white")) {
                checkMoves.add(move);
            }
            board.undoLastMove();
        }

        // get random captures, if empty get random check otherwise get random move
        Random random = new Random();
        Move chosenMove;
        if (!captureMoves.isEmpty()) {
            chosenMove = captureMoves.get(random.nextInt(captureMoves.size()));
        } else if (!checkMoves.isEmpty()) {
            chosenMove = checkMoves.get(random.nextInt(checkMoves.size()));
        } else {
            chosenMove = allMoves.get(random.nextInt(allMoves.size()));
        }

        board.move(chosenMove.getFrom(), chosenMove.getTo(), chosenMove.getPromotionPiece());
    }
}
