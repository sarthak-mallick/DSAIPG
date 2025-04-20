package com.phasmidsoftware.dsaipg.projects.mcts.reversi;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.*;

public class ReversiTest {

    @Test
    public void testOpener() {
        Reversi reversi = new Reversi();
        assertEquals(Reversi.BLACK, reversi.opener());
    }

    @Test
    public void testStart() {
        Reversi reversi = new Reversi();
        State<Reversi> state = reversi.start();
        
        assertNotNull(state);
        assertEquals(Reversi.BLACK, state.player());
        assertFalse(state.isTerminal());
        
        // Check that it's a ReversiState
        assertTrue(state instanceof Reversi.ReversiState);
        
        // Check the initial position
        Reversi.ReversiState reversiState = (Reversi.ReversiState) state;
        ReversiPosition position = reversiState.position();
        
        // Verify initial board setup
        assertEquals(1, position.grid[3][3]); // White
        assertEquals(1, position.grid[4][4]); // White
        assertEquals(0, position.grid[3][4]); // Black
        assertEquals(0, position.grid[4][3]); // Black
    }

    @Test
    public void testConstructorWithSeed() {
        long seed = 12345L;
        Reversi reversi = new Reversi(seed);
        assertNotNull(reversi);
    }

    @Test
    public void testConstructorWithRandom() {
        Reversi reversi = new Reversi(new java.util.Random());
        assertNotNull(reversi);
    }

    @Test
    public void testDefaultConstructor() {
        Reversi reversi = new Reversi();
        assertNotNull(reversi);
    }

    @Test
    public void testReversiMove() {
        Reversi.ReversiMove move = new Reversi.ReversiMove(Reversi.BLACK, 2, 4);
        
        assertEquals(Reversi.BLACK, move.player());
        assertArrayEquals(new int[]{2, 4}, move.move());
    }

    @Test
    public void testReversiStateGameMethod() {
        Reversi reversi = new Reversi();
        Reversi.ReversiState state = (Reversi.ReversiState) reversi.start();
        
        assertEquals(reversi, state.game());
    }

    @Test
    public void testReversiStatePlayer() {
        Reversi reversi = new Reversi();
        Reversi.ReversiState initialState = (Reversi.ReversiState) reversi.start();
        
        // Initial player should be BLACK
        assertEquals(Reversi.BLACK, initialState.player());
        
        // After a move, player should switch to WHITE
        Move<Reversi> move = initialState.moves(Reversi.BLACK).iterator().next();
        Reversi.ReversiState nextState = (Reversi.ReversiState) initialState.next(move);
        
        assertEquals(Reversi.WHITE, nextState.player());
    }

    @Test
    public void testReversiStatePosition() {
        Reversi reversi = new Reversi();
        Reversi.ReversiState state = (Reversi.ReversiState) reversi.start();
        
        ReversiPosition position = state.position();
        assertNotNull(position);
    }

    @Test
    public void testReversiStateWinner() {
        Reversi reversi = new Reversi();
        Reversi.ReversiState initialState = (Reversi.ReversiState) reversi.start();
        
        // Initial state should not have a winner
        Optional<Integer> winner = initialState.winner();
        assertFalse(winner.isPresent());
    }

    @Test
    public void testReversiStateRandom() {
        Reversi reversi = new Reversi(12345L);
        Reversi.ReversiState state = (Reversi.ReversiState) reversi.start();
        
        assertNotNull(state.random());
    }

    @Test
    public void testReversiStateMoves() {
        Reversi reversi = new Reversi();
        Reversi.ReversiState state = (Reversi.ReversiState) reversi.start();
        
        Collection<Move<Reversi>> moves = state.moves(Reversi.BLACK);
        
        // Initial state should have 4 moves for BLACK
        assertEquals(4, moves.size());
    }

    @Test
    public void testReversiStateNext() {
        Reversi reversi = new Reversi();
        Reversi.ReversiState initialState = (Reversi.ReversiState) reversi.start();
        
        Move<Reversi> move = initialState.moves(Reversi.BLACK).iterator().next();
        State<Reversi> nextState = initialState.next(move);
        
        assertNotNull(nextState);
        assertNotEquals(initialState, nextState);
    }

    @Test
    public void testReversiStateIsTerminal() {
        Reversi reversi = new Reversi();
        Reversi.ReversiState initialState = (Reversi.ReversiState) reversi.start();
        
        // Initial state should not be terminal
        assertFalse(initialState.isTerminal());
    }

    @Test
    public void testReversiStateToString() {
        Reversi reversi = new Reversi();
        Reversi.ReversiState state = (Reversi.ReversiState) reversi.start();
        
        String result = state.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("Reversi"));
    }

    @Test
    public void testReversiStateConstructor() {
        Reversi reversi = new Reversi();
        ReversiPosition position = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        Reversi.ReversiState state = reversi.new ReversiState(position);
        
        assertEquals(position, state.position());
    }

    @Test
    public void testReversiStateDefaultConstructor() {
        Reversi reversi = new Reversi();
        Reversi.ReversiState state = reversi.new ReversiState();
        
        assertNotNull(state.position());
    }
}