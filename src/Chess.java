import java.util.LinkedList;

public class Chess {
    private Player p1;
    private Player p2;

    private Board board;

    public Chess (Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
        board = new Board();
    }

    public Board currentBoard () {
        return board;
    }

    public LinkedList<Move> possibleMovesAtPosition (int idx) {
        LinkedList<Move> allMoves = board.getPossibleMoves();
        LinkedList<Move> ret = new LinkedList<>();

        for (Move m : allMoves) {
            if (m.getStartIdx() == idx)
                ret.push(m);
        }
        return ret;
    }
    public void makeMove(int start, int end){
        currentBoard().pieces[end]= currentBoard().pieces[start];
        currentBoard().pieces[start]=0;
        currentBoard().numActualMoves++;
    }

}
