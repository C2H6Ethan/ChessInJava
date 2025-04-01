package chess.gui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import chess.backend.Square;
import chess.backend.Board;
import chess.backend.Piece;

import java.util.ArrayList;
import java.util.List;

public class ChessGUI extends Application {
    private final List<Square> possibleMoves = new ArrayList<>();
    private double squareSize;
    private Square selectedSquare;
    private Board board;
    private Canvas chessCanvas;
    private GraphicsContext gc;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        board = new Board();
        board.setupPieces();

        // Setup screen dimensions
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double boardSize = bounds.getHeight() * 0.8;
        squareSize = boardSize / 8;

        // Create canvas
        chessCanvas = new Canvas(boardSize, boardSize);
        gc = chessCanvas.getGraphicsContext2D();

        // Draw initial board
        drawBoard();

        // Handle mouse events
        chessCanvas.setOnMousePressed(this::handleMousePressed);

        // Setup stage
        StackPane root = new StackPane(chessCanvas);
        primaryStage.setScene(new Scene(root, boardSize, boardSize));
        primaryStage.setTitle("Chess");
        primaryStage.getIcons().add(new Image("chess/gui/images/twinRooks.png"));
        primaryStage.setX((bounds.getMinX() + bounds.getMaxX()) / 2 - boardSize / 2);
        primaryStage.setY((bounds.getMinY() + bounds.getMaxY()) / 2 - boardSize / 2);
        primaryStage.setResizable(false); // TODO
        primaryStage.show();
    }

    private void drawBoard() {
        // Clear canvas
        gc.clearRect(0, 0, chessCanvas.getWidth(), chessCanvas.getHeight());

        // Draw chess board
        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                int guiRow = 7 - row;

                // Draw square
                Color squareColor = (row + col) % 2 == 0 ? Color.GRAY : Color.DARKGRAY;
                gc.setFill(squareColor);
                gc.fillRect(col * squareSize, guiRow * squareSize, squareSize, squareSize);

                // Highlight selected square
                if (selectedSquare != null && selectedSquare.getRow() == row && selectedSquare.getCol() == col) {
                    gc.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.5));
                    gc.fillRect(col * squareSize, guiRow * squareSize, squareSize, squareSize);
                }

                // Draw piece
                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    try {
                        Image pieceImage = new Image("chess/gui/images/" + piece + ".png");
                        gc.drawImage(pieceImage, col * squareSize, guiRow * squareSize, squareSize, squareSize);
                    } catch (Exception e) {
                        // Fallback: Draw text if image fails to load
                        gc.setFill(Color.BLACK);
                        gc.fillText(piece.toString(), col * squareSize + squareSize/2, guiRow * squareSize + squareSize/2);
                    }
                }
            }
        }

        // Draw possible moves
        gc.setFill(Color.BLACK.deriveColor(0, 1, 1, 0.25));
        for (Square destinationSquare : possibleMoves) {
            int guiRow = 7 - destinationSquare.getRow();
            double centerX = destinationSquare.getCol() * squareSize + squareSize/2;
            double centerY = guiRow * squareSize + squareSize/2;
            double radius = squareSize * 0.15;
            gc.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
    }

    private void handleMousePressed(MouseEvent event) {
        int col = (int) (event.getX() / squareSize);
        int guiRow = (int) (event.getY() / squareSize);

        int row = 7 - guiRow;

        if (col < 0 || col > 7 || row < 0 || row > 7) return;

        Square clickedSquare = board.getSquare(row, col);

        // Check if clicking on a possible move
        if (possibleMoves.contains(clickedSquare)) {
            board.move(selectedSquare, clickedSquare);
            if (board.isCheckmate("white") || board.isCheckmate("black")) {
                System.out.println("Checkmate");
            }
            selectedSquare = null;
            possibleMoves.clear();
        } else {
            // Check if clicking on a piece
            if (board.getPieceAt(row, col) != null) {
                selectedSquare = clickedSquare;
                possibleMoves.clear();
                possibleMoves.addAll(board.getPossibleDestinationSquares(clickedSquare));
            } else {
                selectedSquare = null;
                possibleMoves.clear();
            }
        }

        drawBoard();
    }
}