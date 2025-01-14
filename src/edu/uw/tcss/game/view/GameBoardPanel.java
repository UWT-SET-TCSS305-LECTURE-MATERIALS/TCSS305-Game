package edu.uw.tcss.game.view;

import static edu.uw.tcss.game.model.PropertyChangeEnabledGameControls.*;

import edu.uw.tcss.game.model.Game;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;


/**
 * This class represents a visual representation of a model.Game.
 *
 * @author Charles Bryan
 * @version AU 2024
 */
public class GameBoardPanel extends JPanel implements PropertyChangeListener {

    /**
     * The size of a 'square' game piece
     */
    private static final int GAME_PIECE_SIZE = 40;

    /**
     * The game piece.
     */
    private final Point myGamePiece;

    private Color myPieceColor;

    /**
     * Creates and sets-up a Game Panel to be displayed.
     */
    public GameBoardPanel() {
        super();
        setupComponents();
        myGamePiece = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
        myPieceColor = Color.BLACK;
    }

    private void setupComponents() {
        setPreferredSize(
                new Dimension(
                        Game.WIDTH * GAME_PIECE_SIZE,
                        Game.HEIGHT * GAME_PIECE_SIZE));
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

        g2d.setPaint(Color.BLACK);
        for (int row = 1; row < Game.HEIGHT; row++) {
            g2d.drawLine(0,
                    row * GAME_PIECE_SIZE,
                    Game.WIDTH * GAME_PIECE_SIZE,
                    row * GAME_PIECE_SIZE
            );
        }

        for (int col = 1; col < Game.WIDTH; col++) {
            g2d.drawLine(col * GAME_PIECE_SIZE,
                    0,
                    col * GAME_PIECE_SIZE,
                    Game.HEIGHT * GAME_PIECE_SIZE
            );
        }

        g2d.setPaint(myPieceColor);

        g2d.fill(new Rectangle2D.Double(
                myGamePiece.getX() * GAME_PIECE_SIZE,
                myGamePiece.getY() * GAME_PIECE_SIZE,
                GAME_PIECE_SIZE,
                GAME_PIECE_SIZE));
    }


    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {

        switch (theEvent.getPropertyName()) {
            case PROPERTY_DOWN:
            case PROPERTY_LEFT:
            case PROPERTY_UP:
            case PROPERTY_RIGHT:
            case PROPERTY_NEW_GAME:
                myGamePiece.setLocation((Point) theEvent.getNewValue());
                myPieceColor = Color.BLACK;
                repaint();
                break;
            case PROPERTY_INVALID:
                myPieceColor = Color.RED;
                repaint();
                break;
            case PROPERTY_VALID_DIRECTIONS:
                // for now do nothing!
                break;
            default:
                throw new IllegalStateException("Hmmm. Here is the "
                        + "PropertyName you have not considered: "
                        + theEvent.getPropertyName());
        }
    }
}
