public class Chess {
    Player p1;
    Player p2;

    Board board;

    public Chess (Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
        board = new Board();
    }

}
