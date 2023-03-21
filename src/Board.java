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
    boolean[] kingMoved;

    int promotingIdx;

    public boolean BKiC;
    public boolean WKiC;

    boolean[] rookMoved; //top left, top right, bottom left, bottom right

    public Board () {
        //stores if the kings are in check
        BKiC = false;
        WKiC = false;

        kingMoved = new boolean[]{false,false};
        rookMoved = new boolean[]{false,false,false,false};
        pieces = new int[]{
                -R,-N,-B,-Q,-K,-B,-N,-R, // Black Pieces
                -P,-P,-P,-P,-P,-P,-P,-P, // Black Pawns

                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,

                P, P, P, P, P, P, P, P, // White Pawns
                R, N, B, Q, K, B, N, R // White Pieces
        };

        pastMoves = new Stack<>();
        promotingIdx = -1;
    }

    // For AI player / Internally
    // Board is 1D
    public void makeMove (Move m) {
        int start = m.getStartIdx();
        int end = m.getEndIdx();

        if(end==0) rookMoved[0]=true;
        else if(end==7) rookMoved[1]=true;
        else if(end==55) rookMoved[2]=true;
        else if(end==63) rookMoved[3]=true;

        if(start==0) rookMoved[0]=true;
        else if(start==7) rookMoved[1]=true;
        else if(start==55) rookMoved[2]=true;
        else if(start==63) rookMoved[3]=true;

        if(start==4) kingMoved[0]=true;
        else if(start==60) rookMoved[1]=true;

        if(start == 4|| start==60){
            if(end==2){
                pieces[0] = 0;
                pieces[3] =-R;
            }
            else if(end==6){
                pieces[7] = 0;
                pieces[5] =-R;
            }
            else if(end==58){
                pieces[56] = 0;
                pieces[59] =R;
            }
            else if(end==62){
                pieces[63] = 0;
                pieces[61] =R;
            }

        }


        pieces[end] = pieces[start];
        pieces[start] = 0;

        numActualMoves++;

        pastMoves.push(m);
        possibleMoves.clear();

        switch (Math.abs(pieces[end])) {
            case P -> addPawnMoves(end);
            case N -> addKnightMoves(end);
            case B -> addBishopMoves(end);
            case R -> addRookMoves(end);
            case Q -> {
                addBishopMoves(end);
                addRookMoves(end); // TODO : Prevent queen from castling
            }
            case K -> addKingMoves(end);
        }

        for(Move move: possibleMoves){
            if(numActualMoves%2==0){
                if (pieces[move.getEndIdx()]==K)
                    WKiC = true;
            }else{
                if(pieces[move.getEndIdx()]==-K)
                    BKiC=true;
            }


        }
        possibleMoves.clear();
        System.out.println("white king in check: "+WKiC + " black king in check: "+BKiC);
        if (Math.abs(pieces[end]) > P) return;
        int p = pieces[end];
        if ((p == 1 && row(end) == 0) || (p == -1 && row(end) == 7)) {
            promotingIdx = end;
        }

    }

    public int getPromotingIdx () { return promotingIdx; }

    public void promote (int piece) {
        pieces[promotingIdx] = piece * pieces[promotingIdx];
        promotingIdx = -1;
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
        int start;
        int end;
        // start is the leftmost move a pawn can make (when capturing)
        // end is the rightmost move a pawn can make
        start = 7;
        end = 9;

        int testStart = idx+7*dir;
        int testEnd = idx+9*dir;
        if (Math.abs(column(testStart) - column(idx)) == 7)
            start = 8;
        if (Math.abs(column(testEnd) - column(idx)) == 7)
            end = 8;

        if (testStart >= pieces.length || testStart < 0)
            start = 8;
        if (testEnd >= pieces.length || testEnd < 0)
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
            if (pieces[idx - 8*color] == 0 && pieces[newIdx] == 0)
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

                if (Math.abs(col - column(idx)) == 7) {
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

    private void addRookMoves (int idx) { //do a castle check
        if(Math.abs(pieces[idx]) == Math.abs(R) || Math.abs(pieces[idx])==Math.abs(Q)){

            int row = row(idx);
            int column = column(idx);
            //this probably doesn't work but replit doesn't show red squiggly lines !!
            int[] idxChange = {-1,-8,1,8};
            int[] maxIterations = {column,row,7-column,7-row};
            for(int i=0;i<4;i++) {
                for (int j = 1; j <= maxIterations[i]; j++) {
                    if (pieces[idx + idxChange[i] * j] * pieces[idx] <= 0) {
                        possibleMoves.add(new Move(idx, idx + idxChange[i] * j, false, pieces[idx + idxChange[i] * j]));

                        if (pieces[idx + idxChange[i] * j] * pieces[idx] < 0) break;
                    } else {
                        break;
                    }
                }
            }
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
            //castling
            if(pieces[idx]>0 && kingMoved[1]==false){
              if(rookMoved[3]==false && pieces[61]==0 && pieces[62]==0)
                  possibleMoves.add(new Move(60, 62, false, 0));
              if (rookMoved[2]==false && pieces[59]==0 && pieces[58]==0 && pieces[57]==0)
                possibleMoves.add(new Move(60, 58, false, 0));

            }
            else if(pieces[idx]<0 && kingMoved[0]==false){
                if(rookMoved[1]==false && pieces[5]==0 && pieces[6]==0)
                    possibleMoves.add(new Move(4, 6, false, 0));
                if (rookMoved[0]==false && pieces[1]==0 && pieces[2]==0 && pieces[3]==0)
                    possibleMoves.add(new Move(4, 2, false, 0));


            }






        }

    }

    public LinkedList<Move> getPossibleMoves() {
        updatePossibleMoves();
        return possibleMoves;
    }

    public Move getRandPossibleMove(){
        return possibleMoves.get((int)(Math.random() * possibleMoves.size()));
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
