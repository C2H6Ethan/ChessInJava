package backend;

public class GameStatus {
    private boolean gameOver;
    private String status;
    private String reason;

    public GameStatus() {}

    public GameStatus(boolean gameOver, String status, String reason) {
        this.gameOver = gameOver;
        this.status = status;
        this.reason = reason;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
