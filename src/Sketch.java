import processing.core.PApplet;

public class Sketch extends PApplet {

    private Chess chess;

    public static void main (String[] args) {
        String[] processingArgs = {"ChessAI"};
        Sketch sketch = new Sketch();
        PApplet.runSketch(processingArgs, sketch);
    }

    public Sketch () {
        this.chess = new Chess(new HPlayer(), new AIPlayer());
    }

    @Override
    public void settings () {
        size(512, 512);
    }

    private void drawBoard () {
        // BACKGROUND
        fill(20, 100, 135);
        noStroke();
        background(237, 226, 199);
        float squareWidth = width / 8f;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1)
                    square(squareWidth * i, squareWidth * j, squareWidth);
            }
        }
        int[][] currentBoard = chess.currentBoard().pieces;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                int piece = currentBoard[i][j];
                char letter = switch (Math.abs(piece)) {
                    case Board.P: yield 'P';
                    case Board.N: yield 'N';
                    case Board.B: yield 'B';
                    case Board.R: yield 'R';
                    case Board.Q: yield 'Q';
                    case Board.K: yield 'K';
                    default: yield ' ';
                };
                stroke(0);
                textAlign(CENTER, CENTER);
                fill(piece > 0 ? 255 : 0);
                textSize(25);
                text(letter, squareWidth*j + squareWidth/2, squareWidth*i + squareWidth/2);
            }
    }

    // -- GAMELOOP --
    @Override
    public void draw () {
        drawBoard();
    }

    @Override
    public void mouseClicked() {
        return; // STUB
        // Something like
        // Set variables x1, y1 to first mouse click
        // Set x2, y2 to second mouse click
        // Pass that to chess to check if a valid move
    }
}
