# Modern Java Features Guide

## Overview

Java has evolved dramatically since its initial release in 1995. What started as a relatively simple object-oriented language has grown into a sophisticated platform with powerful features for writing expressive, safe, and performant code.

This guide explores the modern Java language features used in this codebase—features introduced between Java 8 (2014) and Java 21 (2023), now compiled and running on Java 25. These aren't third-party libraries or frameworks; they're built into the Java language itself, representing the culmination of years of design, experimentation, and community feedback.

Unlike design patterns (which are conceptual solutions you implement yourself), these are language-level features provided by the JDK. You're learning the evolution of Java from verbose, ceremony-heavy syntax to more expressive, type-safe code.

## Why Modern Java Matters

**Historical Context**: Java gained a reputation in the 2000s for being verbose and requiring excessive boilerplate. Critics pointed to languages like Python, Ruby, and later Kotlin as more "modern" and developer-friendly. Java's stewards at Oracle and the OpenJDK community responded by introducing features that dramatically reduce boilerplate while maintaining Java's core strengths: strong typing, performance, and backward compatibility.

**From Java 8 to Java 25**: The period from 2014 to 2025 saw transformative changes:
- **Java 8 (2014)**: Lambdas and functional programming capabilities
- **Java 10 (2018)**: Local variable type inference
- **Java 14 (2020)**: Switch expressions
- **Java 16 (2021)**: Records and pattern matching for instanceof
- **Java 17 (2021)**: Sealed classes (LTS release)
- **Java 21 (2023)**: Pattern matching for switch, virtual threads (LTS release)
- **Java 25 (2025)**: Unnamed variables and patterns, statements before super(), module import declarations, primitive patterns in switch (LTS release)

Each feature was proposed through the **JEP (JDK Enhancement Proposal) process**, which involves research, prototyping, preview releases, community feedback, and refinement before finalization.

**This Codebase**: While we compile and run on Java 25 (LTS), the features demonstrated in this project were introduced and finalized between Java 8 and Java 21. These are the stable, production-ready features you'll use in most professional environments.

## Features Demonstrated in This Codebase

This project uses Java 25 and showcases several modern language features introduced between Java 8 and Java 21. The documents below explain each feature's history, syntax, purpose, and how it's implemented in our code.

### Table of Contents

1. **[Sealed Classes and Interfaces](./SealedClasses.md)** *(Java 17)*
   Restricting which classes can extend or implement a type, enabling exhaustive pattern matching and better domain modeling. Our `GameEvent` sealed interface demonstrates type-safe event hierarchies.

2. **[Records](./Records.md)** *(Java 16)*
   Immutable data carriers with minimal boilerplate. Our `Point`, `ValidMoves`, and all event types use records to eliminate getter/setter/equals/hashCode/toString ceremony.

3. **[Lambdas, Method References, and Functional Interfaces](./FunctionalProgramming.md)** *(Java 8)*
   First-class functions enabling functional programming in Java. Our strategy maps and action listeners demonstrate how lambdas and method references create concise, readable code.

4. **[Pattern Matching and Switch Expressions](./PatternMatching.md)** *(Java 14, 16, 21)*
   Enhanced instanceof (Java 16), switch expressions (Java 14), and pattern matching for switch (Java 21) work together to eliminate casting and enable type-safe control flow. Our event handlers showcase exhaustive switching over sealed types.

---

## How to Use This Guide

Each feature document follows a consistent structure:

1. **Java Version & JEP**: When it was introduced and the proposal that added it
2. **What It Is**: Technical explanation of the feature
3. **Why It Exists**: The problem it solves and historical context
4. **Syntax and Examples**: Basic syntax with simple examples
5. **In Our Codebase**: How we use it with specific line references
6. **Benefits**: What this feature gives you
7. **Common Mistakes**: What students typically get wrong
8. **Related Features**: How it connects to other Java features
9. **Further Reading**: JEPs, tutorials, and migration guides

### Reading Order

If you're new to modern Java, read in this order:

1. **Lambdas, Method References, and Functional Interfaces** - Foundation for functional programming
2. **Records** - Simplest feature, immediate value
3. **Sealed Classes** - Builds on records, shows type hierarchy control
4. **Pattern Matching and Switch Expressions** - Brings it all together

If you're familiar with Java 8 lambdas, start with Records and work forward.

## Before and After: A Taste of Modern Java

**Old Java (Pre-Java 14):**
```java
// Verbose event handling with casting
public void propertyChange(PropertyChangeEvent evt) {
    Object newValue = evt.getNewValue();
    if (newValue instanceof MoveEvent) {
        MoveEvent moveEvent = (MoveEvent) newValue;  // Cast required
        handleMove(moveEvent);
    } else if (newValue instanceof InvalidMoveEvent) {
        InvalidMoveEvent invalidEvent = (InvalidMoveEvent) newValue;
        handleInvalidMove(invalidEvent);
    } else if (newValue instanceof NewGameEvent) {
        NewGameEvent newGameEvent = (NewGameEvent) newValue;
        handleNewGame(newGameEvent);
    } else {
        // No exhaustiveness checking - compiler can't warn about missing cases
    }
}
```

**Modern Java (Java 21):**
```java
// Concise, type-safe, exhaustive pattern matching
public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getNewValue() instanceof GameEvent event) {
        switch (event) {
            case MoveEvent e -> handleMove(e);
            case InvalidMoveEvent e -> handleInvalidMove(e);
            case NewGameEvent e -> handleNewGame(e);
            // Compiler enforces exhaustiveness - no default needed
        }
    }
}
```

The modern version is shorter, safer (no casts to fail at runtime), and the compiler guarantees you handle all cases.

## Connection to Design Patterns

Modern Java features don't replace design patterns—they make them easier to implement:

- **Observer Pattern**: Lambdas simplify listener registration (`game.addPropertyChangeListener(evt -> ...))`)
- **Strategy Pattern**: Method references and functional interfaces eliminate strategy class boilerplate
- **Sealed Types**: Enable compiler-checked variant types (discriminated unions)
- **Records**: Perfect for immutable value objects, DTOs, and event payloads

See the [Design Patterns Guide](../design%20patterns/README.md) for how patterns and language features work together.

## Java Version Requirements

This project requires **Java 25 or higher** because it uses:
- Pattern matching for switch (Java 21 - finalized)
- Sealed classes (Java 17 - finalized)
- Records (Java 16 - finalized)

The features demonstrated were introduced in Java 8 through Java 21. If you're working in an earlier Java environment (Java 11 or Java 17 LTS), you can still use lambdas, method references, and switch expressions—but you'll need Java 21 or higher for pattern matching for switch, and Java 17 or higher for sealed classes.

## Further Exploration

After studying these features, consider exploring:
- **Virtual Threads** (Java 21) - Lightweight concurrency
- **Structured Concurrency** (Preview) - Safer thread management
- **String Templates** (Preview) - Type-safe string interpolation
- **Unnamed Patterns and Variables** (Java 22) - Cleaner code when values aren't used
- The full [OpenJDK JEP Index](https://openjdk.org/jeps/0) for upcoming features

Remember: Language features are tools, not mandates. Use sealed classes when you need exhaustiveness, use records for immutable data, use pattern matching when it clarifies intent. Don't force features where they don't fit.

---

**Version**: Autumn 2025
**Java Version**: 25