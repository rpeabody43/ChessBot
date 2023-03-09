import java.util.LinkedList;
import java.util.Stack;

public class Board {

    // 0 is open
    public static final int P = 1; // PAWN
    public static final int N = 2; // KNIGHT
    public static final int B = 3; // BISHOP
    public static final int R = 4; // ROOK
    public static final int Q = 5; // QUEEN
    public static final int K = 6; // KING

    private LinkedList<Move> possibleMoves;
    private Stack<Move> pastMoves;

    int[] pieces;

    public Board () {
        pieces = new int[]{
                -R,-N,-B,-Q,-K,-B,-N,-R, // Black Pieces
                -P,-P,-P,-P,-P,-P,-P,-P, // Black Pawns
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                P, P, P, P, P, P, P, P, // White Pieces
                R, N, B, Q, K, B, N, R // White Pawns
        };

    }


    // For human player
    // Board is 2D
    public void makeMove (int startR, int startF, int endR, int endF) {

    }

    // For AI player / Internally
    // Board is 1D
    public void makeMove (int start, int end) {

    }

    // checks if a move is valid
    private boolean moveValid (int[] start, int[] end) {
        return false; // STUB
    }

    private int row (int idx) {
        return idx/8;
    }

    private int column (int idx) {
        return idx % 8;
    }

    private void addPawnMoves (int idx) {

    }

    private void addKnightMoves (int idx) {

    }

    private void addBishopMoves (int idx) {

    }

    private void addRookMoves (int idx) {

    }

    private void addKingMoves (int idx) {

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
