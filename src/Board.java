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
    private boolean moveValid (Move move) {
        return possibleMoves.contains(move); // STUB
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

        int[] idxChange = {-17,-15,-10,-6, 6, 10, 15, 17};
        int[] hozChange = {-1, 1, -2, 2, -2, 2, -1, 1};
        if(Math.abs(pieces[idx]) == Math.abs(N)) {

            int column = column(idx);


            for(int i =0; i<idxChange.length; i++){
                if(idxChange[i]>-1 && idxChange[i] < pieces.length) {
                    if(hozChange[i]+column<8 && hozChange[i]+column>-1)
                        if(pieces[idx +idxChange[i]]*pieces[idx]<=0)
                            possibleMoves.add(new Move(idx, idx+idxChange[i], false, pieces[idx+idxChange[i]]));
                }
            }
            }
        }


    private void addBishopMoves (int idx) {

    }

    private void addRookMoves (int idx) {
        if(Math.abs(pieces[idx]) == Math.abs(R)){

            int row = row(idx);
            int column = column(idx);

            // if white
            if (pieces[idx]>0){
                // up vertical moves
                for(int i =1; i<8-row;i++){
                    if(pieces[idx-8*i]<0) {
                        possibleMoves.add(new Move(idx, idx - 8 * i, false, pieces[idx - 8 * i]));
                        break;
                    }
                    else if(pieces[idx-8*i]>0)
                        break;
                    else
                        possibleMoves.add(new Move(idx, idx-8*i, false, 0));
                }
                // down vertical moves
                for(int i =1; i<row;i++){
                    if(pieces[idx+8*i]<0) {
                        possibleMoves.add(new Move(idx, idx + 8* i, false, pieces[idx - 8 * i]));
                        break;
                    }
                    else if(pieces[idx+8*i]>0)
                        break;
                    else
                        possibleMoves.add(new Move(idx, idx +8* i, false, 0));
                }

                // left horizontal moves
                for(int i =1; i<8-column;i++){
                    if(pieces[idx+i]<0) {
                        possibleMoves.add(new Move(idx, idx +  i, false, pieces[idx +  i]));
                        break;
                    }
                    else if(pieces[idx+8*i]>0)
                        break;
                    else
                        possibleMoves.add(new Move(idx, idx+i, false, 0));
                }
                // right horizontal moves
                for(int i =1; i<column;i++){
                    if(pieces[idx-i]<0) {
                        possibleMoves.add(new Move(idx, idx -  i, false, pieces[idx -  i]));
                        break;
                    }
                    else if(pieces[idx-i]>0)
                        break;
                    else
                        possibleMoves.add(new Move(idx, idx-i, false, 0));
                }

            }
            // if black
            else{

                // up vertical moves
                for(int i =1; i<8-row;i++){
                    if(pieces[idx-8*i]>0) {
                        possibleMoves.add(new Move(idx, idx - 8 * i, false, pieces[idx - 8 * i]));
                        break;
                    }
                    else if(pieces[idx-8*i]<0)
                        break;
                    else
                        possibleMoves.add(new Move(idx, idx-8*i, false, 0));
                }
                // down vertical moves
                for(int i =1; i<row;i++){
                    if(pieces[idx+8*i]>0) {
                        possibleMoves.add(new Move(idx, idx + 8* i, false, pieces[idx - 8 * i]));
                        break;
                    }
                    else if(pieces[idx+8*i]<0)
                        break;
                    else
                        possibleMoves.add(new Move(idx, idx +8* i, false, 0));
                }

                // left horizontal moves
                for(int i =1; i<8-column;i++){
                    if(pieces[idx+i]>0) {
                        possibleMoves.add(new Move(idx, idx +  i, false, pieces[idx +  i]));
                        break;
                    }
                    else if(pieces[idx+8*i]<0)
                        break;
                    else
                        possibleMoves.add(new Move(idx, idx+i, false, 0));
                }
                // right horizontal moves
                for(int i =1; i<column;i++){
                    if(pieces[idx-i]>0) {
                        possibleMoves.add(new Move(idx, idx -  i, false, pieces[idx -  i]));
                        break;
                    }
                    else if(pieces[idx-i]<0)
                        break;
                    else
                        possibleMoves.add(new Move(idx, idx-i, false, 0));
                }

            }
        }
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
