package com.company;

import org.lwjgl.Sys;

import java.util.*;
import java.awt.Point;
import java.awt.Color;

public class Main {

    public static void initialize() {
        StdDraw.setCanvasSize(550, 550);
        StdDraw.setXscale(0, 10);
        StdDraw.setYscale(0, 10);

    }
    public static void drawBackGround(GameState newGame) {
        StdDraw.picture(5,5,"2000px-Quoridor-game-board-initial-position.svg.png");
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        StdDraw.filledCircle(newGame.getPlayer(0).getX(), newGame.getPlayer(0).getY(), 0.3);
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.filledCircle(newGame.getPlayer(1).getX(), newGame.getPlayer(1).getY(), 0.3);

    }

    public static void drawBoard(GameState newGame) {
        for (Map.Entry<GameState.Plank, Integer> entry : newGame.getBoard().getUsedPlanks().entrySet()) {
            GameState.Plank plank = entry.getKey();
            int playerPlank = entry.getValue();
            if (playerPlank == 0) {
                StdDraw.setPenColor(StdDraw.BOOK_RED);
            }
            else {
                StdDraw.setPenColor(StdDraw.GREEN);
            }
            if (plank.getDirection() == 0) {

                StdDraw.filledRectangle(plank.getX(), plank.getY(), 1, 0.1);
            }
            if (plank.getDirection() == 1) {

                StdDraw.filledRectangle(plank.getX(), plank.getY(), 0.11, 1);
            }
        }
    }

    public static boolean isPlayerTurn(GameState newGame, GameState.Player p) {
        return newGame.getTurn() == p;
    }

    public static void walk(GameState newGame) {
        System.out.println("You chose to move");
        boolean valid = false;
        while (!valid) {
            if (StdDraw.mousePressed()) {

                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                int roundx = (int) Math.round(x);
                int roundy = (int) Math.round(y);
                Point clickPoint = new Point(roundx, roundy);
                valid = newGame.checkUserValid(clickPoint);
                if (valid) newGame.execute(0, clickPoint, 0, 0, 0);
                if (!valid) {
                    System.out.println("Please choose a neighboring position!");
                }
            }
        }
    }

    public static void putHorizontalPlank(GameState newGame) {
        System.out.println("You chose to put a plank");
        boolean valid = false;
        if (newGame.getTurn().getPlanks() > 0) {
            while (!valid) {
                if (StdDraw.mousePressed()) {
//                  StdDraw.show(10);
                    double x = StdDraw.mouseX();
                    double y = StdDraw.mouseY();
                    double roundx = Math.round(2 * x) / 2.0;
                    double roundy = Math.round(2 * y) / 2.0;

                    System.out.println(roundx);
                    System.out.println(roundy);
                    Point nullPoint = new Point(0, 0);
                    valid = newGame.execute(1, nullPoint, roundx, roundy, 0);
                    if (!valid) {
                        System.out.println("Please choose a valid position to place planks!");
                    }
                }
            }
            // post process if valid is true
        }
    }



    public static void putVerticalPlank(GameState newGame) {
        System.out.println("You chose to put a plank");
        boolean valid = false;
        double rectX = 0;
        double rectY = 0;
        if (newGame.getTurn().getPlanks() > 0) {
            while (!valid) {
                if (StdDraw.mousePressed()) {
                    double x = StdDraw.mouseX();
                    double y = StdDraw.mouseY();
                    System.out.println(x);
                    System.out.println(y);
                    double roundx = Math.round(2 * x) / 2.0;
                    double roundy = Math.round(2 * y) / 2.0;
                    rectX = roundx;
                    rectY = roundy;
                    System.out.println(roundx);
                    System.out.println(roundy);
                    Point nullPoint = new Point(0, 0);
                    valid = newGame.execute(1, nullPoint, roundx, roundy, 1);
                    if (!valid) {
                        System.out.println("Please choose a valid position to place planks!");
                    }
                }
            }
            // post process if valid is true
        }
    }

    public static void finishGame(GameState newGame) {
        StdDraw.setPenColor();
        StdDraw.clear();
        StdDraw.text(5,5,newGame.getWinner()+ " is the winner!");
        StdDraw.show(10);
        System.out.println(newGame.getWinner()+" is the winner!");
    }
    public static void main(String[] args) {
        GameState newGame = new GameState(0);
        QuoridorAgent qa = new QuoridorAgent(newGame);
        initialize();
        while (!newGame.isWin() ) {
            drawBackGround(newGame);
            drawBoard(newGame);
            StdDraw.show(0);
            if (isPlayerTurn(newGame, newGame.P2)) {
            QuoridorAgent.Action AIAction = qa.openings(1, qa.move);
            newGame.execute(AIAction.getActionType(), AIAction.getNextPosition(), AIAction.getPlankX(), AIAction.getPlankY(), AIAction.getPlankDirection());
            continue;
        }
            if (StdDraw.isKeyPressed(49)) {
                walk(newGame);
            }
//            StdDraw.show(0);
            if (StdDraw.isKeyPressed(50)) {
                putHorizontalPlank(newGame);
            }

            if (StdDraw.isKeyPressed(51)) {
                putVerticalPlank(newGame);
            }

        }
        finishGame(newGame);
    }
}
