import processing.core.PApplet;
import processing.core.PImage;

public class Sketch extends PApplet {

    private Chess chess;
    private PImage[] whitePieces;
    private PImage[] blackPieces;

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

    @Override
    public void setup () {
        String whitePath = "sprites/ChessPiecesv1/White/";
        whitePieces = new PImage[6];
        String blackPath = "sprites/ChessPiecesv1/Black/";
        blackPieces = new PImage[6];
        for (int i = 1; i <= 6; i++) {
            String piece = switch (i) {
                case Board.P:
                    yield "pawn";
                case Board.N:
                    yield "knight";
                case Board.B:
                    yield "bishop";
                case Board.R:
                    yield "rook";
                case Board.Q:
                    yield "queen";
                case Board.K:
                    yield "king";
                default:
                    yield "";
            };
            whitePieces[i-1] = loadImage( "sprites/ChessPiecesv1/White/white"+piece+"_v1.png");
            blackPieces[i-1] = loadImage( "sprites/ChessPiecesv1/Black/black"+piece+"_v1.png");
        }

    }

    private void drawBoard () {
        // BACKGROUND
        fill(20, 100, 135);
        noStroke();
        background(237, 226, 199);
        float squareWidth = width / 8f;
        int[][] currentBoard = chess.currentBoard().pieces;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1)
                    square(squareWidth * j, squareWidth * i, squareWidth);
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
                if (piece != 0) {
                    int absPiece = Math.abs(piece);
                    PImage sprite = piece > 0 ? whitePieces[absPiece - 1] : blackPieces[absPiece - 1];
                    image(sprite, squareWidth * j, squareWidth * i, squareWidth, squareWidth);
                }
            }
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
