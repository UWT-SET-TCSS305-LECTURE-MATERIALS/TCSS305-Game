# Sealed Classes and Interfaces

## Java Version & JEP

**Introduced**: Java 15 (Preview), Java 16 (Second Preview), Java 17 (Finalized)
**JEP**: [JEP 409: Sealed Classes](https://openjdk.org/jeps/409)
**Status**: Finalized in Java 17 LTS

## What It Is

Sealed classes and interfaces are a language feature that restricts which other classes or interfaces can extend or implement them. By using the `sealed` modifier along with a `permits` clause, you explicitly declare the complete set of allowed subtypes. This creates a **closed type hierarchy** where the compiler knows every possible subtype at compile time.

Each permitted subtype must be declared as either:
- `final` (cannot be extended further)
- `sealed` (has its own restricted set of permitted subtypes)
- `non-sealed` (reopens the hierarchy, allowing unrestricted extension)

This is fundamentally different from traditional Java inheritance, where any class can extend a non-final class or implement an interface unless restricted by access modifiers.

## Why It Exists

### The Problem: Uncontrolled Type Hierarchies

In traditional Java, when you create an interface or class, you have limited control over what can implement or extend it:

```java
// Traditional interface - ANYONE can implement this
public interface Event {
    long timestamp();
}

// Somewhere else in the codebase (or even a library user)
public class UnexpectedEvent implements Event {  // Nothing stops this
    @Override
    public long timestamp() { return 0; }
}
```

This creates several problems:

1. **No Exhaustiveness Checking**: When you switch over an interface type, the compiler cannot verify you've handled all cases because new implementations could exist anywhere.

2. **Domain Modeling Limitations**: Many real-world domains have a fixed set of variants. For example, a traffic light has exactly three states: Red, Yellow, Green. Traditional Java couldn't express this constraint.

3. **Breaking Changes**: Adding a new event type to a hierarchy could break client code that assumes it knows all possible types.

4. **Defensive Programming**: Without knowing all possible subtypes, you must always include `default` cases in switches, even when you logically believe you've covered everything.

### Historical Context

Before Java 15, developers had several unsatisfying workarounds:

- **Enums**: Could represent fixed types but couldn't carry different data for each variant
- **Visitor Pattern**: Required significant boilerplate and was error-prone
- **Final Classes**: Prevented extension entirely but didn't help with related types
- **Package-Private**: Limited control but was circumvented by reflection or different packages

Languages like Scala, Kotlin, and Rust already had algebraic data types or sealed classes, demonstrating the value of controlled type hierarchies. Java's sealed classes bring this capability to the Java platform while maintaining backward compatibility.

### The Solution: Compiler-Enforced Type Hierarchies

Sealed classes let you declare exactly which types are permitted, enabling:

- **Exhaustive pattern matching** - The compiler knows all possible cases
- **Better domain modeling** - Code reflects business domains accurately
- **Safe evolution** - Changes to hierarchies are compiler-checked
- **Self-documenting code** - The permits clause shows all variants at a glance

## Syntax and Examples

### Basic Sealed Interface

```java
// Define a sealed interface with permitted implementations
public sealed interface Shape permits Circle, Rectangle, Triangle {
    double area();
}

// Each permitted type must be final, sealed, or non-sealed
public final class Circle implements Shape {
    private final double radius;

    public Circle(double radius) { this.radius = radius; }

    @Override
    public double area() { return Math.PI * radius * radius; }
}

public final class Rectangle implements Shape {
    private final double width, height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() { return width * height; }
}

public final class Triangle implements Shape {
    private final double base, height;

    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    public double area() { return 0.5 * base * height; }
}
```

### Sealed Abstract Classes

```java
// Sealed classes work too, not just interfaces
public sealed abstract class Result<T> permits Success, Failure {
    public abstract boolean isSuccess();
}

public final class Success<T> extends Result<T> {
    private final T value;

    public Success(T value) { this.value = value; }

    @Override
    public boolean isSuccess() { return true; }

    public T getValue() { return value; }
}

public final class Failure<T> extends Result<T> {
    private final String error;

    public Failure(String error) { this.error = error; }

    @Override
    public boolean isSuccess() { return false; }

    public String getError() { return error; }
}
```

### Nested Sealed Types with Records

This pattern is particularly powerful when combined with records:

```java
public sealed interface TrafficLight {
    // Nested record implementations - compact and clear
    record Red() implements TrafficLight {}
    record Yellow() implements TrafficLight {}
    record Green() implements TrafficLight {}
}

// Exhaustive switching - no default needed!
public String getAction(TrafficLight light) {
    return switch (light) {
        case TrafficLight.Red r -> "Stop";
        case TrafficLight.Yellow y -> "Caution";
        case TrafficLight.Green g -> "Go";
        // Compiler ensures all cases handled
    };
}
```

### Hierarchical Sealing

You can create multi-level sealed hierarchies:

```java
public sealed interface Expression permits Value, BinaryOp, UnaryOp {
}

public record Value(int n) implements Expression {
}

// This sealed subtype has its own permitted types
public sealed interface BinaryOp extends Expression
        permits Add, Multiply, Subtract, Divide {
    Expression left();
    Expression right();
}

public record Add(Expression left, Expression right) implements BinaryOp {}
public record Multiply(Expression left, Expression right) implements BinaryOp {}
public record Subtract(Expression left, Expression right) implements BinaryOp {}
public record Divide(Expression left, Expression right) implements BinaryOp {}

// This reopens the hierarchy - anyone can extend UnaryOp
public non-sealed interface UnaryOp extends Expression {
    Expression operand();
}
```

## In Our Codebase

Our primary example is the `GameEvent` sealed interface, which demonstrates the powerful combination of sealed types, records, and pattern matching for building a type-safe event system.

### The GameEvent Hierarchy

Location: `/src/edu/uw/tcss/game/model/GameEvent.java` (lines 43-128)

```java
public sealed interface GameEvent permits
        GameEvent.MoveEvent,
        GameEvent.InvalidMoveEvent,
        GameEvent.NewGameEvent {

    /**
     * Returns the timestamp when this event was created.
     */
    long timestamp();

    /**
     * Returns the property name for this event type.
     */
    default String getPropertyName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Event fired when a valid move is made in any direction.
     */
    record MoveEvent(
            GameControls.Move direction,
            GameControls.Point newLocation,
            GameControls.ValidMoves validMoves,
            long timestamp
    ) implements GameEvent {}

    /**
     * Event fired when an invalid move is attempted.
     */
    record InvalidMoveEvent(
            GameControls.Point currentLocation,
            GameControls.Move attemptedDirection,
            long timestamp
    ) implements GameEvent {}

    /**
     * Event fired when a new game is started.
     */
    record NewGameEvent(
            GameControls.Point startingLocation,
            GameControls.ValidMoves validMoves,
            long timestamp
    ) implements GameEvent {}
}
```

### Why This Design?

This sealed interface achieves several goals:

1. **Type Safety**: Only these three event types can ever implement `GameEvent`. No surprises.

2. **Nested Organization**: The event types are nested within the sealed interface, creating a clear namespace (`GameEvent.MoveEvent` is obviously related to `GameEvent`).

3. **Records as Implementations**: Each permitted type is a record, giving us immutability, automatic `equals()`/`hashCode()`/`toString()`, and minimal boilerplate.

4. **Exhaustive Switching**: When handling events, the compiler can verify you've covered all cases.

### Usage with Pattern Matching

From the JavaDoc example in `GameEvent.java` (lines 22-34):

```java
game.addPropertyChangeListener(evt -> {
    if (evt.getNewValue() instanceof GameEvent event) {
        switch (event) {
            case GameEvent.MoveEvent e ->
                handleMove(e.direction(), e.newLocation(), e.validMoves());
            case GameEvent.InvalidMoveEvent e ->
                handleInvalidMove(e.currentLocation(), e.attemptedDirection());
            case GameEvent.NewGameEvent e ->
                handleNewGame(e.startingLocation(), e.validMoves());
            // No default case needed - compiler knows these are all cases
        }
    }
});
```

Notice:
- **No casting**: Pattern matching extracts the variable with the right type
- **No default case**: The sealed interface guarantees these are the only three possibilities
- **Compile-time safety**: If you add a fourth event type to the `permits` clause, this code won't compile until you handle it

### What Happens If You Forget a Case?

If you wrote this:

```java
switch (event) {
    case GameEvent.MoveEvent e -> handleMove(e);
    case GameEvent.InvalidMoveEvent e -> handleInvalidMove(e);
    // Forgot NewGameEvent!
}
```

The compiler would produce an error:
```
error: the switch expression does not cover all possible input values
```

This is **exhaustiveness checking**—the compiler knows the sealed hierarchy is closed and can verify you've handled every permitted type.

### Comparison to Traditional Approach

**Without Sealed Classes (Old Java):**

```java
public interface GameEvent {  // Anyone can implement
    long timestamp();
}

// Separate classes (might be anywhere in the codebase)
public class MoveEvent implements GameEvent { /* ... */ }
public class InvalidMoveEvent implements GameEvent { /* ... */ }
public class NewGameEvent implements GameEvent { /* ... */ }

// Handling requires defensive programming
public void handleEvent(GameEvent event) {
    if (event instanceof MoveEvent e) {
        handleMove(e);
    } else if (event instanceof InvalidMoveEvent e) {
        handleInvalidMove(e);
    } else if (event instanceof NewGameEvent e) {
        handleNewGame(e);
    } else {
        // REQUIRED - might be some unknown implementation
        throw new IllegalArgumentException("Unknown event type: " + event);
    }
}
```

**With Sealed Classes (Modern Java):**

```java
public sealed interface GameEvent permits MoveEvent, InvalidMoveEvent, NewGameEvent {
    long timestamp();
}

// Compiler-checked exhaustive handling
public void handleEvent(GameEvent event) {
    switch (event) {
        case MoveEvent e -> handleMove(e);
        case InvalidMoveEvent e -> handleInvalidMove(e);
        case NewGameEvent e -> handleNewGame(e);
        // No default needed - compiler knows these are all cases
    }
}
```

The sealed version is clearer, safer, and prevents the runtime exception case entirely.

## Benefits

### 1. Exhaustive Pattern Matching

The compiler can verify you've handled all possible cases in switch expressions or statements. This catches errors at compile time instead of runtime.

```java
// Compiler enforces handling all cases
String describe(GameEvent event) {
    return switch (event) {
        case MoveEvent e -> "Moved " + e.direction();
        case InvalidMoveEvent e -> "Invalid move attempted";
        case NewGameEvent e -> "New game started";
        // Forget one? Compilation error!
    };
}
```

### 2. Better Domain Modeling

Sealed classes let you model domains that naturally have a fixed set of variants:

- **State machines**: A connection is `Connecting | Connected | Disconnected`
- **Result types**: An operation returns `Success | Failure`
- **Events**: A game produces `MoveEvent | InvalidMoveEvent | NewGameEvent`
- **AST nodes**: An expression is `Literal | BinaryOp | UnaryOp | Variable`

The code structure mirrors the business domain, making it self-documenting.

### 3. Safe Evolution

When you add a new permitted type to a sealed hierarchy, the compiler finds every place that switches over that type and forces you to handle the new case. This makes refactoring safer—you can't accidentally forget to handle the new variant somewhere.

### 4. Self-Documenting Code

The `permits` clause serves as documentation. Anyone reading the code immediately knows all possible subtypes without searching the entire codebase.

```java
// Crystal clear: these are the only three possible events
public sealed interface GameEvent permits
        GameEvent.MoveEvent,
        GameEvent.InvalidMoveEvent,
        GameEvent.NewGameEvent {
    // ...
}
```

### 5. IDE Support

Modern IDEs understand sealed classes and provide:
- Auto-completion for all permitted types
- Warnings when switch statements are not exhaustive
- Refactoring support when adding new permitted types

### 6. No Reflection Required

Unlike some workarounds (like package-private with reflection), sealed classes provide compile-time enforcement. The restrictions are part of the class file metadata and enforced by the JVM.

## Common Mistakes

### 1. Forgetting to Declare Permitted Types as final/sealed/non-sealed

**Wrong:**
```java
public sealed interface Event permits MoveEvent {}

// ERROR: Permitted subclass must be final, sealed, or non-sealed
public class MoveEvent implements Event {}
```

**Correct:**
```java
public sealed interface Event permits MoveEvent {}

public final class MoveEvent implements Event {}  // Now it's final
```

**Why**: Sealed classes ensure a closed hierarchy. If `MoveEvent` were not final/sealed/non-sealed, anyone could extend it, breaking the closure guarantee.

### 2. Permitted Types in Different Modules

**Wrong:**
```java
// In module A
public sealed interface Event permits MoveEvent {}

// In module B (different module)
public final class MoveEvent implements Event {}  // Compilation error!
```

**Correct:**
```java
// Same module (or same package if unnamed module)
public sealed interface Event permits MoveEvent {}
public final class MoveEvent implements Event {}
```

**Why**: Sealed hierarchies must be in the same module (or same package for unnamed modules) to ensure the compiler can verify completeness.

### 3. Missing Cases in Switch (But Compiler Catches This!)

**Wrong:**
```java
public String describe(GameEvent event) {
    return switch (event) {
        case MoveEvent e -> "Move";
        case InvalidMoveEvent e -> "Invalid";
        // Forgot NewGameEvent - won't compile!
    };
}
```

**Error Message:**
```
error: the switch expression does not cover all possible input values
```

**Correct:**
```java
public String describe(GameEvent event) {
    return switch (event) {
        case MoveEvent e -> "Move";
        case InvalidMoveEvent e -> "Invalid";
        case NewGameEvent e -> "New game";  // Now exhaustive
    };
}
```

**Good News**: The compiler catches this mistake for you! This is a feature, not a bug. The error guides you to write correct code.

### 4. Using default When You Mean to Be Exhaustive

**Problematic:**
```java
public String describe(GameEvent event) {
    return switch (event) {
        case MoveEvent e -> "Move";
        case InvalidMoveEvent e -> "Invalid";
        default -> "Other";  // Masks missing NewGameEvent case
    };
}
```

**Better:**
```java
public String describe(GameEvent event) {
    return switch (event) {
        case MoveEvent e -> "Move";
        case InvalidMoveEvent e -> "Invalid";
        case NewGameEvent e -> "New game";  // Explicit, compiler-checked
    };
}
```

**Why**: Adding `default` defeats the purpose of sealed classes. You lose exhaustiveness checking. Only add `default` if you genuinely want to group multiple cases or handle future variants uniformly.

**However**: Sometimes you legitimately don't need to handle all cases. Here's when `default` is appropriate:

### When You Legitimately Don't Need All Cases

Our codebase has a perfect example in `GameController.propertyChange()` (lines 241-250):

```java
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
```

**Why this approach is preferred:**

The `GameController` only cares about `MoveEvent` and `NewGameEvent` because both require updating which directional buttons are enabled. When an `InvalidMoveEvent` occurs, the buttons are already in the correct state—there's no action to take.

By using an **explicit empty case** instead of `default`, we get the best of both worlds:
- **Compiler verification**: If a new event type is added (like `GameOverEvent`), the compiler will force us to handle it
- **Self-documenting**: The explicit case with comment makes it crystal clear we know about `InvalidMoveEvent` and are intentionally ignoring it
- **Exhaustiveness checking**: We maintain the benefits of sealed classes—the compiler knows we've considered all cases

**Alternative approach using default:**

```java
switch (event) {
    case MoveEvent e -> enableValidDirections(e.validMoves());
    case NewGameEvent e -> enableValidDirections(e.validMoves());
    default -> { }  // InvalidMoveEvent needs no button state changes
}
```

**The tradeoff with default:**

- **Pro**: Slightly more concise when ignoring multiple cases
- **Con**: If a new event type is added (`GameOverEvent`), the compiler won't warn you—it'll silently hit the `default` case, potentially causing bugs

**Rule of thumb**: Prefer explicit empty cases over `default` when working with sealed types. This maintains exhaustiveness checking while making your intent clear. Only use `default` if you genuinely want to ignore multiple cases or future additions uniformly, and always document why.

### 5. Confusing sealed with final

**Wrong Thinking:**
```java
// "Sealed means no one can extend it, right?"
public sealed class Util permits /* nothing */ {}  // Wrong usage
```

**Correct Understanding:**
- **`final`**: This specific class cannot be extended by anyone
- **`sealed`**: This class/interface can only be extended/implemented by the permitted types

**Use `final` when**: You want to prevent extension entirely (e.g., utility classes, security-sensitive classes)

**Use `sealed` when**: You want to allow a specific set of subtypes (e.g., event hierarchies, state enums with data)

### 6. Overusing Sealed Classes

**Not everything should be sealed:**

```java
// Probably don't seal this - it's meant to be extended by users
public sealed interface Plugin permits ??? {}  // Extension is the point!

// Better:
public interface Plugin {}  // Let users implement this
```

**When NOT to use sealed classes:**
- Extensibility is a feature (plugin systems, strategy interfaces)
- You expect third-party implementations
- The domain is inherently open-ended

**When TO use sealed classes:**
- Domain has a fixed set of variants (events, states, results)
- You need exhaustiveness checking
- You control all implementations

## Related Features

### 1. Records (Java 16)

Sealed interfaces combine beautifully with records to create concise, type-safe variant types:

```java
public sealed interface Result permits Success, Failure {}
public record Success(String data) implements Result {}
public record Failure(String error) implements Result {}
```

See [Records.md](./Records.md) for details on how records eliminate boilerplate.

### 2. Pattern Matching for instanceof (Java 16)

Pattern matching eliminates casting when checking types:

```java
// Old way
if (obj instanceof MoveEvent) {
    MoveEvent e = (MoveEvent) obj;  // Cast required
    handle(e);
}

// Pattern matching
if (obj instanceof MoveEvent e) {  // Type pattern
    handle(e);  // 'e' is already MoveEvent
}
```

### 3. Pattern Matching for Switch (Java 21)

Switch expressions with pattern matching enable exhaustive, type-safe control flow over sealed types:

```java
String result = switch (event) {
    case MoveEvent e -> "Moved to " + e.newLocation();
    case InvalidMoveEvent e -> "Cannot move " + e.attemptedDirection();
    case NewGameEvent e -> "Game started at " + e.startingLocation();
};
```

See [PatternMatching.md](./PatternMatching.md) for comprehensive coverage of pattern matching features.

### 4. Enums (Java 5)

Enums are essentially sealed classes with restricted capabilities:

```java
// Enum: fixed instances, no additional data per instance (without complexity)
public enum Direction { NORTH, SOUTH, EAST, WEST }

// Sealed + Records: fixed types, rich data per variant
public sealed interface Direction permits North, South, East, West {}
public record North() implements Direction {}
public record South() implements Direction {}
public record East() implements Direction {}
public record West() implements Direction {}
```

**Use enums when**: You need simple constants with no varying data
**Use sealed types when**: Each variant carries different data or behavior

### 5. Abstract Classes and Interfaces

Sealed types extend traditional inheritance with restrictions:

| Feature | Anyone can extend? | Variants carry data? | Exhaustiveness checking? |
|---------|-------------------|---------------------|------------------------|
| Abstract class | Yes | Yes | No |
| Interface | Yes | No (before default methods) | No |
| Enum | No (fixed instances) | Limited | Yes (in switch) |
| Sealed class/interface | Only permitted types | Yes | Yes (with pattern matching) |

## Further Reading

### Official Documentation

- [JEP 409: Sealed Classes](https://openjdk.org/jeps/409) - The official proposal with rationale and examples
- [JEP 360: Sealed Classes (Preview)](https://openjdk.org/jeps/360) - Original preview version
- [JEP 397: Sealed Classes (Second Preview)](https://openjdk.org/jeps/397) - Refinements before finalization
- [Oracle Java Tutorial: Sealed Classes](https://dev.java/learn/sealed-classes-and-interfaces/) - Official tutorial

### Related JEPs

- [JEP 395: Records](https://openjdk.org/jeps/395) - Often used with sealed classes
- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441) - Enables exhaustive switching
- [JEP 394: Pattern Matching for instanceof](https://openjdk.org/jeps/394) - Type patterns used with sealed types

### Articles and Tutorials

- [Inside Java: Sealed Classes](https://inside.java/2021/04/20/sealed-classes-and-interfaces/) - Deep dive from the Java team
- [Baeldung: Sealed Classes and Interfaces](https://www.baeldung.com/java-sealed-classes-interfaces) - Practical examples
- [Modern Java in Action](https://www.manning.com/books/modern-java-in-action) - Book covering Java 8-21 features

### Design Patterns with Sealed Classes

- [Effective Java, 3rd Edition](https://www.oreilly.com/library/view/effective-java/9780134686097/) - Josh Bloch's guidance (pre-sealed classes, but principles apply)
- [Java Design Patterns](https://java-design-patterns.com/) - Community patterns, increasingly using modern features

### Migration Guides

- [Migrating to Sealed Classes](https://www.oracle.com/java/technologies/javase/17-relnote-issues.html#JDK-8260514) - From traditional hierarchies
- [Java 17 Migration Guide](https://docs.oracle.com/en/java/javase/17/migrate/getting-started.html) - Covers sealed classes and other Java 17 features

---

**Next**: [Records](./Records.md) - Learn how records eliminate boilerplate for immutable data classes

**Previous**: [Modern Java Features Guide](./README.md) - Overview of all modern features in this codebase

**Related**: [Pattern Matching and Switch Expressions](./PatternMatching.md) - How pattern matching works with sealed types

---

**Version**: Autumn 2025
**Java Version**: 17+ (LTS)