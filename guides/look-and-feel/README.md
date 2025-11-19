# Swing Look and Feel (LAF)

## What is Look and Feel?

**Look and Feel (LAF)** in Java Swing refers to the visual appearance and interaction behavior of GUI components. It controls:
- How components are **rendered** (colors, fonts, borders, icons)
- How users **interact** with components (button press animations, focus indicators)
- The overall **aesthetic style** (flat, 3D, native OS appearance)

Swing's pluggable Look and Feel architecture allows you to change the entire appearance of your application at runtime without modifying component code. The same `JButton` can look like a Windows button, a macOS button, or use a completely custom design—just by changing one line of code.

## Why Pluggable Look and Feel?

### The Problem: Platform Independence vs. Native Appearance

When Java 1.0 launched in 1996 with AWT (Abstract Window Toolkit), it used **native peer components**—each Java component was backed by a real OS widget. This gave a native look automatically, but created serious problems:
- **Platform inconsistencies**: Different behavior on Windows, Mac, Unix
- **Layout issues**: Components had different sizes on different platforms
- **Limited customization**: You couldn't modify native component appearance
- **Heavy resource usage**: Each component required an OS-level object

### The Solution: Lightweight Components + Pluggable LAF

When Swing was introduced in Java 1.2 (1998), it took a different approach:
- **Lightweight components**: Swing components are pure Java—they paint themselves directly on a canvas rather than using OS widgets
- **Pluggable LAF**: The rendering and behavior are separated from the component logic through the LAF architecture
- **MVC design**: Each component follows Model-View-Controller, where the LAF provides the View (UI delegate)

This architecture gives you:
- **Consistency**: Same behavior across all platforms
- **Flexibility**: Switch LAFs at runtime or create custom ones
- **Customization**: Fine-grained control over appearance
- **Native option available**: System LAF mimics native OS appearance when desired

## Historical Java Look and Feels

Java has included several built-in LAFs over the years:

### Metal (1998 - Present)
- **Status**: Default cross-platform LAF since Java 1.2
- **Appearance**: Originally a 3D style with beveled edges, updated to "Ocean" theme in Java 5 with flatter appearance
- **Philosophy**: Distinctively "Java" appearance that works identically on all platforms
- **When to use**: When you want guaranteed cross-platform consistency and don't need native appearance
- **Class**: `javax.swing.plaf.metal.MetalLookAndFeel`

### Windows (1998 - Present)
- **Status**: Available on Windows platforms
- **Appearance**: Mimics native Windows UI (updates with Windows versions)
- **Philosophy**: Java applications should blend in with other Windows applications
- **When to use**: Windows-only applications where users expect native appearance
- **Class**: `com.sun.java.swing.plaf.windows.WindowsLookAndFeel`

### Motif/CDE (1998 - Java 8)
- **Status**: Deprecated and removed
- **Appearance**: Unix CDE (Common Desktop Environment) style
- **Historical context**: Targeted Unix workstations with Motif widget toolkit
- **Why removed**: CDE became obsolete; modern Linux uses GTK+ or Qt
- **Class**: `com.sun.java.swing.plaf.motif.MotifLookAndFeel` (removed)

### GTK+ (Java 6 - Present)
- **Status**: Available on Linux/Unix platforms
- **Appearance**: Mimics GTK+ toolkit used by GNOME desktop
- **Philosophy**: Java applications should match native Linux appearance
- **When to use**: Linux applications where users expect native GNOME integration
- **Class**: `com.sun.java.swing.plaf.gtk.GTKLookAndFeel`

### Aqua/Mac (Java for Mac)
- **Status**: Default on macOS
- **Appearance**: Native macOS appearance
- **Philosophy**: Automatic—Java applications look like Mac applications
- **Implementation**: Uses native rendering where possible
- **Class**: Apple proprietary implementation

### Nimbus (Java 6 Update 10 - Present)
- **Status**: Modern cross-platform LAF (available but not default)
- **Appearance**: Flat, vector-based, scalable design with smooth gradients
- **Philosophy**: Modern alternative to Metal that's attractive and consistent across platforms
- **Advantages**: Scalable (vector-based), professional appearance, better HiDPI support than Metal
- **When to use**: When you want a modern look without third-party dependencies
- **Class**: `javax.swing.plaf.nimbus.NimbusLookAndFeel`

## How Look and Feel Works

### The LAF Architecture

Swing uses a **separation of concerns** design where each component has:

1. **Component** (e.g., `JButton`): Maintains model/state, fires events, handles layout
2. **UI Delegate** (e.g., `MetalButtonUI`, `WindowsButtonUI`): Handles rendering and input for a specific LAF
3. **LAF Class** (e.g., `MetalLookAndFeel`): Factory that creates UI delegates and defines defaults (colors, fonts, borders)

When you set a LAF:
```java
UIManager.setLookAndFeel(new FlatIntelliJLaf());
```

Swing:
1. Instantiates the LAF class
2. Loads its UI defaults (colors, fonts, etc.) into `UIManager`
3. Associates each component type with a UI delegate class
4. Updates all existing components by calling `SwingUtilities.updateComponentTreeUI()`

### Setting the Look and Feel

**At application startup** (before creating any GUI):
```java
public static void main(String[] args) {
    // Set LAF before creating components
    try {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch (Exception e) {
        // Fall back to default
    }

    SwingUtilities.invokeLater(() -> createGUI());
}
```

**Using system LAF** (matches OS):
```java
UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
```

**Using cross-platform LAF** (Metal):
```java
UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
```

**Changing LAF at runtime**:
```java
UIManager.setLookAndFeel(new NimbusLookAndFeel());
SwingUtilities.updateComponentTreeUI(frame);  // Update existing components
frame.pack();  // Revalidate sizes
```

### Querying Available LAFs

```java
UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
for (UIManager.LookAndFeelInfo info : lafs) {
    System.out.println(info.getName() + ": " + info.getClassName());
}
```

## Third-Party Look and Feels

While Java's built-in LAFs serve many purposes, the open-source community has created numerous third-party LAFs offering:
- **Modern aesthetics**: Flat, material design, dark themes
- **Better theming**: Fine-grained color customization
- **HiDPI support**: Better scaling on high-resolution displays
- **Specialized designs**: Terminal-style, retro, game-specific

Popular third-party LAFs include:
- **[FlatLaf](./FlatLaf.md)**: Modern flat design with IntelliJ, Darcula, and other themes (used in this project)
- **Substance**: Rich, animated LAF with extensive theme support
- **JTattoo**: Collection of texture-based themes
- **WebLaF**: Web-inspired modern design

This project uses **FlatLaf** for its modern appearance, active development, and excellent HiDPI support.

## Benefits

### 1. Platform Consistency
Write once, looks the same everywhere (with cross-platform LAFs).

### 2. User Choice
Let users select their preferred appearance without code changes.

### 3. Branding
Create custom LAFs matching your company/product branding.

### 4. Accessibility
Different LAFs can provide better contrast, larger fonts, or other accessibility features.

### 5. Modernization
Update old applications with modern LAFs without rewriting GUI code.

## Common Patterns

### LAF Selection Menu

Many applications provide a menu for users to change LAF:

```java
JMenu lafMenu = new JMenu("Look and Feel");
for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    JMenuItem item = new JMenuItem(info.getName());
    item.addActionListener(e -> {
        try {
            UIManager.setLookAndFeel(info.getClassName());
            SwingUtilities.updateComponentTreeUI(frame);
            frame.pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    });
    lafMenu.add(item);
}
```

### Persisting LAF Choice

Save user's LAF preference:
```java
// Save
Preferences prefs = Preferences.userNodeForPackage(MyApp.class);
prefs.put("lookAndFeel", UIManager.getLookAndFeel().getClass().getName());

// Load at startup
String savedLAF = prefs.get("lookAndFeel", UIManager.getSystemLookAndFeelClassName());
UIManager.setLookAndFeel(savedLAF);
```

## Common Mistakes

### 1. Setting LAF After Creating Components

**Problem**: LAF must be set before creating GUI components, otherwise they won't use the new LAF.

**Wrong:**
```java
createGUI();
UIManager.setLookAndFeel(new NimbusLookAndFeel());  // Too late!
```

**Right:**
```java
UIManager.setLookAndFeel(new NimbusLookAndFeel());
createGUI();
```

### 2. Forgetting to Update UI Tree

**Problem**: When changing LAF at runtime, existing components won't update automatically.

**Wrong:**
```java
UIManager.setLookAndFeel(newLAF);
// Components still show old LAF
```

**Right:**
```java
UIManager.setLookAndFeel(newLAF);
SwingUtilities.updateComponentTreeUI(frame);
frame.pack();
```

### 3. Hard-coding Colors/Fonts

**Problem**: Hard-coded values don't respect LAF themes.

**Wrong:**
```java
button.setBackground(Color.BLUE);  // Ignores LAF
```

**Right:**
```java
button.setBackground(UIManager.getColor("Button.background"));
// Or let LAF handle it:
// (don't set background at all)
```

### 4. Not Handling LAF Exceptions

**Problem**: `setLookAndFeel()` throws checked exceptions that should be handled.

**Wrong:**
```java
UIManager.setLookAndFeel("com.some.LAF");  // Won't compile
```

**Right:**
```java
try {
    UIManager.setLookAndFeel("com.some.LAF");
} catch (ClassNotFoundException | InstantiationException |
         IllegalAccessException | UnsupportedLookAndFeelException e) {
    // Fall back to system LAF
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
}
```

## In Our Codebase

This project uses **FlatLaf** for a modern appearance. See [Application.java](../../src/edu/uw/tcss/game/Application.java) (line 30):

```java
FlatIntelliJLaf.setup();
```

The `setup()` method is FlatLaf's convenience method that:
1. Registers FlatLaf with `UIManager`
2. Installs system properties for better rendering
3. Enables HiDPI support

For details on FlatLaf and how to use its various themes, see [FlatLaf.md](./FlatLaf.md).

## Further Reading

### Official Documentation
- **[How to Set the Look and Feel](https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html)** - Oracle's official tutorial
- **[Swing Architecture](https://docs.oracle.com/javase/tutorial/uiswing/overview/arch.html)** - Understanding MVC in Swing
- **[UIManager API](https://docs.oracle.com/en/java/javase/25/docs/api/java.desktop/javax/swing/UIManager.html)** - Managing LAF and UI defaults

### Articles
- **[The Swing Connection: Pluggable Look and Feel](https://web.archive.org/web/19990117010747/http://java.sun.com/products/jfc/tsc/tech_topics/pluggable/pluggable.html)** - Original design article (1998)
- **[Nimbus Look and Feel](https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html)** - Oracle's modern cross-platform LAF

### Books
- **Java Swing** by Matthew Robinson and Pavel Vorobiev (2nd Edition, 2003) - Chapter 21 covers LAF in depth
- **Swing Hacks** by Joshua Marinacci and Chris Adamson (2005) - Practical LAF customization techniques

---

**Next**: Learn about [FlatLaf](./FlatLaf.md), the modern third-party LAF used in this project.
