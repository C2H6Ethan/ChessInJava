package backend;

import bots.GreedyBot;
import bots.RandomBot;
import bots.SmartBot;
import logic.Board;
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
    public ResponseEntity<String> getBoard(@RequestParam int gameId) {
        Board board = chessService.getBoard(gameId);
        return board != null ? ResponseEntity.ok(board.toString()) : ResponseEntity.badRequest().body("No game found with id: " + gameId);
    }

    @GetMapping("/possibleDestinationSquares")
    public ResponseEntity<List<Square>> getPossibleDestinationSquares(@RequestParam int gameId, @RequestParam int row, @RequestParam int col) {
        try {
            return ResponseEntity.ok(chessService.getPossibleDestinationSquares(gameId, row, col));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/gameStatus")
    public ResponseEntity<GameStatus> getGameStatus(@RequestParam int gameId) {
        GameStatus gameStatus = chessService.getGameStatus(gameId);
        return gameStatus != null ? ResponseEntity.ok(gameStatus) : ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/move")
    public ResponseEntity<String> move(@RequestBody MoveRequest moveRequest) {
        boolean success = chessService.move(moveRequest);
        return success ? ResponseEntity.ok(chessService.getBoard(moveRequest.getGameId()).toString()) : ResponseEntity.badRequest().body("Invalid move");
    }

    @PostMapping("/newGame")
    public int newGame() {
       return chessService.newGame();
    }

    @PostMapping("/makeRandomBotMove")
    public ResponseEntity<String> makeRandomBotMove(@RequestParam int gameId) {
        Board board = chessService.getBoard(gameId);
        if (board == null) return ResponseEntity.badRequest().body("No game found with id: " + gameId);

        RandomBot bot = new RandomBot(board);
        bot.makeMove();

        return ResponseEntity.ok(board.toString());
    }

    @PostMapping("/makeGreedyBotMove")
    public ResponseEntity<String> makeGreedyBotMove(@RequestParam int gameId) {
        Board board = chessService.getBoard(gameId);
        if (board == null) return ResponseEntity.badRequest().body("No game found with id: " + gameId);

        GreedyBot bot = new GreedyBot(board);
        bot.makeMove();

        return ResponseEntity.ok(board.toString());
    }

    @PostMapping("/makeSmartBotMove")
    public ResponseEntity<String> makeSmartBotMove(@RequestParam int gameId) {
        Board board = chessService.getBoard(gameId);
        if (board == null) return ResponseEntity.badRequest().body("No game found with id: " + gameId);

        SmartBot bot = new SmartBot(board);
        bot.makeMove();

        return ResponseEntity.ok(board.toString());
    }
}
