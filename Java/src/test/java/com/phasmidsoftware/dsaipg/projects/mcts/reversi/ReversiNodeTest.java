package com.phasmidsoftware.dsaipg.projects.mcts.reversi;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class ReversiNodeTest {

    @Test
    public void testConstructorWithParent() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        ReversiNode parent = new ReversiNode(state);
        ReversiNode node = new ReversiNode(state, parent);
        
        assertNotNull(node);
        assertEquals(parent, node.getParent());
        assertEquals(state, node.state());
    }

    @Test
    public void testConstructorWithoutParent() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        ReversiNode node = new ReversiNode(state);
        
        assertNotNull(node);
        assertNull(node.getParent());
        assertEquals(state, node.state());
    }

    @Test
    public void testIsLeaf() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        ReversiNode node = new ReversiNode(state);
        
        // Initially, node should be a leaf (no children)
        assertTrue(node.isLeaf());
        
        // Add a child
        node.addChild(state);
        
        // Now, node should not be a leaf
        assertFalse(node.isLeaf());
    }

    @Test
    public void testState() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        ReversiNode node = new ReversiNode(state);
        
        assertEquals(state, node.state());
    }

    @Test
    public void testWhite() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        ReversiNode node = new ReversiNode(state);
        
        // In Reversi, BLACK is the opener, so this should return true if the player is BLACK
        assertEquals(state.player() == state.game().opener(), node.white());
    }

    @Test
    public void testChildren() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        ReversiNode node = new ReversiNode(state);
        
        // Initially, node should have no children
        Collection<Node<Reversi>> children = node.children();
        assertNotNull(children);
        assertTrue(children.isEmpty());
    }

    @Test
    public void testAddChild() {
        Reversi reversi = new Reversi();
        State<Reversi> initialState = reversi.start();
        ReversiNode node = new ReversiNode(initialState);
        
        // Create a child state by making a move
        Reversi.ReversiState state = (Reversi.ReversiState) initialState;
        State<Reversi> childState = state.next(state.moves(Reversi.BLACK).iterator().next());
        
        // Add the child
        node.addChild(childState);
        
        // Node should now have one child
        assertEquals(1, node.children().size());
        
        // The child's state should match childState
        Node<Reversi> child = node.children().iterator().next();
        assertEquals(childState, child.state());
    }

    @Test
    public void testBackPropagate() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        ReversiNode node = new ReversiNode(state);
        
        // BackPropagate is implemented externally in MCTS, so this is just a placeholder test
        node.backPropagate();
    }

    @Test
    public void testWins() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        ReversiNode node = new ReversiNode(state);
        
        // For a non-terminal state, wins should be 0
        assertEquals(0, node.wins());
    }

    @Test
    public void testPlayouts() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        ReversiNode node = new ReversiNode(state);
        
        // For a non-terminal state, playouts should be 0
        assertEquals(0, node.playouts());
    }

    @Test
    public void testTerminalNodeInitialization() {
        // Create a terminal state with a winner
        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = 1; // All white
            }
        }
        ReversiPosition position = new ReversiPosition(grid, 0, 64, 1);
        Reversi reversi = new Reversi();
        State<Reversi> terminalState = reversi.new ReversiState(position);
        
        ReversiNode node = new ReversiNode(terminalState);
        
        // For a terminal state, playouts should be 1
        assertEquals(1, node.playouts());
        
        // For a terminal state with a winner, wins should be 2
        assertEquals(2, node.wins());
    }

    @Test
    public void testTerminalNodeDrawInitialization() {
        // Create a terminal state with a draw (equal pieces)
        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = (i + j) % 2; // Alternating black and white
            }
        }
        ReversiPosition position = new ReversiPosition(grid, 32, 32, 1);
        Reversi reversi = new Reversi();
        State<Reversi> terminalState = reversi.new ReversiState(position);
        
        ReversiNode node = new ReversiNode(terminalState);
        
        // For a terminal state, playouts should be 1
        assertEquals(1, node.playouts());
        
        // For a terminal state with a draw, wins should be 1
        assertEquals(1, node.wins());
    }
}