package backend;

import logic.Board;
import logic.Square;
import logic.pieces.Pawn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChessService {
    private final Board board;

    public ChessService() {
        board = new Board();
        board.setupPieces();
        board.getSquare(2, 1).setPiece(new Pawn("black"));
    }

    public String getBoard() {
        return board.toString();
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

    public List<Square> getPossibleDestinationSquares(MinimalSquare square) {
        Square s = board.getSquare(square.getRow(), square.getCol());

        return board.getPossibleDestinationSquares(s);
    }
}
