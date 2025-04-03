package gameLogic.checkers;

public class Checkers {

    CheckersBoard board;
    private Turn turn;
    private WINNER winner;

    public enum Turn {
        WHITE, BLACK
    }

    public enum WINNER {
        WHITE, BLACK, NONE
    }

    public Checkers(CheckersBoard board) {
        this.board = board;
        // Start with Black's turn, matching the FXML initial indicator.
        this.turn = Turn.BLACK;
        this.winner = WINNER.NONE;
    }

    public Turn getTurn() {
        return this.turn;
    }

    public void start() {
        this.board.placeAllPieces();
    }

    public WINNER end(WINNER winner) {
        return this.winner;
    }

    /**
     * Processes a move by applying the Move object (which includes multi-jump capture data)
     * and then switching the turn.
     */
    public void processMove(CheckersPiece piece, int startRow, int startCol, CheckersMove.Move move) {
        CheckersMove.move(board, piece, startRow, startCol, move);
        switchTurn();
    }

    private void switchTurn() {
        if (this.turn == Turn.WHITE) {
            this.turn = Turn.BLACK;
        } else {
            this.turn = Turn.WHITE;
        }
    }

    public WINNER checkWin() {
        winner = WINNER.NONE;
        int whiteCount = 0;
        int blackCount = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CheckersPiece p = board.board[i][j];
                if (p != null) {
                    if (p.getColour() == CheckersPiece.Colour.WHITE)
                        whiteCount++;
                    else if (p.getColour() == CheckersPiece.Colour.BLACK)
                        blackCount++;
                }
            }
        }
        if (whiteCount == 0) {
            winner = WINNER.BLACK;
        } else if (blackCount == 0) {
            winner = WINNER.WHITE;
        }
        return winner;
    }
}
