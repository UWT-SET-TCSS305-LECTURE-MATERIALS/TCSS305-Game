package edu.uw.tcss.game.gui.view;

import edu.uw.tcss.game.model.Game;
import edu.uw.tcss.game.model.GameControls;
import edu.uw.tcss.game.model.GameEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 * Visual representation of a Game, displaying the game board and piece.
 *
 * @author Charles Bryan
 * @version Autumn 2025
 */
public class GameBoardPanel extends JPanel implements PropertyChangeListener {

    /**
     * The size of a 'square' game piece
     */
    private static final int GAME_PIECE_SIZE = 40;

    /**
     * Pre-calculated grid path containing all horizontal and vertical grid lines.
     * This path is calculated once and reused for all paint operations.
     */
    private static final Path2D GRID_PATH = createGridPath();

    /**
     * Pre-calculated rectangle representing the game piece.
     * Updated when the piece moves, eliminating calculations during painting.
     */
    private final Rectangle2D myGamePiece;

    /** The color of the game piece. */
    private Color myPieceColor;

    /**
     * Creates and sets-up a Game Panel to be displayed.
     */
    public GameBoardPanel() {
        super();
        setupComponents();
        myGamePiece = new Rectangle2D.Double(
                Integer.MIN_VALUE, Integer.MIN_VALUE,
                GAME_PIECE_SIZE, GAME_PIECE_SIZE);
        myPieceColor = Color.BLACK;
    }

    /**
     * Sets up component properties including preferred size based on game dimensions.
     */
    private void setupComponents() {
        setPreferredSize(
                new Dimension(
                        Game.WIDTH * GAME_PIECE_SIZE,
                        Game.HEIGHT * GAME_PIECE_SIZE));
    }

    /**
     * Creates a Path2D containing all grid lines for the game board.
     * This method is called once during class initialization to pre-calculate
     * the grid geometry, eliminating the need for repeated calculations during painting.
     *
     * @return a Path2D containing all horizontal and vertical grid lines
     */
    private static Path2D createGridPath() {
        final Path2D path = new Path2D.Double();

        // Add horizontal grid lines
        for (int row = 1; row < Game.HEIGHT; row++) {
            final double y = row * GAME_PIECE_SIZE;
            path.moveTo(0, y);
            path.lineTo(Game.WIDTH * GAME_PIECE_SIZE, y);
        }

        // Add vertical grid lines
        for (int col = 1; col < Game.WIDTH; col++) {
            final double x = col * GAME_PIECE_SIZE;
            path.moveTo(x, 0);
            path.lineTo(x, Game.HEIGHT * GAME_PIECE_SIZE);
        }

        return path;
    }

    /**
     * Updates the game piece rectangle to reflect a new grid position.
     * Converts grid coordinates to pixel coordinates.
     *
     * @param theX the x grid coordinate
     * @param theY the y grid coordinate
     */
    private void updatePieceRectangle(final int theX, final int theY) {
        myGamePiece.setRect(
                theX * GAME_PIECE_SIZE,
                theY * GAME_PIECE_SIZE,
                GAME_PIECE_SIZE,
                GAME_PIECE_SIZE);
    }

    // Suppression is OK here as this method is Overriden from a Library class.
    @SuppressWarnings("PublicMethodNotExposedInInterface")
    @Override
    public void paintComponent(final Graphics theGraphics) {
        super.paintComponent(theGraphics);
        final Graphics2D g2d = (Graphics2D) theGraphics;

        // for better graphics display
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw pre-calculated grid lines
        g2d.setPaint(Color.BLACK);
        g2d.draw(GRID_PATH);

        // Draw pre-calculated game piece rectangle
        g2d.setPaint(myPieceColor);
        g2d.fill(myGamePiece);
    }

    /**
     * Handles a valid move by updating the piece location and repainting.
     *
     * @param theLocation the new location of the piece
     */
    private void movePiece(final GameControls.Point theLocation) {
        updatePieceRectangle(theLocation.x(), theLocation.y());
        myPieceColor = Color.BLACK;
        repaint();
    }

    /**
     * Handles an invalid move attempt by changing the piece color to red.
     */
    private void invalidMove() {
        myPieceColor = Color.RED;
        repaint();
    }

    /**
     * Handles a new game by resetting the piece to the starting location.
     *
     * @param theStartLocation the starting location for the new game
     */
    private void startGame(final GameControls.Point theStartLocation) {
        updatePieceRectangle(theStartLocation.x(), theStartLocation.y());
        myPieceColor = Color.BLACK;
        repaint();
    }


    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {
        if (theEvent.getNewValue() instanceof final GameEvent event) {
            switch (event) {
                case final GameEvent.MoveEvent moveEvent ->
                        movePiece(moveEvent.newLocation());
                case final GameEvent.InvalidMoveEvent invalidEvent ->
                        invalidMove();
                case final GameEvent.NewGameEvent newGameEvent ->
                        startGame(newGameEvent.startingLocation());
            }
        }
    }
}
