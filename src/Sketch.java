import processing.core.PApplet;
import processing.core.PImage;

import java.util.HashMap;
import java.util.LinkedList;

public class Sketch extends PApplet {

    private Chess chess;
    private PImage[] whitePieces;
    private PImage[] blackPieces;

    private int selectedSquare;
    private HashMap<Integer, Move> possibleMoves;

    public static void main(String[] args) {
        String[] processingArgs = {"ChessAI"};
        Sketch sketch = new Sketch();
        PApplet.runSketch(processingArgs, sketch);
    }

    public Sketch() {
        this.chess = new Chess(new HPlayer(), new AIPlayer());
        this.selectedSquare = -1;
    }

    // I LOVE SETTINGS
    @Override
    public void settings() {
        size(512, 512);
    }

    @Override
    public void setup() {
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
            whitePieces[i - 1] = loadImage("sprites/ChessPiecesv2/White/white" + piece + "_v2.png");
            blackPieces[i - 1] = loadImage("sprites/ChessPiecesv2/Black/black" + piece + "_v2.png");
        }

    }

    private HashMap<Integer, Move> possibleMovesAtSelectedSq() {
        HashMap<Integer, Move> ret = new HashMap<>();
        LinkedList<Move> allPossibleMoves = chess.possibleMovesAtPosition(selectedSquare);

        boolean pieceFound = false;
        for (Move m : allPossibleMoves) {
            if (m.getStartIdx() != selectedSquare) {
                if (pieceFound)
                    break;
                else continue;
            }
            pieceFound = true;
            int endIdx = m.getEndIdx();
            ret.put(endIdx, m);
        }
        return ret;
    }

    private boolean promoting() {
        return (chess.currentBoard().getPromotingIdx() > -1);
    }

    private void drawBoard() {
        // BACKGROUND
        fill(105, 59, 73);
        noStroke();
        background(237, 226, 199);
        float squareWidth = width / 8f;
        int[] currentBoard = chess.currentBoard().pieces;
        PImage whitePromoteUI;
        PImage blackPromoteUI;

        if (selectedSquare != -1) {
            possibleMoves = possibleMovesAtSelectedSq();
        } else possibleMoves = new HashMap<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    square(squareWidth * j, squareWidth * i, squareWidth);
                }
                if (i * 8 + j == selectedSquare) {
                    pushStyle();
                    fill(14, 125, 45, 255);
                    square(squareWidth * j, squareWidth * i, squareWidth);
                    popStyle();
                }
                int piece = currentBoard[i * 8 + j];
                if (piece != 0) {
                    int absPiece = Math.abs(piece);
                    PImage sprite = piece > 0 ? whitePieces[absPiece - 1] : blackPieces[absPiece - 1];
                    image(sprite, squareWidth * j, squareWidth * i, squareWidth, squareWidth);
                }
                if (possibleMoves.containsKey(i * 8 + j)) {
                    pushStyle();
                    fill(90);
                    circle(squareWidth * (j + 0.5f), squareWidth * (i + 0.5f), squareWidth / 2f);
                    popStyle();
                }
            }

        }

        // displays a not cursed graphic for promotion
        if (promoting()) {
            if(chess.currentBoard().getPromotingIdx() < 8){
                whitePromoteUI = loadImage("sprites/PromotingGraphicsv1/WhitePromoteUI_v1.png");
                image(whitePromoteUI, 128, 128);
            } else {
                blackPromoteUI = loadImage("sprites/PromotingGraphicsv1/BlackPromoteUI_v1.png");
                image(blackPromoteUI, 128, 128);
            }
        }
    }




    // -- GAMELOOP --
    @Override
    public void draw () {
        drawBoard();
      //  if(chess.currentBoard().numActualMoves%2==1) {
      //      chess.aiMove();
      //  }
    }

    @Override
    public void mouseClicked() {
        if (promoting()) return;
        int row = mouseY*8 / height;
        int col = mouseX*8 / width;

        if(selectedSquare!=-1 && possibleMoves.containsKey(row*8 + col)){
            chess.makeMove(possibleMoves.get(row*8 + col));
        }
        selectedSquare = row*8 + col;
    }

    @Override
    public void keyPressed() {
        if (!promoting()) return;

        switch (key) {
            case '1' -> chess.promote(Board.N);
            case '2' -> chess.promote(Board.B);
            case '3' -> chess.promote(Board.R);
            case '4' -> chess.promote(Board.Q);
        }
    }
}
