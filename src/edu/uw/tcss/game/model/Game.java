package edu.uw.tcss.game.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

/**
 * The Game class is used simulate a simple Game. THe game is made up of a board with
 * a width and height (see public members) and a piece.
 * 
 * @author Charles Bryan
 * @version Winter 2025
 */
public class Game implements PropertyChangeEnabledGameControls {

    /** The width of the game board. */
    public static final int WIDTH = 8;
    
    /** The height of the game board. */
    public static final int HEIGHT = 10;

    /** The manager of PropertyChangeListeners. */
    private final PropertyChangeSupport myPcs;
        
    /** The game's piece location. */
    private Point myLocation;

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
            myLocation = myLocation.transform(0, -1);
            myPcs.firePropertyChange(PROPERTY_UP, null, myLocation);
        } else {
            myPcs.firePropertyChange(PROPERTY_INVALID, myLocation, Boolean.TRUE);
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
            myLocation = myLocation.transform(0, 1);
            myPcs.firePropertyChange(PROPERTY_DOWN, null, myLocation);
        } else {
            myPcs.firePropertyChange(PROPERTY_INVALID, myLocation, Boolean.TRUE);
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
            myLocation = myLocation.transform(1, 0);
            myPcs.firePropertyChange(PROPERTY_RIGHT, null, myLocation);
        } else {
            myPcs.firePropertyChange(PROPERTY_INVALID, myLocation, Boolean.TRUE);
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
            myLocation = myLocation.transform(-1, 0);
            myPcs.firePropertyChange(PROPERTY_LEFT, null, myLocation);
        } else {
            myPcs.firePropertyChange(PROPERTY_INVALID, myLocation, Boolean.TRUE);
        }
        updateValidDirections();
    }

    /**
     * {@inheritDoc}
     *
     * // TODO fix this doc
     */
    @Override
    public void newGame() {
        myLocation = new Point(0, 0);
        myPcs.firePropertyChange(PROPERTY_NEW_GAME, null, myLocation);
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
        final ValidMoves directions =
                new ValidMoves(
                        Map.of(
                            Move.UP, isUpValid(),
                            Move.DOWN, isDownValid(),
                            Move.LEFT, isLeftValid(),
                            Move.RIGHT, isRightValid()));

        myPcs.firePropertyChange(
                PROPERTY_VALID_DIRECTIONS,
                null,
                directions);
    }

    private boolean isUpValid() {
        return myLocation.y() > 0;
    }

    private boolean isDownValid() {
        return myLocation.y() < HEIGHT - 1;
    }

    private boolean isLeftValid() {
        return myLocation.x() > 0;
    }

    private boolean isRightValid() {
        return myLocation.x() < WIDTH - 1;
    }


}
