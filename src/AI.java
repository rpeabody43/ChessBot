import java.util.LinkedList;

public class AI {

    private Board board;

    public AI (Board board) {
        this.board = board;
    }

    public int evaluateCurrentPos(){
        int[] pieces = board.pieces;

        int eval = 0;
        for(int i =0; i< pieces.length; i++) {
            int color = pieces[i]>0?1:pieces[i]<0?-1:0;
            int materialVal = switch(Math.abs(pieces[i])){
                case Board.P -> 100;
                case Board.N -> 300;
                case Board.B -> 300;
                case Board.Q -> 900;
                case Board.R -> 500;
                case Board.K -> 1000000;
                default -> 0;
            };
            // positional code
            int positionalVal = 0;
            eval+=color*(materialVal+positionalVal);
        }
        return eval;
    }
    public int evalMove(Move move, int dep, int alpha, int beta){
        LinkedList<Move> possibleMoves = board.getPossibleMoves();

        board.makeMove(move);
        int res = 0;
        //copied from https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
        if(dep<=0) res = evaluateCurrentPos();
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


    public Move bestNextMove(){
        LinkedList<Move> possibleMoves = board.getPossibleMoves();
        int color = board.whiteToMove() ? 1 : -1;
        Move bestMove = null;
        int bestEval = -1000000000;

        for(Move m : possibleMoves){
            int eval = evalMove(m, 5, -1000000000, 1000000000);
            if(eval*color>bestEval) {
                bestMove = m;
                bestEval = eval;
            }
        }
        return bestMove;
    }

}
