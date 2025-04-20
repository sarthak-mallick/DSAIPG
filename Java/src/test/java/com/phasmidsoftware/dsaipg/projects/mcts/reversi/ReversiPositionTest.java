package com.phasmidsoftware.dsaipg.projects.mcts.reversi;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class ReversiPositionTest {

    @Test
    public void testStartingPosition() {
        ReversiPosition position = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        // Check the correct placement of initial pieces
        assertEquals(1, position.grid[3][3]); // White at center-right-bottom
        assertEquals(1, position.grid[4][4]); // White at center-left-top
        assertEquals(0, position.grid[3][4]); // Black at center-right-top
        assertEquals(0, position.grid[4][3]); // Black at center-left-bottom
        
        // Check counts are correct
        assertEquals(2, position.whiteCount);
        assertEquals(2, position.blackCount);
        
        // Check empty spaces
        assertEquals(-1, position.grid[0][0]);
        assertEquals(-1, position.grid[7][7]);
    }
    
    @Test(expected = RuntimeException.class)
    public void testMoveToOccupiedSpace() {
        ReversiPosition initial = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        // Try to place on an already occupied space
        initial.move(0, 3, 3);
    }
    
    @Test(expected = RuntimeException.class)
    public void testInvalidMove() {
        ReversiPosition initial = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        // Try to place where no pieces would be flipped
        initial.move(0, 0, 0);
    }
    
    @Test(expected = RuntimeException.class)
    public void testConsecutiveMovesBySamePlayer() {
        ReversiPosition initial = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        // Make a valid move
        ReversiPosition afterMove = initial.move(0, 2, 4);
        
        // Try to make another move by the same player
        afterMove.move(0, 2, 3);
    }

    
    @Test
    public void testWinner() {
        // Create a position with more white pieces
        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = 1; // All white
            }
        }
        // Set a few black pieces
        grid[0][0] = 0;
        grid[0][1] = 0;
        
        ReversiPosition position = new ReversiPosition(grid, 2, 62, 1);
        
        // Board is full, white has more pieces
        Optional<Integer> winner = position.winner();
        assertTrue(winner.isPresent());
        assertEquals(Integer.valueOf(1), winner.get()); // White should win
    }
    
    @Test
    public void testDraw() {
        // Create a position with equal number of pieces
        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = (i + j) % 2; // Alternating black and white
            }
        }
        
        ReversiPosition position = new ReversiPosition(grid, 32, 32, 1);
        
        // Board is full, equal pieces
        Optional<Integer> winner = position.winner();
        assertFalse(winner.isPresent()); // Should be a draw
    }
    
    @Test
    public void testGameNotFinished() {
        ReversiPosition initial = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        // Game just started
        Optional<Integer> winner = initial.winner();
        assertFalse(winner.isPresent()); // Game not over yet
    }
    
    @Test
    public void testFull() {
        // Create a position with all spaces filled
        int[][] grid = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                grid[i][j] = (i + j) % 2; // Alternating black and white
            }
        }
        
        ReversiPosition position = new ReversiPosition(grid, 32, 32, 1);
        
        assertTrue(position.full());
    }
    
    @Test
    public void testNotFull() {
        ReversiPosition initial = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        assertFalse(initial.full());
    }
    
    @Test
    public void testRender() {
        ReversiPosition initial = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        String rendered = initial.render();
        
        // Check that the rendered string contains the correct characters
        assertTrue(rendered.contains("."));
        assertTrue(rendered.contains("B"));
        assertTrue(rendered.contains("W"));
    }
    
    @Test
    public void testEquals() {
        ReversiPosition position1 = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        ReversiPosition position2 = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        assertEquals(position1, position2);
    }
    
    
    @Test
    public void testHashCode() {
        ReversiPosition position1 = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        ReversiPosition position2 = ReversiPosition.startingPosition(ReversiPosition.EMPTY);
        
        assertEquals(position1.hashCode(), position2.hashCode());
    }
}