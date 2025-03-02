package edu.uw.tcss.game.model;

import java.beans.PropertyChangeListener;

/**
 * Defines behaviors allowing PropertyChangeListeners to be added or removed from a 
 * GameContols object. Implementing classes should inform PropertyChangeListeners
 * when methods defined in GameControls API mutate the state of the object.
 * <p>
 * Defines a set of Properties that may be listened too. Implementing class may further define
 * more Properties. 
 * 
 * @author Charles Bryan
 * @version Winter 2025
 *
 */
public interface PropertyChangeEnabledGameControls extends GameControls {
 
    /*
     * Add your own constant Property values here. 
     */

    /**
     * A property name for state changes on the model.
     * Used when the game piece is moved up.
     * Expected type for newValue() edu.uw.tcss.game.model.GameControls.Point
     *      (the current location)
     */
    String PROPERTY_UP = "the piece moved UP!";

    /**
     * A property name for state changes on the model.
     * Used when the game piece is moved DOWN.
     * Expected type for newValue() edu.uw.tcss.game.model.GameControls.Point
     *      (the current location)
     */
    String PROPERTY_DOWN = "the piece moved DOWN!";

    /**
     * A property name for state changes on the model.
     * Used when the game piece is moved LEFT.
     * Expected type for newValue() edu.uw.tcss.game.model.GameControls.Point
     *      (the current location)
     */
    String PROPERTY_LEFT = "the piece moved LEFT!";

    /**
     * A property name for state changes on the model.
     * Used when the game piece is moved RIGHT.
     * Expected type for newValue() edu.uw.tcss.game.model.GameControls.Point
     *      (the current location)
     */
    String PROPERTY_RIGHT = "the piece moved RIGHT!";

    /**
     * A property name for state changes on the model.
     * Used when the game piece move is attempted but invalid.
     * Expected type for newValue() Boolean Boolean.TRUE means invalid location
     * Expected type for newValue() edu.uw.tcss.game.model.GameControls.Point
     *      (the current location)
     */
    String PROPERTY_INVALID = "the piece can't move there!";

    /**
     * A property name for state changes on the model.
     * Used when a new game is started.
     * Expected type for newValue() edu.uw.tcss.game.model.GameControls.Point
     *      (the current location)
     */
    String PROPERTY_NEW_GAME = "A new game is afoot!";

    /**
     * A property name for state changes on the model.
     * Used when a new game is started and when the game piece location has changed.
     * Expected type for newValue()
     *      edu.uw.tcss.game.model.GameControls.ValidMoves
     *          (the directions and whether or not they are valid)
     */
    String PROPERTY_VALID_DIRECTIONS = "These are the directions in which you may move!";


    /**
     * Add a PropertyChangeListener to the listener list. The listener is registered for 
     * all properties. The same listener object may be added more than once, and will be 
     * called as many times as it is added. If listener is null, no exception is thrown and 
     * no action is taken.
     * 
     * @param theListener The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(PropertyChangeListener theListener);
    
    
    /**
     * Add a PropertyChangeListener for a specific property. The listener will be invoked only 
     * when a call on firePropertyChange names that specific property. The same listener object
     * may be added more than once. For each property, the listener will be invoked the number 
     * of times it was added for that property. If propertyName or listener is null, no 
     * exception is thrown and no action is taken.
     * 
     * @param thePropertyName The name of the property to listen on.
     * @param theListener The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(String thePropertyName, PropertyChangeListener theListener);

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a 
     * PropertyChangeListener that was registered for all properties. If listener was added 
     * more than once to the same event source, it will be notified one less time after being 
     * removed. If listener is null, or was never added, no exception is thrown and no action 
     * is taken.
     * 
     * @param theListener The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(PropertyChangeListener theListener);
    
    /**
     * Remove a PropertyChangeListener for a specific property. If listener was added more than
     * once to the same event source for the specified property, it will be notified one less 
     * time after being removed. If propertyName is null, no exception is thrown and no action 
     * is taken. If listener is null, or was never added for the specified property, no 
     * exception is thrown and no action is taken.
     * 
     * @param thePropertyName The name of the property that was listened on.
     * @param theListener The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(String thePropertyName, 
                                      PropertyChangeListener theListener);
}
