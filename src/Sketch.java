import processing.core.PApplet;

public class Sketch extends PApplet {

    private Chess chess;

    public static void main (String[] args) {
        String[] processingArgs = {"ChessAI"};
        Sketch sketch = new Sketch();
        PApplet.runSketch(processingArgs, sketch);
    }

    @Override
    public void settings () {
        size(512, 512);
    }

    @Override
    public void draw () {
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
        // Call chess.currentBoard() or something
    }
}
