class FrontierDFS
    implements Frontier
{

    // emulated Stack operations for DFS by implementing LIFO invariant on 
    // Java ArrayDeque data structure
    private final ArrayDeque<State> stack = new ArrayDeque<>(65536);
    private final HashSet<State> set = new HashSet<>(65536);

    @Override
    public void add(State state)
    {
        // everything is the same here
        this.stack.addLast(state);
        this.set.add(state);
    }

    @Override
    public State pop()
    {
        // call pollLast (pop) rather than pollFirst here
        State state = this.stack.pollLast();
        this.set.remove(state);
        return state;
    }

    // nothing out of the ordinary below... use FrontierBFS class as template
    //
 
 