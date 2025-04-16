package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class TicTacToeNode implements Node<TicTacToe> {


    public TicTacToeNode(State<TicTacToe> state, TicTacToeNode parent) {
        this.state = state;
        this.parent = parent;
        this.children = new ArrayList<>();
        initializeNodeData();  // IMPORTANT: restore initialization
    }

    public TicTacToeNode(State<TicTacToe> state) {
        this(state, null);
    }

    public TicTacToeNode getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public State<TicTacToe> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == state.game().opener();
    }

    @Override
    public Collection<Node<TicTacToe>> children() {
        return children;
    }

    @Override
    public void addChild(State<TicTacToe> state) {
        children.add(new TicTacToeNode(state, this));
    }

    @Override
    public void backPropagate() {
        // handled externally in MCTS
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    // Crucial initialization method
    private void initializeNodeData() {
        if (state.isTerminal()) {
            playouts = 1;
            Optional<Integer> winner = state.winner();
            if (winner.isPresent())
                wins = 2;
            else
                wins = 1;
        }
    }

    private final State<TicTacToe> state;
    private final TicTacToeNode parent;
    private final ArrayList<Node<TicTacToe>> children;

    int wins;      // package-private
    int playouts;  // package-private

}
