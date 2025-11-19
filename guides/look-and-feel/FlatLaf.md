# FlatLaf - Modern Look and Feel for Swing

## What is FlatLaf?

**FlatLaf** (Flat Look and Feel) is a modern, open-source Look and Feel for Java Swing applications. Created and maintained by FormDev Software, it provides a flat, clean design similar to contemporary applications and IDEs like IntelliJ IDEA, Visual Studio Code, and modern web applications.

FlatLaf offers:
- **Modern flat design**: No 3D bevels, gradients, or shadows—just clean, flat components
- **Light and dark themes**: Built-in light, dark, and IntelliJ-inspired themes
- **IntelliJ Themes**: Support for hundreds of community-created themes from IntelliJ IDEA and other JetBrains IDEs
- **Excellent HiDPI support**: Crisp rendering on high-resolution displays (2x, 3x scaling)
- **Active development**: Regular updates and bug fixes
- **Native features**: System menu bar on macOS, native window decorations on Windows 11
- **Easy customization**: Simple API for theme adjustments

### Project Information
- **Website**: https://www.formdev.com/flatlaf/
- **GitHub**: https://github.com/JFormDesigner/FlatLaf
- **License**: Apache License 2.0 (free for commercial use)
- **First Release**: 2019
- **Current Status**: Actively maintained

## Why Use FlatLaf?

### The Problem with Built-in LAFs

Java's built-in Look and Feels have limitations for modern applications:

**Metal/Ocean**:
- Looks dated with 3D beveled components
- Doesn't match modern application aesthetics
- Poor HiDPI support

**Nimbus**:
- More modern than Metal, but still not flat design
- Limited theming options
- Some rendering inconsistencies

**System LAF** (Windows, GTK, Aqua):
- Platform-specific—inconsistent cross-platform appearance
- Can't provide dark theme on platforms without native dark mode
- Limited customization

### The FlatLaf Solution

FlatLaf addresses these issues:
- **Consistent modern appearance** across Windows, macOS, Linux
- **Dark theme support** on all platforms
- **Better HiDPI** rendering than built-in LAFs
- **Professional appearance** suitable for commercial applications
- **Easy integration** via Maven/Gradle dependencies
- **Extensive theming** through IntelliJ theme format

## Installation

FlatLaf can be added to a project in several ways:

### This Project: External JARs

This project includes FlatLaf by linking JAR files from the `external/` directory:
- `external/flatlaf-3.5.4.jar` - Core FlatLaf library
- `external/flatlaf-intellij-themes-3.5.4.jar` - IntelliJ themes add-on

These JARs are added to the project's build path in IntelliJ IDEA, making FlatLaf classes available without using a dependency manager like Maven or Gradle.

**Advantages of this approach**:
- No build tool configuration required
- Self-contained project (all dependencies included)
- Simple for educational/demonstration projects
- Works immediately after cloning the repository

### Alternative: Maven
For projects using Maven:
```xml
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>flatlaf</artifactId>
    <version>3.5.4</version>
</dependency>

<!-- For IntelliJ themes -->
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>flatlaf-intellij-themes</artifactId>
    <version>3.5.4</version>
</dependency>
```

### Alternative: Gradle
For projects using Gradle:
```groovy
implementation 'com.formdev:flatlaf:3.5.4'
implementation 'com.formdev:flatlaf-intellij-themes:3.5.4'
```

## Basic Usage

### Setting FlatLaf at Startup

The simplest way to use FlatLaf is to call `setup()` before creating any GUI components:

```java
import com.formdev.flatlaf.FlatLightLaf;

public class Application {
    public static void main(String[] args) {
        FlatLightLaf.setup();  // Install FlatLaf Light theme

        SwingUtilities.invokeLater(() -> createGUI());
    }
}
```

The `setup()` method:
1. Installs the LAF with `UIManager.setLookAndFeel()`
2. Sets system properties for better rendering
3. Enables HiDPI support
4. Returns `true` if successful, `false` otherwise

### Alternative: Manual Installation

For more control:
```java
try {
    UIManager.setLookAndFeel(new FlatLightLaf());
} catch (UnsupportedLookAndFeelException e) {
    System.err.println("Failed to initialize FlatLaf");
}
```

## FlatLaf Themes

FlatLaf provides two categories of themes: built-in core themes and hundreds of community-created IntelliJ themes.

### Built-in Theme Example: FlatIntelliJLaf

```java
import com.formdev.flatlaf.FlatIntelliJLaf;

FlatIntelliJLaf.setup();
```

**FlatIntelliJLaf** mimics IntelliJ IDEA's default light theme:
- Familiar to Java developers
- Balanced colors and contrast
- Professional appearance suitable for development tools

This is the theme used in our project. See the [complete list of built-in themes](#built-in-flatlaf-themes) in the Appendix.

### IntelliJ Theme Example: FlatDarculaLaf

```java
import com.formdev.flatlaf.FlatDarculaLaf;

FlatDarculaLaf.setup();
```

**FlatDarculaLaf** mimics IntelliJ IDEA's Darcula dark theme:
- Very popular dark theme among developers
- Well-suited for code-focused applications
- Reduces eye strain in low-light environments

**This project already includes IntelliJ themes support!** The required JAR (`external/flatlaf-intellij-themes-3.5.4.jar`) is linked into the build path, so you can use any IntelliJ theme without additional setup.

For the complete collection of available themes (100+), see the [Appendix: All Available Themes](#appendix-all-available-themes) at the end of this document.

## In Our Codebase

This project uses FlatLaf's IntelliJ theme. See [Application.java](../../src/edu/uw/tcss/game/Application.java) (lines 30-32):

```java
FlatIntelliJLaf.setup();
//        FlatCarbonIJTheme.setup();
//        FlatDarculaLaf.setup();
```

The active theme is `FlatIntelliJLaf` (line 30), which provides IntelliJ IDEA's default light appearance. Two alternative themes are commented out:
- `FlatCarbonIJTheme` (line 31): Minimalist dark theme
- `FlatDarculaLaf` (line 32): IntelliJ's popular Darcula dark theme

To try different themes, simply comment out line 30 and uncomment one of the alternatives (or add your own choice from the [Appendix](#appendix-all-available-themes)).

## Theme-Aware Colors in Our Codebase

One of FlatLaf's most powerful features is its ability to provide theme-appropriate colors that automatically adapt when switching between light and dark themes. Our codebase demonstrates this in two places:

### Icon Colors - GameIcons.java

The `GameIcons` utility class creates custom icons for directional arrows and the new game button. Instead of hard-coding icon colors (which would look wrong in dark themes), it queries FlatLaf for theme-appropriate colors.

See [GameIcons.java](../../src/edu/uw/tcss/game/gui/util/GameIcons.java) (lines 87-98):

```java
private static Color getIconColor() {
    Color iconColor = UIManager.getColor("Actions.Grey");

    if (iconColor == null) {
        if (FlatLaf.isLafDark()) {
            iconColor = LIGHT_GREY;
        } else {
            iconColor = DARK_GREY;
        }
    }

    return iconColor;
}
```

**How it works**:
1. **First choice**: Query `UIManager` for `"Actions.Grey"` - FlatLaf's semantic color for action icons
   - Light themes: Returns darker grey (`#6E6E6E`)
   - Dark themes: Returns lighter grey (`#AFB1B3`)
2. **Fallback**: If FlatLaf isn't active (null color), detect dark mode with `FlatLaf.isLafDark()` and choose appropriate fallback

**Why this matters**:
- Icons automatically match the theme
- Same code works for light and dark themes
- No manual color adjustments when switching themes
- Uses semantic color names (`"Actions.Grey"`) rather than specific RGB values

This pattern is used in `setupGraphics()` (line 75) when creating arrow icons and play button icons. The icons will always be visible and aesthetically appropriate regardless of the active theme.

### Board Drawing Colors - GameBoardPanel.java

The `GameBoardPanel` uses the same technique to draw the game board grid and game piece. It demonstrates two semantic colors: `Actions.Grey` for normal drawing and `Actions.Red` for error states.

**Normal drawing color** - See [GameBoardPanel.java](../../src/edu/uw/tcss/game/gui/view/GameBoardPanel.java) (lines 145-157):

```java
private static Color getDrawColor() {
    Color color = UIManager.getColor("Actions.Grey");

    if (color == null) {
        if (FlatLaf.isLafDark()) {
            color = LIGHT_GREY;
        } else {
            color = DARK_GREY;
        }
    }

    return color;
}
```

**Error color** - See [GameBoardPanel.java](../../src/edu/uw/tcss/game/gui/view/GameBoardPanel.java) (lines 167-174):

```java
private static Color getErrorColor() {
    Color color = UIManager.getColor("Actions.Red");

    if (color == null) {
        color = Color.RED;
    }

    return color;
}
```

**How it works**:
- `"Actions.Grey"` adapts for neutral UI elements (grid lines, normal piece color)
  - Light themes: Darker grey (`#6E6E6E`)
  - Dark themes: Lighter grey (`#AFB1B3`)
- `"Actions.Red"` adapts for error/warning states (invalid moves)
  - Light themes: Darker red (`#DB5860`)
  - Dark themes: Lighter red (`#C75450`)

**Usage in painting**:
- Line 129: Grid lines use `getDrawColor()` for theme-appropriate contrast
- Line 184: Valid moves reset piece to `getDrawColor()` (normal state)
- Line 192: Invalid moves change piece to `getErrorColor()` (error state)
- Line 203: New game resets piece to `getDrawColor()` (normal state)

**Benefits of this approach**:
1. **Automatic adaptation**: Grid lines and error colors are visible in both light and dark themes
2. **Consistent aesthetics**: All UI elements match the active theme's color palette
3. **Maintainability**: Change theme once, everything updates (grid, piece, error state)
4. **Progressive enhancement**: Works with FlatLaf, gracefully degrades without it
5. **Semantic naming**: `Actions.Red` clearly communicates "error/warning" regardless of actual RGB values

### Key Takeaway: Semantic Colors

Both examples demonstrate a critical FlatLaf pattern: **use semantic color names from `UIManager` rather than hard-coded RGB values**.

**Wrong** (hard-coded):
```java
g2d.setColor(new Color(110, 110, 110));  // Looks bad in dark themes
myPieceColor = Color.RED;                 // Same red in all themes
```

**Right** (semantic):
```java
g2d.setColor(UIManager.getColor("Actions.Grey"));  // Adapts to theme
myPieceColor = UIManager.getColor("Actions.Red");  // Theme-appropriate red
```

FlatLaf provides many semantic colors (our codebase uses the first two):
- `"Actions.Grey"` - Neutral action icons *(used for grid lines, icons, normal piece color)*
- `"Actions.Red"` - Destructive actions and errors *(used for invalid move indicator)*
- `"Actions.Blue"` - Primary actions
- `"Actions.Green"` - Success states
- `"Actions.Yellow"` - Warning states
- `"Panel.background"` - Panel backgrounds
- `"Component.borderColor"` - Component borders
- Many more in FlatLaf documentation

## Customizing FlatLaf

### Changing UI Properties

FlatLaf allows runtime customization of colors, fonts, and other properties:

```java
UIManager.put("Button.arc", 10);  // Rounded button corners
UIManager.put("Component.arc", 10);  // Rounded component corners
UIManager.put("TextComponent.arc", 10);  // Rounded text fields

UIManager.put("Button.background", new Color(0x3574F0));  // Custom button color
```

### Loading Custom .theme.json Files

Create your own theme or load community themes:
```java
FlatLaf.registerCustomDefaultsSource("themes");  // Load from themes/ directory
FlatIntelliJLaf.setup();
UIManager.setLookAndFeel(new FlatLaf.IntelliJTheme(
    new File("mytheme.theme.json")
));
```

### Per-Component Customization

Apply custom properties to specific components:
```java
button.putClientProperty("JButton.buttonType", "roundRect");
scrollPane.putClientProperty("JScrollPane.smoothScrolling", true);
```

## Benefits

### 1. Modern Appearance
Flat design matches contemporary application aesthetics, making Swing applications look current rather than dated.

### 2. Cross-Platform Consistency
Same appearance on Windows, macOS, and Linux—users get a consistent experience regardless of platform.

### 3. Dark Theme Support
Built-in dark themes work on all platforms, even those without native dark mode support.

### 4. HiDPI Excellence
Excellent rendering on 4K and Retina displays with proper 2x/3x scaling.

### 5. Developer Familiarity
IntelliJ-based themes are familiar to Java developers who use JetBrains IDEs daily.

### 6. Easy Theme Switching
Swap themes with one line of code—great for user preferences or testing different appearances.

### 7. Active Maintenance
Regular updates ensure compatibility with new Java versions and fix issues quickly.

## Common Patterns

### User-Selectable Themes

Let users choose their preferred theme:

```java
JComboBox<String> themeSelector = new JComboBox<>(new String[]{
    "FlatLaf Light",
    "FlatLaf Dark",
    "IntelliJ",
    "Darcula",
    "Arc Dark",
    "Dracula",
    "Nord",
    "One Dark"
});

themeSelector.addActionListener(e -> {
    String selected = (String) themeSelector.getSelectedItem();
    try {
        switch (selected) {
            case "FlatLaf Light" -> UIManager.setLookAndFeel(new FlatLightLaf());
            case "FlatLaf Dark" -> UIManager.setLookAndFeel(new FlatDarkLaf());
            case "IntelliJ" -> UIManager.setLookAndFeel(new FlatIntelliJLaf());
            case "Darcula" -> UIManager.setLookAndFeel(new FlatDarculaLaf());
            case "Arc Dark" -> FlatArcDarkIJTheme.setup();
            case "Dracula" -> FlatDraculaIJTheme.setup();
            case "Nord" -> FlatNordIJTheme.setup();
            case "One Dark" -> FlatOneDarkIJTheme.setup();
        }
        SwingUtilities.updateComponentTreeUI(frame);
        frame.pack();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
});
```

### System Dark Mode Detection

Match OS dark mode preference:
```java
if (FlatLaf.isLafDark()) {
    // Already using a dark theme
} else if (SystemInfo.isMacOS &&
           MacSupport.getSystemAppearanceIsDark()) {
    FlatDarculaLaf.setup();  // Use dark theme
} else {
    FlatIntelliJLaf.setup();  // Use light theme
}
```

## Common Mistakes

### 1. Not Calling setup() Early Enough

**Problem**: Must call `setup()` before creating any Swing components.

**Wrong:**
```java
JFrame frame = new JFrame();  // Created before LAF set
FlatIntelliJLaf.setup();      // Too late—frame uses old LAF
```

**Right:**
```java
FlatIntelliJLaf.setup();      // First!
JFrame frame = new JFrame();  // Now uses FlatLaf
```

### 2. Missing Dependency

**Problem**: IntelliJ themes require the separate `flatlaf-intellij-themes` dependency.

**Wrong:**
```java
// Only flatlaf dependency in pom.xml
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;  // ClassNotFoundException
```

**Right:**
```xml
<!-- Add both dependencies -->
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>flatlaf</artifactId>
</dependency>
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>flatlaf-intellij-themes</artifactId>
</dependency>
```

### 3. Overriding LAF Colors

**Problem**: Hard-coded colors prevent theme changes from working.

**Wrong:**
```java
panel.setBackground(Color.WHITE);  // Stays white even in dark theme
```

**Right:**
```java
// Let FlatLaf manage colors, or use UIManager:
panel.setBackground(UIManager.getColor("Panel.background"));
```

### 4. Not Updating UI After Theme Change

**Problem**: Forgetting to update component tree when changing themes at runtime.

**Wrong:**
```java
FlatDarkLaf.setup();
// Components still show old theme
```

**Right:**
```java
FlatDarkLaf.setup();
SwingUtilities.updateComponentTreeUI(frame);
frame.pack();
```

## Comparison with Built-in LAFs

| Feature | Metal | Nimbus | System | FlatLaf |
|---------|-------|--------|--------|---------|
| **Modern appearance** | ❌ | ⚠️ | Varies | ✅ |
| **Dark theme** | ❌ | ❌ | Platform | ✅ |
| **Cross-platform consistency** | ✅ | ✅ | ❌ | ✅ |
| **HiDPI support** | ⚠️ | ⚠️ | Varies | ✅ |
| **Customization** | Limited | Limited | ❌ | Extensive |
| **Active development** | Slow | Slow | N/A | ✅ |
| **Theme variety** | 1 | 1 | 1 | 100+ |

## Further Reading

### Official Resources
- **[FlatLaf Website](https://www.formdev.com/flatlaf/)** - Official documentation and getting started guide
- **[FlatLaf GitHub](https://github.com/JFormDesigner/FlatLaf)** - Source code, issues, and releases
- **[FlatLaf Themes Pack](https://www.formdev.com/flatlaf-intellij-themes/)** - Browse all available IntelliJ themes
- **[FlatLaf Demo](https://www.formdev.com/flatlaf/demo/)** - Interactive demo application

### Theme Repositories
- **[IntelliJ Themes](http://www.ideacolorthemes.org/)** - Browse IntelliJ themes compatible with FlatLaf
- **[JetBrains Plugin Repository](https://plugins.jetbrains.com/search?tags=Theme)** - More themes from JetBrains marketplace

### Migration Guides
- **[Migrating from Other LAFs](https://www.formdev.com/flatlaf/migration/)** - Tips for switching to FlatLaf
- **[Customization Guide](https://www.formdev.com/flatlaf/customizing/)** - Advanced theme customization

### Community
- **[FlatLaf Discussions](https://github.com/JFormDesigner/FlatLaf/discussions)** - Ask questions and share tips
- **[FlatLaf Issues](https://github.com/JFormDesigner/FlatLaf/issues)** - Report bugs and request features

---

## Appendix: All Available Themes

This section provides a complete reference of all built-in FlatLaf themes and popular IntelliJ community themes.

### Built-in FlatLaf Themes

FlatLaf provides four core themes included in the base library:

#### FlatLightLaf
```java
import com.formdev.flatlaf.FlatLightLaf;

FlatLightLaf.setup();
```
- Clean, flat light theme
- Good for traditional business applications
- High contrast for readability

#### FlatDarkLaf
```java
import com.formdev.flatlaf.FlatDarkLaf;

FlatDarkLaf.setup();
```
- Modern dark theme
- Reduces eye strain in low-light environments
- Popular for developer tools and IDEs

#### FlatIntelliJLaf
```java
import com.formdev.flatlaf.FlatIntelliJLaf;

FlatIntelliJLaf.setup();
```
- Mimics IntelliJ IDEA's default light theme
- Familiar to Java developers
- Balanced colors and contrast
- **Used in this project**

#### FlatDarculaLaf
```java
import com.formdev.flatlaf.FlatDarculaLaf;

FlatDarculaLaf.setup();
```
- Mimics IntelliJ IDEA's Darcula dark theme
- Very popular dark theme
- Well-suited for code-focused applications

### IntelliJ Community Themes

These themes require the `flatlaf-intellij-themes` dependency (already included in this project via `external/flatlaf-intellij-themes-3.5.4.jar`).

#### Arc Dark (Material Design)
```java
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;

FlatArcDarkIJTheme.setup();
```
- Dark theme with Material Design influences
- Smooth, modern color palette
- Good for long coding sessions

#### Arc Dark Orange
```java
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;

FlatArcDarkOrangeIJTheme.setup();
```
- Arc Dark variant with orange accents
- High contrast for focused elements
- Warm color scheme

#### Carbon
```java
import com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme;

FlatCarbonIJTheme.setup();
```
- Minimalist dark theme
- Low saturation colors
- Reduces visual noise
- **Commented option in Application.java**

#### Cobalt 2
```java
import com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme;

FlatCobalt2IJTheme.setup();
```
- Vibrant blue-based dark theme
- High contrast syntax colors
- Popular with web developers

#### Cyan Light
```java
import com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme;

FlatCyanLightIJTheme.setup();
```
- Light theme with cyan accents
- Fresh, modern appearance
- Good for UI-heavy applications

#### Dracula
```java
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;

FlatDraculaIJTheme.setup();
```
- Famous purple-tinted dark theme
- Used by developers worldwide
- Balanced contrast and saturation

#### Gradianto Dark Fuchsia
```java
import com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme;

FlatGradiantoDarkFuchsiaIJTheme.setup();
```
- Dark theme with magenta/purple gradients
- Bold, distinctive appearance
- High visual impact

#### Gruvbox Dark
```java
import com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme;

FlatGruvboxDarkMediumIJTheme.setup();
```
- Retro-inspired warm dark theme
- Designed for eye comfort
- Brownish tones reduce blue light

#### Hiberbee Dark
```java
import com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme;

FlatHiberbeeDarkIJTheme.setup();
```
- Modern dark theme with bee-inspired yellow accents
- Balanced and professional
- Good all-purpose dark theme

#### Material Design Dark
```java
import com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme;

FlatMaterialDesignDarkIJTheme.setup();
```
- Implements Google's Material Design principles
- Clean, structured appearance
- Familiar to Android developers

#### Monocai
```java
import com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme;

FlatMonocaiIJTheme.setup();
```
- Based on Monokai color scheme
- High contrast dark theme
- Popular with code editors

#### Nord
```java
import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;

FlatNordIJTheme.setup();
```
- Arctic-inspired cool color palette
- Calm, low-contrast dark theme
- Frosty blues and grays

#### One Dark
```java
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;

FlatOneDarkIJTheme.setup();
```
- Atom editor's famous dark theme
- Balanced colors with subtle syntax highlighting
- Very popular among developers

#### Solarized Dark
```java
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;

FlatSolarizedDarkIJTheme.setup();
```
- Scientifically designed color palette
- Reduces eye strain
- Consistent contrast ratios

#### Solarized Light
```java
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;

FlatSolarizedLightIJTheme.setup();
```
- Light version of Solarized
- Same color science principles
- Excellent for daylight conditions

#### Spacegray
```java
import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme;

FlatSpacegrayIJTheme.setup();
```
- Sublime Text-inspired dark theme
- Muted, spacey colors
- Minimal visual distraction

#### Vuesion
```java
import com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme;

FlatVuesionIJTheme.setup();
```
- Vue.js-inspired theme
- Modern web development aesthetic
- Green accents on dark background

#### Xcode Dark
```java
import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;

FlatXcodeDarkIJTheme.setup();
```
- Apple Xcode IDE theme
- Familiar to iOS/macOS developers
- Clean, Apple-style design

### Browsing All Available Themes

To preview all available FlatLaf themes interactively, visit the **[FlatLaf Demo Application](https://www.formdev.com/flatlaf/#demo)** online. This web-based demo lets you switch between themes and see how components render in each style.

You can also programmatically list all installed Look and Feels:
```java
for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    System.out.println(info.getName() + ": " + info.getClassName());
}
```

---

**Previous**: Learn about [Swing Look and Feel](./README.md) architecture and history.
