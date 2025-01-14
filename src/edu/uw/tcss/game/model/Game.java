package edu.uw.tcss.game.model;

import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * The Game class is used simulate a simple Game. THe game is made up of a board with
 * a width and height (see public members) and a piece.
 * 
 * @author Charles Bryan
 * @version Autumn 2018
 */
public class Game implements PropertyChangeEnabledGameControls {

    /** The width of the game board. */
    public static final int WIDTH = 8;
    
    /** The height of the game board. */
    public static final int HEIGHT = 10;
    

    /** The manager of PropertyChangeListeners. */
    private final PropertyChangeSupport myPcs;
        
    /** The game's piece location. */
    private final Point myLocation;

    /**
     * Creates a game with a piece in the starting location 0, 0.
     */
    public Game() {
        super();
        myLocation = new Point(0, 0);
        myPcs = new PropertyChangeSupport(this);
    }

    /**
     * {@inheritDoc}
     *
     * When a piece move is attempted with this method, all interested parties will be notified
     * of the action via either, on a valid move, the PROPERTY associated with the direction
     * described by theMove or PROPERTY_INVALID on an invalid move.
     *
     * @param theMove the direction in which to move the game piece.
     */
    @Override
    public void move(final Move theMove) {
        switch (theMove) {
            case UP -> moveUp();
            case DOWN -> moveDown();
            case LEFT -> moveLeft();
            case RIGHT -> moveRight();
            default -> throw new IllegalStateException("You added a value to the enum "
                    + "but neglected to update this switch statement");
        }
    }

    /**
     * {@inheritDoc}
     *
     * When a piece move is attempted with this method, all interested parties will be notified
     * of the action via either PROPERTY_UP on a valid move or PROPERTY_INVALID on an invalid
     * move.
     */
    @Override
    public void moveUp() {
        if (isUpValid()) {
            myLocation.setLocation(myLocation.getX(), myLocation.getY() - 1);
            myPcs.firePropertyChange(PROPERTY_UP, null, new Point(myLocation));
        } else {
            myPcs.firePropertyChange(PROPERTY_INVALID, new Point(myLocation), Boolean.TRUE);
        }
        updateValidDirections();
    }

    /**
     * {@inheritDoc}
     *
     * When a piece move is attempted with this method, all interested parties will be notified
     * of the action via either PROPERTY_DOWN on a valid move or PROPERTY_INVALID on an invalid
     * move.
     */
    @Override
    public void moveDown() {
        if (isDownValid()) {
            myLocation.setLocation(myLocation.getX(), myLocation.getY() + 1);
            myPcs.firePropertyChange(PROPERTY_DOWN, null, new Point(myLocation));
        } else {
            myPcs.firePropertyChange(PROPERTY_INVALID, new Point(myLocation), Boolean.TRUE);
        }
        updateValidDirections();
    }

    /**
     * {@inheritDoc}
     *
     * When a piece move is attempted with this method, all interested parties will be notified
     * of the action via either PROPERTY_RIGHT on a valid move or PROPERTY_INVALID on an
     * invalid move.
     */
    @Override
    public void moveRight() {
        if (isRightValid()) {
            myLocation.setLocation(myLocation.getX() + 1, myLocation.getY());
            myPcs.firePropertyChange(PROPERTY_RIGHT, null, new Point(myLocation));
        } else {
            myPcs.firePropertyChange(PROPERTY_INVALID, new Point(myLocation), Boolean.TRUE);
        }
        updateValidDirections();
    }

    /**
     * {@inheritDoc}
     *
     * When a piece move is attempted with this method, all interested parties will be notified
     * of the action via either PROPERTY_LEFT on a valid move or PROPERTY_INVALID on an invalid
     * move.
     */
    @Override
    public void moveLeft() {
        if (isLeftValid()) {
            myLocation.setLocation(myLocation.getX() - 1, myLocation.getY());
            myPcs.firePropertyChange(PROPERTY_LEFT, null, new Point(myLocation));
        } else {
            myPcs.firePropertyChange(PROPERTY_INVALID, new Point(myLocation), Boolean.TRUE);
        }
        updateValidDirections();
    }

    /**
     * {@inheritDoc}
     *
     * When a piece move is attempted with this method, all interested parties will be notified
     * of the action via either PROPERTY_LEFT on a valid move or PROPERTY_INVALID on an invalid
     * move.
     */
    @Override
    public void newGame() {
        myLocation.setLocation(0, 0);
        myPcs.firePropertyChange(PROPERTY_NEW_GAME, null, new Point(myLocation));
        updateValidDirections();
    }


    @Override
    public void addPropertyChangeListener(final PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(theListener);
    }

    @Override
    public void addPropertyChangeListener(final String thePropertyName,
                                          final PropertyChangeListener theListener) {
        myPcs.addPropertyChangeListener(thePropertyName, theListener);
    }

    @Override
    public void removePropertyChangeListener(final PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(theListener);
    }

    @Override
    public void removePropertyChangeListener(final String thePropertyName,
                                             final PropertyChangeListener theListener) {
        myPcs.removePropertyChangeListener(thePropertyName, theListener);
    }

    private void updateValidDirections() {
        final Map<Move, Boolean> directions = new HashMap<>();
        directions.put(Move.UP, isUpValid());
        directions.put(Move.DOWN, isDownValid());
        directions.put(Move.LEFT, isLeftValid());
        directions.put(Move.RIGHT, isRightValid());
        myPcs.firePropertyChange(PROPERTY_VALID_DIRECTIONS, null, directions);
    }

    private boolean isUpValid() {
        return myLocation.getY() > 0;
    }

    private boolean isDownValid() {
        return myLocation.getY() < HEIGHT - 1;
    }

    private boolean isLeftValid() {
        return myLocation.getX() > 0;
    }

    private boolean isRightValid() {
        return myLocation.getX() < WIDTH - 1;
    }


}
