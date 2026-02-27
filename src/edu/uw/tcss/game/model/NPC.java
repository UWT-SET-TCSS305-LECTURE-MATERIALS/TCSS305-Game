package edu.uw.tcss.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Encapsulates NPC state and movement logic for the game.
 * The NPC moves one cell per turn, either seeking the player (50% chance)
 * or moving randomly. This class owns its location, movement deltas,
 * and seek/random decision logic.
 *
 * <p>This class is intentionally <strong>package-private</strong> (no access modifier on
 * the class declaration). Package-private means that only other classes within the
 * same package ({@code edu.uw.tcss.game.model}) can see and instantiate this class.
 * Code outside the model package — such as the view or controller — cannot reference
 * {@code NPC} directly.</p>
 *
 * <p><strong>Why package-private?</strong> The NPC is an internal implementation detail
 * of the model. The rest of the application learns about the NPC solely through
 * {@link GameEvent} records fired by {@link Game}. Keeping {@code NPC} package-private
 * enforces this boundary: the view never holds a reference to the NPC, never calls
 * methods on it, and never depends on its existence. If the NPC implementation changes
 * or is removed entirely, nothing outside the model package is affected. This is
 * encapsulation at the package level — the model package exposes only its public
 * interfaces ({@link GameControls}, {@link PropertyChangeEnabledGameControls}) and
 * event types ({@link GameEvent}), while hiding the classes that do the real work.</p>
 *
 * <p>The NPC does not fire events — that responsibility stays with {@link Game},
 * which is the sole event-firing authority in the model.</p>
 *
 * @author Charles Bryan
 * @version Autumn 2025
 */
final class NPC {

    /** The starting location of the NPC. */
    static final GameControls.Point STARTING_LOCATION =
            new GameControls.Point(Game.WIDTH - 1, Game.HEIGHT - 1);

    /** The probability the NPC seeks the player instead of moving randomly. */
    private static final double SEEK_PROBABILITY = 0.5;

    /** Map of move directions to their point deltas. */
    private static final Map<GameControls.Move, GameControls.Point> MOVE_DELTAS = Map.of(
            GameControls.Move.UP, new GameControls.Point(0, -1),
            GameControls.Move.DOWN, new GameControls.Point(0, 1),
            GameControls.Move.LEFT, new GameControls.Point(-1, 0),
            GameControls.Move.RIGHT, new GameControls.Point(1, 0)
    );

    /** Random number generator for NPC movement decisions. */
    private final Random myRandom = new Random();

    /** The NPC's current location. */
    private GameControls.Point myLocation;

    /**
     * Creates an NPC at its starting location.
     */
    NPC() {
        super();
        myLocation = STARTING_LOCATION;
    }

    /**
     * Advances the NPC one move. With {@value SEEK_PROBABILITY} probability the NPC
     * seeks the player; otherwise it moves randomly.
     *
     * @param thePlayerLocation the current player location used for seeking
     * @return a {@link MoveResult} containing the chosen direction and new location
     */
    MoveResult advance(final GameControls.Point thePlayerLocation) {
        final List<GameControls.Move> validMoves = getValidMoves();
        final GameControls.Move chosen;
        if (myRandom.nextDouble() < SEEK_PROBABILITY) {
            chosen = seekTarget(validMoves, thePlayerLocation);
        } else {
            Collections.shuffle(validMoves);
            chosen = validMoves.getFirst();
        }
        final GameControls.Point delta = MOVE_DELTAS.get(chosen);
        myLocation = myLocation.transform(delta.x(), delta.y());
        return new MoveResult(chosen, myLocation);
    }

    /**
     * Resets the NPC to its starting location.
     */
    void reset() {
        myLocation = STARTING_LOCATION;
    }

    /**
     * Returns the NPC's current location.
     *
     * @return the current location
     */
    GameControls.Point getLocation() {
        return myLocation;
    }

    /**
     * Returns all valid moves for the NPC from its current position.
     *
     * @return a mutable list of moves that keep the NPC within board bounds
     */
    private List<GameControls.Move> getValidMoves() {
        final List<GameControls.Move> validMoves = new ArrayList<>();
        for (final GameControls.Move move : GameControls.Move.values()) {
            final GameControls.Point delta = MOVE_DELTAS.get(move);
            final GameControls.Point candidate = myLocation.transform(delta.x(), delta.y());
            if (candidate.x() >= 0 && candidate.x() < Game.WIDTH
                    && candidate.y() >= 0 && candidate.y() < Game.HEIGHT) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Chooses the best move from the valid moves to seek the target location.
     * Prefers reducing the axis with the greatest distance first.
     * Falls back to a random valid move if no seeking move is available.
     *
     * @param theValidMoves the list of valid moves
     * @param theTarget the target location to seek
     * @return the move that best closes distance to the target
     */
    private GameControls.Move seekTarget(final List<GameControls.Move> theValidMoves,
                                         final GameControls.Point theTarget) {
        final List<GameControls.Move> preferred =
                buildPreferredMoves(theTarget.x() - myLocation.x(),
                                    theTarget.y() - myLocation.y());

        // Pick the first preferred move that is valid; fall back to random
        GameControls.Move chosen = null;
        for (final GameControls.Move move : preferred) {
            if (theValidMoves.contains(move)) {
                chosen = move;
                break;
            }
        }
        if (chosen == null) {
            Collections.shuffle(theValidMoves);
            chosen = theValidMoves.getFirst();
        }
        return chosen;
    }

    /**
     * Builds a preference-ordered list of moves that close the gap described
     * by the given deltas. The axis with the larger gap is prioritized.
     *
     * @param theDx horizontal distance (positive = target is right)
     * @param theDy vertical distance (positive = target is below)
     * @return a list of preferred moves, best first
     */
    private List<GameControls.Move> buildPreferredMoves(final int theDx, final int theDy) {
        final List<GameControls.Move> preferred = new ArrayList<>();
        if (Math.abs(theDx) >= Math.abs(theDy)) {
            addHorizontal(preferred, theDx);
            addVertical(preferred, theDy);
        } else {
            addVertical(preferred, theDy);
            addHorizontal(preferred, theDx);
        }
        return preferred;
    }

    /**
     * Adds horizontal move to the list based on the sign of the delta.
     *
     * @param theMoves the list to add to
     * @param theDx the horizontal delta
     */
    private void addHorizontal(final List<GameControls.Move> theMoves, final int theDx) {
        if (theDx > 0) {
            theMoves.add(GameControls.Move.RIGHT);
        } else if (theDx < 0) {
            theMoves.add(GameControls.Move.LEFT);
        }
    }

    /**
     * Adds vertical move to the list based on the sign of the delta.
     *
     * @param theMoves the list to add to
     * @param theDy the vertical delta
     */
    private void addVertical(final List<GameControls.Move> theMoves, final int theDy) {
        if (theDy > 0) {
            theMoves.add(GameControls.Move.DOWN);
        } else if (theDy < 0) {
            theMoves.add(GameControls.Move.UP);
        }
    }

    /**
     * The result of an NPC advance: the direction chosen and the new location.
     *
     * @param direction the move direction the NPC chose
     * @param newLocation the NPC's location after the move
     */
    record MoveResult(GameControls.Move direction, GameControls.Point newLocation) { }
}
