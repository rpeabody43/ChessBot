public class AI {

    private Board board;

    public AI (Board board) {
        this.board = board;
    }

    public int evaluate(){
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

}
