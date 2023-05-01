import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Sketch extends PApplet {

    private AI ai;

    private Board board;
    private PImage[] whitePieces;
    private PImage[] blackPieces;

    private boolean promoting;
    private Move promoteMove;
    private PImage spotLightSprite;

    private int selectedSquare;
    private HashMap<Integer, Move> possibleMoves;

    public static void main(String[] args) {
        String[] processingArgs = {"ChessAI"};
        Sketch sketch = new Sketch();
        PApplet.runSketch(processingArgs, sketch);
    }

    public Sketch() {
        this.board = new Board();
        this.ai = new AI(board);
        this.selectedSquare = -1;
        this.promoting = false;
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
            spotLightSprite = loadImage("sprites/fancy/spotlight.png");
            whitePieces[i - 1] = loadImage("sprites/ChessPiecesv2/White/white" + piece + "_v2.png");
            blackPieces[i - 1] = loadImage("sprites/ChessPiecesv2/Black/black" + piece + "_v2.png");
            float r = random(0, 1);
            if(r >= 0.98333){
                whitePieces[i - 1] = loadImage("sprites/ChessPiecesv1/White/white" + piece + "_v1.png");
                blackPieces[i - 1] = loadImage("sprites/ChessPiecesv1/Black/black" + piece + "_v1.png");
            }
        }

    }

    private HashMap<Integer, Move> possibleMovesAtSelectedSq() {
        HashMap<Integer, Move> ret = new HashMap<>();
        LinkedList<Move> allPossibleMoves = board.possibleMovesAtPosition(selectedSquare);

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

    private void drawBoard() {
        // BACKGROUND
        fill(105, 59, 73);
        noStroke();
        background(237, 226, 199);
        float squareWidth = width / 8f;
        int[] currentBoard = board.pieces;
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
        if (promoting) {
            if(board.whiteToMove()){
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
        switch (board.getGameState()) {
            case Board.WHITEWINS -> {
                // white mated black
                float squareWidth = width / 8f;
                int[] currentBoard = board.pieces;
                fill(0, 205);
                noStroke();
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        int piece = currentBoard[i * 8 + j];
                        if (Math.abs(piece) != 6 && board.lastMove().getEndIdx() != (i * 8 + j)) {
                            square(squareWidth * j, squareWidth * i, squareWidth);
                        }else{
                            image(spotLightSprite, squareWidth * j, squareWidth * i);
                        }
                    }
                }
                String wTitle = "VICTORY";
            }
            case Board.BLACKWINS -> {
                // black mated white
                float squareWidth = width / 8f;
                int[] currentBoard = board.pieces;
                fill(0, 205);
                noStroke();
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        int piece = currentBoard[i * 8 + j];
                        if (Math.abs(piece) != 6 && board.lastMove().getEndIdx() != (i * 8 + j)) {
                            square(squareWidth * j, squareWidth * i, squareWidth);
                        }else{
                            image(spotLightSprite, squareWidth * j, squareWidth * i);
                        }
                    }
                }
                String lTitle = "DEFEAT";
            }
            case Board.DRAW -> {
                // Perfect chess is always a draw
                float squareWidth = width / 8f;
                int[] currentBoard = board.pieces;
                fill(0, 205);
                noStroke();
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        int piece = currentBoard[i * 8 + j];
                        if (Math.abs(piece) != 6) {
                            square(squareWidth * j, squareWidth * i, squareWidth);
                        }else{
                            image(spotLightSprite, squareWidth * j, squareWidth * i);
                        }
                    }
                }
                String dTitle = "DRAW";
            }
        }

        if(board.blackToMove()) {
            Move nextMove = ai.bestNextMove();
            if (nextMove != null) {
                board.makeMove(nextMove);
                selectedSquare = nextMove.getEndIdx();
            }
        }
    }

    @Override
    public void mouseClicked() {
        if (promoting) return;
        int row = mouseY*8 / height;
        int col = mouseX*8 / width;

        if(selectedSquare!=-1 && possibleMoves.containsKey(row*8 + col)){
            Move m = possibleMoves.get(row*8 + col);
            if (Math.abs(board.pieces[m.getStartIdx()]) == Board.P) {
                if (Board.row(m.getEndIdx()) == 0 || Board.row(m.getEndIdx()) == 7) {
                    promoting = true;
                    promoteMove = m;
                }
            }

            if (!promoting)
                board.makeMove(m);
            selectedSquare = -1;

            // show the move without waiting for the bot
            drawBoard();
        }else{
            selectedSquare = selectedSquare==row*8+col ? -1 : row*8 + col;
        }
    }

    @Override
    public void keyPressed() {
        if (promoting) {
            int color = board.whiteToMove() ? 1 : -1;
            int promotingTo = switch (key) {
                case '1' -> Board.N;
                case '2' -> Board.B;
                case '3' -> Board.R;
                case '4' -> Board.Q;
                default -> 0;
            } * color;

            promoteMove.setPromoteTo(promotingTo);

            board.makeMove(promoteMove);
            promoting = false;
        }

        // DEBUGGING PURPOSES
        if (key == 'z') {
            board.undoMove();
        }

        if (key == 'p') {
            board.printPastMoves();
        }

    }
}
