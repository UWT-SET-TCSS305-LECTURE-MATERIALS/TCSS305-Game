# TCSS305 Game - Lecture Code Sample

A simple grid-based game demonstrating modern Java design patterns and GUI programming techniques. This project serves as a teaching example for object-oriented design, event-driven programming, and best practices in software architecture.

## Overview

This application simulates a game piece moving on a grid-based board. The piece can move in four directions (up, down, left, right), with movement constraints based on board boundaries. The project emphasizes clean separation of concerns using the MVC (Model-View-Controller) pattern and modern Java features.

## Learning Objectives

By studying this codebase, you will see practical examples of:

- **Design Patterns**: Singleton, Observer (via PropertyChangeListener), MVC architecture
- **Modern Java Features**: Sealed classes, Records, Pattern matching, Switch expressions
- **GUI Programming**: Swing components, custom painting, event handling
- **Event-Driven Architecture**: PropertyChangeListener pattern with type-safe events
- **Code Organization**: Package structure, separation of concerns, utility classes

## Key Design Patterns & Concepts

### 1. Singleton Pattern
The `Game` class implements the Singleton pattern using lazy initialization with double-checked locking, ensuring only one game instance exists throughout the application.

```java
Game game = Game.getInstance();
```

### 2. Observer Pattern (PropertyChangeListener)
The game model notifies observers (GUI components) of state changes using Java's PropertyChangeListener framework, enhanced with type-safe sealed event types.

### 3. Sealed Classes & Records (Java 21)
The `GameEvent` sealed interface defines a closed hierarchy of event types, enabling exhaustive pattern matching:

```java
game.addPropertyChangeListener(evt -> {
    if (evt.getNewValue() instanceof GameEvent event) {
        switch (event) {
            case GameEvent.MoveEvent e -> handleMove(e);
            case GameEvent.InvalidMoveEvent e -> handleInvalidMove(e);
            case GameEvent.NewGameEvent e -> handleNewGame(e);
            // Compiler ensures all cases are covered
        }
    }
});
```

### 4. MVC Architecture
- **Model**: `Game` class manages game state and business logic
- **View**: `GameBoardPanel` renders the game board and piece
- **Controller**: `GameController` handles user input and coordinates model/view

### 5. Immutability
Extensive use of `final` fields, immutable records (`Point`, `ValidMoves`, event types), and `Map.of()` for thread-safe, predictable behavior.

### 6. Performance Optimization
The view pre-calculates rendering primitives (grid lines, game piece rectangle) to eliminate redundant calculations during paint operations.

## Project Structure

```
src/edu/uw/tcss/game/
├── model/
│   ├── Game.java                              # Singleton game model
│   ├── GameControls.java                      # Game behavior interface
│   ├── PropertyChangeEnabledGameControls.java # PCL-enabled interface
│   └── GameEvent.java                         # Sealed event hierarchy
├── gui/
│   ├── contoller/
│   │   └── GameController.java                # MVC controller, main entry point
│   ├── view/
│   │   └── GameBoardPanel.java                # Game board visualization
│   └── util/
│       └── GameIcons.java                     # Icon generation utility
```

## How to Compile and Run

### Compilation
From the project root directory:

```bash
javac -d out -sourcepath src src/edu/uw/tcss/game/gui/contoller/GameController.java
```

### Running
```bash
java -cp out edu.uw.tcss.game.gui.contoller.GameController
```

### Requirements
- Java 21 or higher (uses sealed classes, records, pattern matching)

## How to Play

### Controls
- **Arrow Keys / WASD**: Move the game piece
  - W / ↑: Move up
  - S / ↓: Move down
  - A / ←: Move left
  - D / →: Move right
- **N Key**: Start a new game
- **GUI Buttons**: Click directional arrow buttons or the play button (new game)

### Game Rules
- The piece starts at position (0, 0)
- Movement is constrained by board boundaries (8 columns × 10 rows)
- Invalid moves (attempting to move out of bounds) are indicated by turning the piece red
- Valid moves update the piece position and turn it black

## Code Highlights to Explore

### Type-Safe Events with Sealed Classes
See `GameEvent.java` for how sealed interfaces enable compile-time exhaustiveness checking in switch expressions.

### Double-Checked Locking Singleton
See `Game.getInstance()` for thread-safe lazy initialization pattern.

### Functional Programming with Method References
See `Game.java` constructor for Map-based strategy pattern using method references:
```java
myMoveValidators = Map.of(
    Move.UP, this::isUpValid,
    Move.DOWN, this::isDownValid,
    // ...
);
```

### Custom Rendering Optimization
See `GameBoardPanel.java` for pre-calculated `Path2D` grid and `Rectangle2D` game piece to eliminate per-frame math.

### Factory Pattern for Icons
See `GameIcons.java` for programmatic icon generation using `BufferedImage` and `Graphics2D`.

## Design Decisions

### Why Singleton for Game?
In this application, there should only ever be one active game. The Singleton pattern enforces this constraint at compile time and provides a global access point.

### Why Sealed Events Instead of String-Based Properties?
Sealed event types provide:
- Compile-time type safety
- Exhaustiveness checking in switch expressions
- Self-documenting event structure
- IDE autocomplete support
- Refactoring safety

### Why Pre-calculate Rendering Primitives?
By calculating grid lines and piece rectangles once (or only when position changes), we eliminate redundant calculations in `paintComponent()`, which may be called frequently during window operations.

### Why Records?
Records provide immutable data carriers with minimal boilerplate, perfect for events, points, and value objects.

## Checkstyle Configuration

This project includes custom checkstyle rules (`tcss305_checkstyle_sameline.xml`) enforcing:
- Naming conventions (Hungarian notation: `my` prefix for fields, `the` prefix for parameters)
- Line length limits (95 characters)
- Method length limits (25 statements)
- Documentation requirements
- Code style consistency

## Common Student Questions

**Q: Why use PropertyChangeListener instead of custom listeners?**
A: PCL is a standard Java pattern that integrates well with Swing and provides a consistent event notification mechanism. The sealed event types add modern type safety on top of this proven pattern.

**Q: Can I modify this code for my assignment?**
A: This is lecture code. Check with your instructor about assignment policies. Understanding the patterns here will help you implement similar designs in your own work.

**Q: Why is the constructor private in Game?**
A: Private constructors enforce the Singleton pattern, preventing direct instantiation via `new Game()`. Use `Game.getInstance()` instead.

**Q: What's the difference between `Game` and `GameControls`?**
A: `GameControls` is the interface defining game behaviors, `Game` is the concrete implementation. This separation allows for testing, mocking, and potential alternative implementations.

## Further Reading

- [JEP 409: Sealed Classes](https://openjdk.org/jeps/409)
- [JEP 395: Records](https://openjdk.org/jeps/395)
- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)
- [PropertyChangeListener Tutorial](https://docs.oracle.com/javase/tutorial/javabeans/writing/properties.html)
- [Effective Java by Joshua Bloch](https://www.pearson.com/store/p/effective-java/P100000149326) - Especially items on Singletons and immutability

## Author

Charles Bryan
University of Washington Tacoma
TCSS 305 - Programming Practicum

---

**Version**: Autumn 2025
