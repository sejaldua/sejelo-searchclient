package searchclient;

import java.util.Comparator;

import java.util.Map;
import java.util.HashMap;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public abstract class Heuristic
        implements Comparator<State>
{
    private final char[][] goals;

    public Heuristic(State initialState)
    {
        this.goals = initialState.goals;

    }
// goals and boxes
    public int h(State n)
    {
        char[][] boxes = n.boxes;

        Map<Character, Integer> boxX = new HashMap<Character, Integer>();
        Map<Character, Integer> boxY = new HashMap<Character, Integer>();
        Map<Character, Integer> goalX = new HashMap<Character, Integer>();
        Map<Character, Integer> goalY = new HashMap<Character, Integer>();

        int i, j;
        for (i = 0; i < this.goals.length; ++i) {
            for (j = 0; j < this.goals[i].length; ++j) {
                if ('A' <= boxes[i][j] && 'Z' <= boxes[i][j]) {
                    boxX.put(boxes[i][j], j);
                    boxY.put(boxes[i][j], i);
                }
                if ('A' <= this.goals[i][j] && 'Z' <= this.goals[i][j]) {
                    goalX.put(this.goals[i][j], j);
                    goalY.put(this.goals[i][j], i);                }
            }
        }

        int manhattanDistance = 0;
        List<Character> keys = new ArrayList<Character>(boxX.keySet());
        int xDist, yDist;
        for (i = 0; i < keys.size(); ++i) {
            xDist = boxX.get(keys.get(i)) - goalX.get(keys.get(i));
            yDist = boxY.get(keys.get(i)) - goalY.get(keys.get(i));

            manhattanDistance += Math.abs(xDist) + Math.abs(yDist);
        }

        return manhattanDistance;
    }

    public abstract int f(State n);

    @Override
    public int compare(State n1, State n2)
    {
        return this.f(n1) - this.f(n2);
    }
}

class HeuristicAStar
        extends Heuristic
{
    public HeuristicAStar(State initialState)
    {
        super(initialState);
    }

    @Override
    public int f(State n)
    {
        return n.g() + this.h(n);
    }

    @Override
    public String toString()
    {
        return "A* evaluation";
    }
}

class HeuristicWeightedAStar
        extends Heuristic
{
    private int w;

    public HeuristicWeightedAStar(State initialState, int w)
    {
        super(initialState);
        this.w = w;
    }

    @Override
    public int f(State n)
    {
        return n.g() + this.w * this.h(n);
    }

    @Override
    public String toString()
    {
        return String.format("WA*(%d) evaluation", this.w);
    }
}

class HeuristicGreedy
        extends Heuristic
{
    public HeuristicGreedy(State initialState)
    {
        super(initialState);
    }

    @Override
    public int f(State n)
    {
        return this.h(n);
    }

    @Override
    public String toString()
    {
        return "greedy evaluation";
    }
}
