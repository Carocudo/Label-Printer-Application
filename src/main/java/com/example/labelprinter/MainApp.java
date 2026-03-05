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
import javafx.scene.control.ScrollPane;
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
import java.util.stream.Collectors;

public class MainApp extends Application {
    private static final double UI_GRID_GAP = 8;

    private final DataStore dataStore = new DataStore(Path.of("data"));
    private final ObservableList<Product> productOptions = FXCollections.observableArrayList();
    private final ObservableList<String> versionOptions = FXCollections.observableArrayList();
    private final ObservableList<String> warehouseOptions = FXCollections.observableArrayList();
    private final List<LabelCell> labelCells = new java.util.ArrayList<>();

    private LabelCell activeCell;
    private boolean updatingControls;
    private PrintSettings settings = new PrintSettings();
    private GridPane gridPane;
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
            showError("Datafel", "Det gick inte att läsa in lokal data.", ex.getMessage());
        }

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        gridPane = buildLabelGrid();
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);

        // Hide scrollbars when content fits, show only when needed
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        root.setCenter(scrollPane);

        VBox controls = buildControls();
        root.setBottom(controls);
        refreshActiveControls();

        Scene scene = new Scene(root, 900, 820);
        primaryStage.setTitle("Etikettsutskrift");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            try {
                dataStore.saveLabels(labelCells);
                dataStore.saveProducts(productOptions);
                dataStore.saveSimpleList(dataStore.getVersionsFilename(), versionOptions);
                dataStore.saveSimpleList(dataStore.getWarehousesFilename(), warehouseOptions);
                dataStore.saveSettings(settings);
            } catch (IOException ex) {
                showError("Sparfel", "Det gick inte att spara etikettdata.", ex.getMessage());
            }
        });
    }

    private void loadInitialData() throws IOException {
        productOptions.setAll(dataStore.loadProducts());
        versionOptions.setAll(sortedValues(dataStore.loadSimpleList(dataStore.getVersionsFilename())));
        warehouseOptions.setAll(sortedValues(dataStore.loadSimpleList(dataStore.getWarehousesFilename())));

        settings = dataStore.loadSettings();
    }

    private GridPane buildLabelGrid() {
        GridPane gridPane = new GridPane();
        updateGridSpacing(gridPane);
        gridPane.setAlignment(Pos.CENTER);

        Map<String, Product> productsByCode = new HashMap<>();
        for (Product product : productOptions) {
            productsByCode.put(product.getCode(), product);
        }

        Map<Integer, LabelData> savedLabels = new HashMap<>();
        try {
            savedLabels = dataStore.loadLabels(productsByCode);
        } catch (IOException ex) {
            showError("Datafel", "Det gick inte att läsa in sparade etiketter.", ex.getMessage());
        }

        for (int row = 0; row < LabelSheetConfig.ROWS; row++) {
            for (int col = 0; col < LabelSheetConfig.COLUMNS; col++) {
                double widthPx = LabelSheetConfig.mmToPixels(settings.getLabelWidthMm());
                double heightPx = LabelSheetConfig.mmToPixels(settings.getLabelHeightMm());
                LabelCell cell = new LabelCell(row, col, widthPx, heightPx);
                applySettingsToCell(cell);
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
        productCombo.setPromptText("Papperskvalitet");
        productCombo.setMaxWidth(Double.MAX_VALUE);
        productCombo.setVisibleRowCount(5);

        versionCombo = new ComboBox<>(versionOptions);
        versionCombo.setPromptText("Ytvikt");
        versionCombo.setMaxWidth(Double.MAX_VALUE);
        versionCombo.setVisibleRowCount(5);

        warehouseCombo = new ComboBox<>(warehouseOptions);
        warehouseCombo.setPromptText("Fabrik");
        warehouseCombo.setMaxWidth(Double.MAX_VALUE);
        warehouseCombo.setVisibleRowCount(5);

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

        Button editDataButton = new Button("Redigera papperskvalitet & ytvikt");
        editDataButton.setOnAction(event -> {
            EditDataDialog dialog = new EditDataDialog(productOptions, versionOptions, warehouseOptions, payload -> {
                productOptions.setAll(payload.products());
                versionOptions.setAll(sortedValues(payload.versions()));
                warehouseOptions.setAll(sortedValues(payload.warehouses()));
                try {
                    dataStore.saveProducts(productOptions);
                    dataStore.saveSimpleList(dataStore.getVersionsFilename(), versionOptions);
                    dataStore.saveSimpleList(dataStore.getWarehousesFilename(), warehouseOptions);
                } catch (IOException ex) {
                    showError("Sparfel", "Det gick inte att spara produktlistor.", ex.getMessage());
                }
                reconcileProducts();
                refreshActiveControls();
            });
            dialog.showAndWait();
        });

        Button editFontButton = new Button("Redigera teckenstil");
        editFontButton.setOnAction(event -> {
            FontSettingsDialog dialog = new FontSettingsDialog(settings.getFontSize());
            Optional<Double> result = dialog.showAndWait();
            result.ifPresent(size -> {
                settings.setFontSize(Math.round(size));
                labelCells.forEach(cell -> cell.setFontSize(settings.getFontSize()));
                try {
                    dataStore.saveSettings(settings);
                } catch (IOException ex) {
                    showError("Sparfel", "Det gick inte att spara teckenstorlek.", ex.getMessage());
                }
            });
        });

        Button sheetSettingsButton = new Button("Arkinställningar");
        sheetSettingsButton.setOnAction(event -> {
            SheetSettingsDialog dialog = new SheetSettingsDialog(settings);
            Optional<PrintSettings> result = dialog.showAndWait();
            result.ifPresent(updated -> {
                settings = updated;
                applySettingsToCells();
                try {
                    dataStore.saveSettings(settings);
                } catch (IOException ex) {
                    showError("Sparfel", "Det gick inte att spara arkinställningar.", ex.getMessage());
                }
            });
        });

        Button clearButton = new Button("Rensa etikett");
        clearButton.setOnAction(event -> clearActiveLabel());

        Button printButton = new Button("Skriv ut");
        printButton.setOnAction(event -> handlePrint());

        HBox row1 = new HBox(10, productCombo, versionCombo, warehouseCombo, datePicker);
        HBox.setHgrow(productCombo, Priority.ALWAYS);
        HBox.setHgrow(versionCombo, Priority.ALWAYS);
        HBox.setHgrow(warehouseCombo, Priority.ALWAYS);
        HBox.setHgrow(datePicker, Priority.ALWAYS);

        HBox row2 = new HBox(10, editDataButton, editFontButton, sheetSettingsButton, clearButton, printButton);
        row2.setAlignment(Pos.CENTER_LEFT);

        VBox container = new VBox(12, new Separator(), row1, row2);
        container.setPadding(new Insets(12, 0, 0, 0));

        return container;
    }

    private List<String> sortedValues(List<String> input) {
        return input.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
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
                showError("Sparfel", "Det gick inte att spara etikettdata.", ex.getMessage());
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
            showError("Sparfel", "Det gick inte att spara etikettdata.", ex.getMessage());
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

    private void applySettingsToCell(LabelCell cell) {
        double widthPx = LabelSheetConfig.mmToPixels(settings.getLabelWidthMm());
        double heightPx = LabelSheetConfig.mmToPixels(settings.getLabelHeightMm());
        double paddingTopPx = LabelSheetConfig.mmToPixels(settings.getPaddingTopMm());
        double paddingLeftPx = LabelSheetConfig.mmToPixels(settings.getPaddingLeftMm());
        cell.setDimensions(widthPx, heightPx);
        cell.setTextPadding(paddingTopPx, paddingLeftPx);
        cell.setFontSize(settings.getFontSize());
    }

    private void applySettingsToCells() {
        labelCells.forEach(this::applySettingsToCell);
        if (gridPane != null) {
            updateGridSpacing(gridPane);
        }
    }

    private void updateGridSpacing(GridPane targetGrid) {
        double gapX = LabelSheetConfig.mmToPixels(settings.getGapXmm());
        double gapY = LabelSheetConfig.mmToPixels(settings.getGapYmm());
        targetGrid.setHgap(gapX > 0 ? gapX : UI_GRID_GAP);
        targetGrid.setVgap(gapY > 0 ? gapY : UI_GRID_GAP);
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
            showAlert("Inget att skriva ut", "Välj minst en etikett att skriva ut.");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showAlert("Skrivare ej tillgänglig", "Kunde inte skapa utskriftsjobb.");
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
            showAlert("Utskrift misslyckades", "Utskriftsjobbet slutfördes inte.");
        }
    }

    private Pane buildPrintPane(PrinterJob job) {
        double labelWidth = LabelSheetConfig.mmToPoints(settings.getLabelWidthMm());
        double labelHeight = LabelSheetConfig.mmToPoints(settings.getLabelHeightMm());
        double gapX = LabelSheetConfig.mmToPoints(settings.getGapXmm());
        double gapY = LabelSheetConfig.mmToPoints(settings.getGapYmm());

        double printableWidth = job.getJobSettings().getPageLayout().getPrintableWidth();
        double printableHeight = job.getJobSettings().getPageLayout().getPrintableHeight();

        double pageWidth = LabelSheetConfig.mmToPoints(settings.getPageWidthMm());
        double pageHeight = LabelSheetConfig.mmToPoints(settings.getPageHeightMm());
        double availableWidth = Math.min(printableWidth, pageWidth);
        double availableHeight = Math.min(printableHeight, pageHeight);
        double marginLeft = LabelSheetConfig.mmToPoints(settings.getMarginLeftMm());
        double marginTop = LabelSheetConfig.mmToPoints(settings.getMarginTopMm());
        double offsetX = Math.min(Math.max(0, marginLeft), availableWidth);
        double offsetY = Math.min(Math.max(0, marginTop), availableHeight);

        Pane root = new Pane();
        root.setPrefSize(printableWidth, printableHeight);

        for (LabelCell cell : labelCells) {
            if (!cell.isSelected()) {
                continue;
            }
            int row = cell.getRow();
            int col = cell.getCol();
            double x = offsetX + col * (labelWidth + gapX);
            double y = offsetY + row * (labelHeight + gapY);
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
        double paddingTop = LabelSheetConfig.mmToPoints(settings.getPaddingTopMm());
        double paddingLeft = LabelSheetConfig.mmToPoints(settings.getPaddingLeftMm());
        box.setPadding(new Insets(paddingTop, 0, 0, paddingLeft));
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
            product.setFont(Font.font(settings.getFontSize()));
            product.setStyle("-fx-font-weight: bold;");
            Label warehouse = new Label(data.getWarehouse() == null ? "" : data.getWarehouse());
            warehouse.setFont(Font.font(settings.getFontSize()));
            Label date = new Label(data.getDate() == null ? "" : data.getDate().toString());
            date.setFont(Font.font(settings.getFontSize()));
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
