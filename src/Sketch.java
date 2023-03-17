import processing.core.PApplet;
import processing.core.PImage;

import java.util.LinkedList;

public class Sketch extends PApplet {

    private Chess chess;
    private PImage[] whitePieces;
    private PImage[] blackPieces;

    private int selectedSquare;

    public static void main (String[] args) {
        String[] processingArgs = {"ChessAI"};
        Sketch sketch = new Sketch();
        PApplet.runSketch(processingArgs, sketch);
    }

    public Sketch () {
        this.chess = new Chess(new HPlayer(), new AIPlayer());
        this.selectedSquare = -1;
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
            whitePieces[i-1] = loadImage( "sprites/ChessPiecesv2/White/white"+piece+"_v2.png");
            blackPieces[i-1] = loadImage( "sprites/ChessPiecesv2/Black/black"+piece+"_v2.png");
        }

    }

    private LinkedList<Integer> possibleMovesAtSelectedSq () {
        LinkedList<Integer> dotIdxs = new LinkedList<>();
        LinkedList<Move> possibleMoves = chess.possibleMovesAtPosition(selectedSquare);

        boolean pieceFound = false;
        for (Move m : possibleMoves) {
            if (m.getStartIdx() != selectedSquare) {
                if (pieceFound)
                    break;
                else continue;
            }
            pieceFound = true;
            int endIdx = m.getEndIdx();
            if (dotIdxs.size() == 0) {
                // If there's nothing in the list, add this move
                dotIdxs.add(endIdx);
            } else if (dotIdxs.get(0) > endIdx) {
                // If everything in the list > endIdx, add to beginning
                dotIdxs.add(0, endIdx);
            } else if (dotIdxs.get(dotIdxs.size() - 1) < endIdx) {
                // If everything in the list < endidx, add to end
                dotIdxs.add(dotIdxs.size(), endIdx);
            } else {
                // Find the point in the list where the next value < endIdx
                int i = 0;
                while (dotIdxs.get(i) < endIdx) {
                    i++;
                }
                dotIdxs.add(i, endIdx);
            }
        }
        return dotIdxs;
    }


    private void drawBoard () {
        // BACKGROUND: (Possible dark square colors: (40, 84, 50), (11, 41, 23), (37, 45, 64), (87, 27, 20))
        fill(40, 84, 50);
        noStroke();
        background(237, 226, 199);
        float squareWidth = width / 8f;
        int[] currentBoard = chess.currentBoard().pieces;

        LinkedList<Integer> dotIdxs;
        if (selectedSquare != -1) {
            dotIdxs = possibleMovesAtSelectedSq();
        }
        else dotIdxs = new LinkedList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    square(squareWidth * j, squareWidth * i, squareWidth);
                }
                if (i*8 + j == selectedSquare) {
                    pushStyle();
                    fill(255, 234, 0, 100);
                    square(squareWidth * j, squareWidth * i, squareWidth);
                    popStyle();
                }
                int piece = currentBoard[i*8+j];
                if (piece != 0) {
                    int absPiece = Math.abs(piece);
                    PImage sprite = piece > 0 ? whitePieces[absPiece - 1] : blackPieces[absPiece - 1];
                    image(sprite, squareWidth * j, squareWidth * i, squareWidth, squareWidth);
                }
                if (dotIdxs.size() > 0 && i*8 + j == dotIdxs.get(0)) {
                    ellipseMode(CORNER);
                    pushStyle();
                    fill(255, 0, 0);
                    circle(squareWidth * j, squareWidth * i, squareWidth - 10);
                    popStyle();
                    dotIdxs.remove(); // Remove the first element
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
        int row = mouseY*8 / height;
        int col = mouseX*8 / width;

        if(selectedSquare!=-1 && possibleMovesAtSelectedSq().contains(row*8+col)){
            chess.makeMove(selectedSquare,row*8+col);
        }
        selectedSquare = row*8 + col;

        return; // STUB
        // Something like
        // Set variables x1, y1 to first mouse click
        // Set x2, y2 to second mouse click
        // Pass that to chess to check if a valid move
    }
}
