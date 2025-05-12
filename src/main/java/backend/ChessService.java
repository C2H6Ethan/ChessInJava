package backend;

import logic.Board;
import logic.Square;
import logic.pieces.King;
import logic.pieces.Pawn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChessService {
    private Board board;

    public ChessService() {
        board = new Board();
        board.setupPieces();
    }

    public String getBoard() {
        return board.toString();
    }

    public List<Square> getPossibleDestinationSquares(MinimalSquare square) {
        Square s = board.getSquare(square.getRow(), square.getCol());

        return board.getPossibleDestinationSquares(s);
    }

    public GameStatus getGameStatus() {
        GameStatus gameStatus = new GameStatus();
        gameStatus.setGameOver(false);

        // checkmate must be checked first since 50th move rule does not apply when last move is a checkmate
        if (board.isCheckmate(board.getNextPlayerColor())) {
            gameStatus.setGameOver(true);
            gameStatus.setStatus((board.getNextPlayerColor().equals("white") ? "black" : "white") + " won");
            gameStatus.setReason("by checkmate");
        } else if (board.isStalemate(board.getNextPlayerColor())) {
            gameStatus.setGameOver(true);
            gameStatus.setStatus("draw");
            gameStatus.setReason("by stalemate");
        } else if (board.isInsufficientMaterial()) {
            gameStatus.setGameOver(true);
            gameStatus.setStatus("draw");
            gameStatus.setReason("by insufficient material");
        } else if (board.isThreefoldRepetition()) {
            gameStatus.setGameOver(true);
            gameStatus.setStatus("draw");
            gameStatus.setReason("by threefold repetition");
        } else if (board.isHalfMoveClockAtLeast50()) {
            gameStatus.setGameOver(true);
            gameStatus.setStatus("draw");
            gameStatus.setReason("by fifty-move rule");
        }

        return gameStatus;
    }

    public Boolean move(MoveRequest moveRequest) {
        Square from;
        Square to;
        try {
            from = board.getSquare(moveRequest.getFrom().getRow(), moveRequest.getFrom().getCol());
            to = board.getSquare(moveRequest.getTo().getRow(), moveRequest.getTo().getCol());
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (board.isLegalMove(from, to)) {
            board.move(from, to);
            return true;
        } else {
            return false;
        }
    }

    public String newGame() {
        board = new Board();
        board.setupPieces();
        return board.toString();
    }
}
