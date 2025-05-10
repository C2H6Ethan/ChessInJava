package backend;

import logic.Board;
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
}
