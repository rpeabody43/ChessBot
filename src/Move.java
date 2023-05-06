public class Move {
    private final int start; // index in 1d array
    private final int end;
    private final int capturedPiece;


    private final boolean firstMove;
    private int promoteTo;

    public Move (int start, int end, int capturedPiece) {
        this.start = start;
        this.end = end;
        this.capturedPiece = capturedPiece;
        this.promoteTo = 0;
        this.firstMove = false;
    }

    public Move (int start, int end, int capturedPiece, int promoteTo) {
        this.start = start;
        this.end = end;
        this.capturedPiece = capturedPiece;
        this.promoteTo = promoteTo;
        this.firstMove = false;
    }

    public Move (int start, int end, int capturedPiece, boolean firstMove) {
        this.start = start;
        this.end = end;
        this.capturedPiece = capturedPiece;
        this.promoteTo = 0;
        this.firstMove = firstMove;
    }

    @Override
    public String toString () {
        return start + " -> " + end;
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

    public boolean isFirstMove() {
        return firstMove;
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
