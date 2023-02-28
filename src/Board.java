import java.util.LinkedList;
import java.util.Stack;

public class Board {

    public static final int OPEN = 0;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    private LinkedList<Move> possibleMoves;
    private Stack<Move> pastMoves;

    int[][] pieces;

    public Board () {
        pieces = new int[8][8];
        // TODO starting position
    }


    // For human player
    // Board is 2D
    public void makeMove (int startR, int startF, int endR, int endF) {

    }

    // For AI player / Internally
    // Board is 1D
    public void makeMove (int start, int end) {

    }

    private boolean moveValid (int[] start, int[] end) {
        return false; // STUB
    }

    private void updatePossibleMoves () {
        possibleMoves = new LinkedList<>();
        // for piece in board
            // if bishop get bishop moves, etc.

        // SPECIAL CASES
            // en passant
            // castling
            // il vaticano

    }
}
