package chess.bots;

import chess.backend.Board;
import chess.backend.Square;
import chess.backend.Piece;
import javafx.util.Pair;

import java.util.*;

public class RandomBot implements ChessBot {
    private final Board board;

    public RandomBot(Board board) {
        this.board = board;
    }

    public void makeMove() {
        List<Pair<Square, Square>> allMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = board.getSquare(row, col);
                Piece piece = square.getPiece();
                if (piece != null && piece.getColor().equals(board.getNextPlayerColor())) {
                    List<Square> possibleDestinationSquares = board.getPossibleDestinationSquares(square);
                    for (Square destinationSquare : possibleDestinationSquares) {
                        allMoves.add(new Pair<>(square, destinationSquare));
                    }
                }
            }
        }

        if (!allMoves.isEmpty()) {
            Random random = new Random();
            Pair<Square, Square> selectedMove = allMoves.get(random.nextInt(allMoves.size()));
            board.move(selectedMove.getKey(), selectedMove.getValue());
        }
    }
}
