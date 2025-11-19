# Design Patterns Guide

## What is a Design Pattern?

A **design pattern** is a reusable solution to a commonly occurring problem in software design. It's not a finished piece of code you can copy and paste, but rather a template or blueprint for solving a problem that can be adapted to many different situations.

Think of design patterns like architectural patterns in building construction. If you're designing a house and want natural light, you don't reinvent the solution—you use the well-established pattern of "windows on two sides of every room." The architect Christopher Alexander documented such patterns in his 1977 book *A Pattern Language*, showing that certain design problems recur across many buildings, and certain solutions work reliably.

Software design patterns work the same way. When you need to notify multiple objects about state changes, you don't invent a new mechanism—you use the Observer pattern. When you need to ensure only one instance of a class exists, you use the Singleton pattern. These solutions have been proven over decades of software development.

Design patterns provide:
- **A shared vocabulary**: Saying "use the Observer pattern" communicates a complete design approach in three words
- **Proven solutions**: These patterns have been tested and refined across countless projects
- **Best practices**: They embody the accumulated wisdom of experienced developers
- **Flexibility**: Patterns are templates, not rigid rules—you adapt them to your needs

## A Brief History

The concept of design patterns in software engineering emerged in the 1990s, but the roots go back further:

### The Inspiration: Christopher Alexander (1977)
Christopher Alexander, an architect and design theorist, published *A Pattern Language: Towns, Buildings, Construction*, which cataloged 253 architectural patterns for designing buildings and communities. His insight was profound: certain design problems recur, and certain solutions work better than others. By documenting these patterns, architects could build on collective experience rather than starting from scratch.

### The Gang of Four (1994)
Erich Gamma, Richard Helm, Ralph Johnson, and John Vlissides (known as the "Gang of Four" or GoF) adapted Alexander's concept to software in their landmark book *Design Patterns: Elements of Reusable Object-Oriented Software*. Published in 1994, this book cataloged 23 fundamental design patterns organized into three categories:
- **Creational Patterns**: Object creation mechanisms (Singleton, Factory, Builder, etc.)
- **Structural Patterns**: Object composition (Adapter, Composite, Decorator, etc.)
- **Behavioral Patterns**: Object interaction and responsibility (Observer, Strategy, Command, etc.)

The Gang of Four explicitly credited Alexander in their book's introduction, noting they were applying his architectural pattern concept to object-oriented software design.

### Evolution and Modern Usage
Since 1994, the software community has:
- Identified many new patterns (Enterprise Integration Patterns, Concurrency Patterns, etc.)
- Adapted classic patterns to new programming paradigms (functional programming, reactive systems)
- Created pattern languages for specific domains (web development, distributed systems, mobile apps)
- Built frameworks and libraries that implement common patterns (Java's Observer framework, Spring's dependency injection, etc.)

While newer paradigms like reactive programming have introduced alternatives, classic design patterns remain foundational because they teach principles—separation of concerns, loose coupling, composition over inheritance—that transcend any single pattern or programming style.

## Patterns Demonstrated in This Codebase

This project implements several classic design patterns from the Gang of Four catalog. The documents below explain each pattern conceptually, then show how it's implemented in our game application.

### Table of Contents

1. **[Observer Pattern](./Observer.md)**
   How objects can subscribe to state changes without tight coupling. The backbone of event-driven programming and the foundation of our MVC architecture. Demonstrates Java's `PropertyChangeListener` framework enhanced with type-safe sealed events.

2. **[Model-View-Controller (MVC) Pattern](./MVC.md)**
   Separating business logic (Model), presentation (View), and user input handling (Controller) for maintainable, testable applications. Shows how Observer enables the Model to notify multiple Views, and explores the realistic complexity of controllers that handle multiple input types.

3. **[Singleton Pattern](./Singleton.md)**
   Ensuring only one instance of a class exists throughout the application. Demonstrates thread-safe lazy initialization with double-checked locking and the critical role of the `volatile` keyword in Java's memory model.

4. **[Strategy Pattern](./Strategy.md)**
   Encapsulating algorithms so they can be selected and swapped at runtime. Shows both the classic object-oriented approach and a modern functional implementation using Java's method references and `Map`-based strategy lookup.

---

## How to Use This Guide

Each pattern document follows a consistent structure:

1. **Brief History**: Origins and evolution of the pattern
2. **The Problem It Solves**: Non-technical, real-world analogies explaining why the pattern exists
3. **How the Pattern Solves It**: Conceptual explanation of the solution
4. **Technical Implementation in Our Codebase**: Detailed code examples with line references
5. **Benefits & Tradeoffs**: When to use the pattern and what it costs
6. **Common Pitfalls**: Mistakes students typically make
7. **Related Patterns**: How patterns connect and compose
8. **Further Reading**: Books, articles, and papers for deeper understanding

Start with **Observer** if you're new to design patterns—it's the most widely used pattern in GUI programming and forms the foundation for understanding MVC. Work through the documents in order, as later patterns reference earlier ones.

Each document includes:
- **UML class diagrams** showing pattern structure (renders on GitHub)
- **Code examples** from the actual codebase with specific line numbers
- **Relative links** to source files so you can explore the implementation

## Further Exploration

After studying these four patterns, consider exploring:
- The other 19 patterns in the Gang of Four catalog
- *Head First Design Patterns* by Freeman & Freeman for accessible explanations
- *Refactoring to Patterns* by Joshua Kerievsky for when and how to introduce patterns
- Pattern implementations in Java standard library (Iterator, Observer, Factory Method in `Collection` classes)

Remember: patterns are tools, not rules. Don't force them where they don't fit. The goal is to recognize recurring problems and know which pattern provides an elegant solution.