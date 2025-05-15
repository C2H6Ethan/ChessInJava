package bots;

import logic.Board;
import logic.Move;

import java.util.*;

public class RandomBot implements ChessBot {
    private final Board board;

    public RandomBot(Board board) {
        this.board = board;
    }

    public void makeMove() {
        List<Move> allMoves = board.generateAllLegalMoves();

        if (!allMoves.isEmpty()) {
            Random random = new Random();
            Move selectedMove = allMoves.get(random.nextInt(allMoves.size()));
            board.move(selectedMove.getFrom(), selectedMove.getTo(), selectedMove.getPromotionPiece());
        }
    }
}
