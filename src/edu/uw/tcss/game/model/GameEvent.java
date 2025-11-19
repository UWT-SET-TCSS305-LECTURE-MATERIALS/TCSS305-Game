package edu.uw.tcss.game.model;

/**
 * Sealed interface representing type-safe events for the game's
 * PropertyChangeListener framework.
 *
 * <p>Each implementing record represents a specific event that can be observed,
 * carrying the actual event data for compile-time safety. This design enables
 * pattern matching and exhaustiveness checking in switch expressions when
 * handling game events.</p>
 *
 * <p><strong>Design Pattern:</strong> Sealed Classes + Records (Java 21)
 * <ul>
 *   <li>Sealed interface ensures all event types are known at compile time</li>
 *   <li>Record implementations provide immutability and reduced boilerplate</li>
 *   <li>Pattern matching enables type-safe event handling without casting</li>
 *   <li>Each event includes a timestamp for temporal tracking</li>
 * </ul>
 *
 * <p><strong>Usage example with PropertyChangeListener:</strong></p>
 * <pre>{@code
 * game.addPropertyChangeListener(evt -> {
 *     if (evt.getNewValue() instanceof GameEvent event) {
 *         switch (event) {
 *             case GameEvent.MoveEvent e ->
 *                 handleMove(e.direction(), e.newLocation(), e.validMoves());
 *             case GameEvent.InvalidMoveEvent e ->
 *                 handleInvalidMove(e.currentLocation(), e.attemptedDirection());
 *             case GameEvent.NewGameEvent e ->
 *                 handleNewGame(e.startingLocation(), e.validMoves());
 *             // compiler ensures all cases are handled
 *         }
 *     }
 * });
 * }</pre>
 *
 *
 * @author Charles Bryan
 * @version Autumn 2025
 * @see <a href="https://openjdk.org/jeps/409">JEP 409: Sealed Classes</a>
 * @see <a href="https://openjdk.org/jeps/395">JEP 395: Records</a>
 */
public sealed interface GameEvent permits
        GameEvent.MoveEvent,
        GameEvent.InvalidMoveEvent,
        GameEvent.NewGameEvent {

    /**
     * Returns the timestamp when this event was created.
     *
     * @return timestamp in milliseconds since epoch
     */
    long timestamp();

    /**
     * Returns the property name for this event type, used when registering property-specific
     * listeners.
     *
     * <p>This method provides a self-describing property name for each event type, enabling
     * type-safe listener registration without hardcoded strings.</p>
     *
     * <p><strong>Default Implementation:</strong> Returns the simple class name of the event
     * (e.g., "MoveEvent", "NewGameEvent"). This ensures consistency between
     * the event type and its property name.</p>
     *
     * @return the property name string for this event type
     */
    default String getPropertyName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Helper method to get the current time.
     *
     * @return current time in milliseconds since epoch
     */
    static long now() {
        return System.currentTimeMillis();
    }

    /**
     * Event fired when a valid move is made in any direction.
     * This event consolidates the movement and the updated valid directions
     * into a single atomic event, eliminating the need for multiple events per action.
     *
     * @param direction the direction of the successful move
     * @param newLocation the new location of the game piece after the move
     * @param validMoves the updated set of valid moves from the new location
     * @param timestamp when the event was created (milliseconds since epoch)
     */
    record MoveEvent(
            GameControls.Move direction,
            GameControls.Point newLocation,
            GameControls.ValidMoves validMoves,
            long timestamp
    ) implements GameEvent {
    }

    /**
     * Event fired when an invalid move is attempted.
     * The piece remains at its current location.
     *
     * @param currentLocation the current location where the piece remains
     * @param attemptedDirection the direction that was attempted but invalid
     * @param timestamp when the event was created (milliseconds since epoch)
     */
    record InvalidMoveEvent(
            GameControls.Point currentLocation,
            GameControls.Move attemptedDirection,
            long timestamp
    ) implements GameEvent {
    }

    /**
     * Event fired when a new game is started.
     * The piece is reset to the starting location (typically 0, 0).
     *
     * @param startingLocation the starting location of the piece (typically 0, 0)
     * @param validMoves the initial set of valid moves from the starting position
     * @param timestamp when the event was created (milliseconds since epoch)
     */
    record NewGameEvent(
            GameControls.Point startingLocation,
            GameControls.ValidMoves validMoves,
            long timestamp
    ) implements GameEvent {
    }

}