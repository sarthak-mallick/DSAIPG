package com.phasmidsoftware.dsaipg.projects.mcts.reversi;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

public class ReversiMCTS {
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        State<Reversi> initialState = new Reversi().start();
        ReversiNode initialRoot = new ReversiNode(initialState);
        ReversiMCTS mcts = new ReversiMCTS(initialRoot);
        mcts.runGame();
        long endTime = System.nanoTime();
        System.out.println("Time in ms: " + (endTime-startTime)/1000000);
    }

    public ReversiMCTS(ReversiNode root) {
        this.root = root;
    }

    public Node<Reversi> runMCTS(int iterations) {
        for (int i=0; i<iterations; i++) {
            Node<Reversi> selected = select(root);
            Node<Reversi> expanded = expand(selected);
            int result = simulate(expanded);
            backPropagate(expanded, result);
        }
        return bestChild(root);
    }

    Node<Reversi> select(Node<Reversi> node) {
        while (!node.isLeaf() && !node.state().isTerminal())
            node = bestUCTChild(node);
        return node;
    }

    Node<Reversi> expand(Node<Reversi> node) {
        if (!node.state().isTerminal() && node.children().isEmpty()) {
            Collection<Move<Reversi>> moves = node.state().moves(node.state().player());
            for (Move<Reversi> move : moves)
                node.addChild(node.state().next(move));
        }
        if (!node.children().isEmpty())
            return node.children().iterator().next();
        return node;
    }

    Node<Reversi> bestUCTChild(Node<Reversi> node) {
        return node.children().stream().max(Comparator.comparingDouble(child -> {
            if (child.playouts() == 0)
                return Double.POSITIVE_INFINITY;
            double exploitation = (double)child.wins()/child.playouts();
            double exploration = Math.sqrt(explorationConstant)*Math.sqrt(Math.log(node.playouts()+1)/child.playouts());
            return exploitation+exploration;
        })).orElse(node);
    }

    int simulate(Node<Reversi> node) {
        State<Reversi> currentState = node.state();
        int currentPlayer = currentState.player();
        boolean alreadySkipped = false; // Track if both players skipped consecutively
        
        int depth = 0;
        while (!currentState.isTerminal() && depth<maxSimulationDepth) {
            depth++;
            
            ReversiPosition position = ((Reversi.ReversiState)currentState).position();
            if (position.full())
                break; // If board is full, game over
            
            List<Move<Reversi>> currentPlayerMoves = new ArrayList<>(currentState.moves(currentPlayer));
            List<Move<Reversi>> opponentMoves = new ArrayList<>(currentState.moves(1-currentPlayer));
            if (currentPlayerMoves.isEmpty() && opponentMoves.isEmpty())
                break; // Neither player has moves, game is over
            if (currentPlayerMoves.isEmpty()) {// Current player has no moves but opponent does
                if (alreadySkipped)
                    break; // Both players skipped consecutively, game is over
                else {
                    currentPlayer = 1-currentPlayer;
                    alreadySkipped = true;
                    continue;
                }
            }
            alreadySkipped = false; // Reset consecutive skips flag
            
            Move<Reversi> move = currentPlayerMoves.get(random.nextInt(currentPlayerMoves.size()));
            currentState = currentState.next(move);
            currentPlayer = 1-currentPlayer;
        }
        return evaluatePositionWithHeuristics((Reversi.ReversiState)currentState);
    }
    
    // Evaluate the board position with positional heuristics
    // Counts pieces with bonuses for strategic positions
    private int evaluatePositionWithHeuristics(Reversi.ReversiState state) {
        ReversiPosition position = state.position();
        int gridSize = ReversiPosition.gridSize;
        
        double blackScore = position.blackCount;
        double whiteScore = position.whiteCount;
        
        int[][] grid = position.grid;
        for (int i=0; i<gridSize; i++) {
            for (int j=0; j<gridSize; j++) {
                if (grid[i][j] < 0) 
                    continue;

                double bonus = 0;
                if ((i==0 && j==0) || (i==gridSize-1 && j==gridSize-1) || (i==0 && j==gridSize-1) || (i==gridSize-1 && j==0))
                    bonus = 2.0; // Corner bonus
                else if (i==0 || j==0 || i==gridSize-1 || j==gridSize-1)
                    bonus = 0.5; // Edge bonus
                
                if (grid[i][j] == Reversi.WHITE)
                    whiteScore += bonus;
                else if (grid[i][j] == Reversi.BLACK)
                    blackScore += bonus;
            }
        }
        if (blackScore < whiteScore) return 1; // White wins
        else if (whiteScore < blackScore) return 0; // Black wins
        else return -1; // Draw
    }

    void backPropagate(Node<Reversi> node, int winner) {
        ReversiNode currentNode = (ReversiNode) node;
        while (currentNode!=null) {
            currentNode.playouts += 1;
            
            Optional<Integer> nodeWinner = currentNode.state().winner();
            if (nodeWinner.isPresent() && nodeWinner.get()==winner)
                currentNode.wins += 2; // 2 points for a win
            else if (winner == -1)
                currentNode.wins += 1; // 1 point for a draw
            currentNode = currentNode.getParent();
        }
    }

    Node<Reversi> bestChild(Node<Reversi> node) {
        if (node.children().isEmpty())
            return node;
        return node.children().stream()
            .max(Comparator.comparingInt(Node::playouts))
            .orElse(node);
    }

    // Find a matching child node for the given state.
    private ReversiNode findMatchingChild(Node<Reversi> parent, State<Reversi> state) {
        for (Node<Reversi> child : parent.children()) {
            if (child.state().equals(state))
                return (ReversiNode) child;
        }
        return null;
    }

    // Advances the tree to use the subtree corresponding to the given move.
    // This preserves all previously learned statistics for the subtree.
    public boolean advanceTree(Move<Reversi> move) {
        if (root.state().isTerminal())
            return false;
        
        if (root.children().isEmpty())
            runMCTS(100); // Run iterations to create children and expand
        
        State<Reversi> nextState = root.state().next(move);
        ReversiNode nextNode = findMatchingChild(root, nextState);
        
        if (nextNode != null) {
            this.root = nextNode; // Found a matching child, make it the new root
            return true;
        } else {
            this.root = new ReversiNode(nextState); // No matching child found, create a new node
            return false;
        }
    }

    // Run a complete game of Reversi using MCTS for move decisions.
    // Preserves and reuses the search tree between moves.
    public void runGame() {
        int currentPlayer = Reversi.BLACK; // Black starts in Reversi

        runMCTS(iterationsPerMove);

        boolean gameOver = false;
        boolean alreadySkipped = false; // Check if both players skipped consecutively
        while (!gameOver) {
            System.out.println(root.state());
            System.out.println("Current player: " + (currentPlayer == Reversi.WHITE ? "White" : "Black"));

            // Check if the board is full
            ReversiPosition position = ((Reversi.ReversiState)root.state()).position();
            if (position.full()) {
                gameOver = true;
                System.out.println("Board is full. Game over.");
                break;
            }
            Collection<Move<Reversi>> availableMoves = root.state().moves(currentPlayer);
            if (availableMoves.isEmpty()) { // Handle case where current player has no moves
                Collection<Move<Reversi>> opponentMoves = root.state().moves(1-currentPlayer);
                if (opponentMoves.isEmpty()) { // Check if opponent player also has no moves
                    gameOver = true;
                    System.out.println("Neither player has valid moves. Game over.");
                    break;
                } else
                    System.out.println("Player " + (currentPlayer == Reversi.WHITE ? "White" : "Black") + " has no valid moves. Skipping turn.");
                if (alreadySkipped) { // If this is the second consecutive skip, game is over
                    gameOver = true;
                    System.out.println("Both players skipped consecutively. Game over.");
                    break;
                }
                alreadySkipped = true; // Mark that we skipped and continue with the next player
                currentPlayer = 1-currentPlayer;
                continue;
            }
            alreadySkipped = false; // Current player has moves, reset consecutive skips flag

            runMCTS(iterationsPerMove); // Run MCTS to find the best move, continuing from current tree
            
            // Ensure the root has children
            if (root.children().isEmpty()) {
                System.out.println("ERROR: MCTS did not generate any children. Creating children manually.");
                for (Move<Reversi> move : availableMoves) // Manually create children
                    root.addChild(root.state().next(move));
            }
            Node<Reversi> bestChild = bestChild(root);
            root = (ReversiNode)bestChild; // Advance the tree to the selected move            
            currentPlayer = 1-currentPlayer;
        }
        System.out.println("Final board:\n" + root.state());

        ReversiPosition position = ((Reversi.ReversiState)root.state()).position();
        if (position.blackCount > position.whiteCount)
            System.out.println("Black wins with " + position.blackCount + " pieces vs White's " + position.whiteCount);
        else if (position.whiteCount > position.blackCount)
            System.out.println("White wins with " + position.whiteCount + " pieces vs Black's " + position.blackCount);
        else
            System.out.println("It's a draw with both players having " + position.blackCount + " pieces");
    }

    private ReversiNode root;
    private final Random random = new Random();
    private final double explorationConstant = 1.0; // Ratio of exploration to exploitation
    private final int iterationsPerMove = 5000; // Number of iterations to run simulation
    private final int maxSimulationDepth = 20; // Depth to explore before predicting winner
}