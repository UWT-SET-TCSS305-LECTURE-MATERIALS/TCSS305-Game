# Learning Guides

## Overview

This directory contains comprehensive educational guides for understanding the concepts, patterns, and technologies used in this codebase.

Each guide combines:
- **Historical context**: Why these concepts exist and how they evolved
- **Problem/Solution framework**: Real-world problems and how patterns/features solve them
- **Concrete examples**: Code from this actual project with specific line references
- **Practical guidance**: Common mistakes, best practices, and when to use each approach

## Guide Sections

### [Design Patterns](./design%20patterns/README.md)

Learn the classic software design patterns used throughout this project:

- **[Observer Pattern](./design%20patterns/Observer.md)** - Event-driven architecture with PropertyChangeListener
- **[Model-View-Controller (MVC)](./design%20patterns/MVC.md)** - Separating concerns in GUI applications
- **[Singleton Pattern](./design%20patterns/Singleton.md)** - Ensuring single instance with thread-safe initialization
- **[Strategy Pattern](./design%20patterns/Strategy.md)** - Encapsulating algorithms with functional interfaces

These patterns come from the "Gang of Four" catalog (1994) and remain fundamental to object-oriented design. You'll see how they're implemented in real code and how modern Java features make them easier to use.

### [Modern Java Features](./modern-java-features/README.md)

Understand the language-level features (Java 8-21) that make modern Java code more expressive and safe:

- **[Sealed Classes](./modern-java-features/SealedClasses.md)** - Type-safe hierarchies with exhaustiveness checking
- **[Records](./modern-java-features/Records.md)** - Immutable data carriers with minimal boilerplate
- **[Functional Programming](./modern-java-features/FunctionalProgramming.md)** - Lambdas, method references, and functional interfaces
- **[Pattern Matching](./modern-java-features/PatternMatching.md)** - Type-safe control flow without casting

These features dramatically reduce boilerplate and enable type-safe patterns that weren't possible in earlier Java versions. The project uses Java 25 but focuses on stable features from Java 8-21.

### [Look and Feel](./look-and-feel/README.md)

Explore how Swing's pluggable Look and Feel system works and why this project uses FlatLaf:

- **[Swing Look and Feel](./look-and-feel/README.md)** - Understanding LAF architecture and Java's built-in options
- **[FlatLaf](./look-and-feel/FlatLaf.md)** - Modern flat design with IntelliJ themes

Learn how Swing separates component logic from visual appearance, allowing you to change your entire application's look with one line of code.

## How These Guides Connect

The three guide sections are interconnected:

**Design Patterns ↔ Modern Java Features**:
- The **Observer pattern** uses **sealed classes** and **records** for type-safe events
- The **Strategy pattern** is simplified by **lambdas** and **method references**
- The **MVC pattern** uses **pattern matching** for exhaustive event handling
- All patterns benefit from **records** for immutable value objects

**Look and Feel ↔ Design Patterns**:
- Swing's LAF architecture uses the **Strategy pattern** (pluggable UI delegates)
- Changing themes at runtime demonstrates the **Observer pattern** (components observe UIManager)
- The MVC pattern explains why LAF works (separation of view from model)

**Look and Feel ↔ Modern Java Features**:
- LAF selection menus use **lambdas** for action listeners
- Theme preferences can use **records** for configuration storage
- **Pattern matching** simplifies theme-based conditional logic

## Recommended Reading Order

### If You're New to Everything:
1. Start with **[Design Patterns](./design%20patterns/README.md)** → Begin with **Observer** (foundational)
2. Move to **[Modern Java Features](./modern-java-features/README.md)** → Start with **Functional Programming** (Java 8 foundation)
3. Finish with **[Look and Feel](./look-and-feel/README.md)** → See patterns and features in action

### If You Know Java 8 Lambdas:
1. **[Modern Java Features](./modern-java-features/README.md)** → Focus on Records, Sealed Classes, Pattern Matching
2. **[Design Patterns](./design%20patterns/README.md)** → See how features enable better pattern implementation
3. **[Look and Feel](./look-and-feel/README.md)** → Optional, but shows real-world LAF usage

### If You Know Design Patterns:
1. **[Modern Java Features](./modern-java-features/README.md)** → See how Java evolved since Gang of Four era
2. **[Design Patterns](./design%20patterns/README.md)** → See modern implementations with new features
3. **[Look and Feel](./look-and-feel/README.md)** → Recognize Strategy pattern in Swing's architecture

### If You Just Want to Change the Theme:
1. Go directly to **[FlatLaf](./look-and-feel/FlatLaf.md)** → Try different IntelliJ themes
2. Read **[Swing Look and Feel](./look-and-feel/README.md)** → Understand how it works under the hood
3. Explore other guides when you're curious about patterns and features used in the code

## Document Structure

Each guide document follows a consistent format:

1. **Introduction**: What the concept is
2. **History**: Where it came from and why it exists
3. **Problem/Solution**: Real-world problems and conceptual solutions
4. **Technical Details**: Syntax, APIs, and implementation patterns
5. **In Our Codebase**: Specific examples with line references
6. **Benefits & Tradeoffs**: When to use (and when not to use)
7. **Common Mistakes**: What students typically get wrong
8. **Related Concepts**: How it connects to other patterns/features
9. **Further Reading**: Books, articles, JEPs, and official docs

## Using These Guides

### As a Student
- **Read actively**: Try to find the concepts in the actual code
- **Experiment**: Modify the code to see what breaks (and why)
- **Ask questions**: Use these guides as context for asking informed questions
- **Cross-reference**: Jump between guides when you see connections

### As an Instructor
- **Assign specific guides**: Each document is self-contained
- **Use as lecture prep**: Historical context and problem framing included
- **Extract examples**: Code snippets are taken from working code
- **Create exercises**: "Find where X pattern is used" or "Refactor Y using Z feature"

### As a Developer
- **Quick reference**: Find syntax and examples quickly
- **Understand decisions**: See why code is structured this way
- **Learn by example**: Real code, not toy examples
- **Refresh knowledge**: Brush up on patterns or features you haven't used recently

## Beyond These Guides

After mastering the content here, consider exploring:

**More Design Patterns**:
- Factory, Builder, Adapter, Decorator, Command, Iterator, etc.
- Enterprise patterns (Repository, Service Layer, Unit of Work)
- Concurrency patterns (Thread Pool, Producer-Consumer, Future)

**Advanced Java Features**:
- Virtual Threads (Java 21)
- Structured Concurrency (Preview)
- Foreign Function & Memory API (Preview)
- Vector API for SIMD operations

**GUI Frameworks**:
- JavaFX (modern Java GUI toolkit)
- Web-based UIs (Vaadin, Spring Boot + JavaScript frameworks)
- Cross-platform frameworks (Electron with JVM languages via GraalVM)

**Software Architecture**:
- Microservices patterns
- Event-driven architecture
- Domain-Driven Design
- Clean Architecture / Hexagonal Architecture

## Contributing

Found an error or unclear explanation? These guides are meant to evolve. Consider:
- Opening an issue with specific feedback
- Suggesting additional examples or clarifications
- Sharing what helped you understand a concept
- Identifying gaps or missing connections

## Acknowledgments

These guides draw from decades of collective wisdom:
- **Gang of Four** (Gamma, Helm, Johnson, Vlissides) - Design Patterns catalog
- **Christopher Alexander** - Pattern language concept
- **Brian Goetz** and the **JSR 335 expert group** - Java 8 lambdas
- **Gavin King** and the **JEP authors** - Modern Java features
- **FormDev Software** - FlatLaf Look and Feel
- **Oracle and OpenJDK community** - Java platform evolution

---

**Version**: Autumn 2025
**Java Version**: 25
