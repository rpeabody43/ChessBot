public class Move {
    private int start;
    private int end;
    private boolean check;
    private int capturedPiece;

    public Move (int start, int end, boolean check, int capturedPiece) {
        this.start = start;
        this.end = end;
        this.check = check;
        this.capturedPiece = capturedPiece;
    }

    public boolean isCheck () {
        return check;
    }

    public int getCapturedPiece () {
        return capturedPiece;
    }
}
