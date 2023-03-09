public class Move {
    private int start; // index in 1d array
    private int end;
    private boolean check;
    private int capturedPiece;

    public Move (int start, int end, boolean check, int capturedPiece) {
        this.start = start;
        this.end = end;
        this.check = check;
        this.capturedPiece = capturedPiece;
    }

    // returns check
    public boolean isCheck () {
        return check;
    }

    // returns which piece was captured
    public int getCapturedPiece () {
        return capturedPiece;
    }
}
