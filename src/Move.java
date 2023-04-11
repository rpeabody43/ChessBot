public class Move {
    private int start; // index in 1d array
    private int end;
    private int capturedPiece;
    private int promoteTo;

    public Move (int start, int end, int capturedPiece) {
        this.start = start;
        this.end = end;
        this.capturedPiece = capturedPiece;
        this.promoteTo = 0;
    }

    public Move (int start, int end, int capturedPiece, int promoteTo) {
        this.start = start;
        this.end = end;
        this.capturedPiece = capturedPiece;
        this.promoteTo = promoteTo;
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

    public int getPromoteTo () {
        return promoteTo;
    }

    public void setPromoteTo (int promoteTo) {
        this.promoteTo = promoteTo;
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Move other = (Move) obj;
        if (other.start != this.start) return false;
        if (other.end != this.end) return false;
        if (other.capturedPiece != this.capturedPiece) return false;

        return true;
    }
}
