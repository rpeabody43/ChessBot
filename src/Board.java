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

    int numActualMoves;
    int[] pieces;

    public Board () {
        pieces = new int[]{
                -R,-N,-B,-Q,-K,-B,-N,-R, // Black Pieces
                -P,-P,-P,-P,-P,-P,-P,-P, // Black Pawns
//                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
//                0, 0, 0, 0, 0, 0, 0, 0,
                P, P, P, P, P, P, P, P, // White Pawns
                R, N, B, Q, K, B, N, R // White Pieces
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
        // TODO : Holy hell
        int color = (pieces[idx] > 0) ? 1 : -1;
        int dir = -color; // White moves up, black moves down
        int col = column(idx);
        int start;
        int end;
        // start is the leftmost move a pawn can make (when capturing)
        // end is the rightmost move a pawn can make
        start = 7;
        end = 9;
        if ((idx+7*dir)%8 == 0 || (idx+7+dir)%8 == 7)
            start = 8;
        if ((idx+9*dir)%8 == 0 || (idx+9+dir)%8 == 7)
            end = 8;


        for (int change = start*dir; Math.abs(change) <= Math.abs(end); change += dir) {
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

        // If on the starting row for that color
        if ((row(idx) == 6 && color == 1) || (row(idx) == 1 && color == -1)) {
            int newIdx = idx - 16*color;
            if (pieces[newIdx] == 0)
                possibleMoves.push(new Move(idx, newIdx, false, 0));
        }
    }

    private void addKnightMoves (int idx) {

        int[] idxChange = {-17,-15,-10,-6, 6, 10, 15, 17};
        int[] hozChange = {-1, 1, -2, 2, -2, 2, -1, 1};
        if(Math.abs(pieces[idx]) == Math.abs(N)) {

            int column = column(idx);


            for(int i =0; i<idxChange.length; i++){
                if(idx+idxChange[i]>-1 && idx+idxChange[i] < pieces.length) {
                    if(hozChange[i]+column<8 && hozChange[i]+column>-1)
                        if(pieces[idx+idxChange[i]]*pieces[idx]<=0)
                            possibleMoves.add(new Move(idx, idx+idxChange[i], false, pieces[idx+idxChange[i]]));
                }
            }
            }
        }


    private void addBishopMoves (int idx) {
        int color = (pieces[idx] > 0) ? 1 : -1;
        int[] deltas = {9, -9, 7, -7};

        for (int d : deltas) {
            boolean blocked = false;
            for (int change = d; idx + change < pieces.length && !blocked; change += d) {
                int newIdx = idx + change;
                int col = column(newIdx);
                if (newIdx < 0 || newIdx >= pieces.length) {
                    blocked = true;
                    continue;
                }

                if (col == 0 || col == 7)
                    blocked = true;

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
        if(Math.abs(pieces[idx]) == Math.abs(R)){

            int row = row(idx);
            int column = column(idx);
            //this probably doesn't work but replit doesn't show red squiggly lines !!
            int[] idxChange = {-1,-8,1,8};
            int[] maxIterations = {column,row,7-column,7-row};
            for(int i=0;i<4;i++){
              for(int j=0;j<maxIterations[i];j++){
                if(pieces[idx+idxChange[i]*j]*pieces[idx]<=0){
                  possibleMoves.add(new Move(idx,idx+idxChange[i]*j,false,pieces[idx+idxChange[i]*j]));
                  if (pieces[idx+idxChange[i]*j]*pieces[idx]<0) break;
                }else{
                  break;
                }
              }
            }
            /*
            // if white
            if (pieces[idx]>0){
                // up vertical moves
                for(int i =1; i<row;i++){
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
                for(int i =1; i<8-row;i++){
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
                for(int i =1; i<column;i++){
                    if(pieces[idx-i]<0) {
                        possibleMoves.add(new Move(idx, idx -  i, false, pieces[idx +  i]));
                        break;
                    }
                    else if(pieces[idx-8*i]>0)
                        break;
                    else
                        possibleMoves.add(new Move(idx, idx-i, false, 0));
                }
                // right horizontal moves
                for(int i =1; i<8-column;i++){
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

            }*/
        }
    }

    private void addKingMoves (int idx) {
        int[] idxChange = {-9,-8,-7,-1,1,7,8,9};
        int[] rizzChange = {-1,0,1,-1,1,-1,0,1};
        if(Math.abs(pieces[idx])==Math.abs(K)){
            int row = row(idx);
            int col = column(idx);
            for(int i = 0; i<idxChange.length; i++){
                if(idx +idxChange[i]>-1 && idx + idxChange[i]<pieces.length){
                    if(rizzChange[i]+col <8 && rizzChange[i]+col>-1){
                        if(pieces[idx + idxChange[i]]*pieces[idx]<=0){
                            possibleMoves.add(new Move(idx, idx+idxChange[i], false, pieces[idx+idxChange[i]]));
                        }
                    }
                }
            }
        }
    }

    public LinkedList<Move> getPossibleMoves() {
        updatePossibleMoves();
        return possibleMoves;
    }

    private void updatePossibleMoves () {
        possibleMoves = new LinkedList<>();
        for (int i=0;i<pieces.length;i++) {
            int piece = pieces[i];
            int whichColor = numActualMoves % 2 == 0 ? 1 : -1;
            if(piece * whichColor > 0) {
                switch (Math.abs(piece)) {
                    case P -> addPawnMoves(i);
                    case N -> addKnightMoves(i);
                    case B -> addBishopMoves(i);
                    case R -> addRookMoves(i);
                    case Q -> {
                        addBishopMoves(i);
                        addRookMoves(i); // TODO : Prevent queen from castling
                    }
                    case K -> addKingMoves(i);
                }
            }
        }

        // for piece in board
            // if bishop get bishop moves, etc.

        // SPECIAL CASES
            // en passant
            // castling
            // il vaticano

    }
}
