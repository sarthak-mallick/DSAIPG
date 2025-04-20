package com.phasmidsoftware.dsaipg.projects.mcts.reversi;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class ReversiMCTSTest {

    @Test
    public void testConstructor() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        assertNotNull(mcts);
    }

    @Test
    public void testRunMCTS() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        // Run a small number of iterations for testing
        Node<Reversi> bestNode = mcts.runMCTS(10);
        
        assertNotNull(bestNode);
        // The best node should have been played out at least once
        assertTrue(bestNode.playouts() > 0);
    }

    @Test
    public void testSelect() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        // With no exploration yet, select should return the root
        Node<Reversi> selected = mcts.select(root);
        assertEquals(root, selected);
        
        // Run MCTS to explore the tree
        mcts.runMCTS(10);
        
        // Now select should return a leaf node
        selected = mcts.select(root);
        assertTrue(selected.isLeaf() || selected.state().isTerminal());
    }

    @Test
    public void testExpand() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        // Expand the root node
        Node<Reversi> expanded = mcts.expand(root);
        
        assertNotNull(expanded);
        // Root should now have children
        assertFalse(root.children().isEmpty());
        // Expanded node should be one of the children
        assertTrue(root.children().contains(expanded));
    }

    @Test
    public void testBestUCTChild() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        // First, expand the root to create children
        mcts.expand(root);
        
        // Manually set wins and playouts for children to test UCT selection
        int i = 0;
        for (Node<Reversi> child : root.children()) {
            ReversiNode reversiChild = (ReversiNode) child;
            reversiChild.playouts = i + 1;
            reversiChild.wins = i;
            i++;
        }
        
        // Get best UCT child
        Node<Reversi> bestChild = mcts.bestUCTChild(root);
        
        assertNotNull(bestChild);
        // With our setup, the node with highest wins/playouts ratio 
        // plus exploration bonus should be selected
    }

    @Test
    public void testSimulate() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        // Simulate a game from the root
        int result = mcts.simulate(root);
        
        // Result should be either -1 (draw), 0 (black wins), or 1 (white wins)
        assertTrue(result == -1 || result == 0 || result == 1);
    }

    @Test
    public void testBackPropagate() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        // First, expand the root to create a child
        Node<Reversi> child = mcts.expand(root);
        
        // Back-propagate a win
        mcts.backPropagate(child, 0);
        
        // Root and child should have updated playouts
        assertTrue(root.playouts() > 0);
        assertTrue(child.playouts() > 0);
    }

    @Test
    public void testBestChild() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        // First, expand the root to create children
        mcts.expand(root);
        
        // Manually set playouts for children to test best child selection
        int i = 0;
        for (Node<Reversi> child : root.children()) {
            ReversiNode reversiChild = (ReversiNode) child;
            reversiChild.playouts = i;
            i++;
        }
        
        // Get best child
        Node<Reversi> bestChild = mcts.bestChild(root);
        
        // The child with the most playouts should be selected
        assertEquals(i - 1, bestChild.playouts());
    }

    @Test
    public void testEvaluatePositionWithHeuristics() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        // This is a private method, so we can't test it directly
        // We can indirectly test it through the simulate method
        int result = mcts.simulate(root);
        
        // Result should be either -1 (draw), 0 (black wins), or 1 (white wins)
        assertTrue(result == -1 || result == 0 || result == 1);
    }
    
    @Test
    public void testFindMatchingChild() {
        State<Reversi> state = new Reversi().start();
        ReversiNode root = new ReversiNode(state);
        ReversiMCTS mcts = new ReversiMCTS(root);
        
        // First, expand the root to create children
        mcts.expand(root);
        
        // Select a child state
        Collection<Node<Reversi>> children = root.children();
        Node<Reversi> selectedChild = children.iterator().next();
        State<Reversi> selectedState = selectedChild.state();
        
        // Find a matching child is private, so we indirectly test through advanceTree
        // Make a move that leads to the selected child's state
        Reversi.ReversiState reversiState = (Reversi.ReversiState) state;
        Collection<Move<Reversi>> moves = reversiState.moves(Reversi.BLACK);
        
        // Find the move that leads to the selected child's state
        Move<Reversi> targetMove = null;
        for (Move<Reversi> move : moves) {
            State<Reversi> nextState = reversiState.next(move);
            if (nextState.equals(selectedState)) {
                targetMove = move;
                break;
            }
        }
        
        // If we found a matching move, test advanceTree
        if (targetMove != null) {
            boolean advanced = mcts.advanceTree(targetMove);
            assertTrue(advanced);
        }
    }
}