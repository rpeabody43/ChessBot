import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    public static final int PLAYING = 0;
    // Maybe add draw types? (stalemate, insufficient material, etc.)
    public static final int DRAW = 1;
    public static final int WHITEWINS = 2;
    public static final int BLACKWINS = 3;

    private int gameState;
    private int movesWithoutCap; // 50 moves without captures = draw
    private int whitePieceValue;
    private int whitePieceCount;
    private int blackPieceValue;
    private int blackPieceCount;


    private int repeatedWhiteMoves;
    private int repeatedBlackMoves;
    private Move lastWhiteMove;
    private Move lastBlackMove;


    private LinkedList<Move> possibleMoves;
    private Stack<Move> pastMoves;

    int numActualMoves;
    int[] pieces;
    boolean[] kingMoved;

    HashSet<Integer> pinnedPieces;
    HashSet<Integer> possibleBlocks;

    public boolean blackInCheck;
    private int blackKing;
    public boolean whiteInCheck;
    private int whiteKing;

    boolean[] rookMoved; //top left, top right, bottom left, bottom right

    public Board() {
        //stores if the kings are in check
        blackInCheck = false;
        whiteInCheck = false;

        kingMoved = new boolean[]{false, false};
        rookMoved = new boolean[]{false, false, false, false};
        pieces = new int[]{
                -R, -N, -B, -Q, -K, -B, -N, -R, // Black Pieces
                -P, -P, -P, -P, -P, -P, -P, -P, // Black Pawns

                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,

                P, P, P, P, P, P, P, P, // White Pawns
                R, N, B, Q, K, B, N, R // White Pieces
        };

        blackKing = 4;
        whiteKing = 60;

        pastMoves = new Stack<>();
        possibleBlocks = new HashSet<>();

        updatePossibleMoves();
        pinnedPieces = new HashSet<>();

        whitePieceValue = 8*P + 2*N + 2*B + 2*R + Q + K;
        whitePieceCount = 16;
        blackPieceValue = 8*P + 2*N + 2*B + 2*R + Q + K;
        blackPieceCount = 16;

        gameState = PLAYING;
    }

    private int tileAttackedByPiece(int color, int attackingPiece, Iterable<Integer> iter) {
        for (int newIdx : iter) {
            int p = pieces[newIdx];
            if (p == 0) continue;

            // queen is essentially a bishop and rook
            boolean queenMove = attackingPiece == B || attackingPiece == R;
            boolean attacked = p == -color * attackingPiece || (queenMove && p == -color*Q);
            if (attacked) return newIdx;
            // Knight is the only one that can't end early
            else if (attackingPiece != N) return -1;
        }
        return -1;
    }

    private ArrayList<Integer> piecesAttackingTile (int idx, int color) {
        ArrayList<Integer> attackingPieces = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            // BISHOP MOVES
            int bishopMove = tileAttackedByPiece(color, B, DiagIterator.iter(idx, i));
            if (bishopMove > -1) {
                attackingPieces.add(bishopMove);
            }

            // ROOK MOVES
            int rookMove = tileAttackedByPiece(color, R, StraightIterator.iter(idx, i));
            if (rookMove > -1) {
                attackingPieces.add(rookMove);
            }
        }
        // KNIGHT MOVES
        int knightMove = tileAttackedByPiece(color, N, KnightIterator.iter(idx));
        if (knightMove > -1) {
            // This will only add one knight per square,
            // but I don't think that will be a problem functionally
            attackingPieces.add(knightMove);
        }

        // PAWN MOVES
        int[] pawnDeltas = {9, 7}; // Handle up/down on color
        for (int delta : pawnDeltas) {
            int newIdx = idx + delta;
            if (newIdx >= pieces.length || newIdx < 0) continue;
            int newCol = column(newIdx);
            if (Math.abs(newCol - column(idx)) == 7) continue;
            if (pieces[newIdx] == -color * P) attackingPieces.add(newIdx);
        }

        return attackingPieces;
    }

    private boolean tileSafe(int idx, int color) {
        ArrayList<Integer> attackingPieces = piecesAttackingTile(idx, color);
        return attackingPieces.size() == 0;
    }

    private HashSet<Integer> blockIdxs (int kingIdx, int attackingIdx) {
        HashSet<Integer> ret = new HashSet<>();
        ret.add(attackingIdx); // Capturing the attacker is always an option
        int delta = attackingIdx - kingIdx;
        int numSquaresHorz = Math.abs(column(attackingIdx) - column(kingIdx));
        int numSquaresVert = Math.abs(row(attackingIdx) - row(kingIdx));

        int attackingPiece = Math.abs(pieces[attackingIdx]);
        // A queen functionally becomes a bishop/rook here
        if (attackingPiece == Q)
            attackingPiece = numSquaresHorz == 0 ? R : B;
        if (attackingPiece == N || attackingPiece == P) {
            // Knights/Pawns can only be "blocked" by being captured
            return ret;
        }

        if (attackingPiece == B) {
            int direction = switch (delta / numSquaresHorz) {
                case -9 -> 0;
                case -7 -> 1;
                case 7 -> 2;
                case 9 -> 3;

                default -> 4; // This should never happen but hey it might
            };

            for (int idx : DiagIterator.iter(kingIdx, direction)) {
                if (idx == attackingIdx) break;
                ret.add(idx);
            }
        }
        if (attackingPiece == R) {
            int direction;
            if (numSquaresVert == 0) {
                direction = delta > 0 ? 1 : -1;
            }
            else {
                direction = delta > 0 ? 8 : -8;
            }

            for (int idx : StraightIterator.iter(kingIdx, direction)) {
                if (idx == attackingIdx) break;
                ret.add(idx);
            }
        }

        return ret;
    }

    public boolean whiteToMove () {
        return (numActualMoves % 2 == 0);
    }

    public boolean blackToMove () {
        return (numActualMoves % 2 == 1);
    }

    public void makeMove(Move m) {
        int start = m.getStartIdx();
        int end = m.getEndIdx();

        int endPiece = pieces[end];

        if ((start == 4 || start == 60) && Math.abs(pieces[start])==K) {
            if (end == 2) {
                pieces[0] = 0;
                pieces[3] = -R;
                rookMoved[0] = true;
            } else if (end == 6) {
                pieces[7] = 0;
                pieces[5] = -R;
                rookMoved[1] = true;
            } else if (end == 58) {
                pieces[56] = 0;
                pieces[59] = R;
                rookMoved[2] = true;
            } else if (end == 62) {
                pieces[63] = 0;
                pieces[61] = R;
                rookMoved[3] = true;
            }

        }

        pieces[end] = pieces[start];
        if (pieces[end] == K) whiteKing = end;
        else if (pieces[end] == -K) blackKing = end;
        pieces[start] = 0;

        if (m.getPromoteTo() != 0) {
            pieces[end] = m.getPromoteTo();
        }

        pastMoves.push(m);
        possibleMoves.clear();
        numActualMoves++;

        int kingIdx = whiteToMove() ? whiteKing : blackKing;
        int kingColor = whiteToMove() ? 1 : -1;
        ArrayList<Integer> checkingPieces = piecesAttackingTile(kingIdx, kingColor);
//        System.out.println(checkingPieces.size());
        whiteInCheck = false;
        blackInCheck = false;
        possibleBlocks.clear();
        if (checkingPieces.size() > 0) {
            if (whiteToMove())
                whiteInCheck = true;
            else
                blackInCheck = true;

            // Blocks are only possible if one piece is attacking
            if (checkingPieces.size() == 1)
                possibleBlocks = blockIdxs(kingIdx, checkingPieces.get(0));
        }
        //System.out.println("white king in check: " + whiteInCheck + " black king in check: " + blackInCheck);


        int p = pieces[end];

        if (Math.abs(p) == R && m.isFirstMove()) {
            int i = switch (start) {
                case 0 -> 0;
                case 7 -> 1;
                case 56 -> 2;
                case 63 -> 3;
                default -> -1;
            };
            rookMoved[i] = true;
        }
        else if (Math.abs(endPiece) == R) {
            int i = switch (end) {
                case 0 -> 0;
                case 7 -> 1;
                case 56 -> 2;
                case 63 -> 3;
                default -> -1;
            };
            if (i > -1)
                rookMoved[i] = true;
        }

        if (start == 4) kingMoved[0] = true;
        else if (start == 60) rookMoved[1] = true;

        updatePossibleMoves();
        //System.out.println(possibleMoves.size());

        if (Math.abs(p) == P) {
            int delta = Math.abs(start - end);
            // If en passant
            if ((delta == 7 || delta == 9) && endPiece == 0) {
                int color = pieces[end];
                pieces[end + 8 * color] = 0;
            }
        }

        int capturedPiece = m.getCapturedPiece();
        if (capturedPiece == 0) movesWithoutCap++;
        else movesWithoutCap = 0;

        if (whiteToMove()) {
            if (capturedPiece != 0) {
                blackPieceCount--;
                blackPieceValue--;
            }

            if (m.equals(lastWhiteMove)) repeatedWhiteMoves++;
            else {
                lastWhiteMove = m;
                repeatedWhiteMoves = 1;
            }
        }
        else {
            if (capturedPiece != 0) {
                whitePieceCount++;
                whitePieceValue++;
            }

            if (m.equals(lastBlackMove)) repeatedBlackMoves++;
            else {
                lastBlackMove = m;
                repeatedBlackMoves = 1;
            }
        }


        gameState = getGameState();
        String s = switch (gameState) {
            case PLAYING -> "Playing";
            case DRAW -> "Draw";
            case WHITEWINS -> "White Wins";
            case BLACKWINS -> "Black Wins";
            default -> "Unknown game state" + gameState;
        };
        //System.out.println(s);

    }

    public int getGameState () {
        // CHECKMATE
        if (possibleMoves.size() == 0) {
            if (whiteToMove() && whiteInCheck)
                return BLACKWINS;
            else if (blackToMove() && blackInCheck)
                return WHITEWINS;
            else
                return DRAW; // Stalemate
        }

        // REPEATED MOVES
        if (repeatedWhiteMoves == 3 && repeatedBlackMoves == 3)
            return DRAW;

        // Insufficient material occurs when both players have only
        // 1. King
        // 2. King and Knight
        // 3. King and Bishop
        if (whitePieceCount <= 2 && blackPieceCount <= 2) {
            boolean whiteInsufficient = (whitePieceValue <= K + B && whitePieceValue != K + P);
            boolean blackInsufficient = (blackPieceValue <= K + B && blackPieceValue != K + P);
            if (whiteInsufficient && blackInsufficient) return DRAW;
        }

        return PLAYING;
    }

    // checks if a move is valid
    private boolean moveValid(Move move) {
        return possibleMoves.contains(move); // STUB
    }

    public Move lastMove () {
        return pastMoves.peek();
    }

    public static int row(int idx) {
        return idx / 8;
    }

    public static int column(int idx) {
        return idx % 8;
    }

    public LinkedList<Move> possibleMovesAtPosition (int idx) {
        LinkedList<Move> ret = new LinkedList<>();

        for (Move m : possibleMoves) {
            if (m.getStartIdx() == idx)
                ret.push(m);
        }
        return ret;
    }

    private void addPossibleMove (LinkedList<Move> moveList, int start, int end, int capturedPiece, int promoteTo) {
        if (possibleBlocks.size() > 0 && !possibleBlocks.contains(end)) return;
        Move m = new Move(start, end, capturedPiece, promoteTo);
        if (capturedPiece > 0)
            moveList.push(m);
        else
            moveList.add(m);
    }

    private void addPossibleMove (LinkedList<Move> moveList, int start, int end, int capturedPiece) {
        // If in check and this move doesn't block, don't add the move
        if (possibleBlocks.size() > 0 && !possibleBlocks.contains(end)) return;
        Move m = new Move(start, end, capturedPiece);
        moveList.add(m);
    }

    private void addPossibleMove (LinkedList<Move> moveList, int start, int end, int capturedPiece, boolean firstMove) {
        if (possibleBlocks.size() > 0 && !possibleBlocks.contains(end)) return;
        Move m = new Move(start, end, capturedPiece, firstMove);
        moveList.add(m);
    }

    private LinkedList<Move> getPawnMoves(int idx) {
        LinkedList<Move> ret = new LinkedList<>();

        int color = (pieces[idx] > 0) ? 1 : -1;
        int dir = -color; // White moves up, black moves down
        int start;
        int end;
        // start is the leftmost move a pawn can make (when capturing)
        // end is the rightmost move a pawn can make
        start = 7;
        end = 9;

        int testStart = idx + 7 * dir;
        int testEnd = idx + 9 * dir;
        if (Math.abs(column(testStart) - column(idx)) == 7)
            start = 8;
        if (Math.abs(column(testEnd) - column(idx)) == 7)
            end = 8;

        if (testStart >= pieces.length || testStart < 0)
            start = 8;
        if (testEnd >= pieces.length || testEnd < 0)
            end = 8;


        for (int change = start * dir; Math.abs(change) <= Math.abs(end); change += dir) {
            int p = pieces[idx + change];
            int capturedPiece;
            if (change == 8 || change == -8) { // Straight up/down
                if (p != 0) continue;
                else capturedPiece = 0;
            } else {
                if (p * color < 0) // If different colors

                    capturedPiece = p;
                else continue;
            }
            if (row(idx + change) == 0 || row(idx + change) == 7) { // Promoting
                for (int i = N; i <= Q; i++)
                    addPossibleMove(ret, idx, idx + change, capturedPiece, i * color);
            }
            else {
                addPossibleMove(ret, idx, idx + change, capturedPiece);
            }
        }

        // Starting move
        // If on the starting row for that color
        if ((row(idx) == 6 && color == 1) || (row(idx) == 1 && color == -1)) {
            int newIdx = idx - 16 * color;
            // If space is empty
            if (pieces[idx - 8 * color] == 0 && pieces[newIdx] == 0)
                addPossibleMove(ret, idx, newIdx, 0);
        }

        // En Passant
        // If advanced to 5th rank
        if ((row(idx) == 4 && color == -1) || (row(idx) == 3 && color == 1)) {
            Move lastMove = pastMoves.peek();
            // If the last move was a pawn move immediately next to this pawn
            if (Math.abs(lastMove.getEndIdx() - idx) == 1 && Math.abs(pieces[lastMove.getEndIdx()]) == P) {
                // If the last move was a 2 square pawn move
                if (Math.abs(row(lastMove.getEndIdx()) - row(lastMove.getStartIdx())) == 2) {
                    int capturingIdx = lastMove.getEndIdx();
                    addPossibleMove(ret, idx, capturingIdx - 8 * color, 0);
                    // Don't pass in any captured piece because otherwise it would be indistinguishable
                }
            }
        }
        return ret;
    }

    private LinkedList<Move> getKnightMoves(int idx) {
        LinkedList<Move> ret = new LinkedList<>();

        int[] idxChange = {-17, -15, -10, -6, 6, 10, 15, 17};
        int[] hozChange = {-1, 1, -2, 2, -2, 2, -1, 1};
        if (Math.abs(pieces[idx]) == Math.abs(N)) {

            int column = column(idx);

            for (int newIdx : KnightIterator.iter(idx))
                if (pieces[newIdx] * pieces[idx] <= 0)
                    addPossibleMove(ret, idx, newIdx, pieces[newIdx]);
//            for (int i = 0; i < idxChange.length; i++) {
//                if (idx + idxChange[i] > -1 && idx + idxChange[i] < pieces.length) {
//                    if (hozChange[i] + column < 8 && hozChange[i] + column > -1)
//                }
//            }
        }
        return ret;
    }

    private LinkedList<Move> getBishopMoves(int idx) {
        LinkedList<Move> ret = new LinkedList<>();
        int color = (pieces[idx] > 0) ? 1 : -1;

        for (int i = 0; i <= 3; i++) {
            for (int newIdx : DiagIterator.iter(idx, i)) {
                // If an open space or an opposite color piece, add a move
                if (pieces[newIdx] * pieces[idx] <= 0) {
                    addPossibleMove(ret, idx, newIdx, pieces[newIdx]);

                    // add no more moves if it's a capture
                    if (pieces[newIdx] * pieces[idx] < 0) break;
                } else {
                    break;
                }
            }
        }
        return ret;
    }

    private LinkedList<Move> getRookMoves(int idx) {
        LinkedList<Move> ret = new LinkedList<>();
        int rookStart = switch (idx) {
            case 0 -> 0;
            case 7 -> 1;
            case 56 -> 2;
            case 63 -> 3;
            default -> -1;
        };

        boolean firstMove = rookStart > -1 && !rookMoved[rookStart];

        //this probably doesn't work but replit doesn't show red squiggly lines !!
        for (int i = 0; i < 4; i++) {
            for (int newIdx : StraightIterator.iter(idx, i)) {
                if (pieces[newIdx] * pieces[idx] <= 0) {
                    addPossibleMove(ret, idx, newIdx, pieces[newIdx], firstMove);

                    if (pieces[newIdx] * pieces[idx] < 0) break;
                } else {
                    break;
                }
            }
        }
        return ret;
    }

    private LinkedList<Move> addPinnedPieceMoves(int idx){
        int row = row(idx);
        int col = column(idx);


        int color = pieces[idx]>0?1:-1;
        int kingIdx = color==-1?blackKing:whiteKing;

        int krow = row(kingIdx);
        int kcol = column(kingIdx);

        LinkedList<Move> res = new LinkedList<>();
        if((col == kcol || row == krow) && (Math.abs(pieces[idx]) ==R || Math.abs(pieces[idx]) ==Q)) { // vertical
            int[] dirs = {0,2};
            if(col == kcol)
                dirs = new int[]{1,3};
            for(int dir: dirs) {
                for (int tile : StraightIterator.iter(idx, dir)) {
                    if (pieces[tile] * color > 0) break;
                    res.add(new Move(idx, tile, pieces[tile]));
                    if (pieces[tile] * color < 0) break;
                }
            }

        }
        else if(((kingIdx -idx) % 9==0 || (kingIdx -idx) % 7==0) && (Math.abs(pieces[idx]) ==B || Math.abs(pieces[idx]) ==Q)){
            int[] dirs = {1,2};
            if((kingIdx -idx) % 9==0 )
                dirs = new int[]{0,3};
            for(int dir: dirs) {
                for (int tile : DiagIterator.iter(idx, dir)) {
                    if (pieces[tile] * color > 0) break;
                    res.add(new Move(idx, tile, pieces[tile]));
                    if (pieces[tile] * color < 0) break;
                }
            }

        }
        else if(Math.abs(pieces[idx])==P){
            if((col == kcol || row == krow)){ //if pawn pinned by rook
                int moveTo = idx+(color==-1?8:-8);
                if(pieces[moveTo]==0)
                    res.add(new Move(idx,moveTo,pieces[moveTo]));
            }else{ //pawn pinned by bishop
                int dir;
                if((kingIdx -idx) % 9==0 )
                    dir = color==-1?9:-9;
                else
                    dir = color==-1?7:-7;
                if(pieces[idx+dir]*color<0) {
                    //this theoretically should only be true when pinned by bishop which pawn can take
                    if(pieces[idx+dir]*color<0)
                        res.add(new Move(idx,idx+dir,pieces[idx+dir]));
                }else{
                    //this means bishop is pinning on a longer diag, only possible move the pawn theoretically can make here is enpassant
                    //warning my logic might be wrong
                    Move lastMove = pastMoves.peek();
                    int endPos = idx+(dir==-9||dir==7?-1:1);
                    if(Math.abs(pieces[lastMove.getEndIdx()])==P && Math.abs(lastMove.getEndIdx()- lastMove.getStartIdx())==16 && lastMove.getEndIdx()==endPos){
                        res.add(new Move(idx,endPos,pieces[endPos]));
                    }
                }
            }
        }

        return res;
    }



    private HashMap<Integer,Integer> getPinnedPieces(int color){
        HashMap<Integer,Integer> r = new HashMap<>();
        int kingLoc = color==-1?blackKing:whiteKing;
        // Bishop pin checker
        for(int i =0; i<4;i++){
            int pinnedIdx =-1;
            int pinningPiece=-1;
            for(int dTile: DiagIterator.iter(kingLoc,i)){
                if(pieces[dTile]*color > 0){
                    if(pinnedIdx!=-1){
                        pinnedIdx=-1;
                        break;
                    }
                    pinnedIdx = dTile;
                }
                else if(pieces[dTile]*color < 0){
                    pinningPiece=dTile;
                    if(Math.abs(pieces[dTile])!= B && Math.abs(pieces[dTile])!= Q){
                        pinnedIdx=-1;
                    }
                    break;
                }
            }
            if(pinnedIdx != -1 && pinningPiece!=-1)
                r.put(pinnedIdx,pinningPiece);
            // Rook pin checker
            pinnedIdx =-1;
            pinningPiece=-1;
            for(int sTile: StraightIterator.iter(kingLoc,i)){
                if(pieces[sTile]*color > 0){
                    if(pinnedIdx!=-1){
                        pinnedIdx=-1;
                        break;
                    }
                    pinnedIdx = sTile;
                }
                else if(pieces[sTile]*color < 0){
                    pinningPiece=sTile;
                    if(Math.abs(pieces[sTile])!= R && Math.abs(pieces[sTile])!= Q){
                        pinnedIdx=-1;
                    }
                    break;
                }
            }
            if(pinnedIdx != -1 && pinningPiece!=-1)
                r.put(pinnedIdx,pinningPiece);
        }

        return r;
    }


    private LinkedList<Move> getKingMoves(int idx) {
        LinkedList<Move> ret = new LinkedList<>();

        int color = (pieces[idx] > 0) ? 1 : -1;
        int[] idxChange = {-9, -8, -7, -1, 1, 7, 8, 9};
        int[] rizzChange = {-1, 0, 1, -1, 1, -1, 0, 1}; // hoRIZZontal change

        int kingIdx = color == -1 ? 0 : 1;

        if (Math.abs(pieces[idx]) == Math.abs(K)) {
            int row = row(idx);
            int col = column(idx);
            for (int i = 0; i < idxChange.length; i++) {
                if (idx + idxChange[i] > -1 && idx + idxChange[i] < pieces.length) {
                    if (rizzChange[i] + col < 8 && rizzChange[i] + col > -1) {
                        if (pieces[idx + idxChange[i]] * pieces[idx] <= 0) {
                            if (tileSafe(idx+idxChange[i], color))
                                ret.add(new Move(idx, idx + idxChange[i], pieces[idx + idxChange[i]], !kingMoved[kingIdx]));
                        }
                    }
                }
            }
            //castling
            if (pieces[idx] > 0 && kingMoved[1] == false) {
                if (rookMoved[3] == false && pieces[61] == 0 && pieces[62] == 0){
                    if(tileSafe(60,1) && tileSafe(61,1) && tileSafe(62,1))
                        ret.add(new Move(60, 62, 0, true));
                }


                if (rookMoved[2] == false && pieces[59] == 0 && pieces[58] == 0 && pieces[57] == 0){
                    if(tileSafe(60,1) && tileSafe(59,1) && tileSafe(58,1))
                        ret.add(new Move(60, 58, 0, true));
                }


            } else if (pieces[idx] < 0 && kingMoved[0] == false) {
                if (rookMoved[1] == false && pieces[5] == 0 && pieces[6] == 0){
                    if(tileSafe(4,-1) && tileSafe(5,-1) && tileSafe(6,-1))
                        ret.add(new Move(4, 6, 0, true));
                }
                if (rookMoved[0] == false && pieces[1] == 0 && pieces[2] == 0 && pieces[3] == 0){
                    if(tileSafe(2,-1) && tileSafe(3,-1) && tileSafe(4,-1))
                        ret.add(new Move(4, 2, 0, true));
                }
            }
        }
        return ret;
    }

    public LinkedList<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public Move getRandPossibleMove() {
        return possibleMoves.get((int) (Math.random() * possibleMoves.size()));
    }

    private void updatePossibleMoves() {
        possibleMoves = new LinkedList<>();
        HashMap<Integer,Integer> pinnedPieces = getPinnedPieces(numActualMoves%2==0?1:-1);
        //System.out.println(numActualMoves);
        //System.out.println(pinnedPieces);
        //HashSet<Integer> temp = getPinnedPieces(numActualMoves%2==0?1:-1);
//        System.out.println(numActualMoves);
//        System.out.println(temp);
        for (int i = 0; i < pieces.length; i++) {
            if(pinnedPieces.containsKey(i)){
                possibleMoves.addAll(addPinnedPieceMoves(i));
//                System.out.println(addPinnedPieceMoves((i)));
                continue;
            }
            int piece = pieces[i];
            int whichColor = whiteToMove() ? 1 : -1;
            if (piece * whichColor > 0) {
                LinkedList<Move> movesAtIdx =  switch (Math.abs(piece)) {
                    case P -> getPawnMoves(i);
                    case N -> getKnightMoves(i);
                    case B, Q -> getBishopMoves(i); // We add rook moves after
                    case R -> getRookMoves(i);
                    case K -> getKingMoves(i);
                    default -> new LinkedList<>();
                };
                if (Math.abs(piece) == Q) movesAtIdx.addAll(getRookMoves(i));

                possibleMoves.addAll(movesAtIdx);
            }
        }
    }
    public void undoMove(){
        Move lastMove = pastMoves.pop();
        int start = lastMove.getStartIdx();
        int end = lastMove.getEndIdx();
        int capturedPiece = lastMove.getCapturedPiece();

        int movedPiece = pieces[end];
        int color = movedPiece < 0 ? -1 : 1;

        // Undo the move
        pieces[start] = movedPiece;
        pieces[end] = capturedPiece;

        if (pieces[start] == K)
            whiteKing = start;
        else if (pieces[start] == -K)
            blackKing = start;

        // En passant
        if (Math.abs(movedPiece) == P && capturedPiece == 0) {
            int delta = end - start;
            if (Math.abs(delta) % 8 != 0) 
                pieces[(delta == -9*color) ? start-1*color : start+1*color] = P * -color;
        }   
        // promotion, if this is bugged then it's because I'm getting an infusion and im writing this on my phone
        if (lastMove.getPromoteTo()!=0){
            pieces[start]=P*color;
        }

        int startPieceType = Math.abs(pieces[start]);

        //castle
        if(startPieceType==K && Math.abs(end-start)==2){
            pieces[start+(end-start)/2]=0;
            pieces[end+((end-start)<0 ? -2 : 1)] = R*color;
            kingMoved[color==-1?0:1]=false;
            rookMoved[end==2?0 : end==6?1 : end==62?3:2]=false;
        }
        // rook moved
        if(startPieceType==R){
            int i = switch (start) {
                case 0 -> 0;
                case 7 -> 1;
                case 56 -> 2;
                case 63 -> 3;
                default -> -1;
            };
            if (i > -1 && lastMove.isFirstMove())
                rookMoved[i] = false;
        }
        //king moved
        if(startPieceType==K){
            kingMoved[pieces[start] == -K ? 0 : 1] = !lastMove.isFirstMove();
        }

        numActualMoves--;
        updatePossibleMoves(); //idk if this is necessary
        //potentially other things

    }
}
