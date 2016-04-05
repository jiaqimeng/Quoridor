package com.company;

import java.util.*;
import java.awt.Point;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Richard Meng on 3/27/16.
 */
public class QuoridorAgent {
    private GameState currentState;
    int move = 0;
    public QuoridorAgent(GameState game) {
        currentState = game;
    }

    class PointComparator implements Comparator<PointWrapper> {
        @Override
        public int compare(PointWrapper p1, PointWrapper p2) {
            return p1.getCostSum()-p2.getCostSum();
        }
    }

    class PointWrapper {
        Point p;
        int cost;
        int heuristicCost;
        PointWrapper(Point toWrap, int pathCost, int heuristic) {
            p = toWrap;
            cost = pathCost;
            heuristicCost = heuristic;
        }

        int getCost() {
            return cost;
        }

        int getHeuristicCost() {
            return heuristicCost;
        }

        int getCostSum() {
            return cost+heuristicCost;
        }
        Point getPoint() {
            return p;
        }
        @Override
        public int hashCode() {
            return Objects.hash(p, cost, heuristicCost);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final PointWrapper other = (PointWrapper) obj;
            return Objects.equals(this.p, other.p)
                    && Objects.equals(this.cost, other.cost)
                    && Objects.equals(this.heuristicCost, other.heuristicCost);
        }
    }

    public Action openings(int opening, int moveNo) {
        if (moveNo <= 2) {
            move += 1;
            if (opening == 1) {
                if (!currentState.execute(1, new Point(0,0), 3.5, 3.5, 0)) {
                    boolean success = currentState.execute(1, new Point(0,0), 6.5, 3.5, 0);
                    if (!success) {
                        return valueIteration(currentState, currentState.P2, currentState.P1);
                    }
                    return new Action(1, new Point(0,0),6.5,3.5, 0);
                }
                return new Action(1, new Point(0, 0), 3.5, 3.5, 0);
            }
        }
        move += 1;
        return valueIteration(currentState, currentState.P2, currentState.P1);
    }
    public static int BFS(GameState state, GameState.Player p) {
        Stack<Point> s = new Stack<Point>();
        Set<Point> visited = new HashSet<Point>();
        s.push(p.getPosition());
        int cost = 0;
        while (!s.isEmpty()) {
            Point currentPoint = s.pop();
            if (state.isWin(p, currentPoint, true)) {
                return cost;
            }
            visited.add(currentPoint);
            ArrayList<GameState.Node> currentPointAdjcent = state.getNodebyPoint(currentPoint).getAdjacentList();
            for (GameState.Node node : currentPointAdjcent) {
                if (!visited.contains(node.getLabel())) {
                    s.push(node.getLabel());
                }
            }
            cost += 1;
        }
        return -1;
    }

    public int AstarSearch(GameState state, GameState.Player p) {
        Comparator<PointWrapper> comparator = new PointComparator();
        PriorityQueue<PointWrapper> pq = new PriorityQueue<>(81, comparator);
        HashMap<Point, PointWrapper> visited = new HashMap<>();
        PointWrapper pstart = new PointWrapper(p.getPosition(), 0, mahhantanHeuristic(p, p.getPosition()));
        pq.add(pstart);
        int cost = 1;
        while (pq.size()!=0) {
            PointWrapper current = pq.poll();
            if (state.isWin(p, current.getPoint(), true)) {
                return current.getCost();
            }
            visited.put(current.getPoint(),current);
            ArrayList<GameState.Node> currentPointAdjcent = state.getNodebyPoint(current.getPoint()).getAdjacentList();
            for (GameState.Node node : currentPointAdjcent) {
                int currentCost = visited.get(current.getPoint()).getCost();
                PointWrapper pw = new PointWrapper(node.getLabel(), currentCost+cost, mahhantanHeuristic(p, node.getLabel()));
                if (!visited.containsKey(pw.getPoint()) || visited.get(pw.getPoint()).getCost() > pw.getCost()) {
                    visited.put(pw.getPoint(), pw);
                    pq.add(pw);
                }
            }
        }
        return Integer.MAX_VALUE;

    }

    public int AstarSearchWithTarget(GameState state, GameState.Player p, Point target) {
        Comparator<PointWrapper> comparator = new PointComparator();
        PriorityQueue<PointWrapper> pq = new PriorityQueue<>(81, comparator);
        HashMap<Point, PointWrapper> visited = new HashMap<>();
        PointWrapper pstart = new PointWrapper(p.getPosition(), 0, mahhantanHeuristic(p, p.getPosition()));
        pq.add(pstart);
        int cost = 1;
        while (pq.size()!=0) {
            PointWrapper current = pq.poll();
            if (current.getPoint().getX()==target.getX() && current.getPoint().getY() == target.getY()) {
                return current.cost;
            }
            if (state.isWin(p, current.getPoint(), true)) {
                continue;
            }
            visited.put(current.getPoint(),current);
            ArrayList<GameState.Node> currentPointAdjcent = state.getNodebyPoint(current.getPoint()).getAdjacentList();
            for (GameState.Node node : currentPointAdjcent) {
                int currentCost = visited.get(current.getPoint()).getCost();
                PointWrapper pw = new PointWrapper(node.getLabel(), currentCost+cost, mahhantanHeuristic(p, node.getLabel()));
                if (!visited.containsKey(pw.getPoint()) || visited.get(pw.getPoint()).getCost() > pw.getCost()) {
                    visited.put(pw.getPoint(), pw);
                    pq.add(pw);
                }
            }
        }
        return -1;
    }

    public ArrayList<Integer> costOfGoals(GameState state, GameState.Player p) {
        ArrayList<Integer> result = new ArrayList<>();
        int y = 9;
        if (p.getLabel() == 1) {
            y = 1;
        }
        for (int i = 1; i<= 9; i++) {
            Point targetPoint = new Point(i, y);
            int cost = AstarSearchWithTarget(state, p, targetPoint);
            result.add(cost);
        }
        return result;
    }

    public int mahhantanHeuristic(GameState.Player player, Point point) {
        boolean isPlayer1 = currentState.isP1(player);
        if (isPlayer1) {
            return (int)(9-point.getY());
        }
        return (int)(point.getY()-1);
    }

    public int costToNextColumn(GameState state, GameState.Player p) {
        Stack<Point> s = new Stack<Point>();
        Set<Point> visited = new HashSet<Point>();
        Point locationCopy = new Point(p.getPosition());
        s.push(p.getPosition());
        int cost = 0;
        while (!s.isEmpty()) {
            Point currentPoint = s.pop();
            if (p == state.P1 && (int)currentPoint.getY() > locationCopy.getY()) {
                return cost;
            }
            if (p == state.P2 && (int)currentPoint.getY() < locationCopy.getY()) {
                return cost;
            }
            visited.add(currentPoint);
            ArrayList<GameState.Node> currentPointAdjcent = state.getNodebyPoint(currentPoint).getAdjacentList();
            for (GameState.Node node : currentPointAdjcent) {
                if (!visited.contains(node.getLabel())) {
                    s.push(node.getLabel());
                }
            }
            cost += 1;
        }
        return cost;
    }
    public int mahhatanHeuristicToScore(GameState.Player player) {
        Point position = player.getPosition();
        boolean isPlayer1 = currentState.isP1(player);
        if (isPlayer1) {
            return (int)Math.abs(position.getY());
        }
        return 9-(int)Math.abs(position.getY());

    }

    public static int sum(ArrayList<Integer> input) {
        int sum = 0;
        for (Integer i : input) {
            sum += i;
        }
        return sum;
    }
    public double evaluationFunction(GameState state) {
        if (state.isWin(state.P2, state.P2.getPosition(), true)) {
            return 999999;
        }
//        double feature5 = Collections.min(BFS(state, state.P2));
//        if (feature5 == Integer.MAX_VALUE) {
//            feature5 = -1;
//        }
        double feature1 = mahhatanHeuristicToScore(state.P2);
//        double feature2 = (double)(mahhatanHeuristicToScore(state.P2) - mahhatanHeuristicToScore(state.P1));
//        double feature3 = 1.0/(double)costToNextColumn(state, state.P2);
//        double feature4 = (double)costToNextColumn(state, state.P1);
//        double AstarP2 = AstarSearch(state, state.P2);
//        System.out.println(AstarP2);
        double feature5 = (1.0/(double)AstarSearch(state, state.P2));
        double feature6 = (-1.0/(double)AstarSearch(state, state.P1));
        double feature7 = 1.0/(double)sum(costOfGoals(state, state.P2));
        double feature8 = -1.0/(double)state.P2.getPlanks();
//        System.out.println(feature7);
//        if (feature5 < 1) {
//            feature5 = -feature5;
//        }
//        double feature6 = (double)state.P2.getPlanks();
        double weight1 = 6;
        double weight2 = 8;
        double weight3 = 14.5;
        double weight4 = 5.5;
        double weight5 = 4;
        double weight6 = 8;
        double weight7 = 10;
        double weight8 = 0.5;
        double opponentWin = mahhatanHeuristicToScore(state.P1);
        weight4 = 1.5*opponentWin;
        Random generator = new Random();
        double number = generator.nextDouble();
        return feature5*weight5 + weight7*feature7 + weight6*feature6 +weight8*feature8;
//        return feature3*weight3+feature4*weight4+feature5*weight5;
//        return feature4*weight4+feature2*weight2+feature3*weight3;
    }


    public Action valueIteration(GameState state, GameState.Player currentPlayer, GameState.Player opponent) {
        int depth = 2;
        double maxValue = -999999.0;
        Action result = new Action(2, new Point(0,0), 0,0,01);
        Point locationCopy = new Point(currentPlayer.getPosition());
        double alpha = -999999.0;
        double beta = 999999.0;
        System.out.println("locationcopy x is "+locationCopy.getX());
        System.out.println("locationcopy y is "+locationCopy.getY());
        for (Point p : state.getMoveSuccessors2(locationCopy, opponent)) {
//            System.out.println("P x is "+p.getX());
//            System.out.println("P y is "+p.getY());
            currentPlayer.updatePosition(p);
            double tempValue = value(depth, state, 1, alpha, beta);
            System.out.println("temp value is "+ tempValue);
            if (tempValue > maxValue) {
                System.out.println("x is "+ p.getX());
                System.out.println("y is "+ p.getY());

                maxValue = tempValue;
                result = new Action(0, p, 0, 0, 0);
            }
            currentPlayer.updatePosition(locationCopy);
        }
        if (currentPlayer.getPlanks() == 0) {
            return result;
        }
        for (Map.Entry<GameState.Plank, Boolean> entry : state.getPlankSuccessors().entrySet()) {
            if (!entry.getValue()) {
                continue;
            }
            state.getBoard().removeEdges(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
            if (AstarSearch(state, opponent)==Integer.MAX_VALUE) {
                state.getBoard().addEdges(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
                continue;
            }
            currentPlayer.decrementPlank();
//            currentPlayer.putPlank(state.board, entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
            double tempValue = value(depth, state, 1, alpha, beta);
            System.out.println("x plank is "+ entry.getKey().getX());
            System.out.println("y plank is "+ entry.getKey().getY());
            System.out.println("direction is "+ entry.getKey().getDirection());
            System.out.println("temp value is "+ tempValue);
            if (tempValue > maxValue) {

                maxValue = tempValue;
                result = new Action(1, new Point(0, 0), entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
            }

            state.getBoard().addEdges(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());

            currentPlayer.incrementPlank();
            if (maxValue > beta) return result;
            alpha = Math.max(alpha, maxValue);
        }
        System.out.println("Action Type " + result.getActionType());
        System.out.println("Next Position X" + result.getNextPosition().getX());
        System.out.println("Next Position Y" + result.getNextPosition().getY());
        System.out.println("Plank X " + result.getPlankX());
        System.out.println("Plank Y " + result.getPlankY());
        System.out.println("Plank Direction " + result.getPlankDirection());
        return result;
    }

    public double value(int depth, GameState state, int index, double alpha, double beta) {
        if (state.isWin()) {
            return evaluationFunction(state);
        }
        if (depth == 0) {
            return evaluationFunction(state);
        }
        if (index == 2) {
            return maxValue(depth - 1, state, 0, alpha, beta);
        }
//        GameState.Player currentPlayer = state.getPlayer(1-index);
//        GameState.Player opponent = state.getPlayer(index);

        else {
            return minValue(depth, state, index, alpha, beta);
        }
    }
    public double maxValue(int depth, GameState state, int index, double alpha, double beta) {
        GameState.Player currentPlayer = state.getPlayer(1-index);
        GameState.Player opponent = state.getPlayer(index);
        Point locationCopy = new Point(currentPlayer.getPosition());
        double maxValue = -999999.0;
        if (depth == 0) {
            return evaluationFunction(state);
        }
        for (Point p : state.getMoveSuccessors2(locationCopy, opponent)) {
            currentPlayer.updatePosition(p);
            double tempValue = 0.0;
            if (index != 0) {
                tempValue = value(depth, state, 0, alpha, beta);
            }
            else {
                tempValue = value(depth, state, 1, alpha, beta);
            }
            maxValue = Math.max(tempValue, maxValue);
            currentPlayer.updatePosition(locationCopy);
        }
        if (currentPlayer.getPlanks() == 0) {
            return maxValue;
        }
//        ConcurrentHashMap<GameState.Plank, Boolean> planklist = state.getPlankSuccessors();
        for (Map.Entry<GameState.Plank, Boolean> entry : state.getPlankSuccessors().entrySet()) {
//            GameState.Plank plank = entry.getKey();
            if (entry.getValue() == false) {
                continue;
            }
            state.getBoard().removeEdges(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
            if (AstarSearch(state, opponent)==Integer.MAX_VALUE) {
                state.getBoard().addEdges(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
                continue;
            }
            currentPlayer.decrementPlank();
//            currentPlayer.putPlank(state.board, plank.getX(), plank.getY(), plank.getDirection());
            double tempValue = 0.0;
            if (index != 0) {
                tempValue = value(depth, state, 0, alpha, beta);
            }
            else {
                tempValue = value(depth, state, 1, alpha, beta);
            }
            maxValue = Math.max(tempValue, maxValue);
//            currentPlayer.removePlank(state.board, plank.getX(), plank.getY(), plank.getDirection());
            state.getBoard().addEdges(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
            currentPlayer.incrementPlank();
            if (maxValue > beta) return maxValue;
            alpha = Math.max(alpha, maxValue);
        }
        return maxValue;
    }


    public double minValue(int depth, GameState state, int index, double alpha, double beta) {
        GameState.Player currentPlayer = state.getPlayer(1-index);
        GameState.Player opponent = state.getPlayer(index);
        Point locationCopy = new Point(currentPlayer.getPosition());
        double minValue = 999999.0;
        for (Point p : state.getMoveSuccessors2(locationCopy, opponent)) {
            currentPlayer.updatePosition(p);
            double tempValue = 0.0;
            if (index != 0) {
                tempValue = value(depth, state, index + 1, alpha, beta);
            }
            else {
                tempValue = value(depth, state, index, alpha, beta);
            }
            minValue = Math.min(tempValue, minValue);
            currentPlayer.updatePosition(locationCopy);
        }

        if (currentPlayer.getPlanks() == 0) {
            return minValue;
        }
        for (Map.Entry<GameState.Plank, Boolean> entry : state.getPlankSuccessors().entrySet()) {
            if (entry.getValue() == false) {
                continue;
            }

            state.getBoard().removeEdges(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
            if (AstarSearch(state, opponent)==Integer.MAX_VALUE) {
                state.getBoard().addEdges(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
                continue;
            }
            currentPlayer.decrementPlank();
//            GameState.Plank plank = entry.getKey();
//            currentPlayer.putPlank(state.board, plank.getX(), plank.getY(), plank.getDirection());
            double tempValue = 0.0;
            if (index != 0) {
                tempValue = value(depth, state, index + 1, alpha, beta);
            }
            else {
                tempValue = value(depth, state, index, alpha, beta);
            }
//            currentPlayer.removePlank(state.board, plank.getX(), plank.getY(), plank.getDirection());
            minValue = Math.min(tempValue, minValue);
            state.getBoard().addEdges(entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getDirection());
            currentPlayer.incrementPlank();
            if (minValue < alpha) return minValue;
            beta = Math.min(beta, minValue);
        }
        return minValue;
    }

    class Action {
        int actionType;
        Point nextPosition;
        double plankX;
        double plankY;
        int plankDirection;

        public Action(int action, Point p, double x, double y, int dir) {
            actionType = action;
            nextPosition = new Point(p);
            plankX = x;
            plankY = y;
            plankDirection = dir;
        }

        public int getActionType() {
            return actionType;
        }
        public Point getNextPosition() {
            return nextPosition;
        }
        public double getPlankX() {
            return plankX;
        }
        public double getPlankY() {
            return plankY;
        }
        public int getPlankDirection() {
            return plankDirection;
        }
    }
}
