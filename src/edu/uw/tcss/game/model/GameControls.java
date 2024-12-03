package edu.uw.tcss.game.model;

/**
 * Defines mutable behaviors for a simple "game".
 * 
 * @author Charles Bryan
 * @version 1
 */
public interface GameControls {

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
     * Starts a new game with the piece in teh starting location.
     */
    void newGame();

}
