 
package edu.uw.tcss.game.gui.contoller;

import static edu.uw.tcss.game.model.PropertyChangeEnabledGameControls.PROPERTY_NEW_GAME;
import static edu.uw.tcss.game.model.PropertyChangeEnabledGameControls.PROPERTY_VALID_DIRECTIONS;

import edu.uw.tcss.game.gui.view.GameBoardPanel;
import edu.uw.tcss.game.model.Game;
import edu.uw.tcss.game.model.GameControls;
import edu.uw.tcss.game.model.PropertyChangeEnabledGameControls;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/**
 *  A controller for theGame.
 * 
 *  @author Charles Bryan
 *  @version Winter 2025
 */
public class GameController extends JPanel implements PropertyChangeListener {
    
    /**
     * A generated serial version UID for object Serialization. 
     * <a href="http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html">...</a>
     */
    @Serial
    private static final long serialVersionUID = 8452917670991316606L;
    
    // constants to capture screen dimensions
    /** A ToolKit. */
    private static final Toolkit KIT = Toolkit.getDefaultToolkit();
    
    /** The Dimension of the screen. */
    private static final Dimension SCREEN_SIZE = KIT.getScreenSize();
    
    /** The number of rows. */
    private static final int ROW = 3;
    
    /** The number of columns. */
    private static final int COL = 3;
    
    /** Amount in Pixels for the Horizontal margin. */
    private static final int HORIZONTAL_MARGIN = 20;
    
    /** Amount in Pixels for the Vertical margin. */
    private static final int VERTICAL_MARGIN = 10;
    
    /** Amount in columns for the text area. */
    private static final int BUTTON_SIZE = 45;
    
    /** The default size of the color panel. */
    private static final int WINDOW_SIZE = 200;
    
    /** The Color object this class controls. */
    private final PropertyChangeEnabledGameControls myGame;

    private final JButton myUpButton;
    private final JButton myDownButton;
    private final JButton myLeftButton;
    private final JButton myRightButton;
    private final JButton myNewGameButton;
    

    /**
     * Constructs a Game Controller.
     * 
     * @param theGame the Game object this class controls
     */
    public GameController(final Game theGame) {
        super(new GridLayout(ROW, COL));
        myGame = theGame;
        myDownButton = new JButton("v");
        myUpButton = new JButton("^");
        myLeftButton = new JButton("<");
        myRightButton = new JButton(">");
        myNewGameButton = new JButton("*");

        setupComponents();
        layoutComponents();
        addListeners();
    }

    private void setupComponents() {
        myUpButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        myUpButton.setEnabled(false);
        myDownButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        myDownButton.setEnabled(false);
        myLeftButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        myLeftButton.setEnabled(false);
        myRightButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        myRightButton.setEnabled(false);
        myNewGameButton.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
    }
    
    /**
     * Lay out the components.
     */
    private void layoutComponents() {
        setBorder(BorderFactory.createEmptyBorder(VERTICAL_MARGIN,
                HORIZONTAL_MARGIN,
                VERTICAL_MARGIN,
                VERTICAL_MARGIN));
        add(new JPanel());
        add(myUpButton);
        add(new JPanel());
        add(myLeftButton);
        add(myNewGameButton);
        add(myRightButton);
        add(new JPanel());
        add(myDownButton);
        add(new JPanel());
    }
    
    private void addListeners() {
        myUpButton.addActionListener(theEvent -> {
            myGame.moveUp();
            // needed to give foucs back to the contaiing panel. This is so that
            // the panel with the KeyListener captures the KeyEvents, not the
            // button that was just clicked.
            requestFocusInWindow();
        });
        myDownButton.addActionListener(theEvent -> {
            myGame.moveDown();
            requestFocusInWindow(); // See above

        });
        myRightButton.addActionListener(theEvent -> {
            myGame.moveRight();
            requestFocusInWindow(); // See above
        });
        myLeftButton.addActionListener(theEvent -> {
            myGame.moveLeft();
            requestFocusInWindow(); // See above
        });
        myNewGameButton.addActionListener(theEvent -> {
            myGame.newGame();
            requestFocusInWindow(); // See above
        });
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     * <p>
     * NOTE: This is the place where all of the parts and pieces of this project are in 
     *      the same place. This is where we should instantiate our Model and add it to the
     *      controller and view.  
     */
    public static void createAndShowGUI() {
        //Create and set up the window.
        final JFrame frame = new JFrame("Play the Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final Game game = new Game();
       
        //Create and set up the content pane.
        final GameController pane = new GameController(game);
        pane.setOpaque(true); //content panes must be opaque
        
        //Add the ColorSlider object itself as a PCL to the model.  
        game.addPropertyChangeListener(pane);
        frame.setContentPane(pane);
        // position the frame in the center of the screen
        frame.pack();
        frame.setLocation(SCREEN_SIZE.width / 2 - frame.getWidth() / 2,
                    SCREEN_SIZE.height / 2 - frame.getHeight() / 2);
        
        //Create a Game Panel to listen to and demonstrate our
        //Game.
        final GameBoardPanel gamePanel = new GameBoardPanel();
        game.addPropertyChangeListener(gamePanel);
        SwingUtilities.invokeLater(() -> createAndShowView(frame, gamePanel));

        //Display the window.
        frame.setVisible(true);
    }

    private static void createAndShowView(final JFrame theControllerFrame,
                                          final JPanel theView) {
        final JFrame colorFrame = new JFrame();
        colorFrame.setTitle("Color Display");
        colorFrame.setContentPane(theView);
        colorFrame.pack();
        colorFrame.setVisible(true);
        colorFrame.setLocation(
                theControllerFrame.getLocation().x + theControllerFrame.getWidth(),
                theControllerFrame.getLocation().y);
    }


    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {
        if (PROPERTY_NEW_GAME.equals(theEvent.getPropertyName())) {
            myUpButton.setEnabled(true);
            myDownButton.setEnabled(true);
            myLeftButton.setEnabled(true);
            myRightButton.setEnabled(true);
        } else if (PROPERTY_VALID_DIRECTIONS.equals(theEvent.getPropertyName())) {
            final Map<GameControls.Move, Boolean> moves =
                    ((GameControls.ValidMoves) theEvent.getNewValue()).moves();
            myUpButton.setEnabled(moves.get(GameControls.Move.UP));
            myDownButton.setEnabled(moves.get(GameControls.Move.DOWN));
            myLeftButton.setEnabled(moves.get(GameControls.Move.LEFT));
            myRightButton.setEnabled(moves.get(GameControls.Move.RIGHT));
        }

    }
}