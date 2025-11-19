# Pattern Matching in Java

## Overview

Pattern matching is one of the most significant modern Java features, introduced progressively across **Java 14, 16, and 21**. It represents a fundamental shift in how we write type-safe control flow, eliminating manual type casting and making code more expressive and less error-prone.

This guide covers three interconnected features that work together:
1. **Switch Expressions** (Java 14, JEP 361)
2. **Pattern Matching for instanceof** (Java 16, JEP 394)
3. **Pattern Matching for Switch** (Java 21, JEP 441)

## Java Versions & JEPs

| Feature | Java Version | JEP | Status |
|---------|--------------|-----|--------|
| Switch Expressions | Java 14 | [JEP 361](https://openjdk.org/jeps/361) | Standard |
| Pattern Matching for instanceof | Java 16 | [JEP 394](https://openjdk.org/jeps/394) | Standard |
| Pattern Matching for Switch | Java 21 | [JEP 441](https://openjdk.org/jeps/441) | Standard |

## What It Is

Pattern matching allows you to **test that a value matches a specific pattern and extract data from it** in a single, concise expression. Combined with switch expressions, this enables:

- **Type-safe control flow** without manual casting
- **Exhaustiveness checking** when used with sealed types
- **Cleaner, more readable code** with less boilerplate

### The Three Features Explained

#### 1. Switch Expressions (Java 14)

Traditional switch statements don't return values. Switch expressions do:

```java
// Old: switch statement (no return value)
String result;
switch (value) {
    case 1:
        result = "one";
        break;
    case 2:
        result = "two";
        break;
    default:
        result = "other";
}

// New: switch expression (returns value)
String result = switch (value) {
    case 1 -> "one";
    case 2 -> "two";
    default -> "other";
};
```

**Key improvements:**
- Returns a value directly
- Arrow syntax (`->`) eliminates fall-through
- No need for `break` statements
- Compiler ensures all cases are covered

#### 2. Pattern Matching for instanceof (Java 16)

Eliminates the need for explicit casting after type checks:

```java
// Old: instanceof with manual casting
if (obj instanceof String) {
    String str = (String) obj;  // manual cast required
    System.out.println(str.length());
}

// New: pattern matching for instanceof
if (obj instanceof String str) {  // 'str' is already cast
    System.out.println(str.length());
}
```

**The pattern variable `str`** is:
- Automatically cast to the correct type
- Only in scope when the type test succeeds
- Final by default (can be declared explicitly with `final`)

#### 3. Pattern Matching for Switch (Java 21)

Combines switch expressions with pattern matching, enabling type-based switching:

```java
// Pattern matching in switch
Object obj = getObject();
String description = switch (obj) {
    case String s -> "String of length " + s.length();
    case Integer i -> "Integer: " + i;
    case null -> "null value";
    default -> "Unknown type";
};
```

When combined with **sealed types**, the compiler can verify exhaustiveness:

```java
sealed interface Shape permits Circle, Square, Triangle {}

// Compiler verifies all permitted types are handled
double area = switch (shape) {
    case Circle c -> Math.PI * c.radius() * c.radius();
    case Square s -> s.side() * s.side();
    case Triangle t -> 0.5 * t.base() * t.height();
    // No default needed - compiler knows all cases are covered!
};
```

## Why It Exists

Before pattern matching, Java code was plagued by repetitive casting and type checks:

```java
// The old way: verbose, error-prone, repetitive
if (evt.getNewValue() instanceof GameEvent) {
    GameEvent event = (GameEvent) evt.getNewValue();

    if (event instanceof MoveEvent) {
        MoveEvent moveEvent = (MoveEvent) event;
        movePiece(moveEvent.newLocation());
    } else if (event instanceof InvalidMoveEvent) {
        InvalidMoveEvent invalidEvent = (InvalidMoveEvent) event;
        invalidMove();
    } else if (event instanceof NewGameEvent) {
        NewGameEvent newGameEvent = (NewGameEvent) event;
        startGame(newGameEvent.startingLocation());
    }
}
```

Pattern matching eliminates this boilerplate:

```java
// The new way: concise, type-safe, readable
if (evt.getNewValue() instanceof final GameEvent event) {
    switch (event) {
        case final GameEvent.MoveEvent moveEvent ->
                movePiece(moveEvent.newLocation());
        case final GameEvent.InvalidMoveEvent invalidEvent ->
                invalidMove();
        case final GameEvent.NewGameEvent newGameEvent ->
                startGame(newGameEvent.startingLocation());
    }
}
```

**Key benefits:**
- **Eliminates casting errors**: The compiler handles type casting
- **Enforces exhaustiveness**: With sealed types, you can't forget a case
- **More readable**: The intent is clear at a glance
- **Fewer bugs**: Less manual casting = fewer ClassCastExceptions

## Syntax and Examples

### 1. Switch Expressions Basics

```java
// Simple value-returning switch
Runnable action = switch (keyCode) {
    case KeyEvent.VK_W -> myGame::moveUp;
    case KeyEvent.VK_S -> myGame::moveDown;
    case KeyEvent.VK_A -> myGame::moveLeft;
    case KeyEvent.VK_D -> myGame::moveRight;
    default -> () -> { };  // do nothing
};
```

**From our codebase:** [`GameController.java:285-291`](../src/edu/uw/tcss/game/gui/contoller/GameController.java#L285-L291)

For multi-line cases, use a block with `yield`:

```java
String result = switch (value) {
    case 1 -> {
        System.out.println("Processing one");
        yield "one";  // yield returns the value
    }
    case 2 -> "two";  // single expression doesn't need yield
    default -> "other";
};
```

### 2. Pattern Matching for instanceof

```java
// Basic pattern
if (theEvent.getNewValue() instanceof final GameEvent event) {
    // 'event' is in scope here and is of type GameEvent
    processEvent(event);
}
// 'event' is NOT in scope here
```

**Scope rules:**
```java
if (obj instanceof String s && s.length() > 5) {
    // s is in scope in the AND condition
    System.out.println(s);
}

if (obj instanceof String s || s.isEmpty()) {  // COMPILE ERROR!
    // s is NOT in scope in the OR condition
}
```

The pattern variable is only in scope when the condition **must be true**.

### 3. Pattern Matching for Switch

**Basic type switching:**
```java
String format = switch (obj) {
    case Integer i -> String.format("int %d", i);
    case Long l -> String.format("long %d", l);
    case Double d -> String.format("double %f", d);
    case String s -> String.format("String %s", s);
    default -> obj.toString();
};
```

**With guards (when clauses):**
```java
String classify = switch (obj) {
    case String s when s.isEmpty() -> "Empty string";
    case String s when s.length() < 10 -> "Short string";
    case String s -> "Long string";
    case Integer i when i > 0 -> "Positive integer";
    case Integer i -> "Non-positive integer";
    default -> "Other type";
};
```

## In Our Codebase

Our codebase demonstrates the full progression of pattern matching features working together.

### Example 1: Event Handling with Pattern Matching

**File:** [`GameBoardPanel.java:160-172`](../src/edu/uw/tcss/game/gui/view/GameBoardPanel.java#L160-L172)

```java
@Override
public void propertyChange(final PropertyChangeEvent theEvent) {
    // Pattern matching for instanceof (Java 16)
    if (theEvent.getNewValue() instanceof final GameEvent event) {
        // Pattern matching for switch (Java 21)
        switch (event) {
            case final GameEvent.MoveEvent moveEvent ->
                    movePiece(moveEvent.newLocation());
            case final GameEvent.InvalidMoveEvent invalidEvent ->
                    invalidMove();
            case final GameEvent.NewGameEvent newGameEvent ->
                    startGame(newGameEvent.startingLocation());
        }
        // No 'default' needed - compiler knows all GameEvent types are covered!
    }
}
```

**Why this works without a default case:**
- `GameEvent` is a **sealed interface** (see [`GameEvent.java:43-46`](../src/edu/uw/tcss/game/model/GameEvent.java#L43-L46))
- The compiler knows all permitted subtypes
- Exhaustiveness is verified at compile time

### Example 2: Controller Event Handling

**File:** [`GameController.java:241-251`](../src/edu/uw/tcss/game/gui/contoller/GameController.java#L241-L251)

```java
@Override
public void propertyChange(final PropertyChangeEvent theEvent) {
    if (theEvent.getNewValue() instanceof final GameEvent event) {
        switch (event) {
            case final GameEvent.MoveEvent moveEvent ->
                    enableValidDirections(moveEvent.validMoves());
            case final GameEvent.NewGameEvent newGameEvent ->
                    enableValidDirections(newGameEvent.validMoves());
            default -> { }  // InvalidMoveEvent doesn't need button updates
        }
    }
}
```

**Note:** This example includes a `default` case because not all event types require action. The default case is intentionally empty.

### Example 3: Switch Expression Returning Values

**File:** [`GameController.java:285-291`](../src/edu/uw/tcss/game/gui/contoller/GameController.java#L285-L291)

```java
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
```

**This demonstrates:**
- Switch as an expression (returns a value)
- Method references as case results
- Default case providing a sensible fallback

### The Sealed Type That Enables It All

**File:** [`GameEvent.java:43-46`](../src/edu/uw/tcss/game/model/GameEvent.java#L43-L46)

```java
public sealed interface GameEvent permits
        GameEvent.MoveEvent,
        GameEvent.InvalidMoveEvent,
        GameEvent.NewGameEvent {
    // ...
}
```

The `sealed` keyword restricts which classes can implement this interface. This allows the compiler to verify that switch statements handle all possible cases.

**See also:** [Sealed Classes and Interfaces Guide](SealedClasses.md)

## Benefits

### 1. Type Safety

```java
// Before: runtime error possible
Object obj = evt.getNewValue();
GameEvent event = (GameEvent) obj;  // Could throw ClassCastException
MoveEvent move = (MoveEvent) event; // Another cast, another risk

// After: compile-time verification
if (evt.getNewValue() instanceof final GameEvent event) {
    switch (event) {
        case MoveEvent move -> // 'move' is guaranteed to be MoveEvent
    }
}
```

### 2. Exhaustiveness Checking

With sealed types, the compiler ensures all cases are handled:

```java
sealed interface GameEvent permits MoveEvent, InvalidMoveEvent, NewGameEvent {}

// Compiler error if any case is missing!
switch (event) {
    case MoveEvent m -> handleMove(m);
    case InvalidMoveEvent i -> handleInvalid(i);
    case NewGameEvent n -> handleNew(n);
    // No default needed - compiler knows these are the only options
}
```

If you add a new event type to the sealed interface, **all switch statements will fail to compile** until they handle the new case. This makes refactoring safer.

### 3. Readability

Pattern matching reduces cognitive load:

```java
// Old way: lots of ceremony
if (evt.getNewValue() instanceof GameEvent) {
    GameEvent event = (GameEvent) evt.getNewValue();
    if (event instanceof MoveEvent) {
        MoveEvent moveEvent = (MoveEvent) event;
        movePiece(moveEvent.newLocation());
    }
}

// New way: the intent is clear
if (evt.getNewValue() instanceof GameEvent event) {
    switch (event) {
        case MoveEvent m -> movePiece(m.newLocation());
    }
}
```

### 4. Fewer Runtime Errors

- **No manual casting** = no ClassCastException
- **Exhaustiveness checking** = no forgotten cases
- **Compiler verification** = errors caught at compile time

## Common Mistakes

### 1. Forgetting That Switch Expressions Need to Return Values

```java
// ERROR: not all code paths return a value
int value = switch (x) {
    case 1 -> 10;
    // Missing default case!
};

// FIX: provide exhaustive cases
int value = switch (x) {
    case 1 -> 10;
    default -> 0;
};
```

### 2. Using `yield` Incorrectly

```java
// ERROR: can't use yield in arrow form
int value = switch (x) {
    case 1 -> yield 10;  // WRONG!
};

// FIX 1: use expression directly
int value = switch (x) {
    case 1 -> 10;
};

// FIX 2: use block form with yield
int value = switch (x) {
    case 1 -> {
        System.out.println("Processing");
        yield 10;  // OK in block form
    }
};
```

### 3. Not Understanding Exhaustiveness Requirements

```java
// ERROR: non-sealed types require default
interface Event {}  // NOT sealed

switch (event) {
    case MoveEvent m -> handleMove(m);
    // Compiler error: missing default case!
}

// FIX 1: add default
switch (event) {
    case MoveEvent m -> handleMove(m);
    default -> throw new IllegalStateException("Unexpected: " + event);
}

// FIX 2: make Event sealed
sealed interface Event permits MoveEvent, InvalidMoveEvent {}
// Now exhaustiveness checking works!
```

### 4. Pattern Variable Scope Confusion

```java
// ERROR: pattern variable scope
if (obj instanceof String s) {
    System.out.println(s);  // OK: s is in scope
}
System.out.println(s);  // ERROR: s is not in scope here

// ERROR: pattern variable not available in OR
if (obj instanceof String s || s.isEmpty()) {  // ERROR!
    // s might not be defined if obj isn't a String
}

// CORRECT: pattern variable available in AND
if (obj instanceof String s && s.isEmpty()) {  // OK!
    // s is definitely a String here
}
```

### 5. Null Handling in Switch

```java
// Old switch: NullPointerException!
switch (value) {  // NPE if value is null
    case "hello" -> System.out.println("Hi");
}

// New switch: can handle null explicitly
switch (value) {
    case null -> System.out.println("Got null");
    case "hello" -> System.out.println("Hi");
    default -> System.out.println("Other");
}
```

### 6. Confusing Statement vs Expression Forms

```java
// Switch STATEMENT: doesn't return value
switch (x) {
    case 1 -> System.out.println("one");
    case 2 -> System.out.println("two");
}

// Switch EXPRESSION: must return value
String result = switch (x) {
    case 1 -> "one";      // returns String
    case 2 -> "two";      // returns String
    default -> "other";   // must be exhaustive
};
```

## Related Features

### Sealed Classes and Interfaces

Pattern matching for switch works best with sealed types, which enable exhaustiveness checking.

**See:** [Sealed Classes and Interfaces Guide](SealedClasses.md)

```java
sealed interface GameEvent permits MoveEvent, InvalidMoveEvent, NewGameEvent {}

// Compiler verifies all cases are covered
switch (event) {
    case MoveEvent m -> handleMove(m);
    case InvalidMoveEvent i -> handleInvalid(i);
    case NewGameEvent n -> handleNew(n);
    // No default needed!
}
```

### Records

Records work naturally with pattern matching:

```java
record Point(int x, int y) {}

if (obj instanceof Point p) {
    System.out.printf("Point at (%d, %d)%n", p.x(), p.y());
}
```

**Future feature (Preview):** Record patterns will allow destructuring:
```java
// Java 21+ preview feature
if (obj instanceof Point(int x, int y)) {
    System.out.printf("Point at (%d, %d)%n", x, y);
}
```

### Local Variable Type Inference (`var`)

Pattern variables can be combined with `var`:

```java
// Explicit type
if (obj instanceof String s) { }

// Using var (type is inferred)
var result = switch (obj) {
    case String s -> s.length();
    case Integer i -> i;
    default -> 0;
};  // result is inferred as int
```

## The Evolution: Java 14 → 16 → 21

Here's how our event handling would look at each Java version:

### Java 11 (Before Pattern Matching)

```java
@Override
public void propertyChange(PropertyChangeEvent theEvent) {
    if (theEvent.getNewValue() instanceof GameEvent) {
        GameEvent event = (GameEvent) theEvent.getNewValue();

        if (event instanceof GameEvent.MoveEvent) {
            GameEvent.MoveEvent moveEvent = (GameEvent.MoveEvent) event;
            movePiece(moveEvent.newLocation());
        } else if (event instanceof GameEvent.InvalidMoveEvent) {
            GameEvent.InvalidMoveEvent invalidEvent =
                (GameEvent.InvalidMoveEvent) event;
            invalidMove();
        } else if (event instanceof GameEvent.NewGameEvent) {
            GameEvent.NewGameEvent newGameEvent =
                (GameEvent.NewGameEvent) event;
            startGame(newGameEvent.startingLocation());
        }
    }
}
```

**Problems:**
- Lots of manual casting
- Easy to make casting mistakes
- Verbose and repetitive
- No compile-time exhaustiveness checking

### Java 14 (Switch Expressions)

```java
@Override
public void propertyChange(PropertyChangeEvent theEvent) {
    if (theEvent.getNewValue() instanceof GameEvent) {
        GameEvent event = (GameEvent) theEvent.getNewValue();

        // Can't use switch with patterns yet, but can use switch expressions
        // Still need if-else for type checking
        if (event instanceof GameEvent.MoveEvent) {
            GameEvent.MoveEvent moveEvent = (GameEvent.MoveEvent) event;
            movePiece(moveEvent.newLocation());
        } else if (event instanceof GameEvent.InvalidMoveEvent) {
            invalidMove();
        } else if (event instanceof GameEvent.NewGameEvent) {
            GameEvent.NewGameEvent newGameEvent =
                (GameEvent.NewGameEvent) event;
            startGame(newGameEvent.startingLocation());
        }
    }
}
```

**Improvement:** Switch can return values, but doesn't help with type checking.

### Java 16 (Pattern Matching for instanceof)

```java
@Override
public void propertyChange(PropertyChangeEvent theEvent) {
    // Pattern matching eliminates first cast
    if (theEvent.getNewValue() instanceof GameEvent event) {
        // But still need if-else chain for subtypes
        if (event instanceof GameEvent.MoveEvent moveEvent) {
            movePiece(moveEvent.newLocation());
        } else if (event instanceof GameEvent.InvalidMoveEvent invalidEvent) {
            invalidMove();
        } else if (event instanceof GameEvent.NewGameEvent newGameEvent) {
            startGame(newGameEvent.startingLocation());
        }
    }
}
```

**Improvement:** No more manual casting, but still using if-else chains.

### Java 21 (Pattern Matching for Switch)

```java
@Override
public void propertyChange(PropertyChangeEvent theEvent) {
    if (theEvent.getNewValue() instanceof final GameEvent event) {
        switch (event) {
            case final GameEvent.MoveEvent moveEvent ->
                    movePiece(moveEvent.newLocation());
            case final GameEvent.InvalidMoveEvent invalidEvent ->
                    invalidMove();
            case final GameEvent.NewGameEvent newGameEvent ->
                    startGame(newGameEvent.startingLocation());
            // No default needed - exhaustiveness guaranteed by sealed type!
        }
    }
}
```

**Improvements:**
- Clean switch syntax for type-based dispatch
- No manual casting anywhere
- Exhaustiveness checking with sealed types
- Most readable and maintainable

## Best Practices

1. **Use pattern matching for instanceof whenever appropriate**
   - It's always better than manual casting
   - Makes code more concise and safer

2. **Combine with sealed types for exhaustiveness**
   - Design your type hierarchies to be sealed when possible
   - Let the compiler verify all cases are handled

3. **Prefer switch expressions over statements**
   - Returning a value makes intent clearer
   - Exhaustiveness checking is enforced

4. **Use meaningful pattern variable names**
   ```java
   // Bad: reusing the same name
   if (obj instanceof String s) { }

   // Good: descriptive names
   if (obj instanceof GameEvent.MoveEvent moveEvent) { }
   ```

5. **Consider guards for complex conditions**
   ```java
   switch (event) {
       case MoveEvent m when m.newLocation().x() > 5 -> handleFarMove(m);
       case MoveEvent m -> handleNormalMove(m);
       case InvalidMoveEvent i -> handleInvalid(i);
   }
   ```

6. **Make pattern variables final when appropriate**
   ```java
   if (obj instanceof final String s) {
       // Signals immutability
   }
   ```

## Performance Considerations

Pattern matching has **no performance penalty** compared to manual casting:

- The JVM performs the same type checks
- Pattern variables are just syntactic sugar for casts
- Switch expressions may be **slightly faster** due to better optimization opportunities

The real benefit is **developer productivity** and **code safety**, not runtime performance.

## Further Reading

### Official Documentation

- [JEP 361: Switch Expressions](https://openjdk.org/jeps/361) (Java 14)
- [JEP 394: Pattern Matching for instanceof](https://openjdk.org/jeps/394) (Java 16)
- [JEP 441: Pattern Matching for Switch](https://openjdk.org/jeps/441) (Java 21)
- [JEP 409: Sealed Classes](https://openjdk.org/jeps/409) (Java 17)

### Related Guides in This Project

- [Sealed Classes and Interfaces](SealedClasses.md) - Enables exhaustiveness checking
- [Records](Records.md) - Work great with pattern matching
- [Local Variable Type Inference](LocalVariableTypeInference.md) - Complements pattern matching

### Tutorials and Articles

- [Oracle Java Tutorials: Pattern Matching](https://docs.oracle.com/en/java/javase/21/language/pattern-matching.html)
- [Inside Java: Pattern Matching](https://inside.java/tag/pattern-matching)
- [Dev.java: Pattern Matching](https://dev.java/learn/pattern-matching/)

### In This Codebase

- **GameEvent.java**: Sealed interface demonstrating exhaustiveness
- **GameBoardPanel.java**: Pattern matching in PropertyChangeListener
- **GameController.java**: Switch expressions and pattern matching combined

---

Pattern matching represents a fundamental improvement to Java's type system. By eliminating manual casting and enabling exhaustiveness checking, it makes Java code more concise, more readable, and significantly safer. Combined with sealed types and records, it enables elegant, type-safe polymorphism that was previously impossible in Java.