package edu.uw.tcss.game;

import com.formdev.flatlaf.FlatIntelliJLaf;
import edu.uw.tcss.game.gui.contoller.GameController;
import javax.swing.SwingUtilities;

/**
 * The driver class for this demonstration code. 
 * @author Charles Bryan
 * @version Winter 2025
 *
 */
public final class Application {
    
    /**
     * Private empty constructor to avoid accidental instantiation. 
     */
    private Application() {
        super();
    }
    
    /**
     * Creates a JFrame to demonstrate this panel.
     * It is OK, even typical to include a main method 
     * in the same class file as a GUI for testing purposes. 
     * 
     * @param theArgs Command line arguments, ignored.
     */
    public static void main(final String[] theArgs) {
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(GameController::createAndShowGUI);
    }
}
