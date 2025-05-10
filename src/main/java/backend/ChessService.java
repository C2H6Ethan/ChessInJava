package backend;

import logic.Board;
import logic.Square;
import org.springframework.stereotype.Service;

@Service
public class ChessService {
    private final Board game;

    public ChessService() {
        game = new Board();
        game.setupPieces();
    }

    public String getBoard() {
        return game.toString();
    }

    public Boolean move(MoveRequest moveRequest) {
        Square from;
        Square to;
        try {
            from = game.getSquare(moveRequest.getFromRow(), moveRequest.getFromCol());
            to = game.getSquare(moveRequest.getToRow(), moveRequest.getToCol());
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (game.isLegalMove(from, to)) {
            game.move(from, to);
            return true;
        } else {
            return false;
        }
    }
}
