# Label Printer Application

A JavaFX desktop application for managing and printing product labels on fixed-size sheets. Built for internal use in a factory environment.

---

## Features

- Fill label sheets with paper quality, grammage (g/m²), factory and date information
- Click any label cell to select and edit it
- Double-click to edit existing entries in all data lists
- Configurable label sheet layout (size, margins, gaps)
- Adjustable font size
- 4 UI themes: Corporate, Dark, Minimal, Ocean
- Swedish and English UI — automatically matches OS language
- Saves all data locally between sessions
- Prints directly to any installed printer
- Portable ZIP version available — no installation needed

---

## Download & Install

Go to the [Releases](../../releases) page and download the latest version:

| File | Description |
|---|---|
| `Label-Printer-*.exe` | Windows installer — installs to Program Files with Start Menu and Desktop shortcut |
| `Label-Printer-app-image-*.zip` | Portable version — unzip and run, no installation needed |

No Java installation required — runtime is bundled in both versions.

> **Note:** Windows may show a SmartScreen warning on first launch since the app is not code-signed. Click **"More info" → "Run anyway"** to proceed. If your IT department has blocked unknown apps entirely, ask them to whitelist the installer.

---

## Data Storage

All data is stored locally in a `data/` folder next to the application:

| File | Contents |
|---|---|
| `Papperskvalitet.csv` | Product list (code + name) |
| `Ytvikt.txt` | Grammage options (g/m²) |
| `Fabrik.txt` | Factory/warehouse list |
| `etiketter.csv` | Saved label sheet state |
| `config.properties` | Sheet layout, font and theme settings |

---

## Releasing a New Version

The project uses GitHub Actions to automatically build and publish installers when a version tag is pushed:

```bash
git tag v1.0
git push origin v1.0
```

Or trigger a build manually from the **Actions** tab on GitHub — click **"Run workflow"** and enter a version number.

The resulting installer and portable ZIP will appear under [Releases](../../releases) automatically.

---

## Development

Built with:
- Java 17
- JavaFX 17
- Maven (maven-shade-plugin for fat JAR, jpackage for EXE)
- JNA 5.13 (Windows title bar theming)

To run locally:
```bash
mvn clean javafx:run
```