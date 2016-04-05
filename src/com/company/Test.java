package com.company;
import org.lwjgl.Sys;

import java.awt.Point;
import java.util.*;
//import java.lang.System.*;

public class Test {
    public static void main(String[] args) {
        GameState game = new GameState(0);
//        System.out.println(game.getBoard().getValidPlankPositions().size());
//        game.execute(1, new Point(0, 0), 3.5, 2.5, 0);
        QuoridorAgent qa = new QuoridorAgent(game);
        int cost = qa.AstarSearch(game, game.P1);
        game.getBoard().removeEdges(5.5, 5.5, 0);
        game.getBoard().removeEdges(4.5,6.5, 1);
        game.getBoard().removeEdges(6.5, 6.5, 1);
        game.getBoard().removeEdges(4.5, 8.5, 1);
        game.P2.updatePosition(new Point(5,6));
//        cost = qa.AstarSearch(game, game.P2);
        Point target = new Point(1,1);
        cost = qa.AstarSearchWithTarget(game, game.P2, target);
        System.out.println(cost);
//        System.out.println(cost);
//        QuoridorAgent.Action AIAction = qa.valueIteration(game, game.P2, game.P1);
//
////        System.out.println(AIAction.getPlankX());
////        System.out.println(AIAction.getPlankY());
//        game.execute(AIAction.getActionType(),AIAction.getNextPosition(), AIAction.getPlankX(), AIAction.getPlankY(), AIAction.getPlankDirection());
//        game.execute(0,new Point(5, 2), 5.5, 8.5, 0);
//        AIAction = qa.valueIteration(game, game.P2, game.P1);
//        game.execute(AIAction.getActionType(),AIAction.getNextPosition(), AIAction.getPlankX(), AIAction.getPlankY(), AIAction.getPlankDirection());

//        Point newP1 = new Point(3,3);
//        Point newP2 = new Point(3,4);
//        game.P1.updatePosition(newP1);
//        game.P2.updatePosition(newP2);
//        Point nextMoveforP2 = new Point(4,3);
//        System.out.println(game.checkUserValid(nextMoveforP2));
//        ArrayList<Point> nextMoves = game.getMoveSuccessors2(newP1, game.P2);



    }
}