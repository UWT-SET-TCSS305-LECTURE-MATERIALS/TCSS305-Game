package edu.uw.tcss.game;


import edu.uw.tcss.game.contoller.GameController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The driver class for this demonstration code. 
 * @author Charles Bryan
 * @version 1
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
        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (final UnsupportedLookAndFeelException
                       | IllegalAccessException
                       | InstantiationException
                       | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        
        SwingUtilities.invokeLater(GameController::createAndShowGUI);
    }
}
