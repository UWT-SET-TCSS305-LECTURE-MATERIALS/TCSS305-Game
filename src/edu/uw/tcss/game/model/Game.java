package edu.uw.tcss.game.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * The Game class simulates a simple game with a piece moving on a board.
 * The game is made up of a board with a width and height (see public constants)
 * and a piece that can move in four directions.
 *
 * <p>This class implements the Singleton pattern to ensure only one game instance
 * exists throughout the application. Use {@link #getInstance()} to obtain the instance.</p>
 *
 * <p>Game state changes are broadcast using the {@link PropertyChangeListener} pattern
 * with type-safe sealed {@link GameEvent} types. Instead of string-based property names,
 * events are represented as immutable record instances that can be pattern-matched
 * for type-safe handling.</p>
 *
 * <p><strong>Event Types:</strong></p>
 * <ul>
 *   <li>{@link GameEvent.MoveEvent} - Fired on successful moves</li>
 *   <li>{@link GameEvent.InvalidMoveEvent} - Fired on invalid move attempts</li>
 *   <li>{@link GameEvent.NewGameEvent} - Fired when a new game starts</li>
 * </ul>
 *
 * @author Charles Bryan
 * @version Autumn 2025
 */
public final class Game implements PropertyChangeEnabledGameControls {

    /** The width of the game board. */
    public static final int WIDTH = 8;
    
    /** The height of the game board. */
    public static final int HEIGHT = 10;

    /** The starting location of the game piece. */
    public static final Point STARTING_LOCATION = new Point(0, 0);

    /** The singleton instance of Game. */
    private static volatile Game instance;

    /** The manager of PropertyChangeListeners. */
    private final PropertyChangeSupport myPcs;

    /** The game's piece location. */
    private Point myLocation;

    /** Map of move directions to their validation predicates. */
    private final Map<Move, BooleanSupplier> myMoveValidators;

    /** Map of move directions to their transformation functions. */
    private final Map<Move, Supplier<Point>> myMovements;

    /**
     * Creates a game with a piece in the starting location 0, 0.
     * Private constructor to enforce singleton pattern.
     */
    private Game() {
        super();
        myLocation = STARTING_LOCATION;
        myPcs = new PropertyChangeSupport(this);
        myMoveValidators = Map.of(
                Move.UP, this::isUpValid,
                Move.DOWN, this::isDownValid,
                Move.LEFT, this::isLeftValid,
                Move.RIGHT, this::isRightValid);
        myMovements = Map.of(
                Move.UP, () -> myLocation.transform(0, -1),
                Move.DOWN, () -> myLocation.transform(0, 1),
                Move.LEFT, () -> myLocation.transform(-1, 0),
                Move.RIGHT, () -> myLocation.transform(1, 0));
    }

    /**
     * Returns the singleton instance of Game.
     * Uses double-checked locking for thread-safe lazy initialization.
     *
     * @return the singleton Game instance
     */
    public static Game getInstance() {
        if (instance == null) {
            synchronized (Game.class) {
                if (instance == null) {
                    instance = new Game();
                }
            }
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Attempts to move the game piece in the specified direction. All registered
     * {@link PropertyChangeListener}s will be notified with a type-safe {@link GameEvent}:</p>
     * <ul>
     *   <li>On valid move: {@link GameEvent.MoveEvent} containing the direction,
     *       new location, and updated valid moves</li>
     *   <li>On invalid move: {@link GameEvent.InvalidMoveEvent} containing the current
     *       location and attempted direction</li>
     * </ul>
     *
     * @param theMove the direction in which to move the game piece.
     * @throws NullPointerException when theMove is null.
     */
    @Override
    public void move(final Move theMove) {
        Objects.requireNonNull(theMove, "theMove cannot be null");
        if (myMoveValidators.get(theMove).getAsBoolean()) {
            myLocation = myMovements.get(theMove).get();
            final GameEvent event =
                    new GameEvent.MoveEvent(theMove,
                            myLocation,
                            calculateValidDirections(),
                            GameEvent.now());
            myPcs.firePropertyChange(event.getPropertyName(), null, event);
        } else {
            final GameEvent event =
                    new GameEvent.InvalidMoveEvent(myLocation,
                            theMove,
                            GameEvent.now());
            myPcs.firePropertyChange(event.getPropertyName(), null, event);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Attempts to move the game piece up. Registered listeners will receive
     * either a {@link GameEvent.MoveEvent} on success or a
     * {@link GameEvent.InvalidMoveEvent} if the move is invalid.</p>
     */
    @Override
    public void moveUp() {
        move(Move.UP);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Attempts to move the game piece down. Registered listeners will receive
     * either a {@link GameEvent.MoveEvent} on success or a
     * {@link GameEvent.InvalidMoveEvent} if the move is invalid.</p>
     */
    @Override
    public void moveDown() {
        move(Move.DOWN);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Attempts to move the game piece right. Registered listeners will receive
     * either a {@link GameEvent.MoveEvent} on success or a
     * {@link GameEvent.InvalidMoveEvent} if the move is invalid.</p>
     */
    @Override
    public void moveRight() {
        move(Move.RIGHT);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Attempts to move the game piece left. Registered listeners will receive
     * either a {@link GameEvent.MoveEvent} on success or a
     * {@link GameEvent.InvalidMoveEvent} if the move is invalid.</p>
     */
    @Override
    public void moveLeft() {
        move(Move.LEFT);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Starts a new game by resetting the piece to the starting location (0, 0).
     * All registered listeners will receive a {@link GameEvent.NewGameEvent} containing
     * the starting location and initial valid moves.</p>
     */
    @Override
    public void newGame() {
        myLocation = STARTING_LOCATION;
        final GameEvent.NewGameEvent event =
                new GameEvent.NewGameEvent(myLocation,
                    calculateValidDirections(),
                    GameEvent.now());
        myPcs.firePropertyChange(event.getPropertyName(), null, event);
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

    /**
     * Calculates and returns the current valid moves based on the piece's location.
     *
     * @return a {@link ValidMoves} object containing the validity status of each direction
     */
    private ValidMoves calculateValidDirections() {
        return new ValidMoves(
                Map.of(
                        Move.UP, isUpValid(),
                        Move.DOWN, isDownValid(),
                        Move.LEFT, isLeftValid(),
                        Move.RIGHT, isRightValid()));
    }

    /**
     * Checks if moving up is valid from the current location.
     *
     * @return true if the piece can move up (y &gt; 0), false otherwise
     */
    private boolean isUpValid() {
        return myLocation.y() > 0;
    }

    /**
     * Checks if moving down is valid from the current location.
     *
     * @return true if the piece can move down (y &lt; HEIGHT - 1), false otherwise
     */
    private boolean isDownValid() {
        return myLocation.y() < HEIGHT - 1;
    }

    /**
     * Checks if moving left is valid from the current location.
     *
     * @return true if the piece can move left (x &gt; 0), false otherwise
     */
    private boolean isLeftValid() {
        return myLocation.x() > 0;
    }

    /**
     * Checks if moving right is valid from the current location.
     *
     * @return true if the piece can move right (x &lt; WIDTH - 1), false otherwise
     */
    private boolean isRightValid() {
        return myLocation.x() < WIDTH - 1;
    }

}
