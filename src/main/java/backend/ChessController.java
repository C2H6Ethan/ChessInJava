package backend;

import logic.Square;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
class ChessController {
    private final ChessService chessService;

    public ChessController(ChessService chessService) {
        this.chessService = chessService;
    }

    @GetMapping("/board")
    public String getBoard() {
        return chessService.getBoard();
    }

    @GetMapping("/possibleDestinationSquares")
    public ResponseEntity<List<Square>> getPossibleDestinationSquares(@RequestParam int row, @RequestParam int col) {
        try {
            return ResponseEntity.ok(chessService.getPossibleDestinationSquares(new MinimalSquare(row, col)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @GetMapping("/gameStatus")
    public GameStatus getGameStatus() {
        return chessService.getGameStatus();
    }

    @PostMapping("/move")
    public ResponseEntity<String> move(@RequestBody MoveRequest moveRequest) {
        boolean success = chessService.move(moveRequest);
        return success ? ResponseEntity.ok(chessService.getBoard()) : ResponseEntity.badRequest().body("Invalid move");
    }

    @PostMapping("/newGame")
    public String newGame() {
       return chessService.newGame();
    }
}
