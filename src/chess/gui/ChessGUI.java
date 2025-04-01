package chess.gui;

import chess.backend.*;
import chess.bots.ChessBot;
import chess.bots.RandomBot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ChessGUI extends Application {
    private final List<Square> possibleMoves = new ArrayList<>();
    private double squareSize;
    private Square selectedSquare;
    private Board board;
    private Canvas chessCanvas;
    private StackPane root;
    private GraphicsContext gc;
    private boolean isGameOver = false;
    private ChessBot bot;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        board = new Board();
        //board.setupPieces();
        board.randomlySetPiece(new King("white"));
        board.randomlySetPiece(new King("black"));
        board.randomlySetPiece(new Queen("white"));
        board.setPromotionHandler(new GUIPromotionHandler(this));

        bot = new RandomBot(board);

        setupGUI(primaryStage);
    }

    private void setupGUI(Stage primaryStage) {
        // Setup screen dimensions
        Screen screen = Screen.getScreens().getLast();
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
        root = new StackPane(chessCanvas);
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
        if (isGameOver) {
            return;
        }
        int col = (int) (event.getX() / squareSize);
        int guiRow = (int) (event.getY() / squareSize);

        int row = 7 - guiRow;

        if (col < 0 || col > 7 || row < 0 || row > 7) return;

        Square clickedSquare = board.getSquare(row, col);

        // Check if clicking on a possible move
        if (possibleMoves.contains(clickedSquare)) {
            Piece movedPiece = board.getPieceAt(selectedSquare.getRow(), selectedSquare.getCol());
            boolean isPawnPromoting = (movedPiece instanceof Pawn && (clickedSquare.getRow() == 0 || clickedSquare.getRow() == 7));

            board.move(selectedSquare, clickedSquare);
            selectedSquare = null;
            possibleMoves.clear();
            if (!isPawnPromoting) {
                // otherwise wait until promotion piece has been selected (handlePromotionSelection)
                checkIfGameEnded();

                // Now the bot makes its move
                moveBot();
            }
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

    private void moveBot() {
        if (!isGameOver) {
            bot.makeMove();
            drawBoard();
            checkIfGameEnded();
        }
    }

    private void checkIfGameEnded() {
        if (board.isInsufficientMaterial()) {
            showGameOverModal("Draw!", "insufficient material");
            isGameOver = true;
        } else if (board.isThreefoldRepetition()) {
            showGameOverModal("Draw!", "threefold repetition");
            isGameOver = true;
        } else if (board.isHalfMoveClockAtLeast50()) {
            showGameOverModal("Draw!", "50 move rule");
            isGameOver = true;
        } else if (board.isCheckmate(board.getNextPlayerColor())) {
            showGameOverModal("Checkmate!", (board.getNextPlayerColor().equals("white") ? "black" : "white") +  " wins");
            isGameOver = true;
        } else if (board.isStalemate(board.getNextPlayerColor())) {
            showGameOverModal("Draw!", "stalemate");
            isGameOver = true;
        }
    }

    private void showGameOverModal(String title, String message) {
        // Create a semi-transparent overlay
        Canvas overlay = new Canvas(chessCanvas.getWidth(), chessCanvas.getHeight());
        GraphicsContext overlayGC = overlay.getGraphicsContext2D();
        overlayGC.setFill(Color.BLACK.deriveColor(0, 1, 1, 0.5));
        overlayGC.fillRect(0, 0, overlay.getWidth(), overlay.getHeight());

        // Create modal content
        StackPane modal = new StackPane();
        modal.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f0f0, #e0e0e0); " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        modal.setMaxWidth(chessCanvas.getWidth() * 0.6);
        modal.setMaxHeight(chessCanvas.getHeight() * 0.4);

        // Create content
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        Label winnerLabel = new Label(message);
        winnerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; ");

        // Add crown icon for winner
        ImageView crown = new ImageView();
        try {
            Image crownImage = new Image("chess/gui/images/twinRooks.png"); // Todo
            crown.setImage(crownImage);
            crown.setFitWidth(60);
            crown.setFitHeight(60);
        } catch (Exception e) {
            // Fallback if image not found
            crown = new ImageView();
        }

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #4a6fa5; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(_ -> Platform.exit());

        content.getChildren().addAll(titleLabel, crown, winnerLabel, closeButton);
        modal.getChildren().add(content);

        // Add to root
        root.getChildren().addAll(overlay, modal);

        // Center the modal
        StackPane.setAlignment(modal, Pos.CENTER);
    }

    private static class GUIPromotionHandler implements PromotionHandler {
        private final ChessGUI gui;
        private final List<PieceType> promotionPieces = List.of(
                PieceType.QUEEN,
                PieceType.ROOK,
                PieceType.BISHOP,
                PieceType.KNIGHT
        );
        private Canvas overlay;
        private Canvas selectionCanvas;

        public GUIPromotionHandler(ChessGUI gui) {
            this.gui = gui;
        }

        @Override
        public void choosePromotionPiece(Pawn promotingPawn, Consumer<PieceType> callback) {
            Platform.runLater(() -> showPromotionDialog(promotingPawn, callback));
        }

        private void showPromotionDialog(Pawn promotingPawn, Consumer<PieceType> callback) {
            StackPane root = gui.root;
            Canvas chessCanvas = gui.chessCanvas;
            double squareSize = gui.squareSize;

            // Create a semi-transparent overlay
            overlay = new Canvas(chessCanvas.getWidth(), chessCanvas.getHeight());
            GraphicsContext overlayGC = overlay.getGraphicsContext2D();
            overlayGC.setFill(Color.BLACK.deriveColor(0, 1, 1, 0.5));
            overlayGC.fillRect(0, 0, overlay.getWidth(), overlay.getHeight());

            // Create selection canvas
            selectionCanvas = new Canvas(squareSize * 4, squareSize);
            GraphicsContext sc = selectionCanvas.getGraphicsContext2D();
            sc.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.7));
            sc.fillRoundRect(0, 0, squareSize * 4, squareSize, 15, 15);

            for (int i = 0; i < promotionPieces.size(); i++) {
                PieceType type = promotionPieces.get(i);
                Image pieceImage = new Image("chess/gui/images/" + promotingPawn.getColor() + " " + type + ".png");
                sc.drawImage(pieceImage, i * squareSize, 0, squareSize, squareSize);
            }

            selectionCanvas.setOnMousePressed(event -> handlePromotionSelection(event, callback));

            // Add to root
            root.getChildren().addAll(overlay, selectionCanvas);
        }

        private void handlePromotionSelection(MouseEvent event, Consumer<PieceType> callback) {
            double squareSize = gui.squareSize;
            int index = (int) (event.getX() / squareSize);

            if (index >= 0 && index < promotionPieces.size()) {
                PieceType selectedPiece = promotionPieces.get(index);
                gui.root.getChildren().removeAll(overlay, selectionCanvas);

                // Call the callback with the selected piece
                callback.accept(selectedPiece);

                // Redraw the board to show the promoted piece
                gui.drawBoard();
                gui.checkIfGameEnded();

                gui.moveBot();
            }
        }

    }
}