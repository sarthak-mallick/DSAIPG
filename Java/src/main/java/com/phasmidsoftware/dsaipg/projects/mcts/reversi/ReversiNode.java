package com.phasmidsoftware.dsaipg.projects.mcts.reversi;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ReversiNode implements Node<Reversi> {

    public ReversiNode(State<Reversi> state, ReversiNode parent) {
        this.state = state;
        this.parent = parent;
        this.children = new ArrayList<>();
        initializeNodeData();  // IMPORTANT: restore initialization
    }

    public ReversiNode(State<Reversi> state) {
        this(state, null);
    }

    public ReversiNode getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public State<Reversi> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == state.game().opener();
    }

    @Override
    public Collection<Node<Reversi>> children() {
        return children;
    }

    @Override
    public void addChild(State<Reversi> state) {
        children.add(new ReversiNode(state, this));
    }

    @Override
    public void backPropagate() {
        // This is handled externally in the ReversiMCTS algorithm
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    private void initializeNodeData() {
        if (state.isTerminal()) {
            playouts = 1;
            Optional<Integer> winner = state.winner();
            if (winner.isPresent())
                wins = 2; // If there's a winner, it's worth 2 points
            else
                wins = 1; // Draw is worth 1 point
        }
    }

    private final State<Reversi> state;
    private final ReversiNode parent;
    private final ArrayList<Node<Reversi>> children;

    int wins;
    int playouts;
}