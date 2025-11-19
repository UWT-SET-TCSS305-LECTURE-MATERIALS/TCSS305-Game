/**
 * Game model - Contains all game logic, state management, and business rules.
 *
 * <p>This package implements the <strong>Model</strong> component of the MVC pattern,
 * maintaining complete separation from presentation concerns. The model is responsible
 * for game state, move validation, and notifying observers of state changes.</p>
 *
 * <h2>Architecture and Design Patterns</h2>
 *
 * <h3>Singleton Pattern</h3>
 * <p>The {@link edu.uw.tcss.game.model.Game} class implements the Singleton pattern
 * using double-checked locking to ensure thread-safe lazy initialization. This guarantees
 * a single source of truth for game state throughout the application.</p>
 *
 * <h3>Observer Pattern (PropertyChangeListener)</h3>
 * <p>The model uses Java's PropertyChangeListener framework to implement the Observer
 * pattern. Instead of traditional string-based property names, this implementation
 * uses type-safe sealed {@link edu.uw.tcss.game.model.GameEvent} types that can be
 * pattern-matched for compile-time safety.</p>
 *
 * <h3>Immutable Data Transfer</h3>
 * <p>All data objects ({@link edu.uw.tcss.game.model.GameControls.Point},
 * {@link edu.uw.tcss.game.model.GameControls.ValidMoves}, and all GameEvent subtypes)
 * are implemented as immutable records, ensuring thread safety and preventing
 * accidental state mutations.</p>
 *
 * <h2>Key Classes and Interfaces</h2>
 *
 * <dl>
 *   <dt>{@link edu.uw.tcss.game.model.Game}</dt>
 *   <dd>The core game model implementing Singleton and Observer patterns.
 *       Manages game state and coordinates all game operations.</dd>
 *
 *   <dt>{@link edu.uw.tcss.game.model.GameControls}</dt>
 *   <dd>Defines the contract for game operations. Contains nested types for
 *       moves, positions, and valid move state.</dd>
 *
 *   <dt>{@link edu.uw.tcss.game.model.PropertyChangeEnabledGameControls}</dt>
 *   <dd>Extends GameControls with PropertyChangeListener support for
 *       event-driven architecture.</dd>
 *
 *   <dt>{@link edu.uw.tcss.game.model.GameEvent}</dt>
 *   <dd>Sealed interface defining type-safe game events. Permits exactly three
 *       implementations: MoveEvent, InvalidMoveEvent, and NewGameEvent.</dd>
 * </dl>
 *
 * <h2>Event Flow</h2>
 *
 * <pre>{@code
 * // Listeners register for events
 * game.addPropertyChangeListener(event -> {
 *     // Pattern matching on sealed types
 *     switch (event.getNewValue()) {
 *         case GameEvent.MoveEvent e ->
 *             System.out.println("Moved to: " + e.newLocation());
 *         case GameEvent.InvalidMoveEvent e ->
 *             System.out.println("Invalid move: " + e.attemptedDirection());
 *         case GameEvent.NewGameEvent e ->
 *             System.out.println("New game started");
 *     }
 * });
 *
 * // Game operations trigger events
 * game.moveRight();  // Fires MoveEvent or InvalidMoveEvent
 * game.newGame();    // Fires NewGameEvent
 * }</pre>
 *
 * <h2>Modern Java Features</h2>
 *
 * <p>This package demonstrates several modern Java features:</p>
 * <ul>
 *   <li><strong>Records</strong> (Java 16+) - Immutable data carriers for events and state</li>
 *   <li><strong>Sealed Classes</strong> (Java 17+) - Controlled type hierarchy for GameEvent</li>
 *   <li><strong>Pattern Matching</strong> (Java 21+) - Type-safe event handling in switch expressions</li>
 *   <li><strong>Default Methods</strong> - Intelligent defaults in GameEvent interface</li>
 * </ul>
 *
 * @see <a href="https://refactoring.guru/design-patterns/observer">Observer Pattern</a>
 * @see <a href="https://refactoring.guru/design-patterns/singleton">Singleton Pattern</a>
 *
 * @author Charles Bryan
 * @version Winter 2025
 */
package edu.uw.tcss.game.model;
