# Label Printer Application

A JavaFX-based desktop app for selecting and printing labels on a fixed-size sheet. Data is stored locally under the `data/` directory.

## Requirements

- Java 17+
- Gradle (or configure IntelliJ to use Gradle)

## Running

### Using Gradle

From the repo root:

```bash
gradle run
```

> If you see **"JavaFX runtime components are missing"**, it typically means the app was launched directly by the IDE without the JavaFX module path. Use the Gradle run task instead, or configure the IDE as shown below.

### IntelliJ IDEA

**Recommended:** Run the app using Gradle.

1. Open **Settings → Build, Execution, Deployment → Build Tools → Gradle**.
2. Set **Run tests using** and **Build and run using** to **Gradle**.
3. Run the `run` task under **Gradle → Tasks → application**.

**Alternative (manual JavaFX SDK):**

1. Download the JavaFX SDK that matches your JDK (e.g., JavaFX 21).
2. Add VM options to your run configuration:

```
--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.graphics,javafx.fxml
```

## Data Storage

The app writes local data files under `data/`:

- `products.csv`
- `versions.txt`
- `warehouses.txt`
- `labels.csv`
- `config.properties`
