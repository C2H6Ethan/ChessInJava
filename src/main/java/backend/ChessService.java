package backend;

import logic.Board;
import logic.Square;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChessService {
    private final Map<Integer, Board> boards;

    public ChessService() {
        boards = new HashMap<>();
    }

    public Board getBoard(Integer id) {
        return boards.get(id);
    }

    public List<Square> getPossibleDestinationSquares(int gameId, int row, int col) {
        Board board = boards.get(gameId);
        if (board == null) throw new IllegalArgumentException("game not found with id: " + gameId);
        Square s = board.getSquare(row, col);

        return board.getPossibleDestinationSquares(s);
    }

    public GameStatus getGameStatus(int gameId) {
        Board board = boards.get(gameId);
        if (board == null) return null;

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
        Board board = boards.get(moveRequest.getGameId());
        if (board == null) return false;

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

    public int newGame() {
        boolean idIsUnique = false;
        int gameId = 0;

        while(!idIsUnique) {
            // random 6 digit int as game id
            gameId = 100_000 + new Random().nextInt(900_000);

            if (!boards.containsKey(gameId)) {
                idIsUnique = true;
            }
        }

        Board board = new Board();
        board.setupPieces();
        boards.put(gameId, board);
        return gameId;
    }
}
