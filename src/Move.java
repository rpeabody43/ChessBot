public class Move {
    private int start; // index in 1d array
    private int end;
    private int capturedPiece;

    public Move (int start, int end, int capturedPiece) {
        this.start = start;
        this.end = end;
        this.capturedPiece = capturedPiece;
    }

    // returns which piece was captured
    public int getCapturedPiece () {
        return capturedPiece;
    }

    public int getStartIdx () {
        return start;
    }

    public int getEndIdx () {
        return end;
    }
}
