package searchclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;


public class State
{
    private static final Random RNG = new Random(1);

    /*
        The agent rows, columns, and colors are indexed by the agent number.
        For example, this.agentRows[0] is the row location of agent '0'.
    */
    public int[] agentRows;
    public int[] agentCols;
    public ArrayList<Color> agentColors;

    /*
        The walls, boxes, and goals arrays are indexed from the top-left of the level, row-major order (row, col).
               Col 0  Col 1  Col 2  Col 3
        Row 0: (0,0)  (0,1)  (0,2)  (0,3)  ...
        Row 1: (1,0)  (1,1)  (1,2)  (1,3)  ...
        Row 2: (2,0)  (2,1)  (2,2)  (2,3)  ...
        ...

        For example, this.walls[2] is an array of booleans for the third row.
        this.walls[row][col] is true if there's a wall at (row, col).
    */
    public static boolean[][] walls;
    public char[][] boxes;
    public ArrayList<Color> boxColors;
    public static char[][] goals;

    public final State parent;
    public final Action[] jointAction;
    private final int g;

    private int _hash = 0;

    /**
     * Constructs an initial state.
     * Arguments are not copied, and therefore should not be modified after being passed in.
     */
    public State(int[] agentRows, int[] agentCols, ArrayList<Color> agentColors,
                 char[][] boxes, ArrayList<Color> boxColors
    )
    {
        this.agentRows = agentRows;
        this.agentCols = agentCols;
        this.agentColors = agentColors;
        this.boxes = boxes;
        this.boxColors = boxColors;
        this.parent = null;
        this.jointAction = null;
        this.g = 0;
    }

    /**
     * Constructs the state resulting from applying jointAction in parent.
     * Precondition: Joint action must be applicable and non-conflicting in parent state.
     */
    private State(State parent, Action[] jointAction)
    {
        // Copy parent.
        this.agentRows = Arrays.copyOf(parent.agentRows, parent.agentRows.length);
        this.agentCols = Arrays.copyOf(parent.agentCols, parent.agentCols.length);
        this.agentColors = (ArrayList<Color>)(parent.agentColors);
        //this.walls = new boolean[parent.walls.length][];
        //for (int i = 0; i < parent.walls.length; i++)
        //{
        //    this.walls[i] = Arrays.copyOf(parent.walls[i], parent.walls[i].length);
        //}
        this.boxes = new char[parent.boxes.length][];
        for (int i = 0; i < parent.boxes.length; i++)
        {
            this.boxes[i] = Arrays.copyOf(parent.boxes[i], parent.boxes[i].length);
        }
        this.boxColors = new ArrayList<Color>(parent.boxColors);

        //this.goals = new char[parent.goals.length][];
        //for (int i = 0; i < parent.goals.length; i++)
        //{
        //   this.goals[i] = Arrays.copyOf(parent.goals[i], parent.goals[i].length);
        //}
        this.parent = parent;
        this.jointAction = Arrays.copyOf(jointAction, jointAction.length);
        this.g = parent.g + 1;

        // Apply each action.
        int numAgents = this.agentRows.length;
        for (int agent = 0; agent < numAgents; ++agent)
        {
            Action action = jointAction[agent];
            char box;

            switch (action.type)
            {
                case NoOp:
                    break;

                case Move:
                    this.agentRows[agent] += action.agentRowDelta;
                    this.agentCols[agent] += action.agentColDelta;
                    break;

                case Push:
                    this.agentRows[agent] += action.agentRowDelta;
                    this.agentCols[agent] += action.agentColDelta;
                    box = this.boxes[this.agentRows[agent]][this.agentCols[agent]];
                    this.boxes[this.agentRows[agent]][this.agentCols[agent]] = 0;
                    this.boxes[this.agentRows[agent] + action.boxRowDelta]
                              [this.agentCols[agent] + action.boxColDelta] = box;
                    break;

                case Pull:
                    box = this.boxes[this.agentRows[agent] + action.boxRowDelta]
                                    [this.agentCols[agent] + action.boxColDelta];
                    this.boxes[this.agentRows[agent] + action.boxRowDelta]
                              [this.agentCols[agent] + action.boxColDelta] = 0;
                    this.boxes[this.agentRows[agent]][this.agentCols[agent]] = box;
                    this.agentRows[agent] += action.agentRowDelta;
                    this.agentCols[agent] += action.agentColDelta;
                    break;
            }
        }
    }

    public int g()
    {
        return this.g;
    }

    public boolean isGoalState()
    {
        for (int row = 1; row < goals.length - 1; row++)
        {
            for (int col = 1; col < goals[row].length - 1; col++)
            {
                char goal = goals[row][col];

                if ('A' <= goal && goal <= 'Z' && this.boxes[row][col] != goal)
                {
                    return false;
                }
                else if ('0' <= goal && goal <= '9' &&
                         !(this.agentRows[goal - '0'] == row && this.agentCols[goal - '0'] == col))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<State> getExpandedStates()
    {
        int numAgents = this.agentRows.length;

        // Determine list of applicable actions for each individual agent.
        Action[][] applicableActions = new Action[numAgents][];
        for (int agent = 0; agent < numAgents; ++agent)
        {
            ArrayList<Action> agentActions = new ArrayList<>(Action.values().length);
            for (Action action : Action.values())
            {
                if (this.isApplicable(agent, action))
                {
                    agentActions.add(action);
                }
            }
            applicableActions[agent] = agentActions.toArray(new Action[0]);
        }

        // Iterate over joint actions, check conflict and generate child states.
        Action[] jointAction = new Action[numAgents];
        int[] actionsPermutation = new int[numAgents];
        ArrayList<State> expandedStates = new ArrayList<>(16);
        while (true)
        {
            for (int agent = 0; agent < numAgents; ++agent)
            {
                jointAction[agent] = applicableActions[agent][actionsPermutation[agent]];
            }

            if (!this.isConflicting(jointAction))
            {
                expandedStates.add(new State(this, jointAction));
            }

            // Advance permutation.
            boolean done = false;
            for (int agent = 0; agent < numAgents; ++agent)
            {
                if (actionsPermutation[agent] < applicableActions[agent].length - 1)
                {
                    ++actionsPermutation[agent];
                    break;
                }
                else
                {
                    actionsPermutation[agent] = 0;
                    if (agent == numAgents - 1)
                    {
                        done = true;
                    }
                }
            }

            // Last permutation?
            if (done)
            {
                break;
            }
        }

        Collections.shuffle(expandedStates, State.RNG);
        return expandedStates;
    }

    private boolean isApplicable(int agent, Action action)
    {
        int agentRow = this.agentRows[agent];
        int agentCol = this.agentCols[agent];
        Color agentColor = this.agentColors.get(agent);
        int boxRow;
        int boxCol;
        char box;
        int destinationRow;
        int destinationCol;
        switch (action.type)
        {
            case NoOp:
                return true;

            case Move:
                destinationRow = agentRow + action.agentRowDelta;
                destinationCol = agentCol + action.agentColDelta;
                return this.cellIsFree(destinationRow, destinationCol);

            case Push:
                boxRow = agentRow + action.agentRowDelta;
                boxCol = agentCol + action.agentColDelta;
                box = this.boxes[boxRow][boxCol];
                if (box == 0 || agentColor != this.boxColors.get(box - 'A'))
                {
                    return false;
                }
                destinationRow = boxRow + action.boxRowDelta;
                destinationCol = boxCol + action.boxColDelta;
                return this.cellIsFree(destinationRow, destinationCol);

            case Pull:
                boxRow = agentRow + action.boxRowDelta;
                boxCol = agentCol + action.boxColDelta;
                box = this.boxes[boxRow][boxCol];
                if (box == 0 || agentColor != this.boxColors.get(box - 'A'))
                {
                    return false;
                }
                destinationRow = agentRow + action.agentRowDelta;
                destinationCol = agentCol + action.agentColDelta;
                return this.cellIsFree(destinationRow, destinationCol);
        }

        // Unreachable:
        return false;
    }

    private boolean isConflicting(Action[] jointAction)
    {
        int numAgents = this.agentRows.length;

        int[] destinationRows = new int[numAgents];
        int[] destinationCols = new int[numAgents];
        int[] boxRows = new int[numAgents];
        int[] boxCols = new int[numAgents];

        // Collect cells to be occupied and boxes to be moved.
        for (int agent = 0; agent < numAgents; ++agent)
        {
            Action action = jointAction[agent];
            int agentRow = this.agentRows[agent];
            int agentCol = this.agentCols[agent];
            int boxRow;
            int boxCol;

            switch (action.type)
            {
                case NoOp:
                    break;

                case Move:
                    destinationRows[agent] = agentRow + action.agentRowDelta;
                    destinationCols[agent] = agentCol + action.agentColDelta;
                    boxRows[agent] = agentRow; // Distinct dummy value.
                    boxCols[agent] = agentCol; // Distinct dummy value.
                    break;

                case Push:
                    boxRow = agentRow + action.agentRowDelta;
                    boxCol = agentCol + action.agentColDelta;
                    boxRows[agent] = boxRow;
                    boxCols[agent] = boxCol;
                    destinationRows[agent] = boxRow + action.boxRowDelta;
                    destinationCols[agent] = boxCol + action.boxColDelta;
                    break;

                case Pull:
                    boxRow = agentRow + action.boxRowDelta;
                    boxCol = agentCol + action.boxColDelta;
                    boxRows[agent] = boxRow;
                    boxCols[agent] = boxCol;
                    destinationRows[agent] = agentRow + action.agentRowDelta;
                    destinationCols[agent] = agentCol + action.agentColDelta;
                    break;
            }
        }

        for (int a1 = 0; a1 < numAgents; ++a1)
        {
            if (jointAction[a1] == Action.NoOp)
            {
                continue;
            }

            for (int a2 = a1 + 1; a2 < numAgents; ++a2)
            {
                if (jointAction[a2] == Action.NoOp)
                {
                    continue;
                }

                // Moving into same cell?
                if (destinationRows[a1] == destinationRows[a2] && destinationCols[a1] == destinationCols[a2])
                {
                    return true;
                }

                // Moving same box?
                if (boxRows[a1] == boxRows[a2] && boxCols[a1] == boxCols[a2])
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean cellIsFree(int row, int col)
    {
        return !walls[row][col] && this.boxes[row][col] == 0 && this.agentAt(row, col) == 0;
    }

    private char agentAt(int row, int col)
    {
        for (int i = 0; i < this.agentRows.length; i++)
        {
            if (this.agentRows[i] == row && this.agentCols[i] == col)
            {
                return (char) ('0' + i);
            }
        }
        return 0;
    }

    public Action[][] extractPlan()
    {
        Action[][] plan = new Action[this.g][];
        State state = this;
        while (state.jointAction != null)
        {
            plan[state.g - 1] = state.jointAction;
            state = state.parent;
        }
        return plan;
    }

    @Override
    public int hashCode()
    {
        if (this._hash == 0)
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(this.agentRows);
            result = prime * result + Arrays.hashCode(this.agentCols);
            result = prime * result + this.agentColors.hashCode();
            result = prime * result + Arrays.deepHashCode(walls);
            result = prime * result + Arrays.deepHashCode(this.boxes);
            result = prime * result + this.boxColors.hashCode();
            result = prime * result + Arrays.deepHashCode(goals);
            this._hash = result;
        }
        return this._hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        State other = (State) obj;
        return Arrays.equals(this.agentRows, other.agentRows) &&
               Arrays.equals(this.agentCols, other.agentCols) &&
               this.agentColors.equals(other.agentColors) &&
               Arrays.deepEquals(this.boxes, other.boxes) &&
               this.boxColors.equals(other.boxColors);
    }

    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for (int row = 0; row < walls.length; row++)
        {
            for (int col = 0; col < walls[row].length; col++)
            {
                if (this.boxes[row][col] > 0)
                {
                    s.append(this.boxes[row][col]);
                }
                else if (walls[row][col])
                {
                    s.append("+");
                }
                else if (this.agentAt(row, col) != 0)
                {
                    s.append(this.agentAt(row, col));
                }
                else
                {
                    s.append(" ");
                }
            }
            s.append("\n");
        }
        return s.toString();
    }
}
