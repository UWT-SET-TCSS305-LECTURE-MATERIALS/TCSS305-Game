
package edu.uw.tcss.game.gui.controller;

import edu.uw.tcss.game.gui.util.GameIcons;
import edu.uw.tcss.game.gui.view.GameBoardPanel;
import edu.uw.tcss.game.model.Game;
import edu.uw.tcss.game.model.GameControls;
import edu.uw.tcss.game.model.GameEvent;
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
 * A controller for the Game.
 *
 * @author Charles Bryan
 * @version Autumn 2025
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
    
    /** The size of control buttons in pixels. */
    private static final int BUTTON_SIZE = 45;

    /** The default size of the panel. */
    private static final int WINDOW_SIZE = 200;
    
    /** The Game object this class controls. */
    private final GameControls myGame;

    /** Button to move the game piece up. */
    private final JButton myUpButton;

    /** Button to move the game piece down. */
    private final JButton myDownButton;

    /** Button to move the game piece left. */
    private final JButton myLeftButton;

    /** Button to move the game piece right. */
    private final JButton myRightButton;

    /** Button to start a new game. */
    private final JButton myNewGameButton;
    

    /**
     * Constructs a Game Controller.
     * 
     * @param theGame the Game object this class controls
     */
    public GameController(final Game theGame) {
        super(new GridLayout(ROW, COL));
        myGame = theGame;
        myUpButton = new JButton(GameIcons.createArrowIcon(GameControls.Move.UP));
        myDownButton = new JButton(GameIcons.createArrowIcon(GameControls.Move.DOWN));
        myLeftButton = new JButton(GameIcons.createArrowIcon(GameControls.Move.LEFT));
        myRightButton = new JButton(GameIcons.createArrowIcon(GameControls.Move.RIGHT));
        myNewGameButton = new JButton(GameIcons.createNewGameIcon());

        setupComponents();
        layoutComponents();
        addListeners();
    }

    /**
     * Sets up component properties including button sizes and initial states.
     */
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
     * Arranges components in a 3x3 grid layout with directional buttons.
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

    /**
     * Adds action and key listeners to buttons and panel.
     */
    private void addListeners() {
        myUpButton.addActionListener(theEvent ->
                buttonAction(myGame::moveUp));
        myDownButton.addActionListener(theEvent ->
                buttonAction(myGame::moveDown));
        myRightButton.addActionListener(theEvent ->
                buttonAction(myGame::moveRight));
        myLeftButton.addActionListener(theEvent ->
                buttonAction(myGame::moveLeft));
        myNewGameButton.addActionListener(theEvent ->
                buttonAction(myGame::newGame));

        addKeyListener(new MyNewKeyAdapter());
        myNewGameButton.addKeyListener(new MyNewKeyAdapter());
        addKeyListener(new MyControlsKeyAdapter());
    }

    private void buttonAction(final Runnable theGameAction) {
        theGameAction.run();
        // needed to give focus back to the containing panel. This is so that
        // the panel with the KeyListener captures the KeyEvents, not the
        // button that was just clicked.
        requestFocusInWindow();
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

        final Game game = Game.getInstance();
       
        //Create and set up the content pane.
        final GameController pane = new GameController(game);

        //Add the GameController object itself as a PCL to the model.
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

    /**
     * Creates and displays the game board view window.
     *
     * @param theControllerFrame the controller frame to position the view relative to
     * @param theView the game board panel to display
     */
    private static void createAndShowView(final JFrame theControllerFrame,
                                          final JPanel theView) {
        final JFrame gameBoardFrame = new JFrame();
        gameBoardFrame.setTitle("Game Board");
        gameBoardFrame.setContentPane(theView);
        gameBoardFrame.pack();
        gameBoardFrame.setResizable(false);
        gameBoardFrame.setVisible(true);
        gameBoardFrame.setLocation(
                theControllerFrame.getLocation().x + theControllerFrame.getWidth(),
                theControllerFrame.getLocation().y);
    }

    /**
     * Enables or disables directional buttons based on valid moves.
     *
     * @param theValidMoves the valid moves for the current game state
     */
    private void enableValidDirections(final GameControls.ValidMoves theValidMoves) {
        final Map<GameControls.Move, Boolean> moves =
                theValidMoves.moves();
        myUpButton.setEnabled(moves.get(GameControls.Move.UP));
        myDownButton.setEnabled(moves.get(GameControls.Move.DOWN));
        myLeftButton.setEnabled(moves.get(GameControls.Move.LEFT));
        myRightButton.setEnabled(moves.get(GameControls.Move.RIGHT));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent theEvent) {
        if (theEvent.getNewValue() instanceof final GameEvent event) {
            switch (event) {
                case final GameEvent.MoveEvent moveEvent ->
                        enableValidDirections(moveEvent.validMoves());
                case final GameEvent.NewGameEvent newGameEvent ->
                        enableValidDirections(newGameEvent.validMoves());
                case final GameEvent.InvalidMoveEvent e -> { }  // Explicitly ignored
            }
        }
    }

    /**
     * Key adapter to handle 'N' key for starting a new game.
     */
    private final class MyNewKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(final KeyEvent theEvent) {
            if (theEvent.getKeyCode() == KeyEvent.VK_N) {
                myGame.newGame();
            }
        }
    }

    /**
     * Key adapter to handle WASD keys for directional movement.
     */
    private final class MyControlsKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(final KeyEvent theEvent) {
            mapKeys(theEvent.getKeyCode()).run();
        }

        /**
         * Maps key codes to game movement actions.
         *
         * @param theKeyCode the key code to map
         * @return the corresponding movement action or no-op
         */
        private Runnable mapKeys(final int theKeyCode) {
            final Runnable doNothing = () -> { };

            return switch (theKeyCode) {
                case KeyEvent.VK_W -> myGame::moveUp;
                case KeyEvent.VK_S -> myGame::moveDown;
                case KeyEvent.VK_A -> myGame::moveLeft;
                case KeyEvent.VK_D -> myGame::moveRight;
                default -> doNothing;
            };
        }
    }

    /**
     * Alternative key adapter implementation using a map-based approach.
     * Demonstrates a different pattern for key handling (unused in current implementation).
     */
    private final class MyOtherControlsKeyAdapter extends KeyAdapter {

        /** Map of key codes to their corresponding game actions. */
        private final Map<Integer, Runnable> myKeyMap;

        /**
         * Constructs the adapter and initializes the key mapping.
         */
        MyOtherControlsKeyAdapter() {
            super();
            myKeyMap = mapKeys();
        }

        /**
         * Creates the map of key codes to game actions.
         *
         * @return map of key codes to actions
         */
        private Map<Integer, Runnable> mapKeys() {
            return Map.of(
                    KeyEvent.VK_W, myGame::moveUp,
                    KeyEvent.VK_S, myGame::moveDown,
                    KeyEvent.VK_A, myGame::moveDown,
                    KeyEvent.VK_D, myGame::moveRight);
        }


        @Override
        public void keyPressed(final KeyEvent theEvent) {
            final Runnable doNothing = () -> { };
            myKeyMap.getOrDefault(theEvent.getKeyCode(), doNothing).run();
        }
    }
}


























