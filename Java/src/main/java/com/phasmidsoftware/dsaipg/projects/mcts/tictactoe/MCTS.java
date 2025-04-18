package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

public class MCTS {
    final TicTacToeNode root;
    private final Random random = new Random();

    public static void main(String[] args) {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        MCTS mcts = new MCTS(root);
        mcts.runGame(); // Launch game simulation
    }

    public MCTS(TicTacToeNode root) {
        this.root = root;
    }

    public Node<TicTacToe> runMCTS(int iterations) {
        for (int i = 0; i < iterations; i++) {
            Node<TicTacToe> selected = select(root);
            Node<TicTacToe> expanded = expand(selected);
            int result = simulate(expanded);
            backPropagate(expanded, result);
        }
        return bestChild(root);
    }

    Node<TicTacToe> select(Node<TicTacToe> node) {
        while (!node.isLeaf() && !node.state().isTerminal()) {
            node = bestUCTChild(node);
        }
        return node;
    }

    Node<TicTacToe> expand(Node<TicTacToe> node) {
        if (!node.state().isTerminal() && node.children().isEmpty()) {
            Collection<Move<TicTacToe>> moves = node.state().moves(node.state().player());
            for (Move<TicTacToe> move : moves) {
                node.addChild(node.state().next(move));
            }
        }
        if (!node.children().isEmpty()) {
            return node.children().iterator().next();
        }
        return node;
    }

    Node<TicTacToe> bestUCTChild(Node<TicTacToe> node) {
        final double C = Math.sqrt(2);
        return node.children().stream()
                .max(Comparator.comparingDouble(child -> {
                    if (child.playouts() == 0)
                        return Double.POSITIVE_INFINITY;
                    return ((double) child.wins() / child.playouts()) +
                            C * Math.sqrt(Math.log(node.playouts() + 1) / child.playouts());
                }))
                .orElse(node);
    }

    int simulate(Node<TicTacToe> node) {
        State<TicTacToe> currentState = node.state();
        while (!currentState.isTerminal()) {
            List<Move<TicTacToe>> moves = new ArrayList<>(currentState.moves(currentState.player()));
            if (moves.isEmpty()) break;
            Move<TicTacToe> move = moves.get(random.nextInt(moves.size()));
            currentState = currentState.next(move);
        }
        return currentState.winner().orElse(-1);
    }

    void backPropagate(Node<TicTacToe> node, int winner) {
        TicTacToeNode currentNode = (TicTacToeNode) node;
        while (currentNode != null) {
            currentNode.playouts += 1;
            Optional<Integer> nodeWinner = currentNode.state().winner();
            if (nodeWinner.isPresent() && nodeWinner.get() == winner)
                currentNode.wins += 2;
            else if (winner == -1)
                currentNode.wins += 1;
            currentNode = currentNode.getParent();
        }
    }

    Node<TicTacToe> bestChild(Node<TicTacToe> node) {
        return node.children().stream()
                .max(Comparator.comparingInt(Node::playouts))
                .orElse(node);
    }

    public void runGame() {
        Node<TicTacToe> currentNode = root;

        while (!currentNode.state().isTerminal()) {
            System.out.println("Current board:");
            System.out.println(currentNode.state());

            // Let MCTS decide the next move
            MCTS mcts = new MCTS((TicTacToeNode) currentNode);
            Node<TicTacToe> bestMove = mcts.runMCTS(5000);

            currentNode = bestMove;
        }

        System.out.println("Final board:");
        System.out.println(currentNode.state());

        Optional<Integer> winner = currentNode.state().winner();
        if (winner.isPresent()) {
            System.out.println("Player " + winner.get() + " wins!");
        } else {
            System.out.println("It's a draw!");
        }
    }
}
