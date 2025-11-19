# Records

## Java Version & JEP

**Introduced**: Java 14 (Preview), Java 15 (Second Preview)
**Finalized**: Java 16 (March 2021)
**JEP**: [JEP 395 - Records](https://openjdk.org/jeps/395)

Records are a special kind of class designed to act as transparent carriers for immutable data. They dramatically reduce boilerplate code when creating simple data classes, automatically generating constructors, accessors, `equals()`, `hashCode()`, and `toString()` methods.

## What It Is

A **record** is a compact syntax for declaring classes whose primary purpose is to hold immutable data. When you declare a record, the Java compiler automatically provides:

1. **A canonical constructor** - Initializes all fields from parameters
2. **Accessor methods** - Public getters matching field names (no `get` prefix)
3. **`equals()` method** - Value-based equality checking all fields
4. **`hashCode()` method** - Consistent hash based on all fields
5. **`toString()` method** - Readable string representation including all field values
6. **Implicitly final fields** - All components are `private final`
7. **Implicitly final class** - Records cannot be extended

Despite this automatic generation, records can still have custom methods, static fields, static methods, and even custom constructors (compact or canonical).

## Why It Exists

### The Boilerplate Problem

Before records, creating simple data classes required writing massive amounts of repetitive code:

**Traditional Java Class (Pre-Java 16):**
```java
public final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point[x=" + x + ", y=" + y + "]";
    }
}
```

That's **29 lines of code** for a simple data class with two integer fields. Most of this is mechanical boilerplate that's error-prone to write and maintain. If you add a third field, you must remember to update the constructor, `equals()`, `hashCode()`, and `toString()`.

### Historical Context

Java's reputation for verbosity became a significant pain point in the 2010s:
- **Kotlin** (2011) introduced data classes with automatic generation
- **Scala** (2004) had case classes for pattern matching
- **C#** (2002) evolved to include record types in C# 9.0 (2020)
- **Python, JavaScript, and other dynamic languages** required far less ceremony

Developers were choosing other JVM languages (especially Kotlin) or non-JVM languages partly because Java required too much boilerplate for simple tasks. Records addressed this concern while maintaining Java's core principles: strong typing, immutability, and explicit semantics.

### The Java Philosophy on Records

From JEP 395:
> "Records are transparent carriers for immutable data. A record acquires many standard members automatically, and it's impossible to add members that would violate the semantic restrictions."

This philosophy ensures:
- **Transparency**: All fields are part of the state, visible through accessors
- **Immutability**: Fields are final, no setters provided
- **Semantic clarity**: A record is clearly "just data"—not a complex object with behavior

## Syntax and Examples

### Basic Record Declaration

```java
// Compact declaration - just list the components
record Point(int x, int y) { }
```

This single line generates:
- Constructor: `Point(int x, int y)`
- Accessors: `int x()`, `int y()`
- `equals()`, `hashCode()`, `toString()`

### Using Records

```java
Point p1 = new Point(10, 20);
System.out.println(p1.x());        // 10 (accessor, not getter)
System.out.println(p1.y());        // 20

Point p2 = new Point(10, 20);
System.out.println(p1.equals(p2)); // true (value equality)
System.out.println(p1);            // Point[x=10, y=20]
```

### Records with Methods

Records can have instance methods, static methods, and static fields:

```java
record Point(int x, int y) {
    // Instance method
    Point transform(int deltaX, int deltaY) {
        return new Point(x + deltaX, y + deltaY);
    }

    // Static method
    static Point origin() {
        return new Point(0, 0);
    }
}
```

### Compact Constructor

Records support a **compact constructor** for validation without repeating parameter assignments:

```java
record Range(int min, int max) {
    // Compact constructor - parameters are implicit
    Range {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max");
        }
        // No need to write: this.min = min; this.max = max;
        // Assignment happens automatically after this block
    }
}
```

### Generic Records

Records can be generic:

```java
record Pair<T, U>(T first, U second) { }

Pair<String, Integer> pair = new Pair<>("age", 25);
```

## In Our Codebase

Our codebase uses records extensively for immutable data carriers and event payloads.

### 1. Point Record (GameControls.java, lines 88-92)

The simplest example, with a custom transformation method:

```java
/**
 * Data class that represents a "point" on the game board. Positive x
 * moves right across "columns". Positive y moves down "rows".
 *
 * @param x a singular x coordinate on the game board.
 * @param y a singular y coordinate on the game board.
 */
record Point(int x, int y) {
    Point transform(final int theX, final int theY) {
        return new Point(x + theX, y + theY);
    }
}
```

**What it replaces**: A traditional Point class would require ~30 lines of boilerplate. The record reduces this to 5 lines (including the custom method).

**Usage pattern**: The `transform()` method demonstrates that records can have behavior—they're not just passive data structures. Since records are immutable, transformation returns a new `Point` rather than modifying the existing one.

**See**: [GameControls.java](../../src/edu/uw/tcss/game/model/GameControls.java) (lines 88-92)

### 2. ValidMoves Record (GameControls.java, line 79)

A wrapper around a `Map` to provide type safety:

```java
/**
 * Data class that represents the which moves are valid in the current state of
 * the Game.
 *
 * @param moves the Map which represents the which moves are valid
 */
record ValidMoves(Map<Move, Boolean> moves) { }
```

**Design decision**: Rather than exposing a raw `Map<Move, Boolean>` throughout the codebase, wrapping it in a record provides:
- Type safety (compile-time checking)
- Semantic clarity (`ValidMoves` communicates intent better than `Map`)
- Future flexibility (we could add methods like `boolean canMove(Move m)`)

**See**: [GameControls.java](../../src/edu/uw/tcss/game/model/GameControls.java) (line 79)

### 3. Event Records (GameEvent.java, lines 91-127)

Records are perfect for event payloads in the Observer pattern:

```java
/**
 * Event fired when a valid move is made in any direction.
 * This event consolidates the movement and the updated valid directions
 * into a single atomic event, eliminating the need for multiple events per action.
 *
 * @param direction the direction of the successful move
 * @param newLocation the new location of the game piece after the move
 * @param validMoves the updated set of valid moves from the new location
 * @param timestamp when the event was created (milliseconds since epoch)
 */
record MoveEvent(
        GameControls.Move direction,
        GameControls.Point newLocation,
        GameControls.ValidMoves validMoves,
        long timestamp
) implements GameEvent { }

/**
 * Event fired when an invalid move is attempted.
 * The piece remains at its current location.
 *
 * @param currentLocation the current location where the piece remains
 * @param attemptedDirection the direction that was attempted but invalid
 * @param timestamp when the event was created (milliseconds since epoch)
 */
record InvalidMoveEvent(
        GameControls.Point currentLocation,
        GameControls.Move attemptedDirection,
        long timestamp
) implements GameEvent { }

/**
 * Event fired when a new game is started.
 * The piece is reset to the starting location (typically 0, 0).
 *
 * @param startingLocation the starting location of the piece (typically 0, 0)
 * @param validMoves the initial set of valid moves from the starting position
 * @param timestamp when the event was created (milliseconds since epoch)
 */
record NewGameEvent(
        GameControls.Point startingLocation,
        GameControls.ValidMoves validMoves,
        long timestamp
) implements GameEvent { }
```

**Why records for events:**
- **Immutability**: Events should never change after creation
- **Value semantics**: Two events with the same data should be equal
- **Minimal boilerplate**: Each event is just 4-6 lines, not 30-50
- **Type safety**: Each event type is distinct, enabling pattern matching

**Before records**: Each event would require a separate class file with constructors, getters, `equals()`, `hashCode()`, and `toString()`. That's potentially 150+ lines of boilerplate across three event types.

**With records**: Three concise declarations totaling about 40 lines (including documentation).

**See**: [GameEvent.java](../../src/edu/uw/tcss/game/model/GameEvent.java) (lines 91-127)

### 4. Records Implementing Interfaces

Notice that our event records implement the `GameEvent` sealed interface:

```java
public sealed interface GameEvent permits
        GameEvent.MoveEvent,
        GameEvent.InvalidMoveEvent,
        GameEvent.NewGameEvent {

    long timestamp();
    // ...
}
```

Records can implement interfaces, so each event record automatically satisfies the contract by having a `timestamp()` accessor method.

**Key insight**: Sealed interfaces + records = type-safe variant types (also called discriminated unions or algebraic data types). This combination enables exhaustive pattern matching in switch expressions.

## Benefits

### 1. Dramatic Reduction in Boilerplate

Records eliminate hundreds of lines of repetitive code:

| Feature | Traditional Class | Record |
|---------|------------------|--------|
| Constructor | Manual | Automatic |
| Getters | Manual (verbose) | Automatic (concise) |
| `equals()` | 5-10 lines | Automatic |
| `hashCode()` | 3-5 lines | Automatic |
| `toString()` | 3-5 lines | Automatic |
| **Total for 2 fields** | ~30 lines | 1 line |

### 2. Guaranteed Immutability

Records enforce immutability at the language level:
- All fields are implicitly `private final`
- No setters can be added
- The record class itself is implicitly `final`

This makes records ideal for:
- **Value objects** (Point, Money, Color, etc.)
- **DTOs** (Data Transfer Objects)
- **Event payloads** (as in our codebase)
- **Configuration objects**

### 3. Clear Intent

When you see a record, you immediately know:
- It's immutable
- It's primarily for data (not complex behavior)
- Equality is value-based
- It's safe to use as a map key or in collections

This semantic clarity improves code readability and maintainability.

### 4. Less Error-Prone

Hand-written `equals()` and `hashCode()` are notorious sources of bugs:
- Forgetting to update them when adding fields
- Breaking the equals/hashCode contract
- Typos in field comparisons

Records eliminate these errors by generating correct implementations automatically.

### 5. Perfect for Pattern Matching

Records integrate seamlessly with pattern matching (Java 16+):

```java
if (evt.getNewValue() instanceof GameEvent event) {
    switch (event) {
        case MoveEvent(var dir, var loc, var moves, var time) ->
            System.out.println("Moved " + dir + " to " + loc);
        case InvalidMoveEvent(var loc, var attemptedDir, var time) ->
            System.out.println("Cannot move " + attemptedDir + " from " + loc);
        case NewGameEvent(var start, var moves, var time) ->
            System.out.println("New game starting at " + start);
    }
}
```

This **deconstruction pattern** lets you extract record components directly in the pattern.

## Common Mistakes

### 1. Trying to Add Setters

**Mistake:**
```java
record Point(int x, int y) {
    public void setX(int x) {  // COMPILE ERROR
        this.x = x;
    }
}
```

**Why it fails**: Record components are implicitly `final`. You cannot reassign them.

**Solution**: Embrace immutability. Create transformation methods that return new instances:
```java
record Point(int x, int y) {
    Point withX(int newX) {
        return new Point(newX, y);
    }
}
```

### 2. Attempting to Extend Records

**Mistake:**
```java
record Point3D(int x, int y, int z) extends Point(x, y) { }  // COMPILE ERROR
```

**Why it fails**: Records are implicitly `final` and cannot be extended.

**Solution**: Use composition instead of inheritance:
```java
record Point(int x, int y) { }
record Point3D(Point base, int z) { }
```

Or if you need a hierarchy, use sealed interfaces:
```java
sealed interface Point permits Point2D, Point3D { }
record Point2D(int x, int y) implements Point { }
record Point3D(int x, int y, int z) implements Point { }
```

### 3. Not Understanding Custom Constructors

**Mistake**: Confusing canonical and compact constructors:
```java
record Range(int min, int max) {
    // Wrong: This is a canonical constructor, not compact
    public Range(int min, int max) {
        if (min > max) throw new IllegalArgumentException();
        this.min = min;  // You must assign all fields
        this.max = max;
    }
}
```

**Better**: Use a compact constructor:
```java
record Range(int min, int max) {
    // Compact constructor - assignments are automatic
    Range {
        if (min > max) {
            throw new IllegalArgumentException("min must be <= max");
        }
    }
}
```

### 4. Assuming Records Can Have Instance Fields

**Mistake:**
```java
record Point(int x, int y) {
    private int cachedHash;  // COMPILE ERROR: instance fields not allowed
}
```

**Why it fails**: Records can only have fields corresponding to their components. This ensures transparency and semantic consistency.

**Solution**: Use static fields or redesign:
```java
record Point(int x, int y) {
    private static final Map<Point, Integer> hashCache = new HashMap<>();

    @Override
    public int hashCode() {
        return hashCache.computeIfAbsent(this, p -> Objects.hash(p.x, p.y));
    }
}
```

(Though in practice, you rarely need to override `hashCode()` since the generated one is optimal.)

### 5. Mutating Mutable Components

**Mistake:**
```java
record Container(List<String> items) { }

Container c = new Container(new ArrayList<>(List.of("A", "B")));
c.items().add("C");  // Mutates the internal list!
```

**Problem**: The record is immutable (you can't reassign `items`), but the `List` itself is mutable.

**Solutions:**
1. **Defensive copying** in a compact constructor:
```java
record Container(List<String> items) {
    Container {
        items = List.copyOf(items);  // Immutable copy
    }
}
```

2. **Use immutable collections**:
```java
Container c = new Container(List.of("A", "B"));  // List.of() is immutable
```

Our `ValidMoves` record wraps a `Map`, which could be mutated. This is acceptable in our design because:
- The `Map` is created fresh for each event
- Consumers should treat it as read-only (documented contract)
- Future refactoring could add `Map.copyOf()` for true immutability

## Related Features

### Sealed Classes (Java 17)

Records combine beautifully with sealed classes to create **type-safe variant types**:

```java
sealed interface Shape permits Circle, Rectangle { }
record Circle(double radius) implements Shape { }
record Rectangle(double width, double height) implements Shape { }
```

This enables exhaustive pattern matching:
```java
double area(Shape shape) {
    return switch (shape) {
        case Circle(var r) -> Math.PI * r * r;
        case Rectangle(var w, var h) -> w * h;
        // Compiler ensures all cases are covered
    };
}
```

See: [Sealed Classes and Interfaces](./SealedClasses.md)

### Pattern Matching (Java 16+)

Pattern matching for `instanceof` (Java 16) and pattern matching for `switch` (Java 21) allow **deconstructing records**:

```java
// Pattern matching for instanceof
if (point instanceof Point(int x, int y)) {
    System.out.println("x=" + x + ", y=" + y);
}

// Pattern matching for switch with deconstruction
String describe(GameEvent event) {
    return switch (event) {
        case MoveEvent(var dir, var loc, _, _) -> "Moved " + dir + " to " + loc;
        case InvalidMoveEvent(var loc, var dir, _) -> "Can't move " + dir;
        case NewGameEvent(var start, _, _) -> "New game at " + start;
    };
}
```

See: [Pattern Matching and Switch Expressions](./PatternMatching.md)

### Lambdas and Functional Interfaces (Java 8)

Records work naturally with streams and functional programming:

```java
List<Point> points = List.of(new Point(0, 0), new Point(1, 1), new Point(2, 2));

// Filter, map, collect
List<Integer> xCoords = points.stream()
    .map(Point::x)  // Method reference to accessor
    .toList();
```

See: [Lambdas, Method References, and Functional Interfaces](./FunctionalProgramming.md)

## Further Reading

### Official Documentation
- **[JEP 395: Records](https://openjdk.org/jeps/395)** - The official proposal with rationale and specification
- **[Java Language Specification - Records](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html#jls-8.10)** - Formal definition
- **[Oracle Tutorial: Record Classes](https://docs.oracle.com/en/java/javase/21/language/records.html)** - Official guide with examples

### Deep Dives
- **[Inside Java: Records Come to Java](https://inside.java/2020/09/14/records-come-to-java/)** - Background and design philosophy
- **[Brian Goetz: Data Classes and Sealed Types for Java](https://cr.openjdk.java.net/~briangoetz/amber/datum.html)** - The vision behind records
- **[JEP 405: Record Patterns (Preview)](https://openjdk.org/jeps/405)** - Pattern matching with records
- **[JEP 440: Record Patterns](https://openjdk.org/jeps/440)** - Finalized record patterns (Java 21)

### Video Presentations
- **JavaOne 2020: Records, Sealed Classes, and Pattern Matching** - Comprehensive overview
- **Inside Java Podcast: Records** - Discussion with language designers

### Migration Guides
- **[Refactoring to Records](https://www.baeldung.com/java-record-vs-lombok)** - Converting traditional classes to records
- **[Records vs. Lombok](https://blogs.oracle.com/javamagazine/post/records-come-to-java)** - When to use each

### Books
- **"Modern Java in Action" (2nd Edition)** - Chapter on records and sealed types
- **"Effective Java" (4th Edition, forthcoming)** - Expected to include record best practices

---

**Key Takeaway**: Records solve the boilerplate problem for immutable data classes while maintaining Java's principles of strong typing and explicit semantics. They're not a replacement for all classes—use them when your primary purpose is to carry immutable data. Our codebase demonstrates ideal use cases: value objects (Point), type-safe wrappers (ValidMoves), and event payloads (MoveEvent, InvalidMoveEvent, NewGameEvent).

When combined with sealed classes and pattern matching, records enable powerful type-safe programming patterns previously unavailable in Java.