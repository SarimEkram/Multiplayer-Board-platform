package gameLogic.checkers;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the logic for moves in a game of checkers, including move generation and execution.
 */
public class CheckersMove {
    /**
     * Represents a single move in the game of checkers.
     */
    public static class Move {
        public int destRow, destCol;
        public List<int[]> capturedPositions;   // List of captured pieces' positions

        /**
         * Constructs a Move with destination and captured positions.
         * @param destRow the destination row
         * @param destCol the destination column
         * @param capturedPositions list of captured pieces
         */
        public Move(int destRow, int destCol, List<int[]> capturedPositions) {
            this.destRow = destRow;
            this.destCol = destCol;
            this.capturedPositions = capturedPositions;
        }
    }

    /**
     * Calculates all available moves for a piece at a given location.
     * @param board the current checkers board
     * @param piece the piece to move
     * @param row the starting row
     * @param col the starting column
     * @return array of available moves
     */
    public static Move[] availableMoves(CheckersBoard board, CheckersPiece piece, int row, int col) {
        List<Move> jumpMoves = new ArrayList<>();
        findJumps(board, piece, row, col, new ArrayList<>(), jumpMoves); // Find jump moves first

        List<Move> simpleMoves = new ArrayList<>(); // List for simple moves
        int[][] directions;

        // Determine directions based on piece type
        if (!piece.isKing()) {
            if (piece.getColour() == CheckersPiece.Colour.WHITE) {
                directions = new int[][]{{-1, -1}, {-1, 1}};
            } else {
                directions = new int[][]{{1, -1}, {1, 1}};
            }
        } else {
            directions = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        }

        // Add simple moves
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (onBoard(newRow, newCol) && board.board[newRow][newCol] == null) {
                simpleMoves.add(new Move(newRow, newCol, new ArrayList<>()));
            }
        }

        // Combine both simple moves and jump moves
        List<Move> allMoves = new ArrayList<>();
        allMoves.addAll(simpleMoves);
        allMoves.addAll(jumpMoves);

        return allMoves.toArray(new Move[0]);
    }

    /**
     * Recursively finds all jump moves available from a position.
     * @param board the current board
     * @param piece the piece to move
     * @param currentRow current row of the piece
     * @param currentCol current column of the piece
     * @param currentCaptures list of captured pieces so far
     * @param result list to collect found moves
     */
    private static void findJumps(CheckersBoard board, CheckersPiece piece, int currentRow, int currentCol,
                                  List<int[]> currentCaptures, List<Move> result) {
        int[][] directions;

        // Determine move directions
        if (!piece.isKing()) {
            if (piece.getColour() == CheckersPiece.Colour.WHITE) {
                directions = new int[][]{{-1, -1}, {-1, 1}};
            } else {
                directions = new int[][]{{1, -1}, {1, 1}};
            }
        } else {
            directions = new int[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        }

        boolean jumpFound = false;
        for (int[] dir : directions) {
            int jumpRow = currentRow + 2 * dir[0];
            int jumpCol = currentCol + 2 * dir[1];
            int midRow = currentRow + dir[0];
            int midCol = currentCol + dir[1];

            if (onBoard(jumpRow, jumpCol) && board.board[jumpRow][jumpCol] == null) {
                CheckersPiece middlePiece = board.board[midRow][midCol];
                if (middlePiece != null && middlePiece.getColour() != piece.getColour()) {
                    jumpFound = true;
                    List<int[]> newCaptures = new ArrayList<>(currentCaptures);
                    newCaptures.add(new int[]{midRow, midCol});

                    // Simulate the jump
                    CheckersPiece captured = board.board[midRow][midCol];
                    board.board[currentRow][currentCol] = null;
                    board.board[midRow][midCol] = null;
                    board.board[jumpRow][jumpCol] = piece;

                    // ADD the "partial" jump move here (so user sees single-jump)
                    Move partialMove = new Move(jumpRow, jumpCol, newCaptures);
                    if (!containsMove(result, partialMove)) {
                        result.add(partialMove);
                    }

                    // Then recurse to find further jumps
                    findJumps(board, piece, jumpRow, jumpCol, newCaptures, result);

                    // Backtrack the board
                    board.board[currentRow][currentCol] = piece;
                    board.board[midRow][midCol] = captured;
                    board.board[jumpRow][jumpCol] = null;
                }
            }
        }
        // Finalize the move if no more jumps
        if (!currentCaptures.isEmpty() && !jumpFound) {
            Move finalMove = new Move(currentRow, currentCol, currentCaptures);
            if (!containsMove(result, finalMove)) {
                result.add(finalMove);
            }
        }
    }

    /**
     * Checks if a move already exists in the list.
     * @param moves list of moves
     * @param newMove move to check
     * @return true if the move exists, false otherwise
     */
    private static boolean containsMove(List<Move> moves, Move newMove) {
        for (Move m : moves) {
            if (m.destRow == newMove.destRow && m.destCol == newMove.destCol
                    && sameCaptures(m.capturedPositions, newMove.capturedPositions)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if two capture lists are identical.
     * @param a first capture list
     * @param b second capture list
     * @return true if identical, false otherwise
     */
    private static boolean sameCaptures(List<int[]> a, List<int[]> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i)[0] != b.get(i)[0] || a.get(i)[1] != b.get(i)[1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a board position is valid.
     * @param row the row index
     * @param col the column index
     * @return true if on board, false otherwise
     */
    private static boolean onBoard(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Executes a move on the board.
     * @param board the board to modify
     * @param piece the piece being moved
     * @param startRow starting row
     * @param startCol starting column
     * @param move the move to apply
     */
    public static void move(CheckersBoard board, CheckersPiece piece, int startRow, int startCol, Move move) {
        board.board[move.destRow][move.destCol] = piece;    // Move piece to destination
        board.board[startRow][startCol] = null;     // Clear the starting spot

        // Remove each captured piece
        for (int[] cap : move.capturedPositions) {
            board.removePiece(cap[0], cap[1]);
        }
        // Promote piece to king if it reaches the end row
        if (piece.getColour() == CheckersPiece.Colour.WHITE && move.destRow == 0) {
            piece.promoteToKing();
        } else if (piece.getColour() == CheckersPiece.Colour.BLACK && move.destRow == 7) {
            piece.promoteToKing();
        }
    }
}
