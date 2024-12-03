package edu.uw.tcss.game.view;

import edu.uw.tcss.game.model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static edu.uw.tcss.game.model.PropertyChangeEnabledGameControls.*;

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
        myPieceColor = Color.BLACK;
        if (PROPERTY_DOWN.equals(theEvent.getPropertyName())) {
            myGamePiece.setLocation((Point) theEvent.getNewValue());
            repaint();
        } else if (PROPERTY_UP.equals(theEvent.getPropertyName())) {
            myGamePiece.setLocation((Point) theEvent.getNewValue());
            repaint();
        } else if (PROPERTY_RIGHT.equals(theEvent.getPropertyName())) {
            myGamePiece.setLocation((Point) theEvent.getNewValue());
            repaint();
        } else if (PROPERTY_LEFT.equals(theEvent.getPropertyName())) {
            myGamePiece.setLocation((Point) theEvent.getNewValue());
            repaint();
        } else if (PROPERTY_NEW_GAME.equals(theEvent.getPropertyName())) {
            myGamePiece.setLocation((Point) theEvent.getNewValue());
            repaint();
        } else if (PROPERTY_INVALID.equals(theEvent.getPropertyName())) {
            myPieceColor = Color.RED;
            repaint();
        }
    }
}
