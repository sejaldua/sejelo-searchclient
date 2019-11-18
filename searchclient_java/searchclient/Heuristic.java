package searchclient;

import java.util.Comparator;

import java.util.Map;
import java.util.HashMap;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public abstract class Heuristic
        implements Comparator<State>
{
    //heuristic 4 - distance maps

    // Class to hold location coordinates (x, y)
    public static class Pair {
        public final int x;
        public final int y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private Map<Character, List<Pair>> goals;
    private int[][][][] distanceMaps;
    private boolean[][] walls;

    public int[][] createDistanceMap(Pair start) {
        int height = walls.length;
        int width = walls[0].length;
        boolean[][] checked = new boolean[height][width];
        int x, y;
        for (y = 0; y < height; ++y) {
            for (x = 0; x < width; ++x) {
                checked[y][x] = this.walls[y][x];
            }
        }

        int[][] distanceMap = new int[height][width];
        ArrayList<Pair> frontier = new ArrayList<Pair>();
        ArrayList<Pair> nextFrontier;
        frontier.add(start);
        int currentDist = 0;
        int i;

        while (!frontier.isEmpty()) {
            nextFrontier = new ArrayList<Pair>();
            for (i = 0; i < frontier.size(); ++i) {
                x = frontier.get(i).x;
                y = frontier.get(i).y;
                // check north
                if ((x == 0 || y == 0) || (x == width - 1 || y == height - 1)) {
                  continue;
                }
                //System.out.println(checked[y - 1][x]);
                if (!checked[y + 1][x]) {
                    nextFrontier.add(new Pair(x, y + 1));
                }
                // south
                if (!checked[y - 1][x]) {
                    nextFrontier.add(new Pair(x, y - 1));
                }
                // east
                if (!checked[y][x + 1]) {
                    nextFrontier.add(new Pair(x + 1, y));
                }
                // west
                if (!checked[y][x - 1]) {
                    nextFrontier.add(new Pair(x - 1, y));
                }
                checked[y][x] = true;
                distanceMap[y][x] = currentDist;
            }
            ++currentDist;
            frontier = new ArrayList<Pair>(nextFrontier);
        }

        return distanceMap;
    }

    private int manhattanDistance(Pair p1, Pair p2) {
         return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    public Heuristic(State initialState)
    {
        this.walls = initialState.walls;
        int height = this.walls.length;
        int width = this.walls[0].length;
        this.goals = new HashMap<Character, List<Pair>>();
        this.distanceMaps = new int[height][width][height][width];
        int x, y;
        char id;
        for (y = 0; y < height; ++y) {
            for (x = 0; x < width; ++x) {
                id = initialState.goals[y][x];
                if ('A' <= id && id <= 'Z') {
                    if (!this.goals.containsKey(id))
                        this.goals.put(id, new ArrayList<Pair>());
                    this.goals.get(id).add(new Pair(x, y));
                }

                // generate distance maps for every tile that is not a wall
                if (!this.walls[y][x])
                    this.distanceMaps[y][x] = createDistanceMap(new Pair(x ,y));
            }
        }
    }

    public int h(State n)
    {
        Map<Character, List<Pair>> boxes = new HashMap<Character, List<Pair>>();

        // Creates a dictionary with values being lists of coord pairs for every
        //  box of the same label
        int x, y;
        char id;
        for (y = 0; y < n.boxes.length; ++y) {
            for (x = 0; x < n.boxes[y].length; ++x) {
                id = n.boxes[y][x];
                if ('A' <= id && id <= 'Z') {
                    if (!boxes.containsKey(id)) {
                        boxes.put(id, new ArrayList<Pair>());
                    }
                    boxes.get(id).add(new Pair(x, y));
                }
            }
        }

        int i, j, k, dist, closestBoxDist, closestBoxIndex, distanceTotal;
        List<Integer> alreadyLinkedBoxes;
        closestBoxDist = closestBoxIndex = distanceTotal = 0;
        Pair goalPair, boxPair;
        Pair closestPair = new Pair(0, 0);
        List<int[]> boxGoalPairs = new ArrayList<int[]>();
        List<Character> keys = new ArrayList<Character>(boxes.keySet());
        id = keys.get(0);

        // The furthest a pair could possibly be is length * width - 2 apart, so
        //  there will always be a pair that is closer than this initial value
        int closestPairDist = n.boxes.length * n.boxes[0].length;

        // loops through every box/goal label
        for (i = 0; i < keys.size(); ++i) {
            id = keys.get(i);
            alreadyLinkedBoxes = new ArrayList<Integer>();
            // loops through every goal with that label
            for (j = 0; j < this.goals.get(id).size(); ++j) {
                goalPair = this.goals.get(id).get(j);
                // Again, this value will always be replaced
                closestBoxDist = n.boxes.length * n.boxes[0].length;

                // loops through every box with the same label
                for (k = 0; k < boxes.get(id).size(); ++k) {
                    if (alreadyLinkedBoxes.contains(k))
                        continue;
                    boxPair = boxes.get(id).get(k);
                    dist = this.distanceMaps[goalPair.y][goalPair.x][boxPair.y][boxPair.x];
                    // If this is the closest box to the goal yet
                    if (dist < closestBoxDist) {
                        closestBoxIndex = k;
                        closestBoxDist = dist;
                        closestPair = boxPair;
                    }
                }

                // We remove the box that was just used since it is already linked to a goal
                alreadyLinkedBoxes.add(closestBoxIndex);
                boxGoalPairs.add(new int[]{closestBoxDist, closestPair.x, closestPair.y, goalPair.x, goalPair.y});
                //distanceTotal += closestBoxDist;
            }
        }

        //System.out.println(Arrays.deepToString(this.distanceMaps[n.agentCols[0]][n.agentRows[0]]).replace("], ", "]\n"));

        // Sort box-goal pairs by the distance between them
        // boxGoalPairs.sort((l1,l2) -> Integer.compare(l1[0], l2[0]));

        // add distance from agent to closest box
        Pair agentPair = new Pair(n.agentCols[0], n.agentRows[0]);
        distanceTotal += this.distanceMaps[agentPair.y][agentPair.x][boxGoalPairs.get(0)[2]][boxGoalPairs.get(0)[1]];
        //distanceTotal += manhattanDistance(agentPair, new Pair(boxGoalPairs.get(0)[1], boxGoalPairs.get(0)[2]));
        //System.out.println("\n\n");
        for (i = 0; i < boxGoalPairs.size(); ++i) {
            //System.out.println(Arrays.toString(boxGoalPairs.get(i)));
            // add distance from goal to paired box
            distanceTotal += boxGoalPairs.get(i)[0];
            //distanceTotal += manhattanDistance(new Pair(boxGoalPairs.get(i)[1], boxGoalPairs.get(i)[2]), new Pair(boxGoalPairs.get(i)[3], boxGoalPairs.get(i)[4]));
            // add distance from goal to next box
            //if (i < boxGoalPairs.size() - 1) {
                //distanceTotal += this.distanceMaps[boxGoalPairs.get(i)[4]][boxGoalPairs.get(i)[3]][boxGoalPairs.get(i + 1)[2]][boxGoalPairs.get(i + 1)[1]];
                //distanceTotal += manhattanDistance(new Pair(boxGoalPairs.get(i)[4], boxGoalPairs.get(i)[3]), new Pair(boxGoalPairs.get(i+1)[2], boxGoalPairs.get(i+1)[1]));
            //}
        }

        return distanceTotal;
    }

    // // ---------- FIXED AGENT POSITION SCORE | HEURISTIC #3 ----------------
    // //Class to hold location coordinates (x, y)
    // public static class Pair {
    //     public final int x;
    //     public final int y;

    //     public Pair(int x, int y) {
    //         this.x = x;
    //         this.y = y;
    //     }
    // }

    // private Map<Character, List<Pair>> goals;

    // public Heuristic(State initialState)
    // {
    //     this.goals = new HashMap<Character, List<Pair>>();

    //     int x, y;
    //     char id;
    //     for (y = 0; y < initialState.goals.length; ++y) {
    //         for (x = 0; x < initialState.goals[y].length; ++x) {
    //             id = initialState.goals[y][x];
    //             if ('A' <= id && id <= 'Z') {
    //                 if (!this.goals.containsKey(id)) {
    //                     this.goals.put(id, new ArrayList<Pair>());
    //                 }
    //                 this.goals.get(id).add(new Pair(x, y));
    //             }
    //         }
    //     }
    // }

    // private int manhattanDistance(Pair p1, Pair p2) {
    //     return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    // }

    // public int h(State n)
    // {
    //     Map<Character, List<Pair>> boxes = new HashMap<Character, List<Pair>>();

    //     // Creates a dictionary with values being lists of coord pairs for every
    //     //  box of the same label
    //     int x, y;
    //     char id;
    //     for (y = 0; y < n.boxes.length; ++y) {
    //         for (x = 0; x < n.boxes[y].length; ++x) {
    //             id = n.boxes[y][x];
    //             if ('A' <= id && id <= 'Z') {
    //                 if (!boxes.containsKey(id)) {
    //                     boxes.put(id, new ArrayList<Pair>());
    //                 }
    //                 boxes.get(id).add(new Pair(x, y));
    //             }
    //         }
    //     }

    //     int i, j, k, minDistBoxIndex, minDist, dist;
    //     int manhattanTotal = 0;
    //     Pair goalPair;
    //     List<Character> keys = new ArrayList<Character>(boxes.keySet());
    //     // The following 2 variables are intialized to the first entries to ensure
    //     //   to ensure the minimum distance pair actually exists
    //     Pair minDistTotalPair = boxes.get(keys.get(0)).get(0);
    //     int minDistTotal = manhattanDistance(this.goals.get(keys.get(0)).get(0),
    //                                          minDistTotalPair);
    //     // loops through every box/goal label
    //     for (i = 0; i < keys.size(); ++i) {
    //         // loops through every goal with that label
    //         for (j = 0; j < this.goals.get(keys.get(i)).size(); ++j) {
    //             goalPair = this.goals.get(keys.get(i)).get(j);
    //             minDistBoxIndex = 0;
    //             minDist = manhattanDistance(goalPair, boxes.get(keys.get(i)).get(0));
    //             if (minDist < minDistTotal && minDist > 0) {
    //                 minDistTotal = minDist;
    //                 minDistTotalPair = boxes.get(keys.get(i)).get(0);
    //             }
    //             // loops through every box with the same label
    //             for (k = 1; k < boxes.get(keys.get(i)).size(); ++k) {
    //                 dist = manhattanDistance(goalPair, boxes.get(keys.get(i)).get(k));
    //                 if (dist < minDist) {
    //                     minDistBoxIndex = k;
    //                     minDist = dist;
    //                     // We are also tracking the lowest distance box/goal pair overall
    //                     //  but it cannot be a box/goal pair already solved
    //                     if (minDist < minDistTotal && minDist > 0) {
    //                       minDistTotal = minDist;
    //                       minDistTotalPair = boxes.get(keys.get(i)).get(k);
    //                     }
    //                 }
    //             }

    //             manhattanTotal += minDist;
    //             // We remove the box that was just used since it is already linked to a goal
    //             boxes.get(keys.get(i)).remove(minDistBoxIndex);
    //         }
    //     }

    //     // Loop through every agent
    //     for (i = 0; i < n.agentCols.length; ++i) {
    //         Pair agentPair = new Pair(n.agentCols[i], n.agentRows[i]);
    //         manhattanTotal += manhattanDistance(agentPair, minDistTotalPair);
    //     }

    //     return manhattanTotal;
    // }


    // ---------- LINKS | HEURISTIC #2 ----------------
    // Class to hold location coordinates (x, y)
    // public static class Pair {
    //     public final int x;
    //     public final int y;
    //
    //     public Pair(int x, int y) {
    //         this.x = x;
    //         this.y = y;
    //     }
    // }
    //
    // private Map<Character, List<Pair>> goals;
    //
    // public Heuristic(State initialState)
    // {
    //     this.goals = new HashMap<Character, List<Pair>>();
    //
    //     int x, y;
    //     char id;
    //     for (y = 0; y < initialState.goals.length; ++y) {
    //         for (x = 0; x < initialState.goals[y].length; ++x) {
    //             id = initialState.goals[y][x];
    //             if ('A' <= id && id <= 'Z') {
    //                 if (!this.goals.containsKey(id)) {
    //                     this.goals.put(id, new ArrayList<Pair>());
    //                 }
    //                 this.goals.get(id).add(new Pair(x, y));
    //             }
    //         }
    //     }
    // }
    //
    // private int manhattanDistance(Pair p1, Pair p2) {
    //     return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    // }
    //
    // public int h(State n)
    // {
    //     Map<Character, List<Pair>> boxes = new HashMap<Character, List<Pair>>();
    //
    //     // Creates a dictionary with values being lists of coord pairs for every
    //     //  box of the same label
    //     int x, y;
    //     char id;
    //     for (y = 0; y < n.boxes.length; ++y) {
    //         for (x = 0; x < n.boxes[y].length; ++x) {
    //             id = n.boxes[y][x];
    //             if ('A' <= id && id <= 'Z') {
    //                 if (!boxes.containsKey(id)) {
    //                     boxes.put(id, new ArrayList<Pair>());
    //                 }
    //                 boxes.get(id).add(new Pair(x, y));
    //             }
    //         }
    //     }
    //
    //     int i, j, k, minDistBoxIndex, minDist, dist;
    //     int manhattanTotal = 0;
    //     Pair goalPair;
    //     List<Character> keys = new ArrayList<Character>(boxes.keySet());
    //     // The following 2 variables are intialized to the first entries to ensure
    //     //   to ensure the minimum distance pair actually exists
    //     Pair minDistTotalPair = boxes.get(keys.get(0)).get(0);
    //     int minDistTotal = manhattanDistance(this.goals.get(keys.get(0)).get(0),
    //                                          minDistTotalPair);
    //     // loops through every box/goal label
    //     for (i = 0; i < keys.size(); ++i) {
    //         // loops through every goal with that label
    //         for (j = 0; j < this.goals.get(keys.get(i)).size(); ++j) {
    //             goalPair = this.goals.get(keys.get(i)).get(j);
    //             minDistBoxIndex = 0;
    //             minDist = manhattanDistance(goalPair, boxes.get(keys.get(i)).get(0));
    //
    //             // loops through every box with the same label
    //             for (k = 1; k < boxes.get(keys.get(i)).size(); ++k) {
    //                 dist = manhattanDistance(goalPair, boxes.get(keys.get(i)).get(k));
    //                 if (dist < minDist) {
    //                     minDistBoxIndex = k;
    //                     minDist = dist;
    //                     // We are also tracking the lowest distance box/goal pair overall
    //                     //  but it cannot be a box/goal pair already solved
    //                     if (minDist < minDistTotal) {
    //                       minDistTotal = minDist;
    //                       minDistTotalPair = boxes.get(keys.get(i)).get(k);
    //                     }
    //                 }
    //             }
    //
    //             manhattanTotal += minDist;
    //             // We remove the box that was just used since it is already linked to a goal
    //             boxes.get(keys.get(i)).remove(minDistBoxIndex);
    //         }
    //     }
    //
    //     // Loop through every agent
    //     for (i = 0; i < n.agentCols.length; ++i) {
    //         Pair agentPair = new Pair(n.agentCols[i], n.agentRows[i]);
    //         manhattanTotal += manhattanDistance(agentPair, minDistTotalPair);
    //     }
    //
    //     return manhattanTotal;
    // }


    // -------- MANHATTAN DISTANCE | HEURISTIC #1 -------------
    // private final char[][] goals;

    // public Heuristic(State initialState)
    // {
    //     this.goals = initialState.goals;
    // }

    // public int h(State n)
    // {
    //     char[][] boxes = n.boxes;

    //     Map<Character, Integer> boxX = new HashMap<Character, Integer>();
    //     Map<Character, Integer> boxY = new HashMap<Character, Integer>();
    //     Map<Character, Integer> goalX = new HashMap<Character, Integer>();
    //     Map<Character, Integer> goalY = new HashMap<Character, Integer>();

    //     int i, j;
    //     for (i = 0; i < this.goals.length; ++i) {
    //         for (j = 0; j < this.goals[i].length; ++j) {
    //             if ('A' <= boxes[i][j] && boxes[i][j] <= 'Z') {
    //                 boxX.put(boxes[i][j], j);
    //                 boxY.put(boxes[i][j], i);
    //             }
    //             if ('A' <= this.goals[i][j] && this.goals[i][j] <= 'Z') {
    //                 goalX.put(this.goals[i][j], j);
    //                 goalY.put(this.goals[i][j], i);                }
    //         }
    //     }

    //     int manhattanDistance = 0;
    //     List<Character> keys = new ArrayList<Character>(boxX.keySet());
    //     int xDist, yDist;
    //     for (i = 0; i < keys.size(); ++i) {
    //         xDist = boxX.get(keys.get(i)) - goalX.get(keys.get(i));
    //         yDist = boxY.get(keys.get(i)) - goalY.get(keys.get(i));

    //         manhattanDistance += Math.abs(xDist) + Math.abs(yDist);
    //     }

    //     return manhattanDistance;
    // }

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

/*
// FIRST HEURISTIC IMPLEMENTATION (bug fixed - regenerate stats)
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
            if ('A' <= boxes[i][j] && boxes[i][j] <= 'Z') {
                boxX.put(boxes[i][j], j);
                boxY.put(boxes[i][j], i);
            }
            if ('A' <= this.goals[i][j] && this.goals[i][j] <= 'Z') {
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
*/
