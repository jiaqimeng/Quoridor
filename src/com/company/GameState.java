package com.company;
import org.lwjgl.Sys;

import java.util.*;
import java.awt.Point;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
    Player P1;
    Player P2;
    Board board;
    boolean TURN; // TRUE FOR P1, FALSE FOR P2
    Player winner;

    public GameState(int mode) {
        if (mode == 0) {
            P1 = new Player(new Point(5, 1), 0);
            P2 = new Player(new Point(5, 9), 1);
            board = new Board();
            TURN = true;
            winner = null;
        }
    }

    public Player getPlayer(int label) {
        if (label == 0) {
            return P1;
        }
        else return P2;
    }
    public Player getTurn() {
        if (TURN) {
            return P1;
        }
        else {
            return P2;
        }
    }

    public ArrayList<Point> getMoveSuccessors2(Point p, Player opponent) {
        ArrayList<Point> successors = new ArrayList<Point>();
        for (Node n : board.getNode(p).getAdjacentList()) {
            if (n.getLabel() == board.getNode(opponent.getPosition()).getLabel()) {
                // jump player under construction
                double xdifference = n.getLabel().getX() - p.getX();
                double ydifference = n.getLabel().getY() - p.getY();
                Point jumpNode = new Point((int) (n.getLabel().getX() + xdifference), (int) (n.getLabel().getY() + ydifference));
                if (n.getAdjacentList().contains(getBoard().getNode(jumpNode))) {
                    successors.add(jumpNode);
                } else {
                    Point neighborOne = new Point((int) (n.getLabel().getX() + ydifference), (int) (n.getLabel().getY() + xdifference));
                    Point neighborTwo = new Point((int) (n.getLabel().getX() - ydifference), (int) (n.getLabel().getY() - xdifference));
                    if (n.getAdjacentList().contains(getBoard().getNode(neighborOne))) {
                        successors.add(neighborOne);

                    }
                    if (n.getAdjacentList().contains(getBoard().getNode(neighborTwo))) {
                        successors.add(neighborTwo);

                    }
                }

            }
            else {
                successors.add(n.getLabel());
            }

        }
        return successors;
    }

    public ConcurrentHashMap<Plank, Boolean> getPlankSuccessors() {
        return board.getValidPlankPositions();
    }

    public boolean isP1(Player player) {
        return (player == P1);
    }

    public Node getNodebyPoint(Point point) {
        return board.getNode(point);
    }
    public Board getBoard() {
        return board;
    }

    public boolean checkUserValid(Point tmp) {
        Player current = getTurn();
        Player opponent = getPlayer(1-current.getLabel());
        return getMoveSuccessors2(current.getPosition(), opponent).contains(tmp);
    }

    public boolean execute(int action, Point nextposition, double x, double y, int direction) {
        // if action is 0, walk, 1, put planks.
        Player currentPlayer = getTurn();
        boolean success = false;
        if (action == 0) {
            success = currentPlayer.walk(board, nextposition);
        }
        if (action == 1) {
            success = currentPlayer.putPlank(board, x, y, direction);
        }
        if (action == 2) {
            winner = getPlayer(1-currentPlayer.getLabel());
            return true;
        }
//        if (isWin(getTurn(), getTurn().getPosition())) {
//            return true;
//        }
        // move is valid, finish and switch turns.
        isWin(currentPlayer, currentPlayer.getPosition());
        if (success) {
            finish();
            return true;
        }
        return false;
    }
    public boolean isWin() {
        if (winner != null) {
            return true;
        }
        return false;
    }
    public boolean isWin(Player player, Point p) {
        // this condition filters out the trivial case.
        if (p.getX() == 0 && p.getY() == 0) {
            return false;
        }
        if (player == P1) {
            if (p.getY() >= 9) {
                winner = P1;
                return true;
            }
            return false;
        }
        else {
            if (p.getY() <= 1) {
                winner = P2;
                return true;
            }
            return false;
        }
    }

    public boolean isWin(Player player, Point p, boolean test) {
        // this condition filters out the trivial case.
        if (p.getX() == 0 && p.getY() == 0) {
            return false;
        }
        if (player == P1) {
            if (p.getY() >= 9) {
                return true;
            }
            return false;
        }
        else {
            if (p.getY() <= 1) {
                return true;
            }
            return false;
        }
    }

    public void finish() {
        TURN = !TURN;
    }

    public String getWinner() {
        if (winner == P1) {
            return "RED";
        }
        else if (winner == P2) {
            return "GREEN";
        }
        return "NONE";
    }

    class Board {
        HashMap<Point, Node> graph;
        ConcurrentHashMap<Plank, Boolean> validPlanksPositions;
//        ArrayList<Plank> validPlankPositions;
        HashMap<Plank, Integer> usedPlanks;
        public Board() {
            graph = new HashMap<Point, Node>();
            validPlanksPositions = new ConcurrentHashMap<Plank, Boolean>();
            usedPlanks = new HashMap<>();
            for (int i = 1; i<=9; i++) {
                for (int j = 1; j<=9; j++) {
                    graph.put(new Point(i,j), new Node(new Point(i, j)));
                    if (i < 9 && j < 9) {
                        Plank plankHorizontal = new Plank((double)(i)+0.5, (double)(j)+0.5, 0);
                        Plank plankVertical = new Plank((double)(i)+0.5, (double)(j)+0.5, 1);
                        validPlanksPositions.put(plankHorizontal, true);
                        validPlanksPositions.put(plankVertical, true);
                    }
                }
            }
            for (Map.Entry<Point, Node> entry : graph.entrySet()) {
                Point point1 = new Point((int)entry.getKey().getX()-1, (int)entry.getKey().getY());
                Point point2 = new Point((int)entry.getKey().getX()+1, (int)entry.getKey().getY());
                Point point3 = new Point((int)entry.getKey().getX(), (int)entry.getKey().getY()-1);
                Point point4 = new Point((int)entry.getKey().getX(), (int)entry.getKey().getY()+1);
                entry.getValue().addAdjcent(graph.get(point1));
                entry.getValue().addAdjcent(graph.get(point2));
                entry.getValue().addAdjcent(graph.get(point3));
                entry.getValue().addAdjcent(graph.get(point4));
            }


        }

        public ConcurrentHashMap<Plank, Boolean> getValidPlankPositions() {
            return validPlanksPositions;
        }

        public HashMap<Plank, Integer> getUsedPlanks() {
            return usedPlanks;
        }
        public void throwPlanktoUsed(Plank p, int player) {
            usedPlanks.put(p, player);
        }
        public void removePlankFromUsed(Plank p) {
            usedPlanks.remove(p);
        }
        public boolean addValidPlank(double x, double y, int direction, boolean centerOverLap) {
            Plank toAdd = new Plank(x, y, direction);
            // possibly not working because hashkey difference
            if (validPlanksPositions.containsKey(toAdd)) {
                if (direction == 0 && centerOverLap == false) {
                    Plank toAddLeft = new Plank(x - 1, y, direction);
                    Plank toAddRight = new Plank(x + 1, y, direction);
                    if (toAddLeft.getX() >= 1) {
                        validPlanksPositions.put(toAdd, true);
                    }
                    if (toAddRight.getX() <= 9) {
                        validPlanksPositions.put(toAddRight, true);
                    }

                }
                if (direction == 1 && centerOverLap == false) {
                    Plank toAddUp = new Plank(x, y + 1, direction);
                    Plank toAddDown = new Plank(x, y - 1, direction);
                    if (toAddUp.getY() <= 9) {
                        validPlanksPositions.put(toAddUp, true);
                    }
                    if (toAddDown.getX() >= 1) {
                        validPlanksPositions.put(toAddDown, true);
                    }
                }
                return validPlanksPositions.put(toAdd, true);
            }
            return false;
        }

        public boolean removePlank(double x, double y, int direction, boolean centerOverLap) {
            // centerOverLap decides if we should just remove the plank in (x,y) with only that direction, true indicates yes, else no.
            Plank toRemove = new Plank(x, y, direction);
            if (!validPlanksPositions.containsKey(toRemove)) {
                return false;
            }
            // if a plank was taken, say, (5.5, 5.5, horizontal), we have to take out (4.5, 5.5, horizontal) and
            // (6.5, 5.5, horizontal) as well; similar for a vertical plank
            if (direction == 0 && centerOverLap == false) {
                Plank toRemoveLeft = new Plank(x - 1, y, direction);
                Plank toRemoveRight = new Plank(x + 1, y, direction);
                if (validPlanksPositions.containsKey(toRemoveLeft)) {validPlanksPositions.put(toRemoveLeft, false);}
                if (validPlanksPositions.containsKey(toRemoveRight)) {validPlanksPositions.put(toRemoveRight, false);}
            }
            if (direction == 1 && centerOverLap == false) {
                Plank toRemoveUp = new Plank(x, y + 1, direction);
                Plank toRemoveDown = new Plank(x, y - 1, direction);
                if (validPlanksPositions.containsKey(toRemoveUp)) {validPlanksPositions.put(toRemoveUp, false);}
                if (validPlanksPositions.containsKey(toRemoveDown)) {validPlanksPositions.put(toRemoveDown, false);}
            }
            return validPlanksPositions.put(toRemove, false);

        }

        public Node getNode(Point p) {
            if (graph.containsKey(p)) {
                return graph.get(p);
            }
            return null;
        }

        public HashMap<Point, Node> getGraph() {
            return graph;
        }
        public void removeEdges(double x, double y, int direction) {
            Point upLeft = new Point((int)(x - 0.5), (int)(y + 0.5));
            Point upRight = new Point((int)(x + 0.5), (int)(y + 0.5));
            Point bottomLeft = new Point((int)(x - 0.5), (int)(y - 0.5));
            Point bottomRight = new Point((int)(x + 0.5), (int)(y - 0.5));
            if (direction == 0) {
                // a b
                // c d
                // cut ab and cd
                if (graph.containsKey(upLeft)) graph.get(upLeft).removeAdjcent(graph.get(bottomLeft));
                if (graph.containsKey(bottomLeft)) graph.get(bottomLeft).removeAdjcent(graph.get(upLeft));
                if (graph.containsKey(upRight)) graph.get(upRight).removeAdjcent(graph.get(bottomRight));
                if (graph.containsKey(bottomRight)) graph.get(bottomRight).removeAdjcent(graph.get(upRight));
            }
            if (direction == 1) {
                // cut ac and bd
                if (graph.containsKey(upLeft)) graph.get(upLeft).removeAdjcent(graph.get(upRight));
                if (graph.containsKey(upRight)) graph.get(upRight).removeAdjcent(graph.get(upLeft));
                if (graph.containsKey(bottomRight)) graph.get(bottomRight).removeAdjcent(graph.get(bottomLeft));
                if (graph.containsKey(bottomLeft)) graph.get(bottomLeft).removeAdjcent(graph.get(bottomRight));
            }
        }
        public void addEdges(double x, double y, int direction) {
            Point upLeft = new Point((int)(x - 0.5), (int)(y + 0.5));
            Point upRight = new Point((int)(x + 0.5), (int)(y + 0.5));
            Point bottomLeft = new Point((int)(x - 0.5), (int)(y - 0.5));
            Point bottomRight = new Point((int)(x + 0.5), (int)(y - 0.5));
            if (direction == 0) {
                // a b
                // c d
                // cut ab and cd
                if (graph.containsKey(upLeft)) graph.get(upLeft).addAdjcent(graph.get(bottomLeft));
                if (graph.containsKey(bottomLeft)) graph.get(bottomLeft).addAdjcent(graph.get(upLeft));
                if (graph.containsKey(upRight)) graph.get(upRight).addAdjcent(graph.get(bottomRight));
                if (graph.containsKey(bottomRight)) graph.get(bottomRight).addAdjcent(graph.get(upRight));
            }
            if (direction == 1) {
                // cut ac and bd
                if (graph.containsKey(upLeft)) graph.get(upLeft).addAdjcent(graph.get(upRight));
                if (graph.containsKey(upRight)) graph.get(upRight).addAdjcent(graph.get(upLeft));
                if (graph.containsKey(bottomRight)) graph.get(bottomRight).addAdjcent(graph.get(bottomLeft));
                if (graph.containsKey(bottomLeft)) graph.get(bottomLeft).addAdjcent(graph.get(bottomRight));
            }
        }

    }

    class Player {
        int planks;
        Point position;
        int label;

        public Player(Point start, int l) {
            planks = 10;
            position = start; //potentially wrong due to pointer
            label = l;
        }
        public int getLabel() {
            return label;
        }
        public int getPlanks() {
            return planks;
        }
        public Point getPosition() {
            return position;
        }

        public void updatePosition(Point p) {
            position = p;
        }
        public int getX() {
            return (int)position.getX();
        }

        public int getY() {
            return (int)position.getY();
        }

        public boolean walk(Board board, Point nextposition) {
//            if (board.getNode(position).getAdjacentList().contains(board.getNode(nextposition))) {
            position = nextposition;
            return true;
//            }
//            else {
//                return false;
//            }
        }

        public boolean decrementPlank() {
            if (planks == 0) {
                return false;
            }
            planks -= 1;
            return true;
        }

        public boolean incrementPlank() {
            if (planks == 10) {
                return false;
            }
            planks += 1;
            return true;
        }
        public boolean putPlank(Board board, double x, double y, int direction) {
            // if direction is 0, horizontal, else vertical.
            // if no planks, do nothing and return false.
            if (planks == 0) {
                return false;
            }
            Plank temp = new Plank(x, y, direction);
            if (board.getValidPlankPositions().get(temp) == null || !board.getValidPlankPositions().get(temp)) {
                return false;
            }
//            HashMap<Point, Node> graph = board.getGraph();
            board.removeEdges(x, y, direction);
            // in order for us to draw planks, we have to remember all used planks in each turn.
            board.getUsedPlanks().put(temp, label);
            planks -=1;
            // once we put a plant in (x,y) horizontally, we have to invalidate of vertical plank in the same position.
            // We also need to invalidate the plank with the same direction next to this one.
            board.removePlank(x, y, direction, false);
            board.removePlank(x, y, 1-direction, true);
            return true;
        }

        public boolean removePlank(Board board, double x, double y, int direction) {
            if (planks == 10) {
                return false;
            }
            Plank temp = new Plank(x, y, direction);
//            HashMap<Point, Node> graph = board.getGraph();
            board.addEdges(x, y, direction);
            // return that used one. 目的未知, possibly related to draw.
            board.getUsedPlanks().remove(temp);
            planks +=1;

            board.addValidPlank(x, y, direction, false);
            board.addValidPlank(x, y, 1-direction, true);
            return true;
        }

    }

    class Node {
        Point label;
        ArrayList<Node> adjacentList;

        public Node(Point coordinate) {
            label = coordinate;
            adjacentList = new ArrayList<Node>();
        }

        public Point getLabel() {
            return label;
        }
        // add adjcent node n to construct edges
        public void addAdjcent(Node n) {
            if (n == null) {
                return;
            }
            if (!adjacentList.contains(n)) {
                adjacentList.add(n);
            }
        }

        // remove n from adjcent list
        public boolean removeAdjcent(Node n) {
            if (n != null) return adjacentList.remove(n);
            return false;
        }

        public ArrayList<Node> getAdjacentList() {
            return adjacentList;
        }
    }

    class Plank {
        double centerX;
        double centerY;

        @Override
        public int hashCode() {
            return Objects.hash(centerX, centerY, direction);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final Plank other = (Plank) obj;
            return Objects.equals(this.centerX, other.centerX)
                    && Objects.equals(this.centerY, other.centerY)
                    && Objects.equals(this.direction, other.direction);
        }

        int direction;

        public Plank(double x, double y, int dir) {
            centerX = x;
            centerY = y;
            direction = dir;
        }

        public double getX() {
            return centerX;
        }

        public double getY() {
            return centerY;
        }

        public  int getDirection() {
            return direction;
        }


    }
}