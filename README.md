# Label Printer Application

A JavaFX desktop application for managing and printing product labels on fixed-size sheets. Built for internal use in factory environment.

---

## Features

- Fill label sheets with product, weight (g/m²), factory and date information
- Click any label cell to select and edit it
- Configurable label sheet layout (size, margins, gaps)
- Adjustable font size
- Saves all data locally between sessions
- Prints directly to any installed printer
- Full Swedish UI

---

## Download & Install

Go to the [Releases](../../releases) page and download the latest `Label-Printer-*.exe` installer.

- Installs to Program Files
- Adds a Start Menu entry and Desktop shortcut
- No Java installation required — runtime is bundled

> **Note:** Windows may show a SmartScreen warning on first launch since the app is not code-signed. Click **"More info" → "Run anyway"** to proceed. If your IT department has blocked unknown apps entirely, ask them to whitelist the installer.

---

## Data Storage

All data is stored locally in a `data/` folder next to the application:

| File | Contents |
|---|---|
| `Papperskvalitet.csv` | Product list (code + name) |
| `Ytvikt.txt` | Weight options (g/m²) |
| `Fabrik.txt` | Factory/warehouse list |
| `etiketter.csv` | Saved label sheet state |
| `config.properties` | Sheet layout & font settings |

---

## Building from Source

### Requirements
- JDK 17+
- Maven 3.8+

### Run locally
```bash
mvn clean javafx:run
```

### Build fat JAR
```bash
mvn clean package
```

### Build EXE installer
Requires [WiX Toolset](https://wixtoolset.org) installed.

```bash
jpackage ^
  --input target/ ^
  --name "Label Printer" ^
  --main-jar label-printer-1.0-SNAPSHOT.jar ^
  --main-class Launcher ^
  --type exe ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --win-shortcut ^
  --win-menu ^
  --vendor "Your Company Name" ^
  --dest output/
```

### Automated builds
The project uses GitHub Actions to automatically build and publish an EXE installer when a version tag is pushed:

```bash
git tag v1.0
git push origin v1.0
```

The resulting installer will appear under [Releases](../../releases) automatically.

---

## Development

Built with:
- Java 17
- JavaFX 17
- Maven (maven-shade-plugin for fat JAR)