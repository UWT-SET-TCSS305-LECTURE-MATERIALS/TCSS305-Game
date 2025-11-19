/**
 * Controller package - Handles user input and coordinates between model and view.
 *
 * <p>This package implements the <strong>Controller</strong> component of the MVC pattern,
 * acting as an intermediary between user input and the game model. The controller
 * translates user actions (button clicks, key presses) into model operations and
 * updates the view in response to model changes.</p>
 *
 * <h2>Key Classes</h2>
 *
 * <dl>
 *   <dt>{@link edu.uw.tcss.game.gui.controller.GameController}</dt>
 *   <dd>Main controller class that:
 *     <ul>
 *       <li>Provides UI controls (directional buttons, new game button)</li>
 *       <li>Handles user input via action listeners and key adapters</li>
 *       <li>Observes model changes via PropertyChangeListener</li>
 *       <li>Updates button states based on valid move information</li>
 *     </ul>
 *   </dd>
 * </dl>
 *
 * <h2>Input Handling</h2>
 *
 * <p>The controller supports two input methods:</p>
 * <ul>
 *   <li><strong>Mouse/Touch</strong> - Directional buttons trigger game moves</li>
 *   <li><strong>Keyboard</strong> - WASD keys for movement, N for new game</li>
 * </ul>
 *
 * <h2>Observer Pattern Integration</h2>
 *
 * <p>The controller registers as a PropertyChangeListener to receive game state updates.
 * When move events arrive, the controller updates button enabled/disabled states based
 * on the {@link edu.uw.tcss.game.model.GameControls.ValidMoves} information included
 * in the event.</p>
 *
 * <pre>{@code
 * // Controller responds to model events
 * public void propertyChange(PropertyChangeEvent evt) {
 *     if (evt.getNewValue() instanceof GameEvent event) {
 *         switch (event) {
 *             case GameEvent.MoveEvent e ->
 *                 enableValidDirections(e.validMoves());
 *             case GameEvent.NewGameEvent e ->
 *                 enableValidDirections(e.validMoves());
 *             default -> { }
 *         }
 *     }
 * }
 * }</pre>
 *
 * @see edu.uw.tcss.game.model
 * @see edu.uw.tcss.game.gui.view
 *
 * @author Charles Bryan
 * @version Winter 2025
 */
package edu.uw.tcss.game.gui.controller;
