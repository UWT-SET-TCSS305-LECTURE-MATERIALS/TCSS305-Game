package edu.uw.tcss.game.gui.util;

import com.formdev.flatlaf.FlatLaf;
import edu.uw.tcss.game.model.GameControls;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * Utility class for creating game control icons.
 * Provides factory methods for generating directional arrow icons and new game icons.
 *
 * @author Charles Bryan
 * @version Autumn 2025
 */
public final class GameIcons {

    /** The size of icons in pixels. */
    private static final int ICON_SIZE = 30;

    /** Stroke width for icon graphics. */
    private static final float STROKE_WIDTH = 2.5f;

    /** The midpoint of the icon. */
    private static final int MID = ICON_SIZE / 2;

    /** The offset from center for arrow drawing. */
    private static final int OFFSET = ICON_SIZE / 3;

    /** Light grey fallback color. */
    private static final Color LIGHT_GREY = new Color(175, 177, 179);

    /** Light grey fallback color. */
    private static final Color DARK_GREY = new Color(47, 45, 43);

    /**
     * Private constructor to prevent instantiation.
     */
    private GameIcons() {
        super();
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Creates an arrow icon pointing in the specified direction.
     *
     * @param theDirection the direction for the arrow
     * @return an ImageIcon containing the arrow
     */
    public static ImageIcon createArrowIcon(final GameControls.Move theDirection) {
        final BufferedImage image = new BufferedImage(
                ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = image.createGraphics();

        setupGraphics(g2d);
        drawArrow(g2d, theDirection);

        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * Configures graphics context with antialiasing and drawing properties.
     * Uses theme-aware colors from FlatLaf - automatically adapts to light/dark themes.
     *
     * @param theG2d the graphics context to configure
     */
    private static void setupGraphics(final Graphics2D theG2d) {
        theG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        theG2d.setColor(getIconColor());
        theG2d.setStroke(new BasicStroke(STROKE_WIDTH));
    }

    /**
     * Gets the appropriate icon color based on the current FlatLaf theme.
     * Uses FlatLaf's Actions.Grey color which automatically adapts:
     * - Light themes: darker grey (#6E6E6E)
     * - Dark themes: lighter grey (#AFB1B3)
     *
     * @return the theme-appropriate color for icons
     */
    private static Color getIconColor() {
        Color iconColor = UIManager.getColor("Actions.Grey");

        if (iconColor == null) {
            if (FlatLaf.isLafDark()) {
                iconColor = LIGHT_GREY;
            } else {
                iconColor = DARK_GREY;
            }
        }

        return iconColor;
    }

    /**
     * Draws an arrow in the specified direction.
     *
     * @param theG2d the graphics context
     * @param theDirection the direction to draw
     */
    private static void drawArrow(final Graphics2D theG2d,
                                   final GameControls.Move theDirection) {
        switch (theDirection) {
            case UP -> drawUpArrow(theG2d);
            case DOWN -> drawDownArrow(theG2d);
            case LEFT -> drawLeftArrow(theG2d);
            case RIGHT -> drawRightArrow(theG2d);
            default -> throw new AssertionError("Unexpected direction: " + theDirection);
        }
    }

    /**
     * Draws an upward-pointing arrow.
     *
     * @param theG2d the graphics context
     */
    private static void drawUpArrow(final Graphics2D theG2d) {
        final int[] xPoints = {MID, MID - OFFSET, MID + OFFSET};
        final int[] yPoints = {OFFSET, ICON_SIZE - OFFSET, ICON_SIZE - OFFSET};
        theG2d.fillPolygon(xPoints, yPoints, xPoints.length);
    }

    /**
     * Draws a downward-pointing arrow.
     *
     * @param theG2d the graphics context
     */
    private static void drawDownArrow(final Graphics2D theG2d) {
        final int[] xPoints = {MID, MID - OFFSET, MID + OFFSET};
        final int[] yPoints = {ICON_SIZE - OFFSET, OFFSET, OFFSET};
        theG2d.fillPolygon(xPoints, yPoints, xPoints.length);
    }

    /**
     * Draws a left-pointing arrow.
     *
     * @param theG2d the graphics context
     */
    private static void drawLeftArrow(final Graphics2D theG2d) {
        final int[] xPoints = {OFFSET, ICON_SIZE - OFFSET, ICON_SIZE - OFFSET};
        final int[] yPoints = {MID, MID - OFFSET, MID + OFFSET};
        theG2d.fillPolygon(xPoints, yPoints, xPoints.length);
    }

    /**
     * Draws a right-pointing arrow.
     *
     * @param theG2d the graphics context
     */
    private static void drawRightArrow(final Graphics2D theG2d) {
        final int[] xPoints = {ICON_SIZE - OFFSET, OFFSET, OFFSET};
        final int[] yPoints = {MID, MID - OFFSET, MID + OFFSET};
        theG2d.fillPolygon(xPoints, yPoints, xPoints.length);
    }

    /**
     * Creates a play button icon (circle with right-facing triangle) for the new game button.
     *
     * @return an ImageIcon containing a play button
     */
    public static ImageIcon createNewGameIcon() {
        final BufferedImage image = new BufferedImage(
                ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2d = image.createGraphics();

        setupGraphics(g2d);

        // Draw circle
        final int margin = 2;
        g2d.drawOval(margin, margin, ICON_SIZE - 2 * margin, ICON_SIZE - 2 * margin);

        // Draw right-facing triangle (play symbol) in center
        final int mid = ICON_SIZE / 2;
        final int triangleSize = ICON_SIZE / 3;
        final int triangleOffset = 2;
        final int[] xPoints = {
            mid - triangleSize / 2 + triangleOffset,
            mid - triangleSize / 2 + triangleOffset,
            mid + triangleSize / 2 + triangleOffset
        };
        final int[] yPoints = {
            mid - triangleSize / 2,
            mid + triangleSize / 2,
            mid
        };
        g2d.fillPolygon(xPoints, yPoints, xPoints.length);

        g2d.dispose();
        return new ImageIcon(image);
    }
}
