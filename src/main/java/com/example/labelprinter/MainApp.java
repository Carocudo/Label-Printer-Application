package com.example.labelprinter;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

public class MainApp extends Application {
    private static final double UI_LABEL_WIDTH = 200;
    private static final double UI_LABEL_HEIGHT = 90;
    private static final double UI_GRID_GAP = 8;

    private final DataStore dataStore = new DataStore(Path.of("data"));
    private final ObservableList<Product> productOptions = FXCollections.observableArrayList();
    private final ObservableList<String> versionOptions = FXCollections.observableArrayList();
    private final ObservableList<String> warehouseOptions = FXCollections.observableArrayList();
    private final List<LabelCell> labelCells = new java.util.ArrayList<>();

    private LabelCell activeCell;
    private boolean updatingControls;
    private double fontSize = 12;
    private ComboBox<Product> productCombo;
    private ComboBox<String> versionCombo;
    private ComboBox<String> warehouseCombo;
    private DatePicker datePicker;

    @Override
    public void start(Stage primaryStage) {
        try {
            dataStore.ensureDataDir();
            loadInitialData();
        } catch (IOException ex) {
            showError("Data error", "Unable to load local data.", ex.getMessage());
        }

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        GridPane gridPane = buildLabelGrid();
        root.setCenter(gridPane);

        VBox controls = buildControls();
        root.setBottom(controls);
        refreshActiveControls();

        Scene scene = new Scene(root, 900, 820);
        primaryStage.setTitle("Label Printer Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            try {
                dataStore.saveLabels(labelCells);
                dataStore.saveProducts(productOptions);
                dataStore.saveSimpleList(dataStore.getVersionsFilename(), versionOptions);
                dataStore.saveSimpleList(dataStore.getWarehousesFilename(), warehouseOptions);
                dataStore.saveFontSize(fontSize);
            } catch (IOException ex) {
                showError("Save error", "Failed to save label data.", ex.getMessage());
            }
        });
    }

    private void loadInitialData() throws IOException {
        productOptions.setAll(dataStore.loadProducts());
        versionOptions.setAll(dataStore.loadSimpleList(dataStore.getVersionsFilename()));
        warehouseOptions.setAll(dataStore.loadSimpleList(dataStore.getWarehousesFilename()));

        OptionalDouble storedFontSize = dataStore.loadFontSize();
        storedFontSize.ifPresent(value -> fontSize = value);
    }

    private GridPane buildLabelGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(UI_GRID_GAP);
        gridPane.setVgap(UI_GRID_GAP);
        gridPane.setAlignment(Pos.CENTER);

        Map<String, Product> productsByCode = new HashMap<>();
        for (Product product : productOptions) {
            productsByCode.put(product.getCode(), product);
        }

        Map<Integer, LabelData> savedLabels = new HashMap<>();
        try {
            savedLabels = dataStore.loadLabels(productsByCode);
        } catch (IOException ex) {
            showError("Data error", "Unable to load saved labels.", ex.getMessage());
        }

        for (int row = 0; row < LabelSheetConfig.ROWS; row++) {
            for (int col = 0; col < LabelSheetConfig.COLUMNS; col++) {
                LabelCell cell = new LabelCell(row, col, UI_LABEL_WIDTH, UI_LABEL_HEIGHT);
                cell.setFontSize(fontSize);
                int index = dataStore.toIndex(row, col);
                LabelData data = savedLabels.get(index);
                if (data != null) {
                    cell.setData(data);
                }
                cell.setOnMouseClicked(event -> {
                    if (event.getButton() != MouseButton.PRIMARY) {
                        return;
                    }
                    setActiveCell(cell);
                });
                labelCells.add(cell);
                gridPane.add(cell, col, row);
            }
        }

        if (!labelCells.isEmpty()) {
            setActiveCell(labelCells.get(0));
        }

        return gridPane;
    }

    private VBox buildControls() {
        productCombo = new ComboBox<>(productOptions);
        productCombo.setPromptText("Product");
        productCombo.setMaxWidth(Double.MAX_VALUE);

        versionCombo = new ComboBox<>(versionOptions);
        versionCombo.setPromptText("Version");
        versionCombo.setMaxWidth(Double.MAX_VALUE);

        warehouseCombo = new ComboBox<>(warehouseOptions);
        warehouseCombo.setPromptText("Warehouse");
        warehouseCombo.setMaxWidth(Double.MAX_VALUE);

        datePicker = new DatePicker();
        datePicker.setPromptText("Date");
        datePicker.setMaxWidth(Double.MAX_VALUE);

        productCombo.setOnAction(event -> updateActiveLabelData(productCombo.getValue(),
                versionCombo.getValue(), warehouseCombo.getValue(), datePicker.getValue()));
        versionCombo.setOnAction(event -> updateActiveLabelData(productCombo.getValue(),
                versionCombo.getValue(), warehouseCombo.getValue(), datePicker.getValue()));
        warehouseCombo.setOnAction(event -> updateActiveLabelData(productCombo.getValue(),
                versionCombo.getValue(), warehouseCombo.getValue(), datePicker.getValue()));
        datePicker.setOnAction(event -> updateActiveLabelData(productCombo.getValue(),
                versionCombo.getValue(), warehouseCombo.getValue(), datePicker.getValue()));

        Button editDataButton = new Button("Edit products & versions");
        editDataButton.setOnAction(event -> {
            EditDataDialog dialog = new EditDataDialog(productOptions, versionOptions, warehouseOptions, payload -> {
                productOptions.setAll(payload.products());
                versionOptions.setAll(payload.versions());
                warehouseOptions.setAll(payload.warehouses());
                try {
                    dataStore.saveProducts(productOptions);
                    dataStore.saveSimpleList(dataStore.getVersionsFilename(), versionOptions);
                    dataStore.saveSimpleList(dataStore.getWarehousesFilename(), warehouseOptions);
                } catch (IOException ex) {
                    showError("Save error", "Unable to save product lists.", ex.getMessage());
                }
                reconcileProducts();
                refreshActiveControls();
            });
            dialog.showAndWait();
        });

        Button editFontButton = new Button("Edit Font parameters");
        editFontButton.setOnAction(event -> {
            FontSettingsDialog dialog = new FontSettingsDialog(fontSize);
            Optional<Double> result = dialog.showAndWait();
            result.ifPresent(size -> {
                fontSize = Math.round(size);
                labelCells.forEach(cell -> cell.setFontSize(fontSize));
                try {
                    dataStore.saveFontSize(fontSize);
                } catch (IOException ex) {
                    showError("Save error", "Unable to save font size.", ex.getMessage());
                }
            });
        });

        Button clearButton = new Button("Clear label");
        clearButton.setOnAction(event -> clearActiveLabel());

        Button printButton = new Button("Print");
        printButton.setOnAction(event -> handlePrint());

        HBox row1 = new HBox(10, productCombo, versionCombo, warehouseCombo, datePicker);
        HBox.setHgrow(productCombo, Priority.ALWAYS);
        HBox.setHgrow(versionCombo, Priority.ALWAYS);
        HBox.setHgrow(warehouseCombo, Priority.ALWAYS);
        HBox.setHgrow(datePicker, Priority.ALWAYS);

        HBox row2 = new HBox(10, editDataButton, editFontButton, clearButton, printButton);
        row2.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(12, new Separator(), row1, row2);
        container.setPadding(new Insets(12, 0, 0, 0));

        return container;
    }

    private void setActiveCell(LabelCell cell) {
        if (activeCell != null) {
            activeCell.setActive(false);
        }
        activeCell = cell;
        if (activeCell != null) {
            activeCell.setActive(true);
        }
        refreshActiveControls();
    }

    private void updateActiveLabelData(Product product, String version, String warehouse, LocalDate date) {
        if (updatingControls || activeCell == null) {
            return;
        }
        boolean isEmpty = product == null
                && (version == null || version.isBlank())
                && (warehouse == null || warehouse.isBlank())
                && date == null;
        if (isEmpty) {
            activeCell.setData(null);
            activeCell.refresh();
            try {
                dataStore.saveLabels(labelCells);
            } catch (IOException ex) {
                showError("Save error", "Unable to save labels.", ex.getMessage());
            }
            return;
        }
        LabelData data = activeCell.getData();
        if (data == null) {
            data = new LabelData();
            activeCell.setData(data);
        }
        data.setProduct(product);
        data.setVersion(version);
        data.setWarehouse(warehouse);
        data.setDate(date);
        activeCell.refresh();
        try {
            dataStore.saveLabels(labelCells);
        } catch (IOException ex) {
            showError("Save error", "Unable to save labels.", ex.getMessage());
        }
    }

    private void refreshActiveControls() {
        if (activeCell == null || productCombo == null) {
            return;
        }
        updatingControls = true;
        LabelData data = activeCell.getData();
        if (data == null || data.isEmpty()) {
            productCombo.setValue(null);
            versionCombo.setValue(null);
            warehouseCombo.setValue(null);
            datePicker.setValue(null);
        } else {
            productCombo.setValue(data.getProduct());
            versionCombo.setValue(data.getVersion());
            warehouseCombo.setValue(data.getWarehouse());
            datePicker.setValue(data.getDate());
        }
        updatingControls = false;
    }

    private void clearActiveLabel() {
        if (activeCell == null) {
            return;
        }
        updatingControls = true;
        productCombo.setValue(null);
        versionCombo.setValue(null);
        warehouseCombo.setValue(null);
        datePicker.setValue(null);
        updatingControls = false;
        updateActiveLabelData(null, null, null, null);
    }

    private void reconcileProducts() {
        Map<String, Product> productByCode = new HashMap<>();
        for (Product product : productOptions) {
            productByCode.put(product.getCode(), product);
        }
        for (LabelCell cell : labelCells) {
            LabelData data = cell.getData();
            if (data == null || data.getProduct() == null) {
                continue;
            }
            Product updated = productByCode.get(data.getProduct().getCode());
            if (updated == null) {
                data.setProduct(null);
            } else {
                data.setProduct(updated);
            }
            cell.refresh();
        }
    }

    private void handlePrint() {
        boolean hasSelection = labelCells.stream().anyMatch(LabelCell::isSelected);
        if (!hasSelection) {
            showAlert("Nothing to print", "Select at least one label to print.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showAlert("Printer unavailable", "No printer job could be created.");
            return;
        }
        boolean proceed = job.showPrintDialog(null);
        if (!proceed) {
            return;
        }

        Pane printPane = buildPrintPane(job);
        boolean success = job.printPage(printPane);
        if (success) {
            job.endJob();
        } else {
            showAlert("Print failed", "Printer job did not complete.");
        }
    }

    private Pane buildPrintPane(PrinterJob job) {
        double labelWidth = LabelSheetConfig.mmToPoints(LabelSheetConfig.LABEL_WIDTH_MM);
        double labelHeight = LabelSheetConfig.mmToPoints(LabelSheetConfig.LABEL_HEIGHT_MM);
        double gapX = LabelSheetConfig.mmToPoints(LabelSheetConfig.GAP_X_MM);
        double gapY = LabelSheetConfig.mmToPoints(LabelSheetConfig.GAP_Y_MM);

        double printableWidth = job.getJobSettings().getPageLayout().getPrintableWidth();
        double printableHeight = job.getJobSettings().getPageLayout().getPrintableHeight();

        Pane root = new Pane();
        root.setPrefSize(printableWidth, printableHeight);

        for (LabelCell cell : labelCells) {
            if (!cell.isSelected()) {
                continue;
            }
            int row = cell.getRow();
            int col = cell.getCol();
            double x = col * (labelWidth + gapX);
            double y = row * (labelHeight + gapY);
            VBox labelNode = createPrintLabel(cell.getData(), labelWidth, labelHeight);
            labelNode.setLayoutX(x);
            labelNode.setLayoutY(y);
            root.getChildren().add(labelNode);
        }
        return root;
    }

    private VBox createPrintLabel(LabelData data, double width, double height) {
        VBox box = new VBox(2);
        box.setPrefSize(width, height);
        box.setPadding(new Insets(4));
        if (data != null && !data.isEmpty()) {
            String productText = "";
            if (data.getProduct() != null) {
                productText = data.getProduct().getName().isBlank()
                        ? data.getProduct().getCode()
                        : data.getProduct().getName();
            }
            String versionText = data.getVersion() == null ? "" : data.getVersion();
            String productLine = (productText + " " + versionText).trim();
            Label product = new Label(productLine);
            product.setFont(Font.font(fontSize));
            product.setStyle("-fx-font-weight: bold;");
            Label warehouse = new Label(data.getWarehouse() == null ? "" : data.getWarehouse());
            warehouse.setFont(Font.font(fontSize));
            Label date = new Label(data.getDate() == null ? "" : data.getDate().toString());
            date.setFont(Font.font(fontSize));
            box.getChildren().addAll(product, warehouse, date);
        }
        return box;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.setContentText(details);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
