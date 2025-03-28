package chess.gui;

import chess.backend.Pawn;
import chess.backend.Square;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.text.*;
import javafx.scene.Node;

import chess.backend.Board;
import chess.backend.Piece;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessGUI extends Application {
    private final List<StackPane> stackPanesWithDots = new ArrayList<>();
    private StackPane previousClickedStackPaneWithPiece;
    private Color previousClickedStackPaneWithPieceColor;

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        Board board = new Board();
        board.setupPieces();
        GridPane grid = setupGrid(board);

        primaryStage.setScene(new Scene(grid, 512, 512));
        primaryStage.setTitle("Chess");
        primaryStage.show();


    }

    private GridPane setupGrid(Board board) throws FileNotFoundException {
        GridPane grid = new GridPane();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane stackPane = new StackPane();
                Rectangle square = new Rectangle(64, 64);
                square.setFill((row + col) % 2 == 0 ? Color.GRAY : Color.WHITE);
                stackPane.getChildren().add(square);

                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    ImageView imageView = new ImageView(new Image("chess/gui/images/" + piece + ".png"));
                    stackPane.getChildren().add(imageView);
                }

                final int finalRow = row;
                final int finalCol = col;

               stackPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
                   @Override
                   public void handle(MouseEvent mouseEvent) {
                       if (!stackPanesWithDots.isEmpty()) {
                           // remove previous dots
                           for (StackPane stackPaneWithDot : stackPanesWithDots) {
                               stackPaneWithDot.getChildren().removeIf(n -> n instanceof Circle);
                           }
                       }

                       if (previousClickedStackPaneWithPiece != null) {
                           // change back color of last clicked piece square
                           Rectangle previousGuiSquare = (Rectangle) previousClickedStackPaneWithPiece.getChildren().getFirst();
                           previousGuiSquare.setFill(previousClickedStackPaneWithPieceColor);
                           previousClickedStackPaneWithPiece = null;
                           previousClickedStackPaneWithPieceColor = null;
                       }

                       if (piece != null) {
                           // clicked on square with a piece
                           Rectangle guiSquare = (Rectangle) stackPane.getChildren().getFirst();
                           previousClickedStackPaneWithPiece = stackPane;
                           previousClickedStackPaneWithPieceColor = (Color) guiSquare.getFill();
                           guiSquare.setFill(Color.LIGHTBLUE);
                           guiSquare.setOpacity(.75);


                           Collection<Square> possibleDestinationSquares = board.getPossibleDestinationSquares(board.getSquare(finalRow, finalCol));
                           for (Square square : possibleDestinationSquares) {
                               StackPane destinationStackPane = getSquareStackPane(grid, square.getRow(), square.getCol());
                               assert destinationStackPane != null;
                               Circle circle = new Circle(10);
                               circle.setFill(Color.LIGHTGRAY);
                               destinationStackPane.getChildren().add(circle);
                               stackPanesWithDots.add(destinationStackPane);
                           }
                       }
                   }
               });

                grid.add(stackPane, col, 7 - row);
            }
        }

        return grid;
    }

    private StackPane getSquareStackPane(GridPane grid, int row, int col)  {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("row or col out of bound");
        }
        
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == (7 - row)) {
                return (StackPane) node;
            }
        }
        
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

