package io.github.carocudo.labelprinter;

import java.time.LocalDate;

public class LabelData {
    private Product product;
    private String version;
    private String warehouse;
    private LocalDate date;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isEmpty() {
        return product == null
                && (version == null || version.isBlank())
                && (warehouse == null || warehouse.isBlank())
                && date == null;
    }

    public String getProductDisplayText() {
        if (product == null) {
            return "";
        }
        return product.getName().isBlank() ? product.getCode() : product.getName();
    }

    public String getProductLineText() {
        String productText = getProductDisplayText();
        String versionText = version == null ? "" : version;
        return (productText + " " + versionText).trim();
    }
}
