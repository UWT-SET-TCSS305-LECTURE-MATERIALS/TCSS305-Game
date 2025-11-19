/**
 * View package - Visual representation of game state.
 *
 * <p>This package implements the <strong>View</strong> component of the MVC pattern,
 * responsible for rendering the game board and providing visual feedback to the user.
 * Views observe the model and update their visual representation when game state changes.</p>
 *
 * <h2>Key Classes</h2>
 *
 * <dl>
 *   <dt>{@link edu.uw.tcss.game.gui.view.GameBoardPanel}</dt>
 *   <dd>Custom JPanel that:
 *     <ul>
 *       <li>Visualizes the game board grid</li>
 *       <li>Renders the current piece location</li>
 *       <li>Observes model changes via PropertyChangeListener</li>
 *       <li>Provides visual feedback for valid/invalid moves</li>
 *     </ul>
 *   </dd>
 * </dl>
 *
 * <h2>Observer Pattern Integration</h2>
 *
 * <p>View components register as PropertyChangeListeners to receive game state updates.
 * When events arrive, the view extracts location information and triggers a repaint
 * to reflect the new game state.</p>
 *
 * <pre>{@code
 * // View responds to model events
 * game.addPropertyChangeListener(evt -> {
 *     if (evt.getNewValue() instanceof GameEvent event) {
 *         switch (event) {
 *             case GameEvent.MoveEvent e ->
 *                 updateDisplay(e.newLocation());
 *             case GameEvent.NewGameEvent e ->
 *                 updateDisplay(e.startingLocation());
 *             default -> { }
 *         }
 *     }
 * });
 * }</pre>
 *
 * <h2>Separation of Concerns</h2>
 *
 * <p>Views in this package maintain strict separation from business logic.
 * They do not modify game state directly - all state changes flow through
 * the controller to the model, which then notifies views of the changes.</p>
 *
 * @see edu.uw.tcss.game.model
 * @see edu.uw.tcss.game.gui.controller
 *
 * @author Charles Bryan
 * @version Winter 2025
 */
package edu.uw.tcss.game.gui.view;
