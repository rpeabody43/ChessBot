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

}
