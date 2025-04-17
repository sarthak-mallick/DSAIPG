package com.phasmidsoftware.dsaipg.projects.mcts.reversi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
* This class represents the board of the Reversi game.
* It is a nxn matrix of 0s, 1s, and -1s for White, Black, and Empty respectively.
*/
public class ReversiPosition {

    /**
     * Create the starting position for Reversi.
     *
     * @param last the last player.
     * @return the initial ReversiPosition setup.
     */
    static ReversiPosition startingPosition(final int last) {
        int[][] matrix = new int[gridSize][gridSize];
        for (int i=0; i<gridSize; i++)
            for (int j=0; j<gridSize; j++) 
                matrix[i][j] = -1;
        
        // Put the four starting pieces in the center
        int mid = gridSize/2;
        matrix[mid][mid-1] = 0;   // Black
        matrix[mid-1][mid] = 0;   // Black
        matrix[mid][mid] = 1;     // White
        matrix[mid-1][mid-1] = 1; // White

        return new ReversiPosition(matrix, 2, 2, EMPTY);
    }

    public static final int EMPTY = -1;

    /**
     * Effect a player's move on this ReversiPosition.
     *
     * @param player the player (0: Black, 1: White)
     * @param x      the first dimension value.
     * @param y      the second dimension value.
     * @return the new ReversiPosition.
     */
    public ReversiPosition move(int player, int x, int y) {
        if (full())
            throw new RuntimeException("ReversiPosition is full");
        else if (!isValidMove(x, y, player))
            throw new RuntimeException("Invalid move: " + x + ", " + y);
            
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        int[][] matrix = copyGrid();

        int updatedWhiteCount = whiteCount;
        int updatedBlackCount = blackCount;

        matrix[x][y] = player;
        if (player == 1) updatedWhiteCount++;
        else updatedBlackCount++;
        
        for (int[] dir : directions) {
            for (int[] pos : getPiecesToFlip(x, y, player, matrix, dir)) {
                if (player == 1) {
                    updatedWhiteCount++;
                    updatedBlackCount--;
                } else {
                    updatedWhiteCount--;
                    updatedBlackCount++;
                }
                matrix[pos[0]][pos[1]] = player;
            }
        }
        return new ReversiPosition(matrix, updatedBlackCount, updatedWhiteCount, player);
    }

    /**
     * Find pieces to flip in a direction.
     */
    private List<int[]> getPiecesToFlip(int x, int y, int player, int[][] matrix, int[] dir) {
        List<int[]> candidatesToFlip = new ArrayList<>();
        List<int[]> tilesToFlip = new ArrayList<>();
        int opponent = 1-player;

        int yStep = dir[1]; int c = y+yStep;
        int xStep = dir[0]; int r = x+xStep;

        while (r>=0 && c>=0 && r<gridSize && c<gridSize) {
            if (matrix[r][c] == player) {
                tilesToFlip.addAll(candidatesToFlip); // Flip all candidates
                break;
            } else if (matrix[r][c] == opponent)
                candidatesToFlip.add(new int[]{r, c}); // Add candidates for flip
            else
                break; // Can't flip because blank tile

            c = c+yStep;
            r = r+xStep;
        }
        return tilesToFlip;
    }

    /**
     * Generate all the possible moves available on this ReversiPosition.
     *
     * @param player the player making the move
     * @return a list of [x,y] arrays representing valid moves.
     */
    public List<int[]> moves(int player) {
        List<int[]> result = new ArrayList<>();
        for (int i=0; i<gridSize; i++)
            for (int j=0; j<gridSize; j++)
                if (grid[i][j]<0 && isValidMove(i, j, player))
                    result.add(new int[]{i, j});
        return result;
    }

    /**
     * Check if a move is valid
     */
    private boolean isValidMove(int row, int col, int player) {
        if (grid[row][col] >= 0)
            return false; // Cell is occupied
        
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        int opponent = 1-player;

        for (int[] dir : directions) {
            int c = col+dir[1]; 
            int r = row+dir[0];

            // Check if step is outside board or doesn't belong to opponent
            if (r<0 || c<0 || r>=gridSize || c>=gridSize || grid[r][c] != opponent)
                continue;
            c = c+dir[1]; 
            r = r+dir[0];

            while (r>=0 && c>=0 && r<gridSize && c<gridSize) {
                if (grid[r][c] == opponent) {
                    r = r+dir[0];
                    c = c+dir[1];
                } else if (grid[r][c] == player)
                    return true; // Can flip player's piece
                else
                    break; // Can't flip empty tile
            }
        }
        return false;
    }

    /**
    * Determine if this ReversiPosition represents a winner.
    *
    * @return an Optional Integer.
    */
    public Optional<Integer> winner() {
        if (!isGameOver())
            return Optional.empty();
        else if (whiteCount > blackCount)
            return Optional.of(1); // Winner: White
        else if (whiteCount < blackCount)
            return Optional.of(0); // Winner: Black
        else
            return Optional.empty(); // Draw
    }

    /**
    * Game is over if board is full or neither player can move
    */
    private boolean isGameOver() {
        if (full()) return true;
        else return moves(1).isEmpty() && moves(0).isEmpty();
    }

    boolean full() {
        return whiteCount+blackCount == gridSize*gridSize;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                sb.append(render(grid[i][j]));
                if (j < gridSize - 1) sb.append(' ');
            }
            if (i < gridSize - 1) sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                sb.append(grid[i][j]);
                if (j < gridSize - 1) sb.append(',');
            }
            if (i < gridSize - 1) sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReversiPosition position)) return false;
        return Arrays.deepEquals(grid, position.grid);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }

    ReversiPosition(int[][] grid, int blackCount, int whiteCount, int last) {
        this.grid = grid;
        this.last = last;
        this.whiteCount = whiteCount;
        this.blackCount = blackCount;
    }

    private int[][] copyGrid() {
        int[][] result = new int[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++)
            result[i] = Arrays.copyOf(grid[i], gridSize);
        return result;
    }

    private char render(int x) {
        return switch (x) {
            case 0 -> 'B';
            case 1 -> 'W';
            default -> '.';
        };
    }

    private final int[][] grid;
    final int last;
    private final int blackCount;
    private final int whiteCount;
    private final static int gridSize = 6;
}