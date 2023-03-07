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
        // TODO : Holy hell
        int color = (pieces[idx] > 0) ? 1 : -1;
        int dir = -color; // White moves up, black moves down
        int col = column(idx);
        int start;
        int end;
        if ((idx+7*dir)%8 == 0 || (idx+7+dir)%8 == 7)
            start = 8;
        if ((idx+9*dir)%8 == 0 || (idx+9+dir)%8 == 7)
            end = 8;

        start = 7;
        end = 9;


        for (int change = start*dir; change <= end*dir; change += dir) {
            int p = pieces[idx + change];
            if (change == 8 || change == -8) { // Straight up/down
                if (p == 0)
                    possibleMoves.push(new Move(idx, idx + change, false, 0));
            }
            else {
                if (p*color < 0) // If different colors
                    possibleMoves.push(new Move(idx, idx+change, false, p));
            }
        }
    }

    private void addKnightMoves (int idx) {

    }

    private void addBishopMoves (int idx) {
        int color = (pieces[idx] > 0) ? 1 : -1;
        boolean blocked = false;

        int[] deltas = {9, -9, 7, -7};

        for (int d : deltas) {
            for (int change = d; idx + change < pieces.length && !blocked; change += d) {
                int p = pieces[idx + change];
                if (color * p > 0) {
                    // If blocked by the same color, don't add a new move
                    blocked = true;
                    continue;
                } else if (color * p < 0) {
                    // If blocked by different color, add a move where you capture, but no more
                    blocked = true;
                }
                possibleMoves.push(new Move(idx, idx + change, false, p)); // TODO : CHECKS
            }
        }
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
