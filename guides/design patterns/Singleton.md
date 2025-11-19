# Singleton Pattern

## Brief History

The Singleton pattern was first catalogued by the "Gang of Four" (Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides) in their seminal 1994 book *Design Patterns: Elements of Reusable Object-Oriented Software*. Among the creational patterns documented in that work, Singleton has become one of the most widely used—and most controversial—patterns in software development.

The pattern's core intent is simple: ensure a class has only one instance and provide a global point of access to it. However, the implementation has evolved significantly since 1994, particularly in Java, where thread safety and lazy initialization have driven several variations:

**Classic Singleton** (1994): The original formulation used a static instance initialized at class loading time, or a simple lazy initialization approach without thread safety. This worked in single-threaded environments but failed in multi-threaded Java applications.

**Synchronized getInstance()** (Late 1990s): As Java developers encountered race conditions with lazy initialization, the naive solution was to synchronize the entire `getInstance()` method. This guaranteed thread safety but caused significant performance bottlenecks—every access to the singleton required acquiring a lock, even after the instance was created.

**Double-Checked Locking** (Early 2000s): This optimization checks if the instance exists before acquiring a lock, then checks again inside the synchronized block. Before Java 5, this pattern was broken due to the Java Memory Model's handling of initialization. **Java 5 (2004) introduced the `volatile` keyword's proper semantics**, making double-checked locking reliable when combined with `volatile`. This is the approach our codebase uses.

**Initialization-on-Demand Holder** (Mid 2000s): This elegant solution leverages Java's class loading guarantees. A static inner class holds the singleton instance, which is initialized only when the inner class is loaded (when `getInstance()` is first called). This provides lazy initialization and thread safety without explicit synchronization. It's now considered the preferred approach for most use cases.

**Enum Singleton** (Java 5+): Joshua Bloch's *Effective Java* popularized using a single-element enum to implement Singleton. This provides serialization safety and protection against reflection attacks "for free." However, it doesn't support lazy initialization and can't extend another class.

## The Problem It Solves

Imagine you're running a country's government. The constitution specifies that there can be only one President at a time. Citizens need to know who the President is, laws need to be signed by the President, and international treaties are negotiated by the President. It would be chaotic—and illegal—if different parts of the government each thought they were working with a different President.

The naive approach would let anyone create a "President" whenever needed. But this leads to inconsistency: different government departments might operate with different President instances, each maintaining separate state. Budget decisions made by one instance wouldn't be visible to another. The country would descend into chaos.

You need a way to guarantee that everyone in the system is working with the same President instance. There should be exactly one President, and everyone should have a reliable way to access that instance. The President role itself should control its own uniqueness—the responsibility for ensuring "only one" shouldn't be scattered across the codebase.

## How the Pattern Solves It

The Singleton pattern enforces uniqueness through three key mechanisms:

**Private Constructor**: The class's constructor is marked `private`, preventing external code from creating instances with `new`. This is crucial—if clients could create instances freely, you couldn't guarantee there's only one. By making the constructor private, the class takes control of its own instantiation.

**Static Instance Variable**: The class holds a reference to its single instance in a `static` field. This field is typically `private` to prevent direct manipulation, though it's owned by the class itself, not individual instances.

**Public Static Access Method**: A public static method (conventionally named `getInstance()`) provides global access to the singleton instance. This method contains the logic for creating the instance (if it doesn't exist) or returning the existing instance. It's the sole entry point for obtaining the singleton.

The key insight: **the class itself controls its instantiation**. Rather than relying on client discipline ("please only create one instance"), the Singleton pattern makes it architecturally impossible to create multiple instances. The class guarantees its own uniqueness.

## Technical Implementation in Our Codebase

This project demonstrates the Singleton pattern using the double-checked locking approach with a `volatile` instance variable—a thread-safe lazy initialization technique appropriate for Java 5+.

### The Singleton: Game Class

The `Game` class implements Singleton to ensure only one game instance exists throughout the application. See the full [Game.java](../../src/edu/uw/tcss/game/model/Game.java) implementation.

**The Singleton Instance Variable**

The singleton instance is stored in a static, volatile field (line 45):

```java
/** The singleton instance of Game. */
private static volatile Game instance;
```

The `volatile` keyword is critical here. It ensures that writes to the `instance` variable are immediately visible to all threads, preventing subtle race conditions in the double-checked locking pattern. Without `volatile`, the Java Memory Model allows threads to see a partially constructed object—a reference to the instance before its constructor completes. This would cause mysterious bugs where parts of the object appear uninitialized.

**Private Constructor**

The constructor is private, preventing external instantiation (lines 63-77):

```java
/**
 * Creates a game with a piece in the starting location 0, 0.
 * Private constructor to enforce singleton pattern.
 */
private Game() {
    super();
    myLocation = STARTING_LOCATION;
    myPcs = new PropertyChangeSupport(this);
    myMoveValidators = Map.of(
            Move.UP, this::isUpValid,
            Move.DOWN, this::isDownValid,
            Move.LEFT, this::isLeftValid,
            Move.RIGHT, this::isRightValid);
    myMovements = Map.of(
            Move.UP, () -> myLocation.transform(0, -1),
            Move.DOWN, () -> myLocation.transform(0, 1),
            Move.LEFT, () -> myLocation.transform(-1, 0),
            Move.RIGHT, () -> myLocation.transform(1, 0));
}
```

By making the constructor private, we prevent code like `Game game = new Game();` from compiling. The only way to obtain a `Game` instance is through the controlled `getInstance()` method.

**Thread-Safe getInstance() with Double-Checked Locking**

The `getInstance()` method provides the global access point and handles lazy initialization with thread safety (lines 85-94):

```java
/**
 * Returns the singleton instance of Game.
 * Uses double-checked locking for thread-safe lazy initialization.
 *
 * @return the singleton Game instance
 */
public static Game getInstance() {
    if (instance == null) {
        synchronized (Game.class) {
            if (instance == null) {
                instance = new Game();
            }
        }
    }
    return instance;
}
```

This implementation uses double-checked locking, an optimization that reduces synchronization overhead:

1. **First check (line 86)**: If the instance already exists, return it immediately without acquiring any lock. This is the common case after initialization—most calls to `getInstance()` take this fast path.

2. **Synchronized block (line 87)**: If the instance doesn't exist, acquire a lock on the `Game.class` object. Only one thread can execute inside this block at a time.

3. **Second check (line 88)**: Check again if the instance is null. Why? Another thread might have initialized the instance while we were waiting to acquire the lock. Without this second check, multiple threads could create separate instances.

4. **Lazy initialization (line 89)**: If the instance is still null after acquiring the lock and rechecking, create it.

The combination of `volatile` and synchronized ensures:
- Only one instance is ever created, even with concurrent access
- After initialization, no synchronization overhead occurs (the fast path)
- All threads see a fully constructed instance (no partial initialization)

**Usage in the Application**

The application accesses the singleton through `getInstance()`. See [GameController.java](../../src/edu/uw/tcss/game/gui/contoller/GameController.java) (lines 179-205):

```java
public static void createAndShowGUI() {
    final Game game = Game.getInstance();
    final GameController pane = new GameController(game);

    // Register observers
    game.addPropertyChangeListener(pane);

    final GameBoardPanel gamePanel = new GameBoardPanel();
    game.addPropertyChangeListener(gamePanel);
    // ...
}
```

No matter how many times `getInstance()` is called throughout the application—from the controller, from the view, from tests—it always returns the same `Game` instance. The GUI, controller, and model all operate on shared state, ensuring consistency.

### Why Game is a Singleton

In this application, the `Game` class represents the core game state: the piece's location and the rules for valid moves. Having multiple `Game` instances would create inconsistencies—the GUI might display one game state while the controller manages a different one. By enforcing the Singleton pattern, we guarantee that all components work with the same authoritative game state.

This is a classic use case for Singleton: a central coordinator or shared resource that must be unique within the application. Other examples include database connection pools, logging systems, and configuration managers.

## Benefits & Tradeoffs

**Benefits:**
- **Controlled access to sole instance**: The class itself manages its uniqueness—you can't accidentally create duplicate instances
- **Reduced namespace pollution**: No global variables cluttering the namespace; the singleton is accessed through a clear, documented method
- **Permits refinement**: Subclassing is possible (though complex) if needed, unlike a pure static class
- **Lazy initialization**: The instance is created only when first needed, saving resources if it's never used (though this benefit varies by implementation)
- **Can control number of instances**: The pattern can be adapted to allow a limited number of instances (called the Multiton pattern), not just one

**Tradeoffs:**
- **Makes unit testing difficult**: Global state persists across tests, causing test interdependencies. Resetting the singleton between tests is cumbersome, and mocking the singleton for isolated testing is complex
- **Violates Single Responsibility Principle**: The class has two responsibilities—its domain logic (game management) and controlling its own instantiation
- **Can hide dependencies**: Code that calls `Game.getInstance()` has a hidden dependency on the Game class. This isn't visible in constructor parameters, making the codebase harder to understand
- **Thread safety complexity**: Correct multi-threaded singleton implementation is subtle (as shown by the evolution from broken to working double-checked locking)
- **Serialization issues**: If a singleton implements `Serializable`, deserialization creates a new instance unless you implement `readResolve()` to return the existing instance

## Common Pitfalls

**Forgetting the volatile keyword**: Before Java 5, double-checked locking was fundamentally broken. Even after Java 5, it only works correctly with `volatile`. Without `volatile`, threads can see a reference to a partially constructed object. The instance appears non-null, but its fields aren't fully initialized yet—leading to bizarre, intermittent bugs that are nearly impossible to debug.

**Not considering thread safety**: The simplest singleton implementation (`if (instance == null) instance = new Singleton();`) has a race condition. Two threads can simultaneously see `instance == null` and both create an instance. Always use one of the thread-safe patterns: double-checked locking with `volatile`, initialization-on-demand holder idiom, or eager initialization.

**Overusing the pattern**: Singleton is often used when dependency injection would be more appropriate. Ask: does this *need* to be globally accessible and unique, or am I just avoiding the work of passing dependencies through constructors? Singletons create global state, making your code harder to test and reason about. Use them sparingly.

**Making everything static**: Developers sometimes try to create a singleton-like class by making all methods and fields static. This prevents inheritance, makes testing even harder (you can't mock a static class), and doesn't actually enforce the "one instance" constraint—it just avoids instances altogether. A true Singleton has instance methods and state.

**Cloning and reflection attacks**: Even with a private constructor, a singleton can be violated. Implementing `Cloneable` without proper safeguards allows `clone()` to create duplicate instances. Reflection can invoke the private constructor directly. To prevent reflection attacks, throw an exception from the constructor if an instance already exists. To prevent cloning, override `clone()` to throw `CloneNotSupportedException`.

**Serialization creating multiple instances**: If a singleton implements `Serializable`, deserializing a saved instance creates a new object—violating uniqueness. Fix this by implementing `readResolve()`:

```java
private Object readResolve() {
    return getInstance();
}
```

This ensures deserialization returns the singleton instance rather than creating a new one.

## Related Patterns

**Observer Pattern**: Our `Game` class combines Singleton with Observer. Game is a singleton to ensure unique game state, and it uses the Observer pattern (via `PropertyChangeListener`) to notify components when that state changes. See [Observer.md](./Observer.md). This combination is common—centralized state (Singleton) that notifies dependents when it changes (Observer).

**Strategy Pattern**: The `Game` singleton uses the Strategy pattern for move validation and execution. Maps of `BooleanSupplier` (validators) and `Supplier<Point>` (movements) encapsulate different movement algorithms. See [Strategy.md](./Strategy.md). Singleton controls *which instance* exists; Strategy controls *which algorithm* that instance uses.

**Model-View-Controller (MVC)**: The `Game` singleton serves as the Model in this application's MVC architecture. Having a single Model instance simplifies the architecture—all Views observe the same state. See [MVC.md](./MVC.md). In MVC, the Model is often a natural candidate for Singleton if the application manages a single domain concept.

**Factory Method**: Singleton is essentially a specialized Factory Method that always returns the same instance. The `getInstance()` method is a factory method that happens to cache its product. Understanding Factory Method helps clarify Singleton's role as an instance creation pattern.

**State Pattern**: If a singleton's behavior varies based on its state, combining Singleton with State pattern is natural. The singleton instance delegates behavior to state objects. This keeps the singleton unique while allowing its behavior to change dynamically.

## Further Reading

- **Design Patterns: Elements of Reusable Object-Oriented Software** by Gamma, Helm, Johnson, Vlissides (1994) - Chapter 3, pages 127-134
- **Effective Java** by Joshua Bloch (3rd Edition, 2018) - Item 3: "Enforce the singleton property with a private constructor or an enum type" provides modern best practices
- **Java Concurrency in Practice** by Brian Goetz et al. (2006) - Section 16.2.4 explains the Java Memory Model issues that made double-checked locking broken before Java 5
- [JSR 133: Java Memory Model and Thread Specification](https://jcp.org/en/jsr/detail?id=133) - The specification that fixed `volatile` in Java 5, enabling safe double-checked locking
- **Head First Design Patterns** by Freeman & Freeman (2004) - Chapter 5 covers Singleton with accessible examples
- [The "Double-Checked Locking is Broken" Declaration](https://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html) by Doug Lea and others - Historical document explaining why double-checked locking failed before Java 5
- **A Pattern Language** by Christopher Alexander (1977) - The architectural work that inspired software design patterns
