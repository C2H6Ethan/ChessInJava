package backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/move")
    public ResponseEntity<String> move(@RequestBody MoveRequest moveRequest) {
        boolean success = chessService.move(moveRequest);
        return success ? ResponseEntity.ok("Move successful") : ResponseEntity.badRequest().body("Invalid move");
    }
}
