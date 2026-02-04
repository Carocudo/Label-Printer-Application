package com.example.labelprinter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public class DataStore {
    private static final String PRODUCTS_FILE = "products.csv";
    private static final String VERSIONS_FILE = "versions.txt";
    private static final String WAREHOUSES_FILE = "warehouses.txt";
    private static final String LABELS_FILE = "labels.csv";
    private static final String CONFIG_FILE = "config.properties";

    private final Path dataDir;

    public DataStore(Path dataDir) {
        this.dataDir = dataDir;
    }

    public void ensureDataDir() throws IOException {
        Files.createDirectories(dataDir);
    }

    public List<Product> loadProducts() throws IOException {
        Path path = dataDir.resolve(PRODUCTS_FILE);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Product> products = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            List<String> values = CsvUtil.parseLine(lines.get(i));
            if (values.size() < 2) {
                continue;
            }
            products.add(new Product(values.get(0), values.get(1)));
        }
        return products;
    }

    public void saveProducts(List<Product> products) throws IOException {
        Path path = dataDir.resolve(PRODUCTS_FILE);
        List<String> lines = new ArrayList<>();
        lines.add("code,name");
        for (Product product : products) {
            lines.add(CsvUtil.escape(product.getCode()) + "," + CsvUtil.escape(product.getName()));
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public List<String> loadSimpleList(String filename) throws IOException {
        Path path = dataDir.resolve(filename);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8)
                .stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toList());
    }

    public void saveSimpleList(String filename, List<String> values) throws IOException {
        Path path = dataDir.resolve(filename);
        List<String> lines = values.stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toList());
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public Map<Integer, LabelData> loadLabels(Map<String, Product> productsByCode) throws IOException {
        Path path = dataDir.resolve(LABELS_FILE);
        Map<Integer, LabelData> labels = new HashMap<>();
        if (!Files.exists(path)) {
            return labels;
        }
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {
            List<String> values = CsvUtil.parseLine(lines.get(i));
            if (values.size() < 6) {
                continue;
            }
            int row;
            int col;
            try {
                row = Integer.parseInt(values.get(0));
                col = Integer.parseInt(values.get(1));
            } catch (NumberFormatException ex) {
                continue;
            }
            LabelData data = new LabelData();
            String productCode = values.get(2);
            if (!productCode.isBlank()) {
                Product product = productsByCode.get(productCode);
                if (product != null) {
                    data.setProduct(product);
                }
            }
            String version = values.get(3);
            if (!version.isBlank()) {
                data.setVersion(version);
            }
            String warehouse = values.get(4);
            if (!warehouse.isBlank()) {
                data.setWarehouse(warehouse);
            }
            String dateValue = values.get(5);
            if (!dateValue.isBlank()) {
                data.setDate(LocalDate.parse(dateValue));
            }
            labels.put(toIndex(row, col), data);
        }
        return labels;
    }

    public void saveLabels(List<LabelCell> cells) throws IOException {
        Path path = dataDir.resolve(LABELS_FILE);
        List<String> lines = new ArrayList<>();
        lines.add("row,col,productCode,version,warehouse,date");
        for (LabelCell cell : cells) {
            LabelData data = cell.getData();
            if (data == null || data.isEmpty()) {
                continue;
            }
            int row = cell.getRow();
            int col = cell.getCol();
            String productCode = data.getProduct() != null ? data.getProduct().getCode() : "";
            String version = data.getVersion() == null ? "" : data.getVersion();
            String warehouse = data.getWarehouse() == null ? "" : data.getWarehouse();
            String date = data.getDate() == null ? "" : data.getDate().toString();
            lines.add(row + "," + col + "," + CsvUtil.escape(productCode) + "," + CsvUtil.escape(version)
                    + "," + CsvUtil.escape(warehouse) + "," + CsvUtil.escape(date));
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public OptionalDouble loadFontSize() throws IOException {
        Path path = dataDir.resolve(CONFIG_FILE);
        if (!Files.exists(path)) {
            return OptionalDouble.empty();
        }
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] parts = line.split("=", 2);
            if (parts.length == 2 && parts[0].trim().equals("fontSize")) {
                try {
                    return OptionalDouble.of(Double.parseDouble(parts[1].trim()));
                } catch (NumberFormatException ex) {
                    return OptionalDouble.empty();
                }
            }
        }
        return OptionalDouble.empty();
    }

    public void saveFontSize(double fontSize) throws IOException {
        Path path = dataDir.resolve(CONFIG_FILE);
        List<String> lines = List.of("fontSize=" + fontSize);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public String getVersionsFilename() {
        return VERSIONS_FILE;
    }

    public String getWarehousesFilename() {
        return WAREHOUSES_FILE;
    }

    public int toIndex(int row, int col) {
        return row * LabelSheetConfig.COLUMNS + col;
    }

    public Path getDataDir() {
        return dataDir;
    }

    public String getProductsFilename() {
        return PRODUCTS_FILE;
    }

    public String getLabelsFilename() {
        return LABELS_FILE;
    }
}
