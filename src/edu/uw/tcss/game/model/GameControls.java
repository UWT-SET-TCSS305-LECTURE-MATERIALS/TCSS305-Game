package edu.uw.tcss.game.model;

import java.util.Map;

/**
 * Defines mutable behaviors for a simple "game".
 * 
 * @author Charles Bryan
 * @version Winter 2025
 */
public interface GameControls {

    /**
     * Moves the game piece in the direction described by the enum theMove.
     * If the piece is unable to move in the direction described by theMove,
     * no action is performed.
     *
     * @param theMove the direction in which to move the game piece.
     */
    void move(Move theMove);

    /**
     * Moves the game piece up. If the piece is unable to move up,
     * no action is performed.
     */
    void moveUp();

    /**
     * Moves the game piece down. If the piece is unable to move down,
     * no action is performed.
     */
    void moveDown();

    /**
     * Moves the game piece right. If the piece is unable to move right,
     * no action is performed.
     */
    void moveRight();

    /**
     * Moves the game piece left. If the piece is unable to move left,
     * no action is performed.
     */
    void moveLeft();

    /**
     * Starts a new game with the piece in the starting location.
     */
    void newGame();

    /**
     * Constants describing the valid moves for a game piece.
     */
    enum Move {
        /**
         * Describes a move in the upward direction.
         */
        UP,
        /**
         * Describes a move in the downward direction.
         */
        DOWN,
        /**
         * Describes a move in the left direction.
         */
        LEFT,
        /**
         * Describes a move in the right direction.
         */
        RIGHT
    }

    /**
     * Data class that represents the which moves are valid in the current state of
     * the Game.
     *
     * @param moves the Map which represents the which moves are valid
     */
    record ValidMoves(Map<Move, Boolean> moves) { }

    /**
     * Data class that represents a "point" on the game board. Positive x
     * moves right across "columns". Positive y moves down "rows".
     *
     * @param x a singular x coordinate on the game board.
     * @param y a singular y coordinate on the game board.
     */
    record Point(int x, int y) {
        Point transform(final int theX, final int theY) {
            return new Point(x + theX, y + theY);
        }
    }

}
