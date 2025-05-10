package logic;

import logic.pieces.*;

import java.util.*;
import java.util.function.Consumer;

public class Board {
    private final Square[][] squares;
    private final Stack<Move> moveHistory = new Stack<>();
    private PromotionHandler promotionHandler = new DefaultPromotionHandler();
    private final CastlingRights castlingRights = new CastlingRights(true, true, true, true);
    private Square enPassantTarget;
    private final Map<GameState, Integer> stateCounts = new HashMap<>();
    private final Stack<GameState> gameHistory = new Stack<>(); // todo: zobrist hash
    private int halfMoveClock = 0;
    private int fullMoveCounter = 1;
    private String nextPlayerColor = "white";

    public Board() {
        // create squares
        this.squares = new Square[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8;  col++) {
                Square square = new Square((row + col) % 2 == 0 ? "black" : "white", row, col);
                squares[row][col] = square;
            }
        }
    }

    //todo: create second constructor with FEN string

    // Allow UI to override the handler later
    public void setPromotionHandler(PromotionHandler handler) {
        this.promotionHandler = handler;
    }

    // Default behavior (e.g., always promote to Queen)
    private static class DefaultPromotionHandler implements PromotionHandler {
        @Override
        public void choosePromotionPiece(Pawn promotingPawn, Consumer<PieceType> callback) {
            callback.accept(PieceType.QUEEN);
        }
    }

    public void setupPieces() {
        // create pieces
        // pawns
        for (int col = 0; col < 8; col++) {
            getSquare(1, col).setPiece(new Pawn("white"));
            getSquare(6, col).setPiece(new Pawn("black"));
        }

        // main pieces (Rook, Knight, Bishop, Queen, King)
        for (int row = 0; row < 8; row += 7) { // First (0) and last (7) rows
            String color = (row == 0) ? "white" : "black";
            getSquare(row, 0).setPiece(new Rook(color));
            getSquare(row, 1).setPiece(new Knight(color));
            getSquare(row, 2).setPiece(new Bishop(color, getSquare(row, 2).getColor().equals("white")));
            getSquare(row, 3).setPiece(new Queen(color));
            getSquare(row, 4).setPiece(new King(color));
            getSquare(row, 5).setPiece(new Bishop(color));
            getSquare(row, 6).setPiece(new Knight(color));
            getSquare(row, 7).setPiece(new Rook(color));
        }
    }

    public void randomlySetPiece(Piece piece) {
        boolean hasFoundEmptySquare = false;
        Random rand = new Random();

        while (!hasFoundEmptySquare) {
            int randRow = rand.nextInt(8);
            int randCol = rand.nextInt(8);

            Square randSquare = getSquare(randRow, randCol);

            if (randSquare.getPiece() == null) {
                hasFoundEmptySquare = true;
                randSquare.setPiece(piece);
            }
        }
    }

    public Square getSquare(int row, int col) {
        if (!isValidPosition(row, col)) {
            throw new IllegalArgumentException("row and/or col out of bounds!");
        }
        return squares[row][col];
    }

    public void move(Square from, Square to) {
        // keeping this to not break tests
        move(from, to, null);
    }

    public void move(Square from, Square to, PieceType promotionPieceType) {
        // moves piece and updates game rule accordingly but does not check if move is legal
        if (gameHistory.empty()) gameHistory.add(getCurrentState());

        Piece piece = from.getPiece();

        handleEnPassant(piece, from, to);
        updateCounters(piece, to);
        handleCastling(piece, from, to);

        movePiece(from, to);
        handlePromotion(to, promotionPieceType);

        setNextPlayerColor(piece.getColor().equals("white") ? "black" : "white");
        updateCastlingRights(piece, from);
        updateEnPassantTarget(piece, from, to);
        recordGameState();
    }

    public boolean isLegalMove(Square from, Square to) {
        Piece sourcePiece = from.getPiece();
        if (sourcePiece == null) {
            return false;
        }

        if (!sourcePiece.getColor().equals(getNextPlayerColor())) {
            return false;
        }

        if (sourcePiece.isValidMove(from, to, this)) {
            // check if move results in a check

            // Simulate the move
            move(from, to);
            boolean isInCheck = isInCheck(sourcePiece.getColor());

            undoLastMove();

            return !isInCheck;
        }

        return false;
    }

    public void undoLastMove() {
        // Undo move to restore board state
        Move lastMove = moveHistory.pop();

        // remove state from map if value will be 0
        if (!stateCounts.isEmpty()) {
            // should never be empty if recordGameState() in move method is not uncommented
            GameState currentState = gameHistory.pop();
            if (stateCounts.get(currentState) == 1) {
                stateCounts.remove(currentState);
            } else {
                stateCounts.merge(currentState, -1, Integer::sum);
            }
        }

        lastMove.getFrom().setPiece(lastMove.getMovingPiece());
        lastMove.getTo().setPiece(lastMove.getCapturedPiece());
        lastMove.getMovingPiece().setMoveCount(lastMove.getMovingPiece().getMoveCount() - 1);

        setNextPlayerColor(lastMove.getMovingPiece().getColor().equals("white") ? "white" : "black");

        // undo king move castling rights
        if (lastMove.getMovingPiece() instanceof King && ((King) lastMove.getMovingPiece()).isCastle(lastMove.getFrom(), lastMove.getTo())) {
            int row;
            int colBefore;
            int colNow;

            if (lastMove.getMovingPiece().getColor().equals("white")) {
                row = 0;
                if (lastMove.getTo().getCol() < lastMove.getFrom().getCol()) {
                    // white queen side
                    colBefore = 0;
                    colNow = 3;
                    castlingRights.setWhiteQueenSide(true);

                    Piece otherRook = getPieceAt(0,7);
                    if (otherRook != null && otherRook.getMoveCount() == 0) castlingRights.setWhiteKingSide(true);
                } else {
                    // white king side
                    colBefore = 7;
                    colNow = 5;
                    castlingRights.setWhiteKingSide(true);
                    Piece otherRook = getPieceAt(0,0);
                    if (otherRook != null && otherRook.getMoveCount() == 0) castlingRights.setWhiteQueenSide(true);
                }
            } else {
                row = 7;
                if (lastMove.getTo().getCol() < lastMove.getFrom().getCol()) {
                    // black queen side
                    colBefore = 0;
                    colNow = 3;
                    castlingRights.setBlackQueenSide(true);
                    Piece otherRook = getPieceAt(7,7);
                    if (otherRook != null && otherRook.getMoveCount() == 0) castlingRights.setBlackKingSide(true);
                } else {
                    // black king side
                    colBefore = 7;
                    colNow = 5;
                    castlingRights.setBlackKingSide(true);
                    Piece otherRook = getPieceAt(7,0);
                    if (otherRook != null && otherRook.getMoveCount() == 0) castlingRights.setBlackQueenSide(true);
                }
            }

            Square rookNowSquare = getSquare(row, colNow);
            Square rookBeforeSquare = getSquare(row, colBefore);
            rookBeforeSquare.setPiece(rookNowSquare.getPiece());
            rookNowSquare.setPiece(null);
        }

        // undo rook move castling rights
        if (lastMove.getMovingPiece() instanceof Rook rook && rook.getMoveCount() == 0) {
            Square kingSquare = null;
            if (lastMove.getFrom().getRow() == 0) {
                kingSquare = getSquare(0,4);
            } else if (lastMove.getFrom().getRow() == 7) {
                kingSquare = getSquare(7,4);
            }

            if (kingSquare != null && kingSquare.getPiece() != null && kingSquare.getPiece() instanceof King king && king.getColor().equals(rook.getColor()) && king.getMoveCount() == 0) {
                if (lastMove.getFrom().getCol() == 0 && lastMove.getFrom().getRow() == 0) castlingRights.setWhiteQueenSide(true);
                if (lastMove.getFrom().getCol() == 7 && lastMove.getFrom().getRow() == 0) castlingRights.setWhiteKingSide(true);
                if (lastMove.getFrom().getCol() == 0 && lastMove.getFrom().getRow() == 7) castlingRights.setBlackQueenSide(true);
                if (lastMove.getFrom().getCol() == 7 && lastMove.getFrom().getRow() == 7) castlingRights.setBlackKingSide(true);
            }
        }

        // undo en passant move
        if (lastMove.getMovingPiece() instanceof Pawn && lastMove.getFrom().getCol() != lastMove.getTo().getCol() && lastMove.getCapturedPiece() == null) {
            int capturedPawnRow = lastMove.getFrom().getRow();
            int capturePawnCol = lastMove.getTo().getCol();
            String capturedPawnColor = lastMove.getMovingPiece().getColor().equals("white") ? "black" : "white";

            getSquare(capturedPawnRow, capturePawnCol).setPiece(new Pawn(capturedPawnColor, 1));
        }

        if (!moveHistory.isEmpty()) {
            updateEnPassantTarget(moveHistory.peek().getMovingPiece(), moveHistory.peek().getFrom(), moveHistory.peek().getTo());
        } else {
            enPassantTarget = null;
        }

        // todo: add half move clock and en passant target to move class and get from lastMove

    }

    public Piece getPieceAt(int row, int col) {
        Square square = this.getSquare(row, col);
        return square.getPiece();
    }

    public boolean isEnemyOnSquare(Square square, String color) {
        var piece = square.getPiece();
        if (piece != null) {
            return (!Objects.equals(piece.getColor(), color));
        }
        return false;
    }

    public Move getLastMove() {
        return moveHistory.getLast();
    }

    public List<Square> getPossibleDestinationSquares(Square sourceSquare) {
        // TODO: abstract method that only checks possible squares (Knight: L shape)
        List<Square> possibleDestinationSquares = new ArrayList<>();
        Piece sourcePiece = sourceSquare.getPiece();

        if (sourcePiece == null) return possibleDestinationSquares;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square targetSquare = getSquare(row, col);
                if (!sourceSquare.equals(targetSquare) && isLegalMove(sourceSquare, targetSquare)) {
                    possibleDestinationSquares.add(targetSquare);
                }
            }
        }
        return possibleDestinationSquares;
    }

    public List<Square> getPeusdoLegalMoves(Square sourceSquare) {
        // TODO: abstract method that only checks possible squares (Knight: L shape)
        List<Square> pseudoLegalMoves = new ArrayList<>();
        Piece sourcePiece = sourceSquare.getPiece();

        if (sourcePiece == null) return pseudoLegalMoves;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square targetSquare = getSquare(row, col);
                if (sourcePiece.getColor().equals(getNextPlayerColor()) && !sourceSquare.equals(targetSquare) && sourcePiece.isValidMove(sourceSquare, targetSquare, this)) {
                    pseudoLegalMoves.add(targetSquare);
                }
            }
        }
        return pseudoLegalMoves;
    }

    public boolean isInCheck(String color) {
        String colorOfAttacker = color.equals("white") ? "black" : "white";
        Square kingSquare = getKingSquare(color);
        if (kingSquare == null) {
            // for testing
            return false;
        }

        return isSquareUnderAttack(kingSquare, colorOfAttacker);
    }

    public boolean isSquareUnderAttack(Square squareUnderAttack, String colorOfAttacker) {
        // TODO: check only possible attacking positions for each piece type (is a pawn top-left or top-right of the source square)
        // create and use method: isPieceAttacking(Class<?> pieceClass, String colorOfAttacker, int direction)
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square s = getSquare(row, col);
                Piece p = s.getPiece();
                if (!s.equals(squareUnderAttack) && p != null && p.getColor().equals(colorOfAttacker) && p.isValidMove(getSquare(row, col), squareUnderAttack, this)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCheckmate(String color) {
        return isInCheck(color) && hasNoValidMoves(color);
    }

    public boolean isStalemate(String color) {
        return !isInCheck(color) && hasNoValidMoves(color);
    }

    public boolean isInsufficientMaterial() {
        List<Piece> pieces = getAllPieces();
        // King vs King
        if (pieces.size() == 2) {
            return true;
        }

        // King + Minor Piece (Knight or Bishop) vs King
        if (pieces.size() == 3 && pieces.stream().anyMatch(p -> p.getClass() == Knight.class ||  p.getClass() == Bishop.class)) {
            return true;
        }

        // King + Bishop vs King + Bishop (Same Color Bishops)
        if (pieces.size() == 4) {
            Optional<Bishop> whiteBishop = pieces.stream().filter(p -> p.getClass() == Bishop.class && p.getColor().equals("white")).findFirst().map(Bishop.class::cast);
            Optional<Bishop> blackBishop = pieces.stream().filter(p -> p.getClass() == Bishop.class && p.getColor().equals("black")).findFirst().map(Bishop.class::cast);

            if (whiteBishop.isPresent() && blackBishop.isPresent()) {
                if (whiteBishop.get().isOnLightSquare() == blackBishop.get().isOnLightSquare()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isThreefoldRepetition() {
        GameState currentState = getCurrentState();
        int count = stateCounts.getOrDefault(currentState, 0);
        return count >= 3;
    }

    public boolean isHalfMoveClockAtLeast50() {
        return halfMoveClock >= 50;
    }

    public String getNextPlayerColor() {
        return nextPlayerColor;
    }

    public void setNextPlayerColor(String color) {
        if (!color.equals("white") && !color.equals("black")) {
            throw new IllegalArgumentException("next player color has to be either white or black!");
        }

        nextPlayerColor = color;
    }

    public List<Move> generateAllLegalMoves() {
        List<Move> allLegalMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = getSquare(row, col);
                Piece piece = square.getPiece();
                if (piece != null) {
                    List<Square> possibleDestinationSquares = getPossibleDestinationSquares(square);
                    for (Square destinationSquare : possibleDestinationSquares) {
                        if (piece instanceof Pawn && (destinationSquare.getRow() == 0 || destinationSquare.getRow() == 7)) {
                            // Pawn Promotion: Add all possible promotions
                            for (PieceType promotionType : List.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                                allLegalMoves.add(new Move(square, destinationSquare, piece, destinationSquare.getPiece(), promotionType));
                            }
                        } else {
                            // Normal move
                            allLegalMoves.add(new Move(square, destinationSquare, piece, destinationSquare.getPiece()));
                        }
                    }
                }
            }
        }
        return allLegalMoves;
    }

    public List<Move> generateMoves() {
        List<Move> allPseudoLegalMoves = new ArrayList<>();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Square square = getSquare(row, col);
                Piece piece = square.getPiece();
                if (piece != null) {
                    List<Square> pseudoLegalMoves = getPeusdoLegalMoves(square);
                    for (Square destinationSquare : pseudoLegalMoves) {
                        if (piece instanceof Pawn && (destinationSquare.getRow() == 0 || destinationSquare.getRow() == 7)) {
                            // Pawn Promotion: Add all possible promotions
                            for (PieceType promotionType : List.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                                allPseudoLegalMoves.add(new Move(square, destinationSquare, piece, destinationSquare.getPiece(), promotionType));
                            }
                        } else {
                            // Normal move
                            allPseudoLegalMoves.add(new Move(square, destinationSquare, piece, destinationSquare.getPiece()));
                        }
                    }
                }
            }
        }
        return allPseudoLegalMoves;
    }

    public GameState getCurrentState() {
        return new GameState(
                squares,
                getNextPlayerColor(),
                castlingRights,
                enPassantTarget
        );
    }


    // Helper Methods

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    private void handleEnPassant(Piece piece, Square from, Square to) {
        if (piece instanceof Pawn && ((Pawn) piece).isEnPassant(from, to, this)) {
            int direction = piece.getColor().equals("white") ? -1 : 1;
            Square targetSquare = getSquare(to.getRow() + direction, to.getCol());
            targetSquare.setPiece(null);
        }
    }

    private void updateCounters(Piece piece, Square to) {
        if (piece instanceof Pawn || to.getPiece() != null) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        if (piece.getColor().equals("black")) fullMoveCounter ++;
    }

    private void handleCastling(Piece piece, Square from, Square to) {
        if (piece instanceof King && ((King) piece).isCastle(from, to)) {
            int row = piece.getColor().equals("white") ? 0 : 7;
            boolean isKingside = (to.getCol() > from.getCol());

            Square rookFrom = getSquare(row, isKingside ? 7 : 0);
            Square rookTo = getSquare(row, isKingside ? 5 : 3);

            rookTo.setPiece(rookFrom.getPiece());
            rookFrom.setPiece(null);
        }
    }

    private void movePiece(Square from, Square to) {
        Piece piece = from.getPiece();
        piece.incrementMoveCount();
        moveHistory.push(new Move(from, to, piece, to.getPiece()));
        to.setPiece(piece);
        from.setPiece(null);
    }

    private void handlePromotion(Square to, PieceType promotionPieceType) {
        Piece piece = to.getPiece();
        if (piece instanceof Pawn promotingPawn && (to.getRow() == 0 || to.getRow() == 7)) {

            if (promotionPieceType != null) {
                Piece newPiece = createPromotedPiece(promotionPieceType, promotingPawn, to.getColor().equals("white"));
                to.setPiece(newPiece);
            } else {

                // Use the GUI callback-based promotion
                promotionHandler.choosePromotionPiece(promotingPawn, selectedType -> {
                    // Ensure a valid piece type is chosen (fallback to Queen if null)
                    PieceType promotedType = (selectedType != null) ? selectedType : PieceType.QUEEN;

                    // Create the promoted piece
                    Piece newPiece = createPromotedPiece(promotedType, promotingPawn, to.getColor().equals("white"));
                    to.setPiece(newPiece);
                });
            }
        }
    }

    private Piece createPromotedPiece(PieceType type, Pawn pawn, boolean isLightSquare) {
        return switch (type) {
            case QUEEN -> new Queen(pawn.getColor(), pawn.getMoveCount());
            case ROOK -> new Rook(pawn.getColor(), pawn.getMoveCount());
            case BISHOP -> new Bishop(pawn.getColor(), pawn.getMoveCount(), isLightSquare);
            case KNIGHT -> new Knight(pawn.getColor(), pawn.getMoveCount());
        };
    }

    private void recordGameState() {
        GameState currentState = getCurrentState();
        stateCounts.merge(currentState, 1, Integer::sum);
        gameHistory.push(currentState);
    }

    private void updateCastlingRights(Piece piece, Square from) {
        if (piece instanceof King) {
            if (piece.getColor().equals("white")) {
                castlingRights.setWhiteKingSide(false);
                castlingRights.setWhiteQueenSide(false);
            } else {
                castlingRights.setBlackKingSide(false);
                castlingRights.setBlackQueenSide(false);
            }
        } else if (piece instanceof Rook) {
            if (from.getCol() == 0) { // Queenside rook
                if (piece.getColor().equals("white")) {
                    castlingRights.setWhiteQueenSide(false);
                } else {
                    castlingRights.setBlackQueenSide(false);
                }
            } else if (from.getCol() == 7) { // Kingside rook
                if (piece.getColor().equals("white")) {
                    castlingRights.setWhiteKingSide(false);
                } else {
                    castlingRights.setBlackKingSide(false);
                }
            }
        }
    }

    private void updateEnPassantTarget(Piece piece, Square from, Square to) {
        if (piece instanceof Pawn && Math.abs(to.getRow() - from.getRow()) == 2) {
            // Pawn moved two squares: set en passant target
            int enPassantRow = from.getRow() + (piece.getColor().equals("white") ? 1 : -1);
            enPassantTarget = getSquare(enPassantRow, from.getCol());
        } else {
            enPassantTarget = null;
        }
    }

    private List<Piece> getAllPieces() {
        // TODO: remove once seperate array lists are implemented
        List<Piece> pieces = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = getPieceAt(row, col);
                if (piece != null) {
                    pieces.add(piece);
                }
            }
        }

        return pieces;
    }

    private Square getKingSquare(String color) {
        // TODO: keep track of where the kings are in separate variables
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = getPieceAt(row, col);
                if (p instanceof King && p.getColor().equals(color)) {
                    return getSquare(row, col);
                }
            }
        }

        return null;
    }

    private boolean hasNoValidMoves(String color) {
        // TODO: track pieces in 2 array lists
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = getPieceAt(row, col);
                if (p != null && p.getColor().equals(color)) {
                    if (!getPossibleDestinationSquares(getSquare(row, col)).isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        // return board as FEN String
        StringBuilder fen = new StringBuilder();

        // pieces (need to go in backwards order because FEN string starts with 8th rank
        for (int i = 7; i >= 0; i--) {
            Square[] rank = this.squares[i];
            int emptySquareCounter = 0;

            for (int j = 0; j <= 7; j++) {
                Square square = rank[j];
                Piece piece = square.getPiece();
                if (piece == null) {
                    emptySquareCounter++;
                } else {
                    if (emptySquareCounter != 0) {
                        fen.append(emptySquareCounter);
                        emptySquareCounter = 0;
                    }

                    fen.append(piece.toChar());
                }
            }

            if (emptySquareCounter != 0) fen.append(emptySquareCounter);
            if (i != 0) fen.append('/'); // only add / in between ranks
        }

        // player to move
        fen.append(" ");
        fen.append(nextPlayerColor.charAt(0));

        // castling rights
        fen.append(" ");
        fen.append(castlingRights.toString());

        // en passant target square
        fen.append(" ");
        if (enPassantTarget != null) fen.append(enPassantTarget.toAlgebraicNotation());
        else fen.append("-");

        // half move clock
        fen.append(" ");
        fen.append(halfMoveClock);

        // full move counter
        fen.append(" ");
        fen.append(fullMoveCounter);

        return fen.toString();
    }

}
