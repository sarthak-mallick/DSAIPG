package com.phasmidsoftware.dsaipg.projects.mcts.reversi;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Game;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

/**
 * Class which models the game of Reversi.
*/
public class Reversi implements Game<Reversi> {

    public static final int BLACK = 0;
    public static final int WHITE = 1;
    public static final int EMPTY = -1;

    /**
     * This method determines the opening player (Black goes first in Reversi).
     *
     * @return the opening player.
     */
    public int opener() {
        return BLACK;
    }

    /**
     * Get the starting state for this game.
    *
    * @return a State of Reversi.
    */
    public State<Reversi> start() {
        return new ReversiState();
    }

    /**
    * Primary constructor.
    *
    * @param random a random source.
    */
    public Reversi(Random random) {
        this.random = random;
    }

    /**
    * Secondary constructor.
    *
    * @param seed a seed for the random source.
    */
    public Reversi(long seed) {
        this(new Random(seed));
    }

    /**
    * Secondary constructor which uses the current time as seed.
    */
    public Reversi() {
        this(System.currentTimeMillis());
    }

    private final Random random;

    /**
    * Inner class to define a Move of Reversi.
    */
    static class ReversiMove implements Move<Reversi> {
        /**
        * @return the player for this Move.
        */
        public int player() {
            return player;
        }

        /**
         * Primary constructor.
        *
        * @param player the player.
        * @param i      the row.
        * @param j      the column.
        */
        public ReversiMove(int player, int i, int j) {
            this.player = player;
            this.i = i;
            this.j = j;
        }

        /**
         * @return this move as an array of two coordinates: row and column.
        */
        public int[] move() {
            return new int[]{i, j};
        }

        private final int player;
        private final int i;
        private final int j;
    }

    /**
     * Inner class to define a State of Reversi.
    */
    class ReversiState implements State<Reversi> {
        /**
         * Method to yield the game of which this is a State.
        *
        * @return a Reversi game
        */
        public Reversi game() {
            return Reversi.this;
        }

        /**
         * Method to determine the player who plays to this State.
        * Black plays first in Reversi.
        *
        * @return a non-negative integer.
        */
        public int player() {
            if (position.last==EMPTY)
                return BLACK;
            return 1 - position.last;
        }

        /**
         * @return the Position of this State.
        */
        public ReversiPosition position() {
            return this.position;
        }

        /**
         * Method to determine if this State represents the end of the game.
        *
        * @return an optional int if this State is a win/loss/draw.
        */
        public Optional<Integer> winner() {
            return position.winner();
        }

        /**
         * A random source associated with this State.
        *
        * @return the appropriate Random.
        */
        public Random random() {
            return random;
        }

        /**
        * Get the moves that can be made directly from the given state.
        * The moves can be in any order--the order will be randomized for usage.
        *
        * @return all the possible moves from this state.
        */
        public Collection<Move<Reversi>> moves(int player) {
            List<int[]> moves = position.moves(player);
            ArrayList<Move<Reversi>> list = new ArrayList<>();
            for (int[] coordinates : moves) {
                list.add(new ReversiMove(player, coordinates[0], coordinates[1]));
            }
            return list;
        }

        /**
        * Implement the given move on the given state.
        *
        * @param move the move to implement.
        * @return a new state.
        */
        public State<Reversi> next(Move<Reversi> move) {
            ReversiMove reversiMove = (ReversiMove) move;
            int[] ints = reversiMove.move();
            return new ReversiState(position.move(move.player(), ints[0], ints[1]));
        }

        /**
        * Is the game over?
        *
        * @return true if position is full or if neither player can move.
        */
        public boolean isTerminal() {
            return position.full() || (moves(BLACK).isEmpty() && moves(WHITE).isEmpty());
        }

        @Override
        public String toString() {
            return "Reversi{\n" +
                    position.render() +
                    "\n}";
        }

        public ReversiState(ReversiPosition position) {
            this.position = position;
        }

        public ReversiState() {
            this(ReversiPosition.startingPosition(EMPTY));
        }

        private final ReversiPosition position;
    }
}