import java.util.LinkedList;
import java.util.List;

public class AI {
    // region constants
    final static int[][] positionalVals = {
            { // Pawns
                    0,  0,  0,  0,  0,  0,  0,  0,
                    50, 50, 50, 50, 50, 50, 50, 50,
                    10, 10, 20, 30, 30, 20, 10, 10,
                    5,  5, 10, 25, 25, 10,  5,  5,
                    0,  0,  0, 20, 20,  0,  0,  0,
                    5, -5,-10,  0,  0,-10, -5,  5,
                    5, 10, 10,-20,-20, 10, 10,  5,
                    0,  0,  0,  0,  0,  0,  0,  0
            },
            { // Knights
                    -50,-40,-30,-30,-30,-30,-40,-50,
                    -40,-20,  0,  0,  0,  0,-20,-40,
                    -30,  0, 10, 15, 15, 10,  0,-30,
                    -30,  5, 15, 20, 20, 15,  5,-30,
                    -30,  0, 15, 20, 20, 15,  0,-30,
                    -30,  5, 10, 15, 15, 10,  5,-30,
                    -40,-20,  0,  5,  5,  0,-20,-40,
                    -50,-40,-30,-30,-30,-30,-40,-50,
            },
            { // Bishops
                    -20,-10,-10,-10,-10,-10,-10,-20,
                    -10,  0,  0,  0,  0,  0,  0,-10,
                    -10,  0,  5, 10, 10,  5,  0,-10,
                    -10,  5,  5, 10, 10,  5,  5,-10,
                    -10,  0, 10, 10, 10, 10,  0,-10,
                    -10, 10, 10, 10, 10, 10, 10,-10,
                    -10,  5,  0,  0,  0,  0,  5,-10,
                    -20,-10,-10,-10,-10,-10,-10,-20,
            },
            { // Rooks
                    0,  0,  0,  0,  0,  0,  0,  0,
                    5, 10, 10, 10, 10, 10, 10,  5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    0,  0,  0,  5,  5,  0,  0,  0
            },
            { // Queens
                    -20,-10,-10, -5, -5,-10,-10,-20,
                    -10,  0,  0,  0,  0,  0,  0,-10,
                    -10,  0,  5,  5,  5,  5,  0,-10,
                    -5,  0,  5,  5,  5,  5,  0, -5,
                    0,  0,  5,  5,  5,  5,  0, -5,
                    -10,  5,  5,  5,  5,  5,  0,-10,
                    -10,  0,  5,  0,  0,  0,  0,-10,
                    -20,-10,-10, -5, -5,-10,-10,-20
            },
            { // King
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -20,-30,-30,-40,-40,-30,-30,-20,
                    -10,-20,-20,-20,-20,-20,-20,-10,
                    20, 20,  0,  0,  0,  0, 20, 20,
                    20, 30, 10,  0,  0, 10, 30, 20
            }
    };

    private static final int CHECKMATEEVAL = 1000000;
    //endregion

    private Board board;
    private int depth;

    public AI (Board board, int depth) {
        this.board = board;
        this.depth = depth;
    }

    public int evaluateCurrentPos(){
        int[] pieces = board.pieces;

        int eval = 0;
        for (int i = 0; i < pieces.length; i++) {
            int piece = pieces[i];
            int color = piece > 0 ? 1 : piece < 0 ? -1 : 0;
            int materialVal = switch (Math.abs(piece)) {
                case Board.P -> 100;
                case Board.N, Board.B -> 300;
                case Board.R -> 500;
                case Board.Q -> 900;
                case Board.K -> 1000000;
                default -> 0;
            };

            // positional code
            int positionalVal = 0;
            if (piece != 0) {
                int positionalIdx = color > 0 ? i : 7 * Board.column(i) + Board.row(i);
                positionalVal = positionalVals[Math.abs(piece)-1][positionalIdx];
            }

            eval += color * (materialVal + positionalVal);
        }
        return eval;
    }
    public int evalMove(Move move, int dep, int alpha, int beta){
        board.makeMove(move);
        int res;
        List<Move> possibleMoves = new LinkedList<>(board.getPossibleMoves());
        int gameState = board.getGameState();
        // Looking for checkmate
        // Slightly weighted toward shorter forced mate
        if (gameState == Board.BLACKWINS) res = -CHECKMATEEVAL + (depth - dep);
        else if (gameState == Board.WHITEWINS) res = CHECKMATEEVAL - (depth - dep);
        else if (gameState == Board.DRAW) res = 0;

        //copied from https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
        else if(dep<=0) res = evaluateCurrentPos();
        else if(board.whiteToMove()){ //maximizing player is white
            int value = -1000000000;
            for(Move m : possibleMoves){
                value = Math.max(value, evalMove(m, dep-1, alpha, beta));
                if (value>beta) break;
                alpha = Math.max(alpha,value);
            }
            res=value;
        }else{
            int value = 1000000000;
            for(Move m : possibleMoves){
                value = Math.min(value, evalMove(m, dep-1, alpha, beta));
                if(value<alpha) break;
                beta = Math.min(beta,value);
            }
            res=value;
        }
        board.undoMove();
        return res;
    }


    public Move bestNextMove() {
        long startTime = System.currentTimeMillis();

        List<Move> possibleMoves = new LinkedList<>(board.getPossibleMoves());
        int color = board.whiteToMove() ? 1 : -1;
        Move bestMove = null;
        int bestEval = -1000000000;

        for(Move m : possibleMoves) {
            int eval = evalMove(m, depth, -1000000000, 1000000000);
//            System.out.println("eval: "+eval + ", possible moves: " + possibleMoves.size());
            if(eval*color>bestEval || bestMove == null) {
                bestMove = m;
                bestEval = eval*color;
            }
        }

        double totalTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
        System.out.println(totalTimeSeconds + "s");

        return bestMove;

    }

}
